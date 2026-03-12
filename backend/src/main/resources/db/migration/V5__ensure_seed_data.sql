-- Ensure seed data exists with correct roles
INSERT INTO users (id, email, password, full_name, role, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000',
     'admin@amalitech.com',
     '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS',
     'Admin User', 'ADMIN', NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440001',
     'user@amalitech.com',
     '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS',
     'Regular User', 'USER', NOW(), NOW())
ON CONFLICT (email) DO UPDATE SET
    role = EXCLUDED.role,
    full_name = EXCLUDED.full_name,
    updated_at = NOW();
