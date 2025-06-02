package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.entities.*;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipCountRepository;
import com.j0aoarthur.pokerbank.repositories.GamePlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GamePlayerService {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ChipService chipService;

    @Autowired
    private PlayerRankingService playerRankingService;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ChipCountRepository chipCountRepository;

    @Transactional
    public GamePlayer addPlayerToGame(GamePlayerRequestDTO dto) {
        Game game = gameService.getGameById(dto.gameId());
        Player player = playerService.getPlayerById(dto.playerId());

        // Verifica se o jogador já está na partida
        if (gamePlayerRepository.existsByGameIdAndPlayerId(game.getId(), player.getId())) {
            throw new EntityNotFoundException("Jogador já está na partida");
        }

        // Criar relação GamePlayer
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(dto, game, player));

        this.addChipCountToGamePlayer(gamePlayer, dto);
        this.countChipsAndBalance(gamePlayer);
        playerRankingService.updatePlayerRanking(gamePlayer.getPlayer().getId());
        return gamePlayer;
    }

    @Transactional
    protected void addChipCountToGamePlayer(GamePlayer gamePlayer, GamePlayerRequestDTO dto) {
        for (GamePlayerRequestDTO.ChipCountRequestDTO chipCountDTO : dto.chips()) {
            Chip chip = chipService.getChipById(chipCountDTO.chipId());

            // Criar relação ChipCount
            ChipCount chipCount = new ChipCount(chipCountDTO, gamePlayer, chip);
            chipCountRepository.save(chipCount);
        }
    }

    @Transactional
    protected void countChipsAndBalance(GamePlayer gamePlayer) {

        List<ChipCount> chipCounts = chipCountRepository.findByGamePlayerId(gamePlayer.getId());

        for (ChipCount chipCount : chipCounts) {
            gamePlayer.setBalance(gamePlayer.getBalance().add(chipCount.getChip().getValue().multiply(new BigDecimal(chipCount.getQuantity()))));
        }

        gamePlayer.setBalance(gamePlayer.getBalance().subtract(gamePlayer.getInitialCash()));

        if (gamePlayer.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            gamePlayer.setPaymentSituation(PaymentSituation.RECEIVE);
        } else if (gamePlayer.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            gamePlayer.setPaymentSituation(PaymentSituation.PAY);
        } else {
            gamePlayer.setPaid(true);
            gamePlayer.setPaymentSituation(PaymentSituation.NONE);
        }

        gamePlayerRepository.save(gamePlayer);
    }

    public GamePlayer getGamePlayer(Long gameId, Long playerId) {
        return gamePlayerRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new EntityNotFoundException("Relação GamePlayer não encontrada com o ID do jogo: " + gameId + " e ID do jogador: " + playerId));
    }

    public List<GamePlayer> getGamesByPlayer(Long playerId) {
        List<GamePlayer> games = gamePlayerRepository.findByPlayerId(playerId);
        if (games.isEmpty()) {
            throw new EntityNotFoundException("Jogador não possui partidas jogadas");
        }

        return games;
    }

    public List<GamePlayer> getGamePlayersByGame(Long gameId) {
        return gamePlayerRepository.findByGameId(gameId);
    }

    public List<GamePlayer> getGamePlayersWithBalanceAndPaymentSituation(Long gameId, PaymentSituation paymentSituation) {
        return gamePlayerRepository.findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(gameId, paymentSituation);
    }

    @Transactional
    public GamePlayer updateGamePlayer(Long gameId, Long playerId, UpdateGamePlayerDTO dto) {
        GamePlayer gamePlayer = this.getGamePlayerByGameAndPlayer(gameId, playerId);

        List<ChipCount> existingChipCounts = chipCountRepository.findByGamePlayerId(gamePlayer.getId());
        chipCountRepository.deleteAll(existingChipCounts);
        gamePlayer.setBalance(BigDecimal.ZERO);

        if (dto.initialCash() != null) {
            gamePlayer.setInitialCash(dto.initialCash());
        }

        this.addChipCountToGamePlayer(gamePlayer, dto.chips());

        this.countChipsAndBalance(gamePlayer);
        playerRankingService.updatePlayerRanking(gamePlayer.getPlayer().getId());

        return gamePlayer;
    }

    @Transactional
    public void updateGamePlayerPayment(GamePlayer gamePlayer) {
        gamePlayerRepository.save(gamePlayer);
    }
}

