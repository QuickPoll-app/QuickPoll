
# Storage Module - Outputs

output "alb_logs_bucket_id" {
  description = "S3 bucket ID for ALB access logs"
  value       = aws_s3_bucket.alb_logs.id
}

output "alb_logs_bucket_arn" {
  description = "S3 bucket ARN for ALB access logs"
  value       = aws_s3_bucket.alb_logs.arn
}

output "efs_monitoring_id" {
  description = "ID of EFS filesystem for monitoring"
  value       = aws_efs_file_system.monitoring.id
}

output "efs_access_point_grafana_id" {
  description = "ID of EFS access point for Grafana"
  value       = aws_efs_access_point.grafana.id
}

output "efs_access_point_loki_id" {
  description = "ID of EFS access point for Loki"
  value       = aws_efs_access_point.loki.id
}

output "efs_access_point_prometheus_id" {
  value = aws_efs_access_point.prometheus.id
}

output "efs_access_point_alertmanager_id" {
  value = aws_efs_access_point.alertmanager.id
}
