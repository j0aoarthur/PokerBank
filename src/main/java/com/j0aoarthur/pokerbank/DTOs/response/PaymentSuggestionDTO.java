package com.j0aoarthur.pokerbank.DTOs.response;

import java.math.BigDecimal;

public record PaymentSuggestionDTO(
        Long payerId,
        String payerName,
        Long receiverId,
        String receiverName,
        BigDecimal amount
) {
}
