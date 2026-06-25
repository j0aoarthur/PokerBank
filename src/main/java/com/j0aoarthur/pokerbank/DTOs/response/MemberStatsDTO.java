package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.MemberStats;

import java.math.BigDecimal;

public record MemberStatsDTO(
        Long memberId,
        Integer rank,
        String memberName,
        int gamesPlayed,
        BigDecimal netBalance
) {
    public MemberStatsDTO(MemberStats pr) {
        this(
                pr.getMember().getId(),
                pr.getRank(),
                pr.getMember().getName(),
                pr.getGamesPlayed(),
                pr.getNetBalance()
        );
    }
}
