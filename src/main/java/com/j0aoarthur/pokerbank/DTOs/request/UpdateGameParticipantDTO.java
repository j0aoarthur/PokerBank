package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record UpdateGameParticipantDTO(
        @NotNull(message = "A banca inicial é obrigatória.")
        @DecimalMin(value = "0.01", message = "A banca inicial deve ser maior que zero.")
        @Schema(description = "Novo valor inicial em dinheiro (Banca)", example = "150.00")
        BigDecimal initialCash,

        @Schema(description = "Nova lista de fichas do jogador")
        List<ChipCountRequestDTO> chips
) {}