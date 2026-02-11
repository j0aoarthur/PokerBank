package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;

import java.math.BigDecimal;
import java.util.List;

public record GamePlayerInfoDTO(
        Long clubMemberId,
        String clubMemberName,
        BigDecimal initialCash,
        BigDecimal balance,
        BigDecimal pendingAmount,
        List<ChipCountDTO> chips
) {

    public GamePlayerInfoDTO(GamePlayer gamePlayer, List<ChipCountDTO> chips) {
        this(
                gamePlayer.getClubMember().getId(),
                gamePlayer.getClubMember().getName(),
                gamePlayer.getInitialCash(),
                gamePlayer.getBalance(),
                gamePlayer.getPendingAmount(),
                chips
        );
    }
}
