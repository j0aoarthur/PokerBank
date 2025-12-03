package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;

import java.util.List;

public interface PlayerRankingService {
    void updatePlayerRanking(GamePlayer gamePlayer);
    List<PlayerRanking> getPlayerRankings();
    List<PlayerRanking> getTopPlayers();
}
