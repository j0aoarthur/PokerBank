package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.util.List;

public interface GamePlayerService {
    GamePlayer addPlayerToGame(GamePlayerRequestDTO gamePlayer);
    GamePlayer getGamePlayerByGameAndMember(Long gameId, Long clubMemberId);
    List<GamePlayer> getGamePlayersByGame(Long gameId);
    List<GamePlayer> getGamePlayersByClubMember(Long clubMemberId);
    List<GamePlayer> getUnpaidGamePlayersByPaymentSituation(Long gameId, PaymentSituation paymentSituation);
    List<ChipCount> getChipCountsByGamePlayer(Long gamePlayerId);
    GamePlayer updateGamePlayer(Long gameId, Long clubMemberId, UpdateGamePlayerDTO dto);
    void updateGamePlayerPayment(GamePlayer gamePlayer);
}
