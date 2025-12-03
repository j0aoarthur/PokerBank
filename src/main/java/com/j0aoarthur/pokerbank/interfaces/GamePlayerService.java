package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;

import java.util.List;

public interface GamePlayerService {
    GamePlayer addPlayerToGame(GamePlayerRequestDTO gamePlayer);
    GamePlayer getGamePlayer(Long gameId, Long clubMemberId);
    List<GamePlayer> getGamePlayersByGame(Long gameId);
    List<GamePlayer> getGamePlayersByPlayer(Long clubMemberId);
    List<GamePlayer> getGamePlayersWithBalanceAndPaymentSituation(Long gameId, PaymentSituation paymentSituation);
    List<ChipCount> getChipCountsByGamePlayer(Long gamePlayerId);
    GamePlayer updateGamePlayer(Long gameId, Long clubMemberId, UpdateGamePlayerDTO dto);
    void updateGamePlayerPayment(GamePlayer gamePlayer);
}
