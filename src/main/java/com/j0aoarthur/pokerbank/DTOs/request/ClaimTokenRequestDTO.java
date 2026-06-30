package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClaimTokenRequestDTO(
        @NotNull(message = "O token de reivindicação é obrigatório.")
        @Schema(description = "UUID do token gerado na criação do membro", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID claimToken
) {}
