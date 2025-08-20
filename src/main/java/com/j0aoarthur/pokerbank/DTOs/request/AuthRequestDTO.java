package com.j0aoarthur.pokerbank.DTOs.request;

import jakarta.validation.constraints.*;

public record AuthRequestDTO(

        @NotBlank(message = "Nome de usuário é obrigatório")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @Pattern(message = "A senha deve ter pelo menos um número, uma letra e um caractere especial", regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
        String password,

        @Email(message = "Email não é válido")
        @NotNull(message = "Email é obrigatório")
        String email
) {}
