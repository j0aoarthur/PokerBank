package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.enums.Role;

public record ClubMemberDTO(Long id, String name, Role role) {
    public ClubMemberDTO(ClubMember clubMember) {
        this(clubMember.getId(), clubMember.getName(), clubMember.getRole());
    }
}
