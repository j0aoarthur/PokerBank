package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.MemberStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberStatsRepository extends JpaRepository<MemberStats, Long> {
    Optional<MemberStats> findByMemberId(Long memberId);

    List<MemberStats> findAllByGamesPlayedAfterOrderByNetBalanceDesc(Integer gamesPlayedAfter);
}
