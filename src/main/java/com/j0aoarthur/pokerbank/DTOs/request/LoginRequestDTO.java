package com.j0aoarthur.pokerbank.dtos.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(

        @NotNull(message = "Nome de usuário é obrigatório")
        String username,

        @NotNull(message = "Senha é obrigatória")
        String password
) {}
