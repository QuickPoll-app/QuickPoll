
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

  deletion_protection       = var.deletion_protection
  skip_final_snapshot       = var.environment == "staging" ? true : false
  final_snapshot_identifier = var.environment == "prod" ? trimsuffix(substr("${var.project_name}-${var.environment}-final-snap", 0, 63), "-") : null

  # Performance Insights
  performance_insights_enabled = true

  # CloudWatch logs
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db"
  })
}
