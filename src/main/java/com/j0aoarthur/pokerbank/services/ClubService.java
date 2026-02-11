package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;

import java.util.List;

public interface ClubService {
    Club createClub(ClubRequestDTO clubRequestDTO);
    Club getClubById(Long clubId);
    List<Club> getAllClubs();
    Club updateClub(Long clubId, ClubRequestDTO clubRequestDTO);
    ClubMember joinClub(String publicCode);
    void deleteClub(Long clubId);
    List<Club> getMyClubs();
    public List<ClubMember> getClubMembers();
}
