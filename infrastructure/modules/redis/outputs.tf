
# Redis Module - Outputs


output "redis_host" {
  description = "Redis primary endpoint address"
  value       = aws_elasticache_cluster.main.cache_nodes[0].address
}

output "redis_port" {
  description = "Redis port"
  value       = tostring(aws_elasticache_cluster.main.cache_nodes[0].port)
}

output "redis_cluster_id" {
  description = "ElastiCache cluster ID"
  value       = aws_elasticache_cluster.main.cluster_id
}
