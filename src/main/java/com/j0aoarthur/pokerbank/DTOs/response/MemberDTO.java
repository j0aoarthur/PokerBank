package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.enums.Role;

public record MemberDTO(Long id, String name, Role role, String username) {
    public MemberDTO(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getRole(),
                member.getAccount() != null ? member.getAccount().getUsername() : null
        );
    }
}
