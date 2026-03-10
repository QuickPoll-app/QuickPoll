
# Database Module - Main Configuration
# RDS PostgreSQL 16 in private subnets


# DB Subnet Group
resource "aws_db_subnet_group" "main" {
  name       = trimsuffix(substr("${var.project_name}-${var.environment}-db-subnet", 0, 63), "-")
  subnet_ids = var.private_subnet_ids

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-subnet"
  })
}

# RDS Instance
resource "aws_db_instance" "main" {
  identifier = trimsuffix(substr("${var.project_name}-${var.environment}-db", 0, 63), "-")

  engine         = "postgres"
  instance_class = var.db_instance_class

  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.rds_security_group_id]

  # Never publicly accessible - only ECS tasks can reach it
  publicly_accessible = false

  multi_az                = var.multi_az
  backup_retention_period = var.backup_retention_period
  backup_window           = "03:00-04:00"
  maintenance_window      = "sun:04:00-sun:05:00"

  # Point-in-time recovery (enabled via backup_retention_period > 0)
  # Copy tags to automated backups and snapshots
  copy_tags_to_snapshot = true

  deletion_protection       = var.deletion_protection
  skip_final_snapshot       = var.environment == "staging" ? true : false
  final_snapshot_identifier = var.environment == "prod" ? trimsuffix(substr("${var.project_name}-${var.environment}-final-snap", 0, 63), "-") : null

  # Performance Insights
  performance_insights_enabled          = true
  performance_insights_retention_period = var.environment == "prod" ? 31 : 7

  # CloudWatch logs
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  # Auto minor version upgrade during maintenance window
  auto_minor_version_upgrade = true

  tags = merge(var.tags, {
    Name   = "${var.project_name}-${var.environment}-db"
    Backup = "enabled"
  })
}

# =============================================================
# Database Backup Strategy
# =============================================================

# SNS Topic for RDS event notifications
resource "aws_sns_topic" "rds_events" {
  name = trimsuffix(substr("${var.project_name}-${var.environment}-rds-events", 0, 255), "-")

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-rds-events"
  })
}

# RDS Event Subscription — backup, recovery, failure notifications
resource "aws_db_event_subscription" "main" {
  name      = trimsuffix(substr("${var.project_name}-${var.environment}-rds-events", 0, 63), "-")
  sns_topic = aws_sns_topic.rds_events.arn

  source_type = "db-instance"
  source_ids  = [aws_db_instance.main.identifier]

  event_categories = [
    "backup",
    "recovery",
    "failure",
    "notification",
    "maintenance",
  ]

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-rds-event-sub"
  })
}

# CloudWatch Alarms for database health
resource "aws_cloudwatch_metric_alarm" "db_cpu" {
  alarm_name          = "${var.project_name}-${var.environment}-db-high-cpu"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "RDS CPU utilization > 80% for 15 minutes"
  alarm_actions       = [aws_sns_topic.rds_events.arn]
  ok_actions          = [aws_sns_topic.rds_events.arn]

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-cpu-alarm"
  })
}

resource "aws_cloudwatch_metric_alarm" "db_free_storage" {
  alarm_name          = "${var.project_name}-${var.environment}-db-low-storage"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 5368709120 # 5 GB in bytes
  alarm_description   = "RDS free storage < 5 GB"
  alarm_actions       = [aws_sns_topic.rds_events.arn]
  ok_actions          = [aws_sns_topic.rds_events.arn]

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-storage-alarm"
  })
}

resource "aws_cloudwatch_metric_alarm" "db_connections" {
  alarm_name          = "${var.project_name}-${var.environment}-db-high-connections"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = var.db_max_connections_alarm
  alarm_description   = "RDS connections > ${var.db_max_connections_alarm} for 10 minutes"
  alarm_actions       = [aws_sns_topic.rds_events.arn]
  ok_actions          = [aws_sns_topic.rds_events.arn]

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-connections-alarm"
  })
}
