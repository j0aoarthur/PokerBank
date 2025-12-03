package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<GamePlayer> findByGameIdOrderByBalanceDesc(Long gameId);

    List<GamePlayer> findByClubMemberId(Long clubMemberId);

    Optional<GamePlayer> findByGameIdAndClubMemberId(Long gameId, Long clubMemberId);

    Boolean existsByGameIdAndClubMemberId(Long gameId, Long clubMemberId);

    List<GamePlayer> findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(Long gameId, PaymentSituation paymentSituation);


}
