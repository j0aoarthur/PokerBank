package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDTO(
        @NotNull @Schema(description = "ID da partida", example = "1") Long gameId,
        @NotNull @Schema(description = "ID do jogador pagador", example = "3") Long payerId,
        @NotNull @Schema(description = "ID do jogador recebedor", example = "5") Long receiverId,
        @NotNull @Schema(description = "Valor transferido", example = "50.00") BigDecimal amount
) {
}
