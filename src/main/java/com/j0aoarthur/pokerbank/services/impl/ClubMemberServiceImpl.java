package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.infra.context.AuthContextServiceImpl;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ClubMemberRepository;
import com.j0aoarthur.pokerbank.security.annotations.ClubIndependent;
import com.j0aoarthur.pokerbank.services.ClubMemberService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final AuthContextServiceImpl authContextService;
    private static final Logger logger = LoggerFactory.getLogger(ClubMemberServiceImpl.class);

    @Override
    @Transactional
    public ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO, Club club) {
        if (club == null || club.getId() == null) {
            throw new IllegalArgumentException("club_id é obrigatório para cadastrar um membro do clube.");
        }

        User currentUser = authContextService.getCurrentUser();

        Optional<ClubMember> existingPlayer = clubMemberRepository.findByUserId(currentUser.getId());

        if (existingPlayer.isPresent()) {
            throw new IllegalArgumentException("O usuário já é um jogador deste clube.");
        }
        logger.info("Club id inside createClubMember: " + club.getId());


        ClubMember clubMember = new ClubMember(clubMemberDTO, club, currentUser);


        return clubMemberRepository.save(clubMember);
    }

    @Override
    @Transactional
    public ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO) {
        return this.createClubMember(clubMemberDTO, authContextService.getCurrentClub());
    }

    @Override
    public ClubMember getClubMemberById(Long id) {
        return clubMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com o ID: " + id));
    }

    @Override
    public List<ClubMember> getAllClubMembers() {
        List<ClubMember> allClubMembers = clubMemberRepository.findAll().stream().sorted(Comparator.comparing(ClubMember::getName)).toList();
        if (allClubMembers.isEmpty()) {
            throw new EntityNotFoundException("Nenhum jogador encontrado.");
        }
        return allClubMembers;
    }

    @Override
    public List<ClubMember> getClubMembersNotInGame(Long gameId) {
        return clubMemberRepository.findClubMembersNotInGameOrderByName(gameId);
    }

    @Override
    public Optional<ClubMember> getMemberByUserAndClub(Long userId, Long clubId) {
        return clubMemberRepository.findByUserIdAndClubId(userId, clubId);
    }

    @Override
    public ClubMember getClubMemberByUserId(Long userId) {
        return clubMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado para o usuário com ID: " + userId));
    }

    @Override
    public List<Club> getClubsByUserId(Long userId) {
        List<ClubMember> clubMembers = clubMemberRepository.findAllByUserId(userId);
        if (!clubMembers.isEmpty()) {
            return clubMembers.stream().map(ClubMember::getClub).toList();
        }
        throw new EntityNotFoundException("O usuário não pertence a nenhum clube.");
    }
}