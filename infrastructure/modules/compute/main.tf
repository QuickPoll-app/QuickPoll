

# Compute Module - Main Configuration
# ECR Repositories for container images
# Repos are created once (manually or via CLI) and looked up here


# Backend ECR Repository
data "aws_ecr_repository" "backend" {
  name = "${var.project_name}-backend"
}

# Frontend ECR Repository
data "aws_ecr_repository" "frontend" {
  name = "${var.project_name}-frontend"
}

