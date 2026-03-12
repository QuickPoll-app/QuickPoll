# Staging Environment - Main Configuration
# Remote state with S3 + DynamoDB locking

terraform {
  required_version = ">= 1.7.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.30"
    }
  }

  backend "s3" {}
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
      Team        = "team7"
    }
  }
}

# Local values
locals {
  environment = var.environment
  tags = {
    Project     = var.project_name
    Environment = local.environment
  }
}

# Networking
module "networking" {
  source = "../../modules/networking"

  project_name = var.project_name
  environment  = local.environment
  tags         = local.tags
}

# Security
module "security" {
  source = "../../modules/security"

  project_name = var.project_name
  environment  = local.environment
  vpc_id       = module.networking.vpc_id
  vpc_cidr     = module.networking.vpc_cidr
  tags         = local.tags
}

# Database
module "database" {
  source = "../../modules/database"

  project_name          = var.project_name
  environment           = local.environment
  vpc_id                = module.networking.vpc_id
  private_subnet_ids    = module.networking.private_subnet_ids
  rds_security_group_id = module.security.rds_security_group_id
  db_password           = var.db_password
  db_instance_class     = var.db_instance_class
  multi_az              = false
  deletion_protection   = false
  tags                  = local.tags
}

# Redis
module "redis" {
  source = "../../modules/redis"

  project_name            = var.project_name
  environment             = local.environment
  private_subnet_ids      = module.networking.private_subnet_ids
  redis_security_group_id = module.security.redis_security_group_id
  redis_node_type         = "cache.t3.micro"
  tags                    = local.tags
}

# Load Balancer
module "loadbalancer" {
  source = "../../modules/loadbalancer"

  project_name          = var.project_name
  environment           = local.environment
  vpc_id                = module.networking.vpc_id
  public_subnet_ids     = module.networking.public_subnet_ids
  alb_security_group_id = module.security.alb_security_group_id
  tags                  = local.tags
}

# ECR Repositories
module "compute" {
  source = "../../modules/compute"

  project_name = var.project_name
  environment  = local.environment
  tags         = local.tags
}

# ECS Services
module "ecs" {
  source = "../../modules/ecs"

  project_name                     = var.project_name
  environment                      = local.environment
  aws_region                       = var.aws_region
  private_subnet_ids               = module.networking.private_subnet_ids
  ecs_tasks_security_group_id      = module.security.ecs_tasks_security_group_id
  ecs_task_execution_role_arn      = module.security.ecs_task_execution_role_arn
  ecs_task_role_arn                = module.security.ecs_task_role_arn
  backend_target_group_arn         = module.loadbalancer.backend_target_group_arn
  frontend_target_group_arn        = module.loadbalancer.frontend_target_group_arn
  backend_image                    = var.backend_image
  frontend_image                   = var.frontend_image
  backend_cpu                      = var.backend_cpu
  backend_memory                   = var.backend_memory
  frontend_cpu                     = var.frontend_cpu
  frontend_memory                  = var.frontend_memory
  backend_desired_count            = var.backend_desired_count
  frontend_desired_count           = var.frontend_desired_count
  service_discovery_namespace_id   = module.networking.service_discovery_namespace_id
  service_discovery_namespace_name = module.networking.service_discovery_namespace_name

  # Autoscaling — staging runs conservative
  backend_min_count      = 1
  backend_max_count      = 2
  frontend_min_count     = 1
  frontend_max_count     = 2
  backend_cpu_target     = 70
  backend_memory_target  = 80
  frontend_cpu_target    = 70
  frontend_memory_target = 80

  db_endpoint = module.database.db_address
  db_name     = module.database.db_name
  db_password = var.db_password
  jwt_secret  = var.jwt_secret
  redis_host  = module.redis.redis_host
  redis_port  = module.redis.redis_port
  tags        = local.tags
}

# Monitoring (Grafana, Loki, Jaeger)
module "monitoring" {
  source = "../../modules/monitoring"

  project_name                     = var.project_name
  environment                      = local.environment
  vpc_id                           = module.networking.vpc_id
  private_subnet_ids               = module.networking.private_subnet_ids
  service_discovery_namespace_id   = module.networking.service_discovery_namespace_id
  service_discovery_namespace_name = module.networking.service_discovery_namespace_name
  monitoring_security_group_id     = module.security.monitoring_security_group_id
  ecs_cluster_id                   = module.ecs.cluster_id
  ecs_task_execution_role_arn      = module.security.ecs_task_execution_role_arn
  monitoring_target_group_arn      = module.loadbalancer.monitoring_target_group_arn
  jaeger_target_group_arn          = module.loadbalancer.jaeger_target_group_arn
  alertmanager_target_group_arn    = module.loadbalancer.alertmanager_target_group_arn
  loki_target_group_arn            = module.loadbalancer.loki_target_group_arn
  efs_monitoring_id                = module.storage.efs_monitoring_id
  efs_access_point_grafana_id      = module.storage.efs_access_point_grafana_id
  efs_access_point_loki_id         = module.storage.efs_access_point_loki_id
  efs_access_point_prometheus_id   = module.storage.efs_access_point_prometheus_id
  efs_access_point_alertmanager_id = module.storage.efs_access_point_alertmanager_id
  grafana_admin_password           = "admin123"
  slack_webhook_url                = var.slack_webhook_url
  alb_domain                       = "${var.project_name}-${local.environment}-alb.${var.aws_region}.elb.amazonaws.com"
  tags                             = local.tags

  depends_on = [module.loadbalancer]
}

# Storage
module "storage" {
  source = "../../modules/storage"

  project_name          = var.project_name
  environment           = local.environment
  private_subnet_ids    = module.networking.private_subnet_ids
  efs_security_group_id = module.security.efs_security_group_id
  tags                  = local.tags
}
