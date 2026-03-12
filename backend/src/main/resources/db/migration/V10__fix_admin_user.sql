-- V10: Definitive admin user fix
-- Previous migrations (V4-V9) attempted to fix this but Flyway state or app seeds may have re-corrupted the data.
-- This migration is idempotent and corrects ANY user at admin@amalitech.com to have role=ADMIN.

-- Step 1: Delete any corrupted/duplicate admin record seeded with wrong role or name
DELETE FROM votes   WHERE user_id IN (SELECT id FROM users WHERE email = 'admin@amalitech.com' AND role != 'ADMIN');
DELETE FROM polls   WHERE creator_id IN (SELECT id FROM users WHERE email = 'admin@amalitech.com' AND role != 'ADMIN');
DELETE FROM users   WHERE email = 'admin@amalitech.com' AND role != 'ADMIN';

-- Step 2: Insert correct admin if missing
INSERT INTO users (id, email, password, full_name, role, created_at, updated_at)
SELECT
  '550e8400-e29b-41d4-a716-446655440000',
  'admin@amalitech.com',
  '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS',
  'Admin User',
  'ADMIN',
  NOW(),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'admin@amalitech.com'
);

-- Step 3: Safety net - force correct values even if insert was skipped
UPDATE users
SET full_name  = 'Admin User',
    role       = 'ADMIN',
    updated_at = NOW()
WHERE email = 'admin@amalitech.com';
