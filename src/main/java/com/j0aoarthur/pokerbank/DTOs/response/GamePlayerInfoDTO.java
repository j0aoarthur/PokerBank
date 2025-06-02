package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;

import java.math.BigDecimal;
import java.util.List;

public record GamePlayerInfoDTO(
        Long playerId,
        String playerName,
        BigDecimal initialCash,
        BigDecimal balance,
        BigDecimal pendingAmount,
        List<ChipCountDTO> chips
) {

    public GamePlayerInfoDTO(GamePlayer gamePlayer, List<ChipCountDTO> chips) {
        this(
                gamePlayer.getPlayer().getId(),
                gamePlayer.getPlayer().getName(),
                gamePlayer.getInitialCash(),
                gamePlayer.getBalance(),
                gamePlayer.getPendingAmount(),
                chips
        );
    }
}
