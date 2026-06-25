package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {
    List<GameParticipant> findByGameIdOrderByBalanceDesc(Long gameId);

    List<GameParticipant> findByMemberId(Long memberId);

    Optional<GameParticipant> findByGameIdAndMemberId(Long gameId, Long memberId);

    Boolean existsByGameIdAndMemberId(Long gameId, Long memberId);

    List<GameParticipant> findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(Long gameId, PaymentSituation paymentSituation);


}
