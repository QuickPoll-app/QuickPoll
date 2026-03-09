
# ECS Module - Variables


variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "quickpoll"
}

variable "environment" {
  description = "Environment name (staging, prod)"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-north-1"
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for ECS tasks"
  type        = list(string)
}

variable "ecs_tasks_security_group_id" {
  description = "Security group ID for ECS tasks"
  type        = string
}

variable "ecs_task_execution_role_arn" {
  description = "ARN of the ECS task execution role"
  type        = string
}

variable "ecs_task_role_arn" {
  description = "ARN of the ECS task role"
  type        = string
}

variable "backend_target_group_arn" {
  description = "ARN of the backend ALB target group"
  type        = string
}

variable "frontend_target_group_arn" {
  description = "ARN of the frontend ALB target group"
  type        = string
}

# Container Images
variable "backend_image" {
  description = "Docker image for the backend service"
  type        = string
}

variable "frontend_image" {
  description = "Docker image for the frontend service"
  type        = string
}

# Task Sizing
variable "backend_cpu" {
  description = "CPU units for backend task (1024 = 1 vCPU)"
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "Memory in MB for backend task"
  type        = number
  default     = 1024
}

variable "frontend_cpu" {
  description = "CPU units for frontend task"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memory in MB for frontend task"
  type        = number
  default     = 512
}

variable "backend_desired_count" {
  description = "Desired number of backend tasks"
  type        = number
  default     = 2
}

variable "frontend_desired_count" {
  description = "Desired number of frontend tasks"
  type        = number
  default     = 2
}

# Application Config
variable "db_endpoint" {
  description = "Database endpoint for backend configuration"
  type        = string
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "quickpoll"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "quickpoll"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret for authentication"
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
