package com.j0aoarthur.pokerbank.dtos.response;

import java.math.BigDecimal;

public record PaymentSuggestionDTO(
        Long payerId,
        String payerName,
        Long receiverId,
        String receiverName,
        BigDecimal amount
) {
}
