# QuickPoll - Autoscaling & Backup Strategy

## ECS Autoscaling

### Backend Service
- **Min Tasks**: 1 (staging) / 2 (prod)
- **Max Tasks**: 2 (staging) / 4 (prod)
- **CPU Target**: 70% utilization
- **Memory Target**: 80% utilization
- **Scale Out**: 60s cooldown
- **Scale In**: 300s cooldown

### Frontend Service
- **Min Tasks**: 1 (staging) / 2 (prod)
- **Max Tasks**: 2 (staging) / 4 (prod)
- **CPU Target**: 70% utilization
- **Memory Target**: 80% utilization
- **Scale Out**: 60s cooldown
- **Scale In**: 300s cooldown

## Database Backup Strategy

### Automated Backups
- **Retention**: 7 days (configurable via `backup_retention_period`)
- **Backup Window**: 03:00-04:00 UTC
- **Maintenance Window**: Sunday 04:00-05:00 UTC
- **Point-in-Time Recovery**: Enabled (via backup retention > 0)
- **Copy Tags to Snapshots**: Enabled

### Storage Autoscaling
- **Initial Storage**: 20 GB
- **Max Storage**: 100 GB (auto-scales when needed)
- **Storage Type**: gp3 (encrypted)

### Monitoring & Alerts
- **CPU Alarm**: > 80% for 15 minutes
- **Storage Alarm**: < 5 GB free space
- **Connections Alarm**: > 80 connections for 10 minutes
- **SNS Topic**: RDS events (backup, recovery, failure, maintenance)

### Final Snapshot
- **Staging**: Skipped on deletion
- **Production**: Created on deletion

### Performance Insights
- **Staging**: 7 days retention
- **Production**: 31 days retention

### CloudWatch Logs
- PostgreSQL logs exported
- Upgrade logs exported
