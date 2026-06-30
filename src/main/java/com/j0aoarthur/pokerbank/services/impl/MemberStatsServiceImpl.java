package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.MemberStats;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.repositories.MemberStatsRepository;
import com.j0aoarthur.pokerbank.services.MemberStatsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberStatsServiceImpl implements MemberStatsService {

    private final MemberStatsRepository memberStatsRepository;
    private final AuthContextService authContextService;

    @Override
    @Transactional
    public void updateMemberStats(GameParticipant gameParticipant) {
        Optional<MemberStats> memberStats = memberStatsRepository.findByMemberId(gameParticipant.getMember().getId());
        if (memberStats.isPresent()) {
            this.calculateAndSaveStats(memberStats.get(), gameParticipant);
        } else {
            MemberStats newMemberStats = new MemberStats();
            newMemberStats.setMember(gameParticipant.getMember());
            newMemberStats.setClub(gameParticipant.getMember().getClub());
            this.calculateAndSaveStats(newMemberStats, gameParticipant);
        }

        if (!gameParticipant.getMember().getClub().equals(authContextService.getCurrentClub())) {
            throw new IllegalArgumentException("O membro não pertence ao clube selecionado.");
        }
    }

    @Override
    public List<MemberStats> getAllMemberStats() {
        return memberStatsRepository.findAllByGamesPlayedAfterOrderByNetBalanceDesc(1);
    }

    @Override
    public List<MemberStats> getTopMembers() {
        return this.getAllMemberStats().stream().limit(3).toList();
    }

    private void calculateAndSaveStats(MemberStats memberStats, GameParticipant gameParticipant) {
        memberStats.setGamesPlayed(memberStats.getGamesPlayed() + 1);

        BigDecimal balance = gameParticipant.getBalance();
        memberStats.setNetBalance(memberStats.getNetBalance().add(balance));

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            memberStats.setTotalWon(memberStats.getTotalWon().add(balance));
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            memberStats.setTotalLost(memberStats.getTotalLost().add(balance.abs()));
        }

        memberStatsRepository.save(memberStats);

        updateStatsPositions();
    }

    private void updateStatsPositions() {
        List<MemberStats> allStats = this.getAllMemberStats();
        for (int i = 0; i < allStats.size(); i++) {
            allStats.get(i).setRank(i + 1);
            memberStatsRepository.save(allStats.get(i));
        }
    }
}