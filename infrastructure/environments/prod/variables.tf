
# Production Environment - Variables


variable "aws_region" {
  description = "AWS region for production environment"
  type        = string
  default     = "eu-north-1"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "quickpoll-team7"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "prod"
}

# Container Images
variable "backend_image" {
  description = "Backend Docker image URI"
  type        = string
}

variable "frontend_image" {
  description = "Frontend Docker image URI"
  type        = string
}

# Database (secrets via TF_VAR_)
variable "db_password" {
  description = "Database password - passed via TF_VAR_db_password from GitHub Secrets"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret - passed via TF_VAR_jwt_secret from GitHub Secrets"
  type        = string
  sensitive   = true
}

# Sizing (prod runs larger)
variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.small"
}

variable "backend_cpu" {
  description = "Backend CPU units"
  type        = number
  default     = 1024
}

variable "backend_memory" {
  description = "Backend memory in MB"
  type        = number
  default     = 2048
}

variable "frontend_cpu" {
  description = "Frontend CPU units"
  type        = number
  default     = 512
}

variable "frontend_memory" {
  description = "Frontend memory in MB"
  type        = number
  default     = 1024
}

variable "backend_desired_count" {
  description = "Number of backend tasks"
  type        = number
  default     = 2
}

variable "frontend_desired_count" {
  description = "Number of frontend tasks"
  type        = number
  default     = 2
}
