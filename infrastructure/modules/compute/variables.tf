
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

variable "image_retention_count" {
  description = "Number of images to retain in ECR"
  type        = number
  default     = 10
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
