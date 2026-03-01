package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.ChipCountRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.entities.*;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipCountRepository;
import com.j0aoarthur.pokerbank.repositories.GamePlayerRepository;
import com.j0aoarthur.pokerbank.services.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GamePlayerServiceImpl implements GamePlayerService {

    private final GameService gameService;
    private final ClubMemberService clubMemberService;
    private final ChipService chipService;
    private final PlayerRankingService playerRankingService;
    private final GamePlayerRepository gamePlayerRepository;
    private final ChipCountRepository chipCountRepository;

    @Override
    @Transactional
    public GamePlayer addPlayerToGame(GamePlayerRequestDTO dto) {
        Game game = gameService.getGameById(dto.gameId());
        ClubMember clubMember = clubMemberService.getClubMemberById(dto.clubMemberId());

        // Verifica se o jogador já está na partida
        if (gamePlayerRepository.existsByGameIdAndClubMemberId(game.getId(), clubMember.getId())) {
            throw new IllegalArgumentException("Jogador já está na partida");
        }

        // Criar relação GamePlayer
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(dto, game, clubMember));

        this.addChipCountsToGamePlayer(gamePlayer, dto.chips());
        gamePlayer.recalculateBalance();
        playerRankingService.updatePlayerRanking(gamePlayer);
        return gamePlayer;
    }

    @Override
    public GamePlayer getGamePlayerByGameAndMember(Long gameId, Long clubMemberId) {
        return gamePlayerRepository.findByGameIdAndClubMemberId(gameId, clubMemberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "O jogador com ID: " + clubMemberId + " não está na partida com ID: " + gameId));
    }

    @Override
    public List<GamePlayer> getGamePlayersByGame(Long gameId) {
        return gamePlayerRepository.findByGameIdOrderByBalanceDesc(gameId);
    }

    @Override
    public List<GamePlayer> getGamePlayersByClubMember(Long clubMemberId) {
        List<GamePlayer> games = gamePlayerRepository.findByClubMemberId(clubMemberId);
        if (games.isEmpty()) {
            throw new EntityNotFoundException("Jogador não possui partidas jogadas");
        }

        return games;
    }

    @Override
    public List<GamePlayer> getUnpaidGamePlayersByPaymentSituation(Long gameId, PaymentSituation paymentSituation) {
        return gamePlayerRepository.findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(gameId,
                paymentSituation);
    }

    @Override
    public List<ChipCount> getChipCountsByGamePlayer(Long gamePlayerId) {
        return gamePlayerRepository.getReferenceById(gamePlayerId).getChipCounts().stream()
                .filter(chipCount -> chipCount.getQuantity() > 0).toList();
    }

    @Override
    @Transactional
    public GamePlayer updateGamePlayer(Long gameId, Long clubMemberId, UpdateGamePlayerDTO dto) {
        GamePlayer gamePlayer = this.getGamePlayerByGameAndMember(gameId, clubMemberId);

        List<ChipCount> existingChipCounts = gamePlayer.getChipCounts();
        existingChipCounts.clear();
        gamePlayer.setBalance(BigDecimal.ZERO);

        if (dto.initialCash() != null) {
            gamePlayer.setInitialCash(dto.initialCash());
        }

        this.addChipCountsToGamePlayer(gamePlayer, dto.chips());

        gamePlayer.recalculateBalance();
        playerRankingService.updatePlayerRanking(gamePlayer);

        return gamePlayer;
    }

    @Override
    @Transactional
    public void updateGamePlayerPayment(GamePlayer gamePlayer) {
        gamePlayerRepository.save(gamePlayer);
    }

    @Transactional
    protected void addChipCountsToGamePlayer(GamePlayer gamePlayer, List<ChipCountRequestDTO> chips) {
        for (ChipCountRequestDTO chipCountDTO : chips) {
            Chip chip = chipService.getChipById(chipCountDTO.chipId());

            // Criar relação ChipCount
            ChipCount chipCount = new ChipCount(chipCountDTO, gamePlayer, chip);

            gamePlayer.getChipCounts().add(chipCount);

            chipCountRepository.save(chipCount);
        }
    }
}
