# Monitoring Module - Main Configuration
# Grafana, Loki, Jaeger on ECS Fargate

# Jaeger

resource "aws_service_discovery_service" "jaeger" {
  name = "jaeger"

  dns_config {
    namespace_id = var.service_discovery_namespace_id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
    failure_threshold = 1
  }

  force_destroy = true

  lifecycle {
    ignore_changes = [health_check_custom_config]
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
        { name = "QUERY_BASE_PATH", value = "/jaeger" },
        { name = "QUERY_UI_CONFIG", value = jsonencode({ "menu" : { "items" : [{ "label" : "About Jaeger", "url" : "http://${var.alb_domain}/jaeger" }] } }) }
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
  cluster         = var.ecs_cluster_id
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
    namespace_id = var.service_discovery_namespace_id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
    failure_threshold = 1
  }

  force_destroy = true

  lifecycle {
    ignore_changes = [health_check_custom_config]
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
  cluster         = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.loki.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  dynamic "load_balancer" {
    for_each = var.loki_target_group_arn != "" ? [1] : []
    content {
      target_group_arn = var.loki_target_group_arn
      container_name   = "loki"
      container_port   = 3100
    }
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
        { name = "GF_SERVER_ROOT_URL", value = "http://${var.alb_domain}/grafana/" },
        { name = "GF_SERVER_SERVE_FROM_SUB_PATH", value = "true" },
        { name = "SLACK_WEBHOOK_URL", value = var.slack_webhook_url },
        { name = "GF_INSTALL_PLUGINS", value = "grafana-piechart-panel" }
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
    },
    {
      name      = "grafana-provisioner"
      image     = "curlimages/curl:latest"
      essential = false
      command   = ["sh", "-c", "sleep 15 && curl -X POST http://grafana:3000/api/datasources -H 'Content-Type: application/json' -u admin:${var.grafana_admin_password} -d '{\"name\":\"Prometheus\",\"type\":\"prometheus\",\"url\":\"http://prometheus.${var.service_discovery_namespace_name}:9090\",\"access\":\"proxy\",\"isDefault\":true}' || true && curl -X POST http://grafana:3000/api/datasources -H 'Content-Type: application/json' -u admin:${var.grafana_admin_password} -d '{\"name\":\"Loki\",\"type\":\"loki\",\"url\":\"http://loki.${var.service_discovery_namespace_name}:3100\",\"access\":\"proxy\"}' || true && curl -X POST http://grafana:3000/api/datasources -H 'Content-Type: application/json' -u admin:${var.grafana_admin_password} -d '{\"name\":\"Jaeger\",\"type\":\"jaeger\",\"uid\":\"jaeger\",\"url\":\"http://jaeger.${var.service_discovery_namespace_name}:16686\",\"access\":\"proxy\"}' || true"]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-grafana"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "grafana-provisioner"
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
  cluster         = var.ecs_cluster_id
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

# Prometheus
resource "aws_service_discovery_service" "prometheus" {
  name = "prometheus"

  dns_config {
    namespace_id = var.service_discovery_namespace_id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
    failure_threshold = 1
  }

  force_destroy = true

  lifecycle {
    ignore_changes = [health_check_custom_config]
  }
}

resource "aws_ecs_task_definition" "prometheus" {
  family                   = "${var.project_name}-${var.environment}-prometheus"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = var.ecs_task_execution_role_arn

  container_definitions = jsonencode([
    {
      name  = "prometheus"
      image = "prom/prometheus:v2.50.1"
      command = [
        "--config.file=/etc/prometheus/prometheus.yml",
        "--storage.tsdb.path=/prometheus"
      ]
      portMappings = [
        { containerPort = 9090, protocol = "tcp" }
      ]
      mountPoints = [
        {
          containerPath = "/etc/prometheus"
          sourceVolume  = "prometheus-config"
        },
        {
          containerPath = "/prometheus"
          sourceVolume  = "prometheus-data"
        }
      ]
      environment = [
        { name = "BACKEND_HOST", value = "backend.${var.service_discovery_namespace_name}" }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-prometheus"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "prometheus"
          "awslogs-create-group"  = "true"
        }
      }
    }
  ])

  volume {
    name = "prometheus-config"
    efs_volume_configuration {
      file_system_id     = var.efs_monitoring_id
      transit_encryption = "ENABLED"
      authorization_config {
        access_point_id = var.efs_access_point_prometheus_id
        iam             = "DISABLED"
      }
    }
  }

  volume {
    name = "prometheus-data"
    # We use the same EFS but a different access point for data? 
    # Actually, the user asked for config. Let's keep data in the container or separate AP if needed.
    # For now, let's just do config as requested.
    efs_volume_configuration {
      file_system_id     = var.efs_monitoring_id
      transit_encryption = "ENABLED"
      authorization_config {
        # Using a subpath /data within the prometheus AP or a new one. 
        # Let's just mount the same AP and let it manage subdirs.
        access_point_id = var.efs_access_point_prometheus_id
        iam             = "DISABLED"
      }
    }
  }
}

resource "aws_ecs_service" "prometheus" {
  name            = "${var.project_name}-${var.environment}-prometheus"
  cluster         = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.prometheus.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  service_registries {
    registry_arn = aws_service_discovery_service.prometheus.arn
  }
}

# Alertmanager
resource "aws_service_discovery_service" "alertmanager" {
  name = "alertmanager"

  dns_config {
    namespace_id = var.service_discovery_namespace_id
    dns_records {
      ttl  = 60
      type = "A"
    }
  }

  health_check_custom_config {
    failure_threshold = 1
  }

  force_destroy = true

  lifecycle {
    ignore_changes = [health_check_custom_config]
  }
}

resource "aws_ecs_task_definition" "alertmanager" {
  family                   = "${var.project_name}-${var.environment}-alertmanager"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = var.ecs_task_execution_role_arn

  container_definitions = jsonencode([
    {
      name  = "alertmanager"
      image = "prom/alertmanager:v0.27.0"
      portMappings = [
        { containerPort = 9093, protocol = "tcp" }
      ]
      mountPoints = [
        {
          containerPath = "/etc/alertmanager"
          sourceVolume  = "alertmanager-config"
        }
      ]
      environment = [
        { name = "SLACK_WEBHOOK_URL", value = var.slack_webhook_url }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/${var.project_name}-${var.environment}-alertmanager"
          "awslogs-region"        = "eu-north-1"
          "awslogs-stream-prefix" = "alertmanager"
          "awslogs-create-group"  = "true"
        }
      }
    }
  ])

  volume {
    name = "alertmanager-config"
    efs_volume_configuration {
      file_system_id     = var.efs_monitoring_id
      transit_encryption = "ENABLED"
      authorization_config {
        access_point_id = var.efs_access_point_alertmanager_id
        iam             = "DISABLED"
      }
    }
  }
}

resource "aws_ecs_service" "alertmanager" {
  name            = "${var.project_name}-${var.environment}-alertmanager"
  cluster         = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.alertmanager.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.private_subnet_ids
    security_groups = [var.monitoring_security_group_id]
  }

  dynamic "load_balancer" {
    for_each = var.alertmanager_target_group_arn != "" ? [1] : []
    content {
      target_group_arn = var.alertmanager_target_group_arn
      container_name   = "alertmanager"
      container_port   = 9093
    }
  }

  service_registries {
    registry_arn = aws_service_discovery_service.alertmanager.arn
  }
}
