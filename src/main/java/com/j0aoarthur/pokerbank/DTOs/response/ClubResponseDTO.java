package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Club;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do clube")
public record ClubResponseDTO(
        @Schema(description = "ID do clube", example = "1")
        Long id,

        @Schema(description = "Nome do clube", example = "Clube dos Amigos")
        String name,

        @Schema(description = "Descrição ou informações do clube", example = "Poker às sextas-feiras")
        String description,

        @Schema(description = "Código público de convite do clube", example = "ABC123")
        String publicCode
) {
    public ClubResponseDTO(Club club) {
        this(club.getId(), club.getName(), club.getDescription(), club.getPublicCode());
    }
}
