package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChipCountRequestDTO(
        @NotNull(message = "O ID da ficha é obrigatório.")
        Long chipId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 0, message = "A quantidade mínima é 0.")
        Integer quantity
) {}