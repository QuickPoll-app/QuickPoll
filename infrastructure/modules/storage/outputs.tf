
# Storage Module - Outputs


output "alb_logs_bucket_id" {
  description = "S3 bucket ID for ALB access logs"
  value       = aws_s3_bucket.alb_logs.id
}

output "alb_logs_bucket_arn" {
  description = "S3 bucket ARN for ALB access logs"
  value       = aws_s3_bucket.alb_logs.arn
}
