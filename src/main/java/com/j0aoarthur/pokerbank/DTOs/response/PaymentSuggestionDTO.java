package com.j0aoarthur.pokerbank.DTOs.response;

import java.math.BigDecimal;

public record PaymentSuggestionDTO(
        String payerName,
        String receiverName,
        BigDecimal amount
) {
}
