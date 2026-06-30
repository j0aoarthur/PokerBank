package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ChipRequestDTO(
        @NotBlank(message = "A cor da ficha é obrigatória.")
        @Schema(description = "Nome da cor da ficha", example = "Vermelha")
        String color,

        @NotBlank(message = "O código hexadecimal da cor é obrigatório.")
        @Pattern(regexp = "^#([0-9A-Fa-f]{3}){1,2}$", message = "O código hexadecimal da cor é inválido.")
        @Schema(description = "Código hexadecimal da cor", example = "#FF0000")
        String colorHex,

        @NotNull(message = "O valor da ficha é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
        @Schema(description = "Valor monetário da ficha", example = "5.00")
        BigDecimal value
) {}

