package com.j0aoarthur.pokerbank.dtos.request;

import com.j0aoarthur.pokerbank.entities.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record MemberRequestDTO(
        @NotBlank(message = "O nome do membro do clube é obrigatório.")
        @Schema(description = "Nome de exibição do membro no clube", example = "Joãozinho")
        String name,

        @Enumerated(EnumType.STRING)
        @Schema(description = "Cargo do membro no clube", example = "PLAYER")
        Role role
) {}
