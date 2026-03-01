-- Create pgcrypto extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Add claim token to member
ALTER TABLE club_members ADD COLUMN claim_token UUID;

INSERT INTO club_members (name, role, club_id) VALUES
    ('Alice', 'PLAYER', 1);

-- Generate claim token for existing members
UPDATE club_members SET claim_token = gen_random_uuid() WHERE claim_token IS NULL;

-- Make sure of unique claim token
ALTER TABLE club_members ADD CONSTRAINT unique_claim_token UNIQUE (claim_token);
