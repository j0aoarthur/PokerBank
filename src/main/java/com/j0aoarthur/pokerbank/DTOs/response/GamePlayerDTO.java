package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;

import java.math.BigDecimal;

public record GamePlayerDTO(
        Long playerId,
        String playerName,
        BigDecimal balance,
        Boolean paid,
        PaymentSituation paymentSituation
) {
    public GamePlayerDTO(GamePlayer gamePlayer) {
        this(
                gamePlayer.getPlayer().getId(),
                gamePlayer.getPlayer().getName(),
                gamePlayer.getBalance(),
                gamePlayer.getPaid(),
                gamePlayer.getPaymentSituation()
        );
    }
}
