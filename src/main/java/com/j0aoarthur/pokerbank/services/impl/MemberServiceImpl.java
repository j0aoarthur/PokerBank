package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.MemberRepository;
import com.j0aoarthur.pokerbank.services.MemberService;
import com.j0aoarthur.pokerbank.tenancy.annotations.ClubIndependent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AuthContextService authContextService;

    @Override
    @Transactional
    public Member createMember(MemberRequestDTO memberDTO, Club club) {
        if (club == null || club.getId() == null) {
            throw new IllegalArgumentException("club_id é obrigatório para cadastrar um membro do clube.");
        }

        Account currentUser = authContextService.getCurrentUser();

        Optional<Member> existingPlayer = memberRepository.findByAccountId(currentUser.getId());

        if (existingPlayer.isPresent()) {
            throw new IllegalArgumentException("O usuário já é um jogador deste clube.");
        }

        Member member = new Member(memberDTO, club, currentUser);

        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member createMember(MemberRequestDTO memberDTO) {
        return this.createMember(memberDTO, authContextService.getCurrentClub());
    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com o ID: " + id));
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> allMembers = memberRepository.findAll().stream()
                .sorted(Comparator.comparing(Member::getName)).toList();
        if (allMembers.isEmpty()) {
            throw new EntityNotFoundException("Nenhum jogador encontrado.");
        }
        return allMembers;
    }

    @Override
    public List<Member> getMembersNotInGame(Long gameId) {
        return memberRepository.findMembersNotInGameOrderByName(gameId);
    }

    @Override
    public Optional<Member> getMemberByUserAndClub(Long accountId, Long clubId) {
        return memberRepository.findByAccountIdAndClubId(accountId, clubId);
    }

    @Override
    public Member getMemberByAccountId(Long accountId) {
        return memberRepository.findByAccountId(accountId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Jogador não encontrado para o usuário com ID: " + accountId));
    }

    @Override
    public List<Club> getClubsByAccountId(Long accountId) {
        List<Member> members = memberRepository.findAllByAccountId(accountId);
        if (!members.isEmpty()) {
            return members.stream().map(Member::getClub).toList();
        }
        throw new EntityNotFoundException("O usuário não pertence a nenhum clube.");
    }

    @Override
    @ClubIndependent
    @Transactional
    public Member claimMember(UUID claimToken) {
        Account currentUser = authContextService.getCurrentUser();

        Integer updatedRows = memberRepository.updateUserByClaimToken(claimToken, currentUser);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Jogador não encontrado com o claim token: " + claimToken);
        }

        if (updatedRows > 1) {
            throw new IllegalStateException("Mais de um jogador encontrado com o claim token: " + claimToken);
        }

        List<Member> userMemberships = memberRepository.findAllByAccountId(currentUser.getId());
        if (userMemberships.isEmpty()) {
            throw new EntityNotFoundException("Jogador não encontrado para o usuário.");
        }

        return userMemberships.get(userMemberships.size() - 1);
    }
}