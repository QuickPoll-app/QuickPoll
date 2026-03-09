
# Networking Module - Variables


variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "quickpoll"
}

variable "environment" {
  description = "Environment name (staging, prod)"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.20.0/24"]
}

variable "availability_zones" {
  description = "Availability zones for subnets"
  type        = list(string)
  default     = ["eu-north-1a", "eu-north-1b"]
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}
