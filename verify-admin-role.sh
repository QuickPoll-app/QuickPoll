#!/bin/bash
# Verify admin role after deployment

set -e

# Configuration
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-quickpoll}"
DB_USER="${DB_USER:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-}"
ADMIN_EMAIL="admin@amalitech.com"
MAX_RETRIES=30
RETRY_INTERVAL=10

echo "🔍 Verifying admin role after deployment..."
echo "Database: $DB_HOST:$DB_PORT/$DB_NAME"
echo ""

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
for i in $(seq 1 $MAX_RETRIES); do
  if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1" > /dev/null 2>&1; then
    echo "✅ Database is ready"
    break
  fi
  if [ $i -eq $MAX_RETRIES ]; then
    echo "❌ Database failed to become ready after $((MAX_RETRIES * RETRY_INTERVAL)) seconds"
    exit 1
  fi
  echo "   Attempt $i/$MAX_RETRIES... retrying in ${RETRY_INTERVAL}s"
  sleep $RETRY_INTERVAL
done

echo ""
echo "🔎 Checking admin user role..."

# Query the admin user role
ADMIN_ROLE=$(PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -c "SELECT role FROM users WHERE email = '$ADMIN_EMAIL';" | xargs)

if [ -z "$ADMIN_ROLE" ]; then
  echo "❌ Admin user ($ADMIN_EMAIL) not found in database"
  echo ""
  echo "📋 All users currently in the database:"
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT id, email, full_name, role FROM users ORDER BY created_at;"
  echo ""
  echo "📋 Flyway migration history:"
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT version, description, type, success, installed_on FROM flyway_schema_history ORDER BY installed_rank;" 2>/dev/null || echo "(flyway_schema_history not accessible)"
  exit 1
fi

echo "   Email: $ADMIN_EMAIL"
echo "   Role: $ADMIN_ROLE"
echo ""

if [ "$ADMIN_ROLE" = "ADMIN" ]; then
  echo "✅ Admin role is CORRECT"
  exit 0
else
  echo "❌ Admin role is INCORRECT (expected: ADMIN, got: [$ADMIN_ROLE])"
  echo ""
  echo "📋 Current admin user state:"
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT id, email, full_name, role, created_at, updated_at FROM users WHERE email = '$ADMIN_EMAIL';"
  echo ""
  echo "📋 Flyway migration history:"
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT version, description, type, success, installed_on FROM flyway_schema_history ORDER BY installed_rank;" 2>/dev/null || echo "(flyway_schema_history not accessible)"
  echo ""
  # Fail hard — do NOT silently fix. V10 migration should have already corrected this.
  # If we reach here, there is a deeper issue that needs investigation.
  echo "⛔ Refusing to auto-fix: V10 migration should have corrected this. Check Flyway migration logs."
  exit 1
fi
