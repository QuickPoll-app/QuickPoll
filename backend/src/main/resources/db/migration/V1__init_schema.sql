CREATE TABLE IF NOT EXISTS users (
                                     id         UUID         PRIMARY KEY,
                                     email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS polls (
                                     id           UUID         PRIMARY KEY,
                                     title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    creator_id   UUID         NOT NULL REFERENCES users(id),
    multi_select BOOLEAN      NOT NULL DEFAULT false,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    expires_at   TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS poll_options (
                                            id          UUID         PRIMARY KEY,
                                            poll_id     UUID         NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_text VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS votes (
                                     id         UUID      PRIMARY KEY,
                                     poll_id    UUID      NOT NULL REFERENCES polls(id)         ON DELETE CASCADE,
    option_id  UUID      NOT NULL REFERENCES poll_options(id)  ON DELETE CASCADE,
    user_id    UUID      NOT NULL REFERENCES users(id)         ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_vote UNIQUE (poll_id, option_id, user_id)
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_email          ON users(email);
CREATE INDEX IF NOT EXISTS idx_role           ON users(role);
CREATE INDEX IF NOT EXISTS idx_poll_creator   ON polls(creator_id);
CREATE INDEX IF NOT EXISTS idx_poll_status    ON polls(status);
CREATE INDEX IF NOT EXISTS idx_option_poll_id ON poll_options(poll_id);
CREATE INDEX IF NOT EXISTS idx_vote_poll_id   ON votes(poll_id);
CREATE INDEX IF NOT EXISTS idx_vote_option_id ON votes(option_id);
CREATE INDEX IF NOT EXISTS idx_vote_user_id   ON votes(user_id);