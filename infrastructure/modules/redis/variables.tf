
# Redis Module - Variables


variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "quickpoll"
}

variable "environment" {
  description = "Environment name (staging, prod)"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for ElastiCache placement"
  type        = list(string)
}

variable "redis_security_group_id" {
  description = "Security group ID for Redis access"
  type        = string
}

variable "redis_node_type" {
  description = "ElastiCache node type"
  type        = string
  default     = "cache.t3.micro"
}

variable "redis_engine_version" {
  description = "Redis engine version"
  type        = string
  default     = "7.1"
}

variable "redis_parameter_group" {
  description = "ElastiCache parameter group name"
  type        = string
  default     = "default.redis7"
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
