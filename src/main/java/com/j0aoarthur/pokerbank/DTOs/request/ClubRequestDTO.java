package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.NotBlank;

public record ClubRequestDTO(

        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
