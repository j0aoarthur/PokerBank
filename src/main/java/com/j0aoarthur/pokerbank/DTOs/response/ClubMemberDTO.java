package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.enums.Role;

public record ClubMemberDTO(Long id, String name, Role role, String username) {
    public ClubMemberDTO(ClubMember clubMember) {
        this(clubMember.getId(), clubMember.getName(), clubMember.getRole(), clubMember.getUser().getUsername());
    }
}
