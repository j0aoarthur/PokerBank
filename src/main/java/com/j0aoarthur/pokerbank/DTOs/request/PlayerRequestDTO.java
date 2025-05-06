package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.NotBlank;

public record PlayerRequestDTO(
        @NotBlank(message = "O nome do jogador é obrigatório.")
        String name
) {}
