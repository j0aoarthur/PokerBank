package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChipRequestDTO(
        @NotBlank(message = "A cor da ficha é obrigatória.")
        String color,

        @NotNull(message = "O valor da ficha é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
        BigDecimal value
) {}

