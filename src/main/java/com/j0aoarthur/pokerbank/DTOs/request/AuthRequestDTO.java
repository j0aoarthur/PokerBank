package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record AuthRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome completo do usuário", example = "João Silva")
        String name,

        @NotBlank(message = "Nome de usuário é obrigatório")
        @Schema(description = "Nome de usuário (nickname)", example = "joao.silva")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @Pattern(message = "A senha deve ter pelo menos um número, uma letra e um caractere especial", regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
        @Schema(description = "Senha do usuário", example = "Senha123!")
        String password,

        @Email(message = "Email não é válido")
        @NotNull(message = "Email é obrigatório")
        @Schema(description = "E-mail válido", example = "joao@email.com")
        String email
) {}
