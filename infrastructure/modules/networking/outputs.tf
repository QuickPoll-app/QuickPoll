
# Networking Module - Outputs


output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "CIDR block of the VPC"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "IDs of the public subnets"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "IDs of the private subnets"
  value       = aws_subnet.private[*].id
}

output "internet_gateway_id" {
  description = "ID of the Internet Gateway"
  value       = aws_internet_gateway.main.id
}

output "nat_gateway_ids" {
  description = "IDs of the NAT Gateways"
  value       = aws_nat_gateway.main[*].id
}

output "service_discovery_namespace_id" {
  description = "ID of the Cloud Map private DNS namespace"
  value       = aws_service_discovery_private_dns_namespace.main.id
}

output "service_discovery_namespace_name" {
  description = "Name of the Cloud Map private DNS namespace"
  value       = aws_service_discovery_private_dns_namespace.main.name
}
