package com.j0aoarthur.pokerbank.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record GamePlayerRequestDTO(

        @NotNull(message = "O ID do jogo é obrigatório.")
        Long gameId,

        @NotNull(message = "O ID do jogador é obrigatório.")
        Long clubMemberId,

        @NotNull(message = "A banca inicial é obrigatória.")
        @DecimalMin(value = "0.01", message = "A banca inicial deve ser maior que zero.")
        BigDecimal initialCash,

        List<ChipCountRequestDTO> chips

) {}

