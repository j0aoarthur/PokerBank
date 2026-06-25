package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ClubRepository;
import com.j0aoarthur.pokerbank.services.MemberService;
import com.j0aoarthur.pokerbank.services.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final AuthContextService authContextService;
    private final MemberService memberService;



    @Override
    public Club createClub(ClubRequestDTO clubRequestDTO) {
        try {
            // 1. Salva o clube em sua própria transação para obter o ID.
            Club club = clubRepository.save(new Club(clubRequestDTO));


            // 2. Define o contexto do tenant para o novo clube.
            ClubContext.setCurrentClubId(club.getId());

            // 3. Configura o dono e a associação do clube.
            Account currentUser = authContextService.getCurrentUser();
            memberService.createMember(new MemberRequestDTO(currentUser.getName(), Role.OWNER), club);

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
    public Member joinClub(String publicCode) {
        Club club = clubRepository.findByPublicCode(publicCode).orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o código: " + publicCode));

        Account currentUser = authContextService.getCurrentUser();

        Optional<Member> member = memberService.getMemberByUserAndClub(currentUser.getId(), club.getId());
        if (member.isPresent()) {
            throw new IllegalArgumentException("Usuário já é membro deste clube.");
        }

        return memberService.createMember(new MemberRequestDTO(currentUser.getName(), Role.PLAYER), club);
    }

    public List<Club> getMyClubs() {
        Account currentUser = authContextService.getCurrentUser();
        return memberService.getClubsByAccountId(currentUser.getId());
    }

    public List<Member> getMembers() {
        Club currentClub = authContextService.getCurrentClub();
        return currentClub.getMembers();
    }

    @Override
    public void removeMemberFromClub(Long clubId, Long memberId) {
        Club club = this.getClubById(clubId);
        Member member = memberService.getMemberById(memberId);
        club.getMembers().remove(member);
        clubRepository.save(club);
    }
}
