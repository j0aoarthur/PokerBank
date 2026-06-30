package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um membro do clube")
public record MemberDTO(
        @Schema(description = "ID do membro", example = "5")
        Long id,

        @Schema(description = "Nome de exibição do membro no clube", example = "Joãozinho")
        String name,

        @Schema(description = "Cargo do membro no clube", example = "PLAYER")
        Role role,

        @Schema(description = "Username da conta vinculada (null se ainda não vinculado)", example = "joao.silva")
        String username
) {
    public MemberDTO(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getRole(),
                member.getAccount() != null ? member.getAccount().getUsername() : null
        );
    }
}
