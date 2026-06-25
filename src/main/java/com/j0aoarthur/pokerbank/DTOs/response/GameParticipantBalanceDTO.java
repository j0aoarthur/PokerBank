package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.math.BigDecimal;

public record GameParticipantBalanceDTO(
        Long memberId,
        String memberName,
        BigDecimal balance,
        PaymentSituation paymentSituation,
        Boolean paid
) {

    public GameParticipantBalanceDTO(GameParticipant gameParticipant) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getBalance(),
                gameParticipant.getPaymentSituation(),
                gameParticipant.getPaid()
        );
    }
}
