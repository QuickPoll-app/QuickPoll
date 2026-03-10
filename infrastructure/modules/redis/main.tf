
# Redis Module - Main Configuration
# ElastiCache Redis in private subnets


# ElastiCache Subnet Group
resource "aws_elasticache_subnet_group" "main" {
  name       = trimsuffix(substr("${var.project_name}-${var.environment}-redis-subnet", 0, 63), "-")
  subnet_ids = var.private_subnet_ids

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-redis-subnet"
  })
}

# ElastiCache Redis Cluster
resource "aws_elasticache_cluster" "main" {
  cluster_id           = trimsuffix(substr("${var.project_name}-${var.environment}-redis", 0, 40), "-")
  engine               = "redis"
  engine_version       = var.redis_engine_version
  node_type            = var.redis_node_type
  num_cache_nodes      = 1
  port                 = 6379
  parameter_group_name = var.redis_parameter_group
  subnet_group_name    = aws_elasticache_subnet_group.main.name
  security_group_ids   = [var.redis_security_group_id]

  snapshot_retention_limit = var.environment == "prod" ? 7 : 1
  snapshot_window          = "02:00-03:00"
  maintenance_window       = "sun:05:00-sun:06:00"

  auto_minor_version_upgrade = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-redis"
  })
}
