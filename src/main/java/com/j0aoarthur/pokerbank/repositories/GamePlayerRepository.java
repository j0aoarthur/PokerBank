package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<GamePlayer> findByGameIdOrderByBalanceDesc(Long gameId);

    List<GamePlayer> findByPlayerId(Long playerId);

    Optional<GamePlayer> findByGameIdAndPlayerId(Long gameId, Long playerId);

    Boolean existsByGameIdAndPlayerId(Long gameId, Long playerId);

    List<GamePlayer> findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(Long gameId, PaymentSituation paymentSituation);


}
