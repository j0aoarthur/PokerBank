package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberService {
    Member createMember(MemberRequestDTO memberDTO, Club club);
    Member createMember(MemberRequestDTO memberDTO);
    Member getMemberById(Long id);
    List<Member> getAllMembers();
    List<Member> getMembersNotInGame(Long gameId);
    Optional<Member> getMemberByUserAndClub(Long accountId, Long clubId);
    List<Club> getClubsByAccountId(Long accountId);
    Member getMemberByAccountId(Long accountId);
    Member claimMember(UUID claimToken);
}
