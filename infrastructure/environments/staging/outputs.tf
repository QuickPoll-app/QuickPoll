
# Staging Environment - Outputs


output "alb_dns_name" {
  description = "Staging ALB DNS name"
  value       = module.loadbalancer.alb_dns_name
}

output "vpc_id" {
  description = "Staging VPC ID"
  value       = module.networking.vpc_id
}

output "ecs_cluster_name" {
  description = "Staging ECS cluster name"
  value       = module.ecs.cluster_name
}

output "db_endpoint" {
  description = "Staging RDS endpoint"
  value       = module.database.db_endpoint
}

output "backend_ecr_url" {
  description = "Backend ECR repository URL"
  value       = module.compute.backend_ecr_repository_url
}

output "frontend_ecr_url" {
  description = "Frontend ECR repository URL"
  value       = module.compute.frontend_ecr_repository_url
}

output "backend_log_group" {
  description = "Backend CloudWatch log group"
  value       = module.ecs.backend_log_group
}

output "frontend_log_group" {
  description = "Frontend CloudWatch log group"
  value       = module.ecs.frontend_log_group
}

output "rds_events_sns_topic" {
  description = "SNS topic for RDS event notifications"
  value       = module.database.rds_events_sns_topic_arn
}
