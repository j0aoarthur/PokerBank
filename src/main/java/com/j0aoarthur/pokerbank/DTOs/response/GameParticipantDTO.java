package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.math.BigDecimal;

public record GameParticipantDTO(
        Long memberId,
        String memberName,
        BigDecimal balance,
        Boolean paid,
        PaymentSituation paymentSituation
) {
    public GameParticipantDTO(GameParticipant gameParticipant) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getBalance(),
                gameParticipant.getPaid(),
                gameParticipant.getPaymentSituation()
        );
    }
}
