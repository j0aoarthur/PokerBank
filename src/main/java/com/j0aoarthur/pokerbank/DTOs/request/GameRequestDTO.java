package com.j0aoarthur.pokerbank.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GameRequestDTO(

        @NotNull(message = "A data do jogo é obrigatória.")
        LocalDate date
) {}
