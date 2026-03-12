-- Force update admin role
UPDATE users SET role = 'ADMIN', updated_at = NOW() WHERE email = 'admin@amalitech.com';

-- Verify the update
SELECT email, role FROM users WHERE email = 'admin@amalitech.com';
