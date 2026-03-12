
# ECS Module - Outputs


output "cluster_id" {
  description = "ECS cluster ID"
  value       = aws_ecs_cluster.main.id
}

output "cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "backend_service_name" {
  description = "Backend ECS service name"
  value       = aws_ecs_service.backend.name
}

output "frontend_service_name" {
  description = "Frontend ECS service name"
  value       = aws_ecs_service.frontend.name
}

output "backend_task_definition_arn" {
  description = "Backend task definition ARN"
  value       = aws_ecs_task_definition.backend.arn
}

output "frontend_task_definition_arn" {
  description = "Frontend task definition ARN"
  value       = aws_ecs_task_definition.frontend.arn
}

output "backend_log_group" {
  description = "CloudWatch log group for backend"
  value       = aws_cloudwatch_log_group.backend.name
}

output "frontend_log_group" {
  description = "CloudWatch log group for frontend"
  value       = aws_cloudwatch_log_group.frontend.name
}

output "backend_autoscaling_target_id" {
  description = "Backend autoscaling target ID"
  value       = aws_appautoscaling_target.backend.resource_id
}

output "frontend_autoscaling_target_id" {
  description = "Frontend autoscaling target ID"
  value       = aws_appautoscaling_target.frontend.resource_id
}
