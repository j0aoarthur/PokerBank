package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(

        @NotNull(message = "Nome de usuário é obrigatório")
        @Schema(description = "Nome de usuário (nickname)", example = "joao.silva")
        String username,

        @NotNull(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "Senha123!")
        String password
) {}
