package com.j0aoarthur.pokerbank.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Sugestão de transferência para liquidar débitos da partida com o menor número de transações")
public record PaymentSuggestionDTO(
        @Schema(description = "ID do jogador que deve pagar", example = "3")
        Long payerId,

        @Schema(description = "Nome do jogador que deve pagar", example = "Roberto")
        String payerName,

        @Schema(description = "ID do jogador que deve receber", example = "5")
        Long receiverId,

        @Schema(description = "Nome do jogador que deve receber", example = "Carlos")
        String receiverName,

        @Schema(description = "Valor a ser transferido", example = "50.00")
        BigDecimal amount
) {
}
