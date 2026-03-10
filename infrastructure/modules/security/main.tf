
# Security Module - Main Configuration
# Security Groups, IAM Roles for ECS


# ALB Security Group
resource "aws_security_group" "alb" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-alb-sg", 0, 255), "-")
  description = "Security group for Application Load Balancer"
  vpc_id      = var.vpc_id

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-alb-sg"
  })
}

resource "aws_vpc_security_group_ingress_rule" "alb_http" {
  security_group_id = aws_security_group.alb.id
  description       = "HTTP from anywhere"
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  ip_protocol       = "tcp"
  to_port           = 80
}

resource "aws_vpc_security_group_ingress_rule" "alb_https" {
  security_group_id = aws_security_group.alb.id
  description       = "HTTPS from anywhere"
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  ip_protocol       = "tcp"
  to_port           = 443
}

resource "aws_vpc_security_group_egress_rule" "alb_all_traffic" {
  security_group_id = aws_security_group.alb.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# ECS Tasks Security Group
resource "aws_security_group" "ecs_tasks" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-ecs-sg", 0, 255), "-")
  description = "Security group for ECS Fargate tasks"
  vpc_id      = var.vpc_id

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-ecs-sg"
  })
}

resource "aws_vpc_security_group_ingress_rule" "backend_from_alb" {
  security_group_id            = aws_security_group.ecs_tasks.id
  description                  = "Backend from ALB"
  referenced_security_group_id = aws_security_group.alb.id
  from_port                    = 8081
  ip_protocol                  = "tcp"
  to_port                      = 8081
}

resource "aws_vpc_security_group_ingress_rule" "frontend_from_alb" {
  security_group_id            = aws_security_group.ecs_tasks.id
  description                  = "Frontend from ALB"
  referenced_security_group_id = aws_security_group.alb.id
  from_port                    = 8080
  ip_protocol                  = "tcp"
  to_port                      = 8080
}

resource "aws_vpc_security_group_egress_rule" "ecs_tasks_all_traffic" {
  security_group_id = aws_security_group.ecs_tasks.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# RDS Security Group
resource "aws_security_group" "rds" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-rds-sg", 0, 255), "-")
  description = "Security group for RDS - only accessible from ECS tasks"
  vpc_id      = var.vpc_id

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-rds-sg"
  })
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_ecs" {
  security_group_id            = aws_security_group.rds.id
  description                  = "PostgreSQL from ECS tasks"
  referenced_security_group_id = aws_security_group.ecs_tasks.id
  from_port                    = 5432
  ip_protocol                  = "tcp"
  to_port                      = 5432
}

resource "aws_vpc_security_group_egress_rule" "rds_all_traffic" {
  security_group_id = aws_security_group.rds.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# Redis Security Group
resource "aws_security_group" "redis" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-redis-sg", 0, 255), "-")
  description = "Security group for ElastiCache Redis - only accessible from ECS tasks"
  vpc_id      = var.vpc_id

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-redis-sg"
  })
}

resource "aws_vpc_security_group_ingress_rule" "redis_from_ecs" {
  security_group_id            = aws_security_group.redis.id
  description                  = "Redis from ECS tasks"
  referenced_security_group_id = aws_security_group.ecs_tasks.id
  from_port                    = 6379
  ip_protocol                  = "tcp"
  to_port                      = 6379
}

resource "aws_vpc_security_group_egress_rule" "redis_all_traffic" {
  security_group_id = aws_security_group.redis.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# ECS Task Execution Role
resource "aws_iam_role" "ecs_task_execution" {
  name = trimsuffix(substr("${var.project_name}-${var.environment}-ecs-exec", 0, 64), "-")

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-ecs-exec"
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Allow pulling from ECR and reading SSM parameters
resource "aws_iam_role_policy" "ecs_task_execution_extra" {
  name = trimsuffix(substr("${var.project_name}-${var.environment}-ecs-exec-extra", 0, 64), "-")
  role = aws_iam_role.ecs_task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssm:GetParameters",
          "ssm:GetParameter",
          "secretsmanager:GetSecretValue",
          "kms:Decrypt"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

# ECS Task Role
resource "aws_iam_role" "ecs_task" {
  name = trimsuffix(substr("${var.project_name}-${var.environment}-ecs-task", 0, 64), "-")

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-ecs-task"
  })
}
