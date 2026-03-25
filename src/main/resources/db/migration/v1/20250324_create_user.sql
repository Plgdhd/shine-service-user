CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id              UUID        PRIMARY KEY,

                       email           VARCHAR(255) NOT NULL UNIQUE,
                       username        VARCHAR(50)  NOT NULL UNIQUE,

                       avatar_url      TEXT,
                       bio             TEXT,

                       role            VARCHAR(20)  NOT NULL DEFAULT 'VIEWER',

                       status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                       CONSTRAINT users_status_check CHECK (status IN ('ACTIVE', 'BANNED', 'SUSPENDED')),
                       CONSTRAINT users_role_check   CHECK (role IN ('VIEWER', 'STREAMER', 'MODERATOR', 'ADMIN')),

                       created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE processed_events (
                                  id        VARCHAR(36)  PRIMARY KEY,
                                  topic           VARCHAR(100) NOT NULL,
                                  processed_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();