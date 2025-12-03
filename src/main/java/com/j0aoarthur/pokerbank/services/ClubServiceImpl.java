package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.interfaces.ClubMemberService;
import com.j0aoarthur.pokerbank.interfaces.ClubService;
import com.j0aoarthur.pokerbank.repositories.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final AuthContextService authContextService;
    private final ClubMemberService clubMemberService;
    private static final Logger logger = LoggerFactory.getLogger(ClubServiceImpl.class);



    @Override
    public Club createClub(ClubRequestDTO clubRequestDTO) {
        try {
            // 1. Salva o clube em sua própria transação para obter o ID.
            Club club = clubRepository.save(new Club(clubRequestDTO));


            // 2. Define o contexto do tenant para o novo clube.
            ClubContext.setCurrentClubId(club.getId());

            // 3. Configura o dono e a associação do clube.
            User currentUser = authContextService.getCurrentUser();
            clubMemberService.createClubMember(new ClubMemberRequestDTO(currentUser.getName(), Role.OWNER), club);

            return club;
        } finally {
            ClubContext.clear();
        }
    }

    @Override
    public Club getClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o ID: " + clubId));
    }

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    public Club updateClub(Long clubId, ClubRequestDTO clubRequestDTO) {
        Club club = this.getClubById(clubId);
        club.setName(clubRequestDTO.name());
        club.setDescription(clubRequestDTO.description());
        return clubRepository.save(club);
    }

    @Override
    public void deleteClub(Long clubId) {
        Club club = this.getClubById(clubId);
        clubRepository.delete(club);
    }

    @Override
    @Transactional
    public ClubMember joinClub(String publicCode) {
        Club club = clubRepository.findByPublicCode(publicCode).orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o código: " + publicCode));

        User currentUser = authContextService.getCurrentUser();

        Optional<ClubMember> clubMember = clubMemberService.getMemberByUserAndClub(currentUser.getId(), club.getId());
        if (clubMember.isPresent()) {
            throw new IllegalArgumentException("Usuário já é membro deste clube.");
        }

        return clubMemberService.createClubMember(new ClubMemberRequestDTO(currentUser.getName(), Role.PLAYER), club);
    }

    public List<Club> getMyClubs() {
        User currentUser = authContextService.getCurrentUser();
        return clubMemberService.getClubsByUserId(currentUser.getId());
    }

    public List<ClubMember> getClubMembers() {
        return clubMemberService.getAllClubMembers();
    }
}
