package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubMemberService {
    ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO, Club club);
    ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO);
    ClubMember getClubMemberById(Long id);
    List<ClubMember> getAllClubMembers();
    List<ClubMember> getClubMembersNotInGame(Long gameId);
    Optional<ClubMember> getMemberByUserAndClub(Long userId, Long clubId);
    List<Club> getClubsByUserId(Long userId);
    ClubMember getClubMemberByUserId(Long userId);
    ClubMember claimClubMember(UUID claimToken);
}
