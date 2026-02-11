package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.ChipCountRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.entities.*;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipCountRepository;
import com.j0aoarthur.pokerbank.repositories.GamePlayerRepository;
import com.j0aoarthur.pokerbank.repositories.GameRepository;
import com.j0aoarthur.pokerbank.services.ChipService;
import com.j0aoarthur.pokerbank.services.ClubMemberService;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.PlayerRankingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GamePlayerServiceImpl implements GamePlayerService {

    private final GameRepository gameRepository;
    private final ClubMemberService clubMemberService;
    private final ChipService chipService;
    private final PlayerRankingService playerRankingService;
    private final GamePlayerRepository gamePlayerRepository;
    private final ChipCountRepository chipCountRepository;

    @Override
    @Transactional
    public GamePlayer addPlayerToGame(GamePlayerRequestDTO dto) {
        Game game = gameRepository.findById(dto.gameId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + dto.gameId()));
        ClubMember clubMember = clubMemberService.getClubMemberById(dto.clubMemberId());

        // Verifica se o jogador já está na partida
        if (gamePlayerRepository.existsByGameIdAndClubMemberId(game.getId(), clubMember.getId())) {
            throw new IllegalArgumentException("Jogador já está na partida");
        }

        // Criar relação GamePlayer
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(dto, game, clubMember));

        this.addChipCountToGamePlayer(gamePlayer, dto.chips());
        gamePlayer.recalculateBalance();
        playerRankingService.updatePlayerRanking(gamePlayer);
        return gamePlayer;
    }

    @Override
    public GamePlayer getGamePlayer(Long gameId, Long clubMemberId) {
        return gamePlayerRepository.findByGameIdAndClubMemberId(gameId, clubMemberId)
                .orElseThrow(() -> new EntityNotFoundException("O jogador com ID: " + clubMemberId + " não está na partida com ID: " + gameId));
    }

    @Override
    public List<GamePlayer> getGamePlayersByGame(Long gameId) {
        return gamePlayerRepository.findByGameIdOrderByBalanceDesc(gameId);
    }

    @Override
    public List<GamePlayer> getGamePlayersByPlayer(Long clubMemberId) {
        List<GamePlayer> games = gamePlayerRepository.findByClubMemberId(clubMemberId);
        if (games.isEmpty()) {
            throw new EntityNotFoundException("Jogador não possui partidas jogadas");
        }

        return games;
    }

    @Override
    public List<GamePlayer> getGamePlayersWithBalanceAndPaymentSituation(Long gameId, PaymentSituation paymentSituation) {
        return gamePlayerRepository.findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(gameId, paymentSituation);
    }

    @Override
    public List<ChipCount> getChipCountsByGamePlayer(Long gamePlayerId) {
        return chipCountRepository.findByGamePlayerId(gamePlayerId).stream().filter(chipCount -> chipCount.getQuantity() > 0).toList();
    }

    @Override
    @Transactional
    public GamePlayer updateGamePlayer(Long gameId, Long clubMemberId, UpdateGamePlayerDTO dto) {
        GamePlayer gamePlayer = this.getGamePlayer(gameId, clubMemberId);

        List<ChipCount> existingChipCounts = chipCountRepository.findByGamePlayerId(gamePlayer.getId());
        chipCountRepository.deleteAll(existingChipCounts);
        gamePlayer.setBalance(BigDecimal.ZERO);

        if (dto.initialCash() != null) {
            gamePlayer.setInitialCash(dto.initialCash());
        }

        this.addChipCountToGamePlayer(gamePlayer, dto.chips());

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
    protected void addChipCountToGamePlayer(GamePlayer gamePlayer, List<ChipCountRequestDTO> chips) {
        for (ChipCountRequestDTO chipCountDTO : chips) {
            Chip chip = chipService.getChipById(chipCountDTO.chipId());

            // Criar relação ChipCount
            ChipCount chipCount = new ChipCount(chipCountDTO, gamePlayer, chip);
            chipCountRepository.save(chipCount);
        }
    }
}

