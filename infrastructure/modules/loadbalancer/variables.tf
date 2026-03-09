
# Load Balancer Module - Variables


variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "quickpoll"
}

variable "environment" {
  description = "Environment name (staging, prod)"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID for target groups"
  type        = string
}

variable "public_subnet_ids" {
  description = "Public subnet IDs for ALB placement"
  type        = list(string)
}

variable "alb_security_group_id" {
  description = "Security group ID for ALB"
  type        = string
}

variable "health_check_path_backend" {
  description = "Health check path for backend target group"
  type        = string
  default     = "/actuator/health"
}

variable "health_check_path_frontend" {
  description = "Health check path for frontend target group"
  type        = string
  default     = "/health"
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
