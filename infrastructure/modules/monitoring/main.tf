
# Monitoring Module - Main Configuration
# Grafana, Loki, Jaeger on ECS Fargate

resource "aws_service_discovery_private_dns_namespace" "monitoring" {
  name        = "monitoring.local"
  description = "Private DNS for monitoring services"
  vpc         = var.vpc_id
}

# Jaeger

resource "aws_service_discovery_service" "jaeger" {
  name = "jaeger"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.monitoring.id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
  }
}

resource "aws_ecs_task_definition" "jaeger" {
  family                   = "${var.project_name}-${var.environment}-jaeger"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = var.ecs_task_execution_role_arn

  container_definitions = jsonencode([
    {
      name  = "jaeger"
      image = "jaegertracing/all-in-one:1.55"
      portMappings = [
        { containerPort = 16686, protocol = "tcp" },
        { containerPort = 4317, protocol = "tcp" },
        { containerPort = 4318, protocol = "tcp" }
      ]
      environment = [
        { name = "COLLECTOR_OTLP_ENABLED", value = "true" },
        { name = "QUERY_BASE_PATH", value = "/jaeger" }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-jaeger"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "jaeger"
          "awslogs-create-group"  = "true"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "jaeger" {
  name            = "${var.project_name}-${var.environment}-jaeger"
  cluster         = "${var.project_name}-${var.environment}-cluster"
  task_definition = aws_ecs_task_definition.jaeger.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  load_balancer {
    target_group_arn = var.jaeger_target_group_arn
    container_name   = "jaeger"
    container_port   = 16686
  }

  service_registries {
    registry_arn = aws_service_discovery_service.jaeger.arn
  }
}

# Loki

resource "aws_service_discovery_service" "loki" {
  name = "loki"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.monitoring.id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
  }
}

resource "aws_ecs_task_definition" "loki" {
  family                   = "${var.project_name}-${var.environment}-loki"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = var.ecs_task_execution_role_arn

  container_definitions = jsonencode([
    {
      name    = "loki"
      image   = "grafana/loki:3.0.0"
      command = ["-config.file=/etc/loki/local-config.yaml"]
      portMappings = [
        { containerPort = 3100, protocol = "tcp" }
      ]
      mountPoints = [
        {
          containerPath = "/loki"
          sourceVolume  = "loki-data"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-loki"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "loki"
          "awslogs-create-group"  = "true"
        }
      }
    }
  ])

  volume {
    name = "loki-data"
    efs_volume_configuration {
      file_system_id     = var.efs_monitoring_id
      transit_encryption = "ENABLED"
      authorization_config {
        access_point_id = var.efs_access_point_loki_id
        iam             = "DISABLED"
      }
    }
  }
}

resource "aws_ecs_service" "loki" {
  name            = "${var.project_name}-${var.environment}-loki"
  cluster         = "${var.project_name}-${var.environment}-cluster"
  task_definition = aws_ecs_task_definition.loki.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  service_registries {
    registry_arn = aws_service_discovery_service.loki.arn
  }
}

# Grafana

resource "aws_ecs_task_definition" "grafana" {
  family                   = "${var.project_name}-${var.environment}-grafana"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = var.ecs_task_execution_role_arn

  container_definitions = jsonencode([
    {
      name  = "grafana"
      image = "grafana/grafana:10.3.3"
      portMappings = [
        { containerPort = 3000, protocol = "tcp" }
      ]
      environment = [
        { name = "GF_SECURITY_ADMIN_PASSWORD", value = var.grafana_admin_password },
        { name = "GF_SERVER_ROOT_URL", value = "/grafana/" },
        { name = "GF_SERVER_SERVE_FROM_SUB_PATH", value = "true" }
      ]
      mountPoints = [
        {
          containerPath = "/var/lib/grafana"
          sourceVolume  = "grafana-data"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-grafana"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "grafana"
          "awslogs-create-group"  = "true"
        }
      }
    }
  ])

  volume {
    name = "grafana-data"
    efs_volume_configuration {
      file_system_id     = var.efs_monitoring_id
      transit_encryption = "ENABLED"
      authorization_config {
        access_point_id = var.efs_access_point_grafana_id
        iam             = "DISABLED"
      }
    }
  }
}

resource "aws_ecs_service" "grafana" {
  name            = "${var.project_name}-${var.environment}-grafana"
  cluster         = "${var.project_name}-${var.environment}-cluster"
  task_definition = aws_ecs_task_definition.grafana.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  load_balancer {
    target_group_arn = var.monitoring_target_group_arn
    container_name   = "grafana"
    container_port   = 3000
  }
}
