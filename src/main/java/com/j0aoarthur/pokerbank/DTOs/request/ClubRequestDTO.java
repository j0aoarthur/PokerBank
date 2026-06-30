package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ClubRequestDTO(

        @NotBlank
        @Schema(description = "Nome do clube", example = "Clube dos Amigos")
        String name,

        @NotBlank
        @Schema(description = "Descrição ou informações do clube", example = "Poker às sextas-feiras")
        String description
) {
}
