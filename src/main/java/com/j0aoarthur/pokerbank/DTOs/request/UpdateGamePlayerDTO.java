package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record UpdateGamePlayerDTO(
        @NotNull(message = "A banca inicial é obrigatória.")
        @DecimalMin(value = "0.01", message = "A banca inicial deve ser maior que zero.")
        BigDecimal initialCash,

        List<ChipCountRequestDTO> chips
) {}