package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenRequestDTO(
        @Schema(description = "Token de atualização válido", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String refreshToken
) {
}
