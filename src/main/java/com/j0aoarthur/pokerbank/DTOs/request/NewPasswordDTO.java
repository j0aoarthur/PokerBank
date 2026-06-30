package com.j0aoarthur.pokerbank.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewPasswordDTO(

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        @Pattern(message = "A senha deve ter pelo menos um número, uma letra e um caractere especial", regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
        @Schema(description = "Nova senha", example = "SenhaNova123!")
        String newPassword,

        @NotBlank
        @Schema(description = "Token de redefinição recebido por e-mail", example = "123e4567-e89b-12d3-a456-426614174000")
        String passwordToken
) {}
