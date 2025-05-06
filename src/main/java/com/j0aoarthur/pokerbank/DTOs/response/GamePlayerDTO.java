package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GamePlayerDTO(
        Long playerId,
        String playerName,
        BigDecimal initialCash,

        BigDecimal balance,

        LocalDate dueDate,
        Boolean paid,
        PaymentSituation paymentSituation,
        List<ChipCountDTO> chipsBalance
) {
    public GamePlayerDTO(GamePlayer gamePlayer, List<ChipCount> chips) {
        this(
                gamePlayer.getPlayer().getId(),
                gamePlayer.getPlayer().getName(),
                gamePlayer.getInitialCash(),
                gamePlayer.getBalance(),
                gamePlayer.getDueDate(),
                gamePlayer.getPaid(),
                gamePlayer.getPaymentSituation(),
                chips.stream().map(ChipCountDTO::new).toList()
        );
    }
}
