package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.math.BigDecimal;

public record GamePlayerDTO(
        Long clubMemberId,
        String clubMemberName,
        BigDecimal balance,
        Boolean paid,
        PaymentSituation paymentSituation
) {
    public GamePlayerDTO(GamePlayer gamePlayer) {
        this(
                gamePlayer.getClubMember().getId(),
                gamePlayer.getClubMember().getName(),
                gamePlayer.getBalance(),
                gamePlayer.getPaid(),
                gamePlayer.getPaymentSituation()
        );
    }
}
