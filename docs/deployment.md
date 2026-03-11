# QuickPoll — Setup & Deployment Guide

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Local Development](#local-development)
- [Infrastructure Overview](#infrastructure-overview)
- [AWS Deployment](#aws-deployment)
  - [Initial Setup](#initial-setup)
  - [Staging Deployment](#staging-deployment)
  - [Production Deployment](#production-deployment)
- [CI/CD Pipelines](#cicd-pipelines)
- [Redis Configuration](#redis-configuration)
- [Environment Variables Reference](#environment-variables-reference)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

---

## Architecture Overview

QuickPoll is a full-stack real-time polling platform:

| Component        | Technology              | Port |
|------------------|-------------------------|------|
| Backend API      | Spring Boot 3.2 / Java 21 | 8081 |
| Frontend         | Angular 19 / Nginx      | 8080 |
| Database         | PostgreSQL 16           | 5432 |
| Cache            | Redis 7                 | 6379 |
| Data Engineering | Python 3.11 ETL         | —    |

**AWS Architecture (per environment):**

```
Internet → ALB (80/443)
             ├── /api/*  → ECS Backend (port 8081, Fargate)
             └── /*      → ECS Frontend (port 8080, Fargate)

ECS Backend → RDS PostgreSQL (private subnets, port 5432)
ECS Backend → ElastiCache Redis (private subnets, port 6379)
```

- **VPC**: 2 AZs, public + private subnets, NAT Gateways
- **ECS Fargate**: Autoscaling on CPU/memory targets
- **RDS**: Multi-AZ (prod), automated backups, CloudWatch alarms
- **ElastiCache Redis**: Single-node (staging `cache.t3.micro`), larger in prod (`cache.t3.small`)
- **ECR**: Separate repos for backend, frontend, data-engineering

---

## Prerequisites

### Tools Required

| Tool        | Version   | Purpose                   |
|-------------|-----------|---------------------------|
| Docker      | 24+       | Containerization          |
| Docker Compose | v2     | Local development         |
| Terraform   | ≥ 1.7.0   | Infrastructure as Code    |
| AWS CLI     | v2        | AWS resource management   |
| Node.js     | 20.x      | Frontend build            |
| Java        | 21 (Temurin) | Backend build          |
| Maven       | 3.9.x     | Backend dependency mgmt   |

### AWS Account Setup

1. **AWS Account** with programmatic access
2. **S3 Bucket** for Terraform state: `quickpoll-team7-tf-state`
3. **DynamoDB Table** for state locking: `quickpoll-team7-tf-lock`
4. **IAM User/Role** with permissions for: ECS, ECR, RDS, ElastiCache, VPC, ALB, S3, DynamoDB, SSM, CloudWatch, SNS, IAM

---

## Local Development

### 1. Clone & Configure

```bash
git clone git@github.com:QuickPoll-app/QuickPoll.git
cd QuickPoll
```

Create a `.env` file in the project root:

```env
POSTGRES_DB=quickpoll
POSTGRES_USER=quickpoll
POSTGRES_PASSWORD=your_local_password
JWT_SECRET=your_local_jwt_secret_at_least_32_chars
```

### 2. Start Services

```bash
# Start app services (backend, frontend, postgres, redis)
docker compose up -d --build

# Start with monitoring stack (Prometheus, Grafana, Alertmanager)
docker compose --profile monitoring up -d --build
```

### 3. Access Points

| Service     | URL                                |
|-------------|------------------------------------|
| Frontend    | http://localhost:8080               |
| Backend API | http://localhost:8081               |
| Swagger UI  | http://localhost:8081/swagger-ui.html |
| Grafana     | http://localhost:3000 (admin/admin) |
| Prometheus  | http://localhost:9090               |

### 4. Default Users

| Email                 | Password    | Role  |
|-----------------------|-------------|-------|
| admin@quickpoll.com   | password123 | Admin |
| user@quickpoll.com    | password123 | User  |

### 5. Common Commands

```bash
docker compose logs -f backend         # Follow backend logs
docker compose logs -f frontend        # Follow frontend logs
docker compose ps                      # Show service status
docker compose down -v                 # Stop and remove volumes
docker compose restart backend         # Restart backend only
```

---

## Infrastructure Overview

### Terraform Module Structure

```
infrastructure/
├── environments/
│   ├── staging/          # Staging env config
│   │   ├── main.tf       # Module composition
│   │   ├── variables.tf  # Env-specific variables
│   │   ├── outputs.tf    # Env outputs
│   │   └── terraform.tfvars.example
│   └── prod/             # Production env config
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       └── terraform.tfvars.example
└── modules/
    ├── networking/       # VPC, subnets, NAT, IGW
    ├── security/         # Security groups (ALB, ECS, RDS, Redis), IAM roles
    ├── database/         # RDS PostgreSQL, backup alarms, SNS events
    ├── redis/            # ElastiCache Redis cluster
    ├── loadbalancer/     # ALB, target groups, listener rules
    ├── compute/          # ECR repositories
    ├── ecs/              # ECS cluster, task definitions, services, autoscaling
    └── storage/          # S3 buckets
```

### Security Groups

| Security Group | Inbound From      | Port(s)    |
|----------------|-------------------|------------|
| ALB SG         | 0.0.0.0/0         | 80, 443    |
| ECS Tasks SG   | ALB SG            | 8080, 8081 |
| RDS SG         | ECS Tasks SG      | 5432       |
| Redis SG       | ECS Tasks SG      | 6379       |

### Redis ↔ ECS Flow

Redis is fully internal — no GitHub Secrets needed:

```
Terraform creates ElastiCache → outputs redis_host & redis_port
    ↓
Passed as module outputs to ECS module via environments/main.tf
    ↓
ECS task definition injects SPRING_DATA_REDIS_HOST & SPRING_DATA_REDIS_PORT
    ↓
Spring Boot auto-configures Redis connection from env vars
```

---

## AWS Deployment

### Initial Setup

#### 1. Create Terraform State Backend

```bash
# Create S3 bucket for state
aws s3api create-bucket \
  --bucket quickpoll-team7-tf-state \
  --region eu-north-1 \
  --create-bucket-configuration LocationConstraint=eu-north-1

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket quickpoll-team7-tf-state \
  --versioning-configuration Status=Enabled

# Create DynamoDB lock table
aws dynamodb create-table \
  --table-name quickpoll-team7-tf-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region eu-north-1
```

#### 2. Configure GitHub Secrets & Variables

**Repository Secrets** (Settings → Secrets and variables → Actions):

| Secret Name              | Description                        |
|--------------------------|------------------------------------|
| `AWS_ACCESS_KEY_ID`      | IAM access key                     |
| `AWS_SECRET_ACCESS_KEY`  | IAM secret key                     |
| `TF_VAR_DB_PASSWORD`     | RDS master password                |
| `TF_VAR_JWT_SECRET`      | JWT signing secret (≥32 chars)     |
| `CI_DB_PASSWORD`         | CI test database password          |
| `CI_JWT_SECRET`          | CI test JWT secret                 |
| `SLACK_DEPLOYMENTS_WEBHOOK` | Slack webhook for notifications |

**Repository Variables**:

| Variable Name  | Value                |
|----------------|----------------------|
| `PROJECT_NAME` | `quickpoll-team7`    |
| `AWS_REGION`   | `eu-north-1`         |
| `CI_DB_NAME`   | `quickpoll_test`     |
| `CI_DB_USER`   | `quickpoll_test`     |

#### 3. Configure GitHub Environments

Create two GitHub Environments: **staging** and **prod**.

- **staging**: Auto-deploys on merge to `develop`
- **prod**: Requires manual approval, deploys on merge to `main`

### Staging Deployment

#### Manual (first time / debugging)

```bash
cd infrastructure/environments/staging

# Initialize with S3 backend
terraform init \
  -backend-config="bucket=quickpoll-team7-tf-state" \
  -backend-config="key=staging/terraform.tfstate" \
  -backend-config="region=eu-north-1" \
  -backend-config="dynamodb_table=quickpoll-team7-tf-lock"

# Plan
export TF_VAR_db_password="your_staging_db_password"
export TF_VAR_jwt_secret="your_staging_jwt_secret"
export TF_VAR_backend_image="442426888142.dkr.ecr.eu-north-1.amazonaws.com/quickpoll-team7-staging-backend:latest"
export TF_VAR_frontend_image="442426888142.dkr.ecr.eu-north-1.amazonaws.com/quickpoll-team7-staging-frontend:latest"

terraform plan -out=tfplan

# Apply
terraform apply tfplan
```

#### Automated (CI/CD)

Push to `develop` branch → `deploy-staging.yml` runs automatically:
1. Builds & pushes Docker images to ECR
2. Runs `terraform apply` with GitHub Secrets
3. Updates ECS services with new images

### Production Deployment

#### Automated (CI/CD)

Merge PR to `main` branch → `deploy-prod.yml` runs with manual approval:
1. Same flow as staging but with prod-sized resources
2. Multi-AZ RDS, higher ECS autoscaling limits
3. Larger Redis node (`cache.t3.small`)

---

## CI/CD Pipelines

| Workflow                | Trigger                          | Purpose                                    |
|-------------------------|----------------------------------|--------------------------------------------|
| `ci.yml`                | Push to develop/feature/hotfix, PR to main/develop | Lint, test, build, docker build, Terraform validate |
| `deploy-staging.yml`    | Merge to `develop`               | Build images → push to ECR → terraform apply staging |
| `deploy-prod.yml`       | Merge to `main`                  | Build images → push to ECR → terraform apply prod    |
| `deploy-hotfix.yml`     | Merge hotfix/* to `main`         | Fast-track prod deployment                 |
| `terraform-pipeline.yml`| Manual / infrastructure changes  | Terraform plan/apply with approval         |

### CI Checks (required to pass)

- `backend-test` — Maven verify with PostgreSQL service container
- `frontend-build` — npm lint + production build
- `terraform-validate` — `terraform fmt -check` + `terraform validate` (staging & prod)
- `docker-build` — Build all 3 Docker images (no push)

---

## Redis Configuration

### How It Works

The backend Spring Boot application uses Redis for **caching** (`spring.cache.type=redis`). Redis connection details are injected as environment variables:

| Env Variable              | Source                              | Spring Property             |
|---------------------------|-------------------------------------|-----------------------------|
| `SPRING_DATA_REDIS_HOST`  | Terraform → ElastiCache endpoint    | `spring.data.redis.host`    |
| `SPRING_DATA_REDIS_PORT`  | Terraform → ElastiCache port (6379) | `spring.data.redis.port`    |

### Local Development

In `docker-compose.yml`, a local Redis container runs alongside the app:
- Host: `redis` (Docker network DNS)
- Port: `6379`

### AWS Environments

ElastiCache Redis is provisioned per environment:

| Environment | Node Type        | Snapshots       | Maintenance Window    |
|-------------|------------------|-----------------|-----------------------|
| Staging     | `cache.t3.micro` | 1-day retention | Sun 05:00–06:00 UTC   |
| Production  | `cache.t3.small` | 7-day retention | Sun 05:00–06:00 UTC   |

No secrets needed — the Redis endpoint is auto-generated by Terraform and passed between modules:

```hcl
# In environments/staging/main.tf (or prod)
module "ecs" {
  ...
  redis_host = module.redis.redis_host   # Auto from ElastiCache
  redis_port = module.redis.redis_port   # Auto from ElastiCache
}
```

---

## Environment Variables Reference

### Backend (ECS Task Definition)

| Variable                      | Source    | Description                    |
|-------------------------------|----------|--------------------------------|
| `SPRING_DATASOURCE_URL`       | Terraform | JDBC URL to RDS               |
| `SPRING_DATASOURCE_USERNAME`  | Terraform | DB username                   |
| `SPRING_DATASOURCE_PASSWORD`  | SSM      | DB password (SecureString)     |
| `JWT_SECRET`                  | SSM      | JWT signing key (SecureString) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Terraform | Hibernate DDL mode (update) |
| `SPRING_PROFILES_ACTIVE`      | Terraform | Active Spring profile         |
| `SERVER_PORT`                 | Terraform | Server port (8081)            |
| `SPRING_DATA_REDIS_HOST`      | Terraform | Redis endpoint from ElastiCache |
| `SPRING_DATA_REDIS_PORT`      | Terraform | Redis port (6379)             |

### Local Development (.env)

| Variable           | Description              |
|--------------------|--------------------------|
| `POSTGRES_DB`      | PostgreSQL database name |
| `POSTGRES_USER`    | PostgreSQL username      |
| `POSTGRES_PASSWORD`| PostgreSQL password      |
| `JWT_SECRET`       | JWT signing key          |

---

## Monitoring

Start with monitoring profile:

```bash
docker compose --profile monitoring up -d
```

| Service      | URL                    | Credentials    |
|--------------|------------------------|----------------|
| Grafana      | http://localhost:3000   | admin / admin  |
| Prometheus   | http://localhost:9090   | —              |
| Alertmanager | http://localhost:9093   | —              |

### Key Metrics

- **Backend**: Spring Boot actuator at `/actuator/prometheus`
- **RDS** (AWS): CloudWatch alarms for CPU > 80%, free storage < 5GB, connection count
- **ECS**: Autoscaling on CPU and memory utilization targets

---

## Troubleshooting

### Common Issues

**ECS tasks failing to start**
```bash
# Check task logs
aws ecs describe-tasks \
  --cluster quickpoll-team7-staging \
  --tasks <task-id> \
  --query 'tasks[0].stoppedReason'

# Check CloudWatch logs
aws logs tail /ecs/quickpoll-team7-staging-backend --follow
```

**RDS connection refused**
- Verify ECS tasks SG has egress to RDS SG on port 5432
- Check SSM parameter `/<project>/<env>/db-password` matches RDS password
- Run: `aws ssm get-parameter --name "/quickpoll-team7/staging/db-password" --with-decryption`

**Redis connection refused**
- Verify Redis SG allows inbound from ECS tasks SG on port 6379
- Check ElastiCache cluster status: `aws elasticache describe-cache-clusters`
- Verify ECS task definition has `SPRING_DATA_REDIS_HOST` env var set

**Terraform state lock**
```bash
terraform force-unlock <LOCK_ID>
```

**Terraform fmt check failing in CI**
```bash
# Auto-format all files
terraform fmt -recursive infrastructure/
git add -A && git commit -m "style(infra): terraform fmt" && git push
```

**Docker build cache issues**
```bash
docker compose build --no-cache
```

### Useful AWS CLI Commands

```bash
# List ECS services
aws ecs list-services --cluster quickpoll-team7-staging

# Force new deployment (pull latest image)
aws ecs update-service \
  --cluster quickpoll-team7-staging \
  --service quickpoll-team7-staging-backend \
  --force-new-deployment

# Check ElastiCache Redis endpoint
aws elasticache describe-cache-clusters \
  --cache-cluster-id quickpoll-team7-staging-redis \
  --show-cache-node-info \
  --query 'CacheClusters[0].CacheNodes[0].Endpoint'

# Check RDS status
aws rds describe-db-instances \
  --db-instance-identifier quickpoll-team7-staging-db \
  --query 'DBInstances[0].DBInstanceStatus'
```

---

## Git Branching Strategy

| Branch          | Purpose                  | Deploys To |
|-----------------|--------------------------|------------|
| `main`          | Production-ready code    | Production |
| `develop`       | Integration branch       | Staging    |
| `feature/*`     | New features             | CI only    |
| `hotfix/*`      | Urgent production fixes  | Production |

See [docs/git-branching-strategy.md](docs/git-branching-strategy.md) for full details.
