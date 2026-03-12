# QuickPoll Git Branching Strategy

## Branch Structure Overview

```
main
    Production only. Protected.
    Nobody pushes here directly.
    Only merges from develop (via PR + Lennox approval)
    or from hotfix/* (emergency only)

develop
    Integration branch. Protected.
    Auto-deploys to staging on every merge.
    Lennox tests here before anything reaches prod.
    Only merges from feature/* branches via PR

feature/ROLE-short-description
    Named with role prefix so everyone knows who owns it.
    Examples:
      feature/be-jwt-auth          (Mahoro)
      feature/be-vote-api          (Emmanuel)
      feature/fe-poll-chart        (Broderick)
      feature/qa-vote-edge-cases   (Lennox)
      feature/de-engagement-metrics (Damas)
      feature/devops-ecs-module    (Viateur)
    Merges into: develop
    Deleted after merge

hotfix/short-description
    For production emergencies only.
    Branches off: main (not develop)
    Merges into: main AND develop (to keep them in sync)
    Examples:
      hotfix/fix-expired-token
      hotfix/fix-duplicate-vote
    Deleted after merge
```

## Branch Details

### main Branch

- **Purpose**: Production-ready code only
- **Protection**: Fully protected, requires PR + approval
- **Deployment**: Auto-deploys to production environment
- **Sources**: Only accepts merges from `develop` or `hotfix/*`
- **Direct pushes**: Forbidden for everyone

### develop Branch

- **Purpose**: Integration and staging environment
- **Protection**: Protected, requires PR + code owner review
- **Deployment**: Auto-deploys to staging on every merge
- **Sources**: Only accepts merges from `feature/*` branches
- **Testing**: Lennox validates all changes here before production

### feature/* Branches

- **Purpose**: Individual developer work on specific features/tasks
- **Naming**: `feature/ROLE-short-description`
- **Protection**: Minimal (allows force push, allows deletion)
- **Source**: Created from `develop`
- **Target**: Merges back to `develop`
- **Lifecycle**: Deleted after successful merge

### hotfix/* Branches

- **Purpose**: Emergency production fixes only
- **Naming**: `hotfix/short-description`
- **Protection**: Requires PR + review (expedited process)
- **Source**: Created from `main`
- **Target**: Merges to both `main` AND `develop`
- **Lifecycle**: Deleted after successful merge

## Role Prefixes

| Role | Prefix | Team Member | Example |
|---|---|---|---|
| Backend | `be1-` | Mahoro | `feature/be1-jwt-auth` |
| Backend | `be2-` | Emmanuel | `feature/be2-vote-api` |
| Frontend | `fe-` | Broderick | `feature/fe-poll-chart` |
| QA | `qa-` | Lennox | `feature/qa-vote-edge-cases` |
| Data Engineering | `de-` | Damas | `feature/de-engagement-metrics` |
| DevOps | `devops-` | Viateur | `feature/devops-ecs-module` |

## Workflow Examples

### Feature Development Flow

1. Developer creates feature branch from `develop`
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/be1-user-authentication
   ```

2. Developer works on feature, commits changes
   ```bash
   git add .
   git commit -m "JWT authentication service"
   git push origin feature/be1-user-authentication
   ```

3. Developer opens PR to `develop`, tags assigned reviewer
4. Reviewer approves, PR gets merged to `develop`
5. Staging deployment happens automatically

6. Feature branch gets deleted

### Hotfix Flow

1. Create hotfix from `main`
   ```bash
   git checkout main
   git pull origin main
   git checkout -b hotfix/fix-vote-duplication
   ```

2. Fix the issue, test thoroughly
   ```bash
   git add .
   git commit -m "Fix duplicate vote bug in poll service"
   git push origin hotfix/fix-vote-duplication
   ```

3. Open PR to `main` (requires Lennox approval)
4. After merge to `main`, also merge to `develop`
5. Both production and staging get updated

6. Hotfix branch gets deleted

### Release Flow

1. When `develop` is stable and ready for production
2. Lennox opens PR from `develop` to `main`
3. Lennox approves and merges (he's the gatekeeper)
4. Production deployment happens automatically
5. `main` and `develop` are in sync

## Branch Protection Rules

### main Branch Protection

- Yes: Require PR before merging
- Yes: Require 1 approval
- Yes: Dismiss stale reviews
- Yes: Require code owner reviews
- Yes: Require status checks (all CI jobs)
- Yes: Require conversation resolution
- Yes: Enforce for administrators
- No: Allow force pushes
- No: Allow deletions

### develop Branch Protection

- Yes: Require PR before merging
- Yes: Require 1 approval  
- Yes: Dismiss stale reviews
- Yes: Require code owner reviews
- Yes: Require status checks (all CI jobs)
- Yes: Require conversation resolution
- No: Allow force pushes
- No: Allow deletions

### feature/* Branch Protection

- No: Require PR before merging (developers can push freely)
- No: Require status checks
- Yes: Allow force pushes (for rebasing)
- Yes: Allow deletions (cleanup after merge)

### hotfix/* Branch Protection

- Yes: Require PR before merging
- Yes: Require 1 approval
- Yes: Require code owner reviews (Lennox)
- Yes: Require status checks (Backend — Test minimum)
- Yes: Require conversation resolution
- No: Allow force pushes
- Yes: Allow deletions (cleanup after merge)

## Team Guidelines

### For All Developers

- Always create feature branches from `develop`
- Use your assigned role prefix in branch names
- Keep PRs small (under 200 lines)
- Tag your assigned reviewer when opening PRs
- Don't merge your own PRs
- Delete feature branches after merge

### For Lennox (QA Lead)

- You are the gatekeeper for production
- All production releases require your approval
- Test thoroughly in staging before approving `develop` → `main` merges
- You can approve hotfixes for emergency situations

## Emergency Procedures

### Production is Broken

1. Create hotfix branch from `main`
2. Fix the issue with minimal changes
3. Open PR to `main`, tag Lennox immediately
4. Deploy to staging first if time permits
5. After Lennox approval, merge and deploy
6. Backport fix to `develop` to keep branches in sync