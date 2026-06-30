package com.j0aoarthur.pokerbank.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tokens de autenticação retornados após login ou refresh")
public record AuthResponse(
        @Schema(description = "JWT de acesso de curta duração", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Token de atualização de longa duração", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {
}
