package com.j0aoarthur.pokerbank.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record ClubRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
