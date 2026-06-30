package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record GameParticipantRequestDTO(

        @NotNull(message = "O ID do jogo é obrigatório.")
        @Schema(description = "ID da partida", example = "1")
        Long gameId,

        @NotNull(message = "O ID do jogador é obrigatório.")
        @Schema(description = "ID do membro do clube", example = "5")
        Long memberId,

        @NotNull(message = "A banca inicial é obrigatória.")
        @DecimalMin(value = "0.01", message = "A banca inicial deve ser maior que zero.")
        @Schema(description = "Valor inicial em dinheiro (Banca)", example = "100.00")
        BigDecimal initialCash,

        @Schema(description = "Lista de fichas do jogador na partida")
        List<ChipCountRequestDTO> chips

) {}

