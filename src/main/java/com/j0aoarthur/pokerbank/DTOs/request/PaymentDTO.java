package com.j0aoarthur.pokerbank.DTOs.request;

import java.math.BigDecimal;

public record PaymentDTO(Long gameId, Long payerId, Long receiverId, BigDecimal amount) {
}
