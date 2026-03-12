
# Load Balancer Module - Outputs

output "alb_arn" {
  description = "ARN of the Application Load Balancer"
  value       = aws_lb.main.arn
}

output "alb_dns_name" {
  description = "DNS name of the ALB"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "Zone ID of the ALB"
  value       = aws_lb.main.zone_id
}

output "backend_target_group_arn" {
  description = "ARN of the backend target group"
  value       = aws_lb_target_group.backend.arn
}

output "frontend_target_group_arn" {
  description = "ARN of the frontend target group"
  value       = aws_lb_target_group.frontend.arn
}

output "monitoring_target_group_arn" {
  description = "ARN of the monitoring (Grafana) target group"
  value       = aws_lb_target_group.monitoring.arn
}

output "jaeger_target_group_arn" {
  description = "ARN of the Jaeger target group"
  value       = aws_lb_target_group.jaeger.arn
}

output "alertmanager_target_group_arn" {
  description = "ARN of the AlertManager target group"
  value       = aws_lb_target_group.alertmanager.arn
}

output "loki_target_group_arn" {
  description = "ARN of the Loki target group"
  value       = aws_lb_target_group.loki.arn
}

output "prometheus_target_group_arn" {
  description = "ARN of the Prometheus target group"
  value       = aws_lb_target_group.prometheus.arn
}

output "http_listener_arn" {
  description = "ARN of the HTTP listener"
  value       = aws_lb_listener.http.arn
}
