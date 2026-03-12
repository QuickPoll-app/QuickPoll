-- V11: TOTAL DATABASE RESET
-- This migration drops everything and recreates a clean schema to fix the corruption once and for all.

-- 1. Drop existing tables
DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS poll_options CASCADE;
DROP TABLE IF EXISTS polls CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 2. Recreate Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 3. Recreate Polls Table
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

-- 4. Recreate Poll Options Table
CREATE TABLE poll_options (
    id UUID PRIMARY KEY,
    poll_id UUID NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_text VARCHAR(255) NOT NULL
);

-- 5. Recreate Votes Table
CREATE TABLE votes (
    id UUID PRIMARY KEY,
    poll_id UUID NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_id UUID NOT NULL REFERENCES poll_options(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_vote UNIQUE (poll_id, option_id, user_id)
);

-- 6. SEED DEFINITIVE ADMIN DATA
INSERT INTO users (id, email, password, full_name, role, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'admin@amalitech.com', '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS', 'Admin User', 'ADMIN', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001', 'user@amalitech.com', '$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS', 'Regular User', 'USER', NOW(), NOW());
