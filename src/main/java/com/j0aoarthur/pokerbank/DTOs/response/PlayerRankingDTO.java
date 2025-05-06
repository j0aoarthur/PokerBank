package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.PlayerRanking;

import java.math.BigDecimal;

public record PlayerRankingDTO(
        Long playerId,
        String playerName,
        int gamesPlayed,
        BigDecimal netBalance
) {
    public PlayerRankingDTO(PlayerRanking pr) {
        this(
                pr.getPlayer().getId(),
                pr.getPlayer().getName(),
                pr.getGamesPlayed(),
                pr.getNetBalance()
        );
    }
}
