
# Load Balancer Module - Main Configuration
# ALB with path-based routing: /api/* → backend, /* → frontend


# Application Load Balancer
resource "aws_lb" "main" {
  name               = trimsuffix(substr("${var.project_name}-${var.environment}-alb", 0, 32), "-")
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_security_group_id]
  subnets            = var.public_subnet_ids

  enable_deletion_protection = var.environment == "prod" ? true : false

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-alb"
  })
}

# Backend Target Group
resource "aws_lb_target_group" "backend" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-be-tg", 0, 32), "-")
  port        = 8081
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    path                = var.health_check_path_backend
    port                = "traffic-port"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 10
    interval            = 30
    matcher             = "200"
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-be-tg"
  })
}

# Frontend Target Group
resource "aws_lb_target_group" "frontend" {
  name        = trimsuffix(substr("${var.project_name}-${var.environment}-fe-tg", 0, 32), "-")
  port        = 80
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    path                = var.health_check_path_frontend
    port                = "traffic-port"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-fe-tg"
  })
}

# HTTP Listener (default → frontend)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-http-listener"
  })
}

# API Path Rule → Backend
resource "aws_lb_listener_rule" "api" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }

  condition {
    path_pattern {
      values = ["/api/*", "/actuator/*", "/swagger-ui/*", "/api-docs*"]
    }
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-api-rule"
  })
}
