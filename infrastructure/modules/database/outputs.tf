
# Database Module - Outputs


output "db_endpoint" {
  description = "RDS instance endpoint (host:port)"
  value       = aws_db_instance.main.endpoint
}

output "db_address" {
  description = "RDS instance address (host only)"
  value       = aws_db_instance.main.address
}

output "db_port" {
  description = "RDS instance port"
  value       = aws_db_instance.main.port
}

output "db_name" {
  description = "Database name"
  value       = aws_db_instance.main.db_name
}

output "db_instance_id" {
  description = "RDS instance ID"
  value       = aws_db_instance.main.id
}

output "db_arn" {
  description = "RDS instance ARN"
  value       = aws_db_instance.main.arn
}

output "rds_events_sns_topic_arn" {
  description = "SNS topic ARN for RDS event notifications"
  value       = aws_sns_topic.rds_events.arn
}
