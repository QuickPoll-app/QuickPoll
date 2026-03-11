
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

variable "monitoring_security_group_id" {
  type = string
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

variable "efs_monitoring_id" {
  type = string
}

variable "efs_access_point_grafana_id" {
  type = string
}

variable "efs_access_point_loki_id" {
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
