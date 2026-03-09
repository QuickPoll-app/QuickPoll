
# Database Module - Variables


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
  description = "VPC ID for the database subnet group"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for RDS placement"
  type        = list(string)
}

variable "rds_security_group_id" {
  description = "Security group ID for RDS access"
  type        = string
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "quickpoll"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "quickpoll"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Allocated storage in GB"
  type        = number
  default     = 20
}

variable "db_max_allocated_storage" {
  description = "Maximum allocated storage for autoscaling in GB"
  type        = number
  default     = 100
}

variable "multi_az" {
  description = "Enable Multi-AZ deployment"
  type        = bool
  default     = false
}

variable "backup_retention_period" {
  description = "Number of days to retain backups"
  type        = number
  default     = 7
}

variable "deletion_protection" {
  description = "Enable deletion protection"
  type        = bool
  default     = false
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
