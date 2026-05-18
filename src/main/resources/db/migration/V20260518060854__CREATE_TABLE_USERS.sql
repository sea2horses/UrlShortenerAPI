CREATE TABLE IF NOT EXISTS users (id uuid PRIMARY KEY, first_name VARCHAR(20) NOT NULL, last_name VARCHAR(50) NOT NULL, email VARCHAR(50) NOT NULL, password_hash bytea NOT NULL);
ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);
CREATE TABLE IF NOT EXISTS refresh_tokens (id SERIAL PRIMARY KEY, token VARCHAR(1000) NOT NULL, user_id uuid NOT NULL, CONSTRAINT fk_refresh_tokens_user_id__id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT);
ALTER TABLE refresh_tokens ADD CONSTRAINT refresh_tokens_token_unique UNIQUE (token);
CREATE SEQUENCE IF NOT EXISTS refresh_tokens_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;