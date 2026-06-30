package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GameRequestDTO(

        @NotNull(message = "A data do jogo é obrigatória.")
        @Schema(description = "Data da partida", example = "2024-05-20")
        LocalDate date
) {}
