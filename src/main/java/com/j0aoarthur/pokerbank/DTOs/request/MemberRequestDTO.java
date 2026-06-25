package com.j0aoarthur.pokerbank.dtos.request;

import com.j0aoarthur.pokerbank.entities.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record MemberRequestDTO(
        @NotBlank(message = "O nome do membro do clube é obrigatório.")
        String name,

        @Enumerated(EnumType.STRING)
        Role role
) {}
