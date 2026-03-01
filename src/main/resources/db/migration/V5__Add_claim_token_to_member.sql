-- Cria a extensão pgcrypto
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Adiciona o claim token ao membro
ALTER TABLE club_members 
ADD COLUMN claim_token UUID;

-- Gera o claim token para os membros existentes
UPDATE club_members 
SET claim_token = gen_random_uuid() 
WHERE claim_token IS NULL 
  AND user_id IS NULL;

-- Garante que o claim token seja único
ALTER TABLE club_members 
ADD CONSTRAINT unique_claim_token UNIQUE (claim_token);
