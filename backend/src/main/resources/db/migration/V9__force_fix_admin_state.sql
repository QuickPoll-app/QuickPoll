-- Force fix the admin user state in case V8 was already applied or overridden
UPDATE users 
SET full_name = 'Admin User', 
    role = 'ADMIN', 
    updated_at = NOW() 
WHERE email = 'admin@amalitech.com';

-- Ensure the user exists if for some reason it doesn't
INSERT INTO users (id, email, password, full_name, role, created_at, updated_at)
SELECT '550e8400-e29b-41d4-a716-446655440000', 'admin@amalitech.com', '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS', 'Admin User', 'ADMIN', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@amalitech.com');
