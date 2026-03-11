# =============================================================
# S3 for ALB Access Logs
# =============================================================

resource "aws_s3_bucket" "alb_logs" {
  bucket = trimsuffix(substr("${var.project_name}-${var.environment}-alb-logs", 0, 63), "-")

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-alb-logs"
  })
}

resource "aws_s3_bucket_versioning" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_lifecycle_configuration" "alb_logs" {
  bucket = aws_s3_bucket.alb_logs.id

  rule {
    id     = "expire-old-logs"
    status = "Enabled"

    filter {}

    expiration {
      days = 90
    }

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
}

# =============================================================
# EFS for Persistent Monitoring Data
# =============================================================

resource "aws_efs_file_system" "monitoring" {
  creation_token = "${var.project_name}-${var.environment}-monitoring-efs"
  encrypted      = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-monitoring-efs"
  })
}

resource "aws_efs_mount_target" "monitoring" {
  count           = length(var.private_subnet_ids)
  file_system_id  = aws_efs_file_system.monitoring.id
  subnet_id       = var.private_subnet_ids[count.index]
  security_groups = [var.efs_security_group_id]
}

resource "aws_efs_access_point" "grafana" {
  file_system_id = aws_efs_file_system.monitoring.id

  posix_user {
    gid = 472
    uid = 472
  }

  root_directory {
    path = "/grafana"
    creation_info {
      owner_gid   = 472
      owner_uid   = 472
      permissions = "755"
    }
  }
}

resource "aws_efs_access_point" "loki" {
  file_system_id = aws_efs_file_system.monitoring.id

  posix_user {
    gid = 10001
    uid = 10001
  }

  root_directory {
    path = "/loki"
    creation_info {
      owner_gid   = 10001
      owner_uid   = 10001
      permissions = "755"
    }
  }
}
