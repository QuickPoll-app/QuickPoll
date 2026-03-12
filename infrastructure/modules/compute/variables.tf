
# Compute Module - Variables
# ECR Repositories


variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "quickpoll"
}

variable "environment" {
  description = "Environment name (staging, prod)"
  type        = string
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
