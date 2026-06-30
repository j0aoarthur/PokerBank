package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.GameParticipantRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGameParticipantDTO;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.util.List;

public interface GameParticipantService {
    GameParticipant addParticipantToGame(GameParticipantRequestDTO gameParticipant);
    GameParticipant getGameParticipantByGameAndMember(Long gameId, Long memberId);
    List<GameParticipant> getGameParticipantsByGame(Long gameId);
    List<GameParticipant> getGameParticipantsByMember(Long memberId);
    List<GameParticipant> getUnpaidGameParticipantsByPaymentSituation(Long gameId, PaymentSituation paymentSituation);
    List<ChipCount> getChipCountsByGameParticipant(Long gameParticipantId);
    GameParticipant updateGameParticipant(Long gameId, Long memberId, UpdateGameParticipantDTO dto);
    void updateGameParticipantPayment(GameParticipant gameParticipant);
}
