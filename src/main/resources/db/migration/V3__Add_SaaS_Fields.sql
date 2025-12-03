CREATE TABLE clubs (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    public_code VARCHAR(10) UNIQUE
);

ALTER TABLE players RENAME TO club_members;
ALTER TABLE player_ranking RENAME COLUMN player_id TO club_member_id;
ALTER TABLE game_players RENAME COLUMN player_id TO club_member_id;

INSERT INTO users (id, name, username, password, email) VALUES
    (1, 'Admin Legado', 'admin_legado', 'senha_nao_usada', 'admin@legado.com');
-- Garante que o próximo utilizador a ser criado terá o ID 2
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));


-- =================================================================
-- PASSO 2: ALTERAR TABELAS EXISTENTES, ADICIONANDO COLUNAS COMO NULLABLE
-- Adicionamos as colunas sem restrições para que as linhas existentes sejam válidas.
-- =================================================================

ALTER TABLE club_members
    ADD COLUMN user_id BIGINT UNIQUE,
    ADD COLUMN club_id BIGINT,
    ADD COLUMN claim_token VARCHAR(255) UNIQUE;

ALTER TABLE games
    ADD COLUMN club_id BIGINT;

ALTER TABLE player_ranking
    ADD COLUMN club_id BIGINT;

ALTER TABLE game_players
    ADD COLUMN club_id BIGINT;

ALTER TABLE chips
    ADD COLUMN club_id BIGINT;

-- =================================================================
-- PASSO 3: MIGRAÇÃO DE DADOS - ASSOCIAR DADOS EXISTENTES A UM CLUBE PADRÃO
-- Este é o passo crucial para não deixar dados órfãos.
-- =================================================================

-- Cria um "Clube Padrão" para abrigar todos os jogadores e jogos existentes.
-- NOTA: O owner_id=1 é um placeholder. Você precisará criar um 'user' admin com ID 1
-- ou ajustar este valor para um utilizador real que será o dono dos dados legados.
INSERT INTO clubs (id, name) VALUES (1, 'Clube Principal');
-- Garante que o próximo clube a ser criado terá o ID 2
SELECT setval('clubs_id_seq', (SELECT MAX(id) FROM clubs));



-- =================================================================
-- PASSO 4: ADICIONAR RESTRIÇÕES 'NOT NULL'
-- Associa todos os jogos, jogadores e rankings existentes a este clube padrão.
-- Agora que todas as linhas têm um club_id, podemos tornar a coluna obrigatória.
-- =================================================================

UPDATE games SET club_id = 1 WHERE club_id IS NULL;
ALTER TABLE games ALTER COLUMN club_id SET NOT NULL;

UPDATE player_ranking SET club_id = 1 WHERE club_id IS NULL;
ALTER TABLE player_ranking ALTER COLUMN club_id SET NOT NULL;

UPDATE club_members SET club_id = 1 WHERE club_id IS NULL;
ALTER TABLE club_members ALTER COLUMN club_id SET NOT NULL;

UPDATE game_players SET club_id = 1 WHERE club_id IS NULL;
ALTER TABLE game_players ALTER COLUMN club_id SET NOT NULL;

UPDATE chips SET club_id = 1 WHERE club_id IS NULL;
ALTER TABLE chips ALTER COLUMN club_id SET NOT NULL;


-- =================================================================
-- PASSO 5: CRIAR TABELAS FINAIS E ADICIONAR TODAS AS CHAVES ESTRANGEIRAS
-- Com os dados consistentes, agora podemos criar os relacionamentos formais.
-- =================================================================

-- Adiciona as chaves estrangeiras nas tabelas alteradas
ALTER TABLE club_members ADD CONSTRAINT fk_club_members_users FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE club_members ADD CONSTRAINT fk_club_members_clubs FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE;
ALTER TABLE games ADD CONSTRAINT fk_games_clubs FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE;
ALTER TABLE player_ranking ADD CONSTRAINT fk_player_ranking_clubs FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE;
ALTER TABLE game_players ADD CONSTRAINT fk_game_players_clubs FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE;
ALTER TABLE chips ADD CONSTRAINT fk_chips_clubs FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE;

-- Cria Tabela de Refresh Tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);