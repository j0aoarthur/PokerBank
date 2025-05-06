package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;

import java.math.BigDecimal;

public record GamePlayerBalanceDTO(
        Long playerId,
        String playerName,
        BigDecimal balance,
        PaymentSituation paymentSituation
) {

    public GamePlayerBalanceDTO(GamePlayer gamePlayer) {
        this(
                gamePlayer.getPlayer().getId(),
                gamePlayer.getPlayer().getName(),
                gamePlayer.getBalance(),
                gamePlayer.getPaymentSituation()
        );
    }
}
