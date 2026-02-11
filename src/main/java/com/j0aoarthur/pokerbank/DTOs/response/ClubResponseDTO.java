package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Club;

public record ClubResponseDTO(
        Long id,
        String name,
        String description,
        String publicCode
) {
    public ClubResponseDTO(Club club) {
        this(club.getId(), club.getName(), club.getDescription(), club.getPublicCode());
    }
}
