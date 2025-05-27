package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record GamePlayerRequestDTO(

        @NotNull(message = "O ID do jogo é obrigatório.")
        Long gameId,

        @NotNull(message = "O ID do jogador é obrigatório.")
        Long playerId,

        @NotNull(message = "A banca inicial é obrigatória.")
        @DecimalMin(value = "0.01", message = "A banca inicial deve ser maior que zero.")
        BigDecimal initialCash,

        List<ChipCountRequestDTO> chips

) {
    public record ChipCountRequestDTO(
            @NotNull(message = "O ID da ficha é obrigatório.")
            Long chipId,

            @NotNull(message = "A quantidade é obrigatória.")
            @Min(value = 0, message = "A quantidade mínima é 0.")
            Integer quantity
    ) {}
}

