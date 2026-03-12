
# Storage Module - Variables

variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
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

variable "private_subnet_ids" {
  description = "IDs of private subnets for EFS mount targets"
  type        = list(string)
}

variable "efs_security_group_id" {
  description = "ID of security group for EFS"
  type        = string
}
