package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;

import java.util.List;

public interface ClubService {
    Club createClub(ClubRequestDTO clubRequestDTO);
    Club getClubById(Long clubId);
    List<Club> getAllClubs();
    Club updateClub(Long clubId, ClubRequestDTO clubRequestDTO);
    Member joinClub(String publicCode);
    void deleteClub(Long clubId);
    List<Club> getMyClubs();
    List<Member> getMembers();
    void removeMemberFromClub(Long clubId, Long memberId);
}
