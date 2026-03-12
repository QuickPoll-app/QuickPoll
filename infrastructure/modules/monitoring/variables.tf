
variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "private_subnet_ids" {
  type = list(string)
}

variable "service_discovery_namespace_id" {
  type = string
}

variable "monitoring_security_group_id" {
  type = string
}

variable "ecs_cluster_id" {
  description = "The ID or ARN of the ECS cluster"
  type        = string
}

variable "ecs_task_execution_role_arn" {
  type = string
}

variable "monitoring_target_group_arn" {
  type = string
}

variable "jaeger_target_group_arn" {
  type = string
}

variable "alertmanager_target_group_arn" {
  type    = string
  default = ""
}

variable "loki_target_group_arn" {
  type    = string
  default = ""
}

variable "efs_monitoring_id" {
  type = string
}

variable "efs_access_point_grafana_id" {
  type = string
}

variable "efs_access_point_loki_id" {
  type = string
}

variable "efs_access_point_prometheus_id" {
  type = string
}

variable "efs_access_point_alertmanager_id" {
  type = string
}

variable "tags" {
  type    = map(string)
  default = {}
}

variable "grafana_admin_password" {
  type    = string
  default = "admin"
}

variable "slack_webhook_url" {
  description = "Slack webhook URL for Grafana alerts"
  type        = string
  sensitive   = true
}

variable "alb_domain" {
  description = "ALB domain name for Grafana and Jaeger root URLs"
  type        = string
  default     = "localhost"
}
