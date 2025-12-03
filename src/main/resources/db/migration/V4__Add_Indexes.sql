-- Este script cria índices nas colunas que serão frequentemente usadas em
-- consultas (JOINs e WHEREs), melhorando a performance da aplicação.

CREATE INDEX idx_games_club_id ON games(club_id);
CREATE INDEX idx_players_club_id ON club_members(club_id);
CREATE INDEX idx_player_ranking_club_id ON player_ranking(club_id);
CREATE INDEX idx_gameplayers_club_id ON game_players(club_id);
CREATE INDEX idx_chips_club_id ON chips(club_id);

CREATE INDEX idx_game_players_game_id ON game_players(game_id);