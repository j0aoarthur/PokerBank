package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import com.j0aoarthur.pokerbank.repositories.PlayerRankingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerRankingService {

    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private PlayerRankingRepository playerRankingRepository;

    @Transactional
    public void updatePlayerRanking(Long playerId) {
        List<GamePlayer> gamePlayers = gamePlayerService.getGamesByPlayer(playerId);

        int gamesPlayed = gamePlayers.size();

        Optional<PlayerRanking> existingRanking = playerRankingRepository.findByPlayerId((playerId));
        PlayerRanking playerRanking = existingRanking.orElseGet(() -> {
            PlayerRanking newRanking = new PlayerRanking();
            newRanking.setPlayer(gamePlayers.get(0).getPlayer());
            return newRanking;
        });

        BigDecimal netBalance = BigDecimal.ZERO;
        BigDecimal totalWon = BigDecimal.ZERO;
        BigDecimal totalLost = BigDecimal.ZERO;

        for (GamePlayer game : gamePlayers) {
            BigDecimal balance = game.getBalance();
            netBalance = netBalance.add(balance);

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                totalWon = totalWon.add(balance);
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                totalLost = totalLost.add(balance.abs());
            }
        }

        playerRanking.setGamesPlayed(gamesPlayed);
        playerRanking.setTotalWon(totalWon);
        playerRanking.setTotalLost(totalLost);
        playerRanking.setNetBalance(netBalance);

        playerRankingRepository.save(playerRanking);
    }

    public List<PlayerRanking> getPlayerRankings() {
        return playerRankingRepository.findAllByGamesPlayedAfter(1).stream()
                .sorted(Comparator.comparing(PlayerRanking::getNetBalance).reversed())
                .toList();
    }

    public List<PlayerRanking> getTopPlayers() {
        return this.getPlayerRankings().stream().limit(3).toList();
    }
}