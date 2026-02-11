package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.PlayerRanking;

import java.math.BigDecimal;

public record PlayerRankingDTO(
        Long clubMemberId,
        Integer rank,
        String clubMemberName,
        int gamesPlayed,
        BigDecimal netBalance
) {
    public PlayerRankingDTO(PlayerRanking pr) {
        this(
                pr.getClubMember().getId(),
                pr.getRank(),
                pr.getClubMember().getName(),
                pr.getGamesPlayed(),
                pr.getNetBalance()
        );
    }
}
