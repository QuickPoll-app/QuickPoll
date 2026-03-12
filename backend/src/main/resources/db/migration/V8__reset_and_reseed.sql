-- Drop all tables
DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS poll_options CASCADE;
DROP TABLE IF EXISTS polls CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Recreate tables
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE polls (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    creator_id UUID NOT NULL REFERENCES users(id),
    multi_select BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE poll_options (
    id UUID PRIMARY KEY,
    poll_id UUID NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_text VARCHAR(255) NOT NULL
);

CREATE TABLE votes (
    id UUID PRIMARY KEY,
    poll_id UUID NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_id UUID NOT NULL REFERENCES poll_options(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_vote UNIQUE (poll_id, option_id, user_id)
);

-- Seed users
INSERT INTO users (id, email, password, full_name, role, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin@amalitech.com', '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS', 'Admin User', 'ADMIN', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001', 'user@amalitech.com', '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS', 'Regular User', 'USER', NOW(), NOW());

-- Final safety check to override any accidental test data
UPDATE users SET full_name = 'Admin User', role = 'ADMIN' WHERE email = 'admin@amalitech.com';

-- Seed polls
INSERT INTO polls (id, title, description, status, creator_id, multi_select, created_at, updated_at, expires_at) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Best Programming Language', 'Vote for your favorite', 'ACTIVE', '550e8400-e29b-41d4-a716-446655440000', true, NOW(), NOW(), NOW() + INTERVAL '30 days'),
('660e8400-e29b-41d4-a716-446655440002', 'Preferred Work Model', 'What arrangement do you prefer?', 'ACTIVE', '550e8400-e29b-41d4-a716-446655440000', false, NOW(), NOW(), NOW() + INTERVAL '14 days'),
('660e8400-e29b-41d4-a716-446655440003', 'Favorite Frontend Framework', 'Which do you use most?', 'ACTIVE', '550e8400-e29b-41d4-a716-446655440001', false, NOW(), NOW(), NOW() + INTERVAL '7 days');

-- Seed poll options
INSERT INTO poll_options (id, poll_id, option_text) VALUES
('770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', 'Java'),
('770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440001', 'Python'),
('770e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440001', 'JavaScript'),
('770e8400-e29b-41d4-a716-446655440004', '660e8400-e29b-41d4-a716-446655440001', 'TypeScript'),
('770e8400-e29b-41d4-a716-446655440005', '660e8400-e29b-41d4-a716-446655440002', 'Remote'),
('770e8400-e29b-41d4-a716-446655440006', '660e8400-e29b-41d4-a716-446655440002', 'Hybrid'),
('770e8400-e29b-41d4-a716-446655440007', '660e8400-e29b-41d4-a716-446655440002', 'On-site'),
('770e8400-e29b-41d4-a716-446655440008', '660e8400-e29b-41d4-a716-446655440003', 'React'),
('770e8400-e29b-41d4-a716-446655440009', '660e8400-e29b-41d4-a716-446655440003', 'Angular'),
('770e8400-e29b-41d4-a716-446655440010', '660e8400-e29b-41d4-a716-446655440003', 'Vue');

-- Seed votes
INSERT INTO votes (id, poll_id, option_id, user_id, created_at) VALUES
(gen_random_uuid(), '660e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', NOW()),
(gen_random_uuid(), '660e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', NOW());
