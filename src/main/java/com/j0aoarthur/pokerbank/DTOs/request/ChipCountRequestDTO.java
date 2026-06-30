package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChipCountRequestDTO(
        @NotNull(message = "O ID da ficha é obrigatório.")
        @Schema(description = "ID da ficha", example = "1")
        Long chipId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 0, message = "A quantidade mínima é 0.")
        @Schema(description = "Quantidade desta ficha com o jogador", example = "10")
        Integer quantity
) {}