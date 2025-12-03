package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.math.BigDecimal;

public record GamePlayerBalanceDTO(
        Long clubMemberId,
        String clubMemberName,
        BigDecimal balance,
        PaymentSituation paymentSituation,
        Boolean paid
) {

    public GamePlayerBalanceDTO(GamePlayer gamePlayer) {
        this(
                gamePlayer.getClubMember().getId(),
                gamePlayer.getClubMember().getName(),
                gamePlayer.getBalance(),
                gamePlayer.getPaymentSituation(),
                gamePlayer.getPaid()
        );
    }
}
