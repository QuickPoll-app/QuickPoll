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
ADMIN_ROLE=$(PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -c "SELECT role FROM users WHERE email = '$ADMIN_EMAIL';")

if [ -z "$ADMIN_ROLE" ]; then
  echo "❌ Admin user not found in database"
  echo ""
  echo "📋 Available users:"
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "SELECT email, role FROM users;"
  exit 1
fi

echo "   Email: $ADMIN_EMAIL"
echo "   Role: $ADMIN_ROLE"
echo ""

if [ "$ADMIN_ROLE" = "ADMIN" ]; then
  echo "✅ Admin role is CORRECT"
  exit 0
else
  echo "❌ Admin role is INCORRECT (expected: ADMIN, got: $ADMIN_ROLE)"
  echo ""
  echo "🔧 Fixing admin role..."
  PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "UPDATE users SET role = 'ADMIN' WHERE email = '$ADMIN_EMAIL';"
  echo "✅ Admin role has been corrected"
  exit 0
fi
