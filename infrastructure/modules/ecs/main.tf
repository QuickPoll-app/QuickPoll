
# ECS Module - Main Configuration
# ECS Fargate Cluster, Services, Task Definitions


# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = trimsuffix(substr("${var.project_name}-${var.environment}", 0, 255), "-")

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-cluster"
  })
}

# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/${var.project_name}-${var.environment}-backend"
  retention_in_days = 30

  tags = merge(var.tags, {
    Name    = "${var.project_name}-${var.environment}-backend-logs"
    Service = "backend"
  })
}

resource "aws_cloudwatch_log_group" "frontend" {
  name              = "/ecs/${var.project_name}-${var.environment}-frontend"
  retention_in_days = 30

  tags = merge(var.tags, {
    Name    = "${var.project_name}-${var.environment}-frontend-logs"
    Service = "frontend"
  })
}

# Backend Task Definition
resource "aws_ecs_task_definition" "backend" {
  family                   = "${var.project_name}-${var.environment}-backend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.backend_cpu
  memory                   = var.backend_memory
  execution_role_arn       = var.ecs_task_execution_role_arn
  task_role_arn            = var.ecs_task_role_arn

  container_definitions = jsonencode([
    {
      name      = "backend"
      image     = var.backend_image
      essential = true

      portMappings = [
        {
          containerPort = 8081
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_endpoint}/${var.db_name}" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
        { name = "SPRING_JPA_HIBERNATE_DDL_AUTO", value = "update" },
        { name = "SPRING_PROFILES_ACTIVE", value = var.environment },
        { name = "SERVER_PORT", value = "8081" },
      ]

      secrets = [
        { name = "SPRING_DATASOURCE_PASSWORD", valueFrom = "arn:aws:ssm:${var.aws_region}:*:parameter/${var.project_name}/${var.environment}/db-password" },
        { name = "JWT_SECRET", valueFrom = "arn:aws:ssm:${var.aws_region}:*:parameter/${var.project_name}/${var.environment}/jwt-secret" },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.backend.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "backend"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:8081/actuator/health || exit 1"]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-backend-task"
  })
}

# Frontend Task Definition
resource "aws_ecs_task_definition" "frontend" {
  family                   = "${var.project_name}-${var.environment}-frontend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.frontend_cpu
  memory                   = var.frontend_memory
  execution_role_arn       = var.ecs_task_execution_role_arn
  task_role_arn            = var.ecs_task_role_arn

  container_definitions = jsonencode([
    {
      name      = "frontend"
      image     = var.frontend_image
      essential = true

      portMappings = [
        {
          containerPort = 80
          protocol      = "tcp"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.frontend.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "frontend"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 10
      }
    }
  ])

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-frontend-task"
  })
}

# Backend Service
resource "aws_ecs_service" "backend" {
  name            = trimsuffix(substr("${var.project_name}-${var.environment}-backend", 0, 255), "-")
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.backend_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.ecs_tasks_security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.backend_target_group_arn
    container_name   = "backend"
    container_port   = 8081
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = 100

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-backend-service"
  })
}

# Frontend Service
resource "aws_ecs_service" "frontend" {
  name            = trimsuffix(substr("${var.project_name}-${var.environment}-frontend", 0, 255), "-")
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = var.frontend_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.ecs_tasks_security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.frontend_target_group_arn
    container_name   = "frontend"
    container_port   = 80
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = 100

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-frontend-service"
  })
}
