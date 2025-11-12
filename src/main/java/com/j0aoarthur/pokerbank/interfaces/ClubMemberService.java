package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.DTOs.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;

import java.util.List;

public interface ClubMemberService {
    ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO, Club club);
    ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO);
    ClubMember getClubMemberById(Long id);
    List<ClubMember> getAllClubMembers();
    List<ClubMember> getClubMembersNotInGame(Long gameId);
}
