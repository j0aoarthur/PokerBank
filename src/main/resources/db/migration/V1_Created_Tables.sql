-- Use the database
\c pokerbank;

-- Create the 'chips' table
CREATE TABLE chips (
    id BIGSERIAL PRIMARY KEY,
    color VARCHAR(255) NOT NULL,
    colorHex VARCHAR(7) NOT NULL,
    chip_value DECIMAL(10, 2) NOT NULL
);

-- Create the 'players' table
CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the 'games' table
CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the 'game_players' table
CREATE TABLE game_players (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    initial_cash DECIMAL(10, 2) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    settled_amount DECIMAL(10, 2) DEFAULT 0.00,
    paid BOOLEAN DEFAULT FALSE,
    payment_situation VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (game_id) REFERENCES games (id),
    FOREIGN KEY (player_id) REFERENCES players (id)
);

-- Create the 'game_player_chips' table (to store chip counts for each player in a game)
CREATE TABLE chip_counts (
   id BIGSERIAL PRIMARY KEY,
   game_player_id BIGINT NOT NULL,
   chip_id BIGINT NOT NULL,
   quantity INT NOT NULL,
   FOREIGN KEY (game_player_id) REFERENCES game_players (id),
   FOREIGN KEY (chip_id) REFERENCES chips (id)
);

CREATE TABLE player_ranking (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL UNIQUE,
    rank INT,
    total_won DECIMAL(19, 2) DEFAULT 0.00,
    total_lost DECIMAL(19, 2) DEFAULT 0.00,
    net_balance DECIMAL(19, 2) DEFAULT 0.00,
    games_played INT DEFAULT 0,
    FOREIGN KEY (player_id) REFERENCES players (id)
);