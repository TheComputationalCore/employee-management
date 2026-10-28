-- One-time setup codes are stored only as a SHA-256 digest and expire.
ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS setup_token_hash VARCHAR(64),
    ADD COLUMN IF NOT EXISTS setup_token_expires_at TIMESTAMP WITHOUT TIME ZONE;
