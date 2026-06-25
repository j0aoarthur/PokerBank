-- Migration V6: Rename Tables for clearer domain terminology

-- 1. Rename 'users' to 'accounts'
ALTER TABLE users RENAME TO accounts;
ALTER TABLE refresh_tokens RENAME COLUMN user_id TO account_id;

-- 2. Rename 'club_members' to 'members'
ALTER TABLE club_members RENAME TO members;
-- Update foreign key column name in members
ALTER TABLE members RENAME COLUMN user_id TO account_id;

-- 3. Rename 'game_players' to 'game_participants'
ALTER TABLE game_players RENAME TO game_participants;
-- Update foreign key column name in game_participants
ALTER TABLE game_participants RENAME COLUMN club_member_id TO member_id;
ALTER TABLE chip_counts RENAME COLUMN game_player_id TO game_participant_id;

-- 4. Rename 'player_rankings' to 'member_stats'
ALTER TABLE player_rankings RENAME TO member_stats;
-- Update foreign key column name in member_stats
ALTER TABLE member_stats RENAME COLUMN club_member_id TO member_id;

-- Renaming sequences (Optional depending on DB, but often table_id_seq)
-- ALTER SEQUENCE users_id_seq RENAME TO accounts_id_seq;
-- ALTER SEQUENCE club_members_id_seq RENAME TO members_id_seq;
-- ALTER SEQUENCE game_players_id_seq RENAME TO game_participants_id_seq;
-- ALTER SEQUENCE player_rankings_id_seq RENAME TO member_stats_id_seq;
