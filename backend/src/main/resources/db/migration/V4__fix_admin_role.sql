UPDATE users
SET role = 'ADMIN', updated_at = NOW()
WHERE email = 'admin@amalitech.com';
