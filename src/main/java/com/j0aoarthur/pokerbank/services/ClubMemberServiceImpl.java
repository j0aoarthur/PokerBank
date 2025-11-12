package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.infra.context.AuthContextServiceImpl;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.interfaces.ClubMemberService;
import com.j0aoarthur.pokerbank.repositories.ClubMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubMemberServiceImpl implements ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final AuthContextServiceImpl authContextService;

    @Override
    @Transactional
    public ClubMember createClubMember(ClubMemberRequestDTO clubMemberDTO, Club club) {
        User currentUser = authContextService.getCurrentUser();

        Optional<ClubMember> existingPlayer = clubMemberRepository.findByUserId(currentUser.getId());

        if (existingPlayer.isPresent()) {
            throw new IllegalArgumentException("O usuário já é um jogador deste clube.");
        }

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
}