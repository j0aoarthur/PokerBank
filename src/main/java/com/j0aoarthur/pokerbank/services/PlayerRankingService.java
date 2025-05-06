package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import com.j0aoarthur.pokerbank.repositories.PlayerRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class PlayerRankingService {

    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private PlayerRankingRepository playerRankingRepository;

    public void updatePlayerRanking(Long playerId) {
        List<GamePlayer> gamePlayers = gamePlayerService.getGamesByPlayer(playerId);

        String name = gamePlayers.get(0).getPlayer().getName();
        int gamesPlayed = gamePlayers.size();

        List<GamePlayer> games = gamePlayers.stream()
                .filter(g -> g.getPlayer().getName().equals(name))
                .toList();

        PlayerRanking playerRanking = playerRankingRepository.findById(playerId).orElseGet(PlayerRanking::new);

        games.stream().map(GamePlayer::getBalance).forEach(balance -> {
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                playerRanking.setTotalWon(playerRanking.getTotalWon().add(balance));
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                playerRanking.setTotalLost(playerRanking.getTotalLost().add(balance.abs()));
            }
        });

        playerRanking.setPlayer(games.get(0).getPlayer());
        playerRanking.setGamesPlayed(gamesPlayed);
        playerRanking.setNetBalance(playerRanking.getTotalWon().subtract(playerRanking.getTotalLost()));

        playerRankingRepository.save(playerRanking);

    }

    public List<PlayerRanking> getPlayerRankings() {
        return playerRankingRepository.findAll().stream()
                .sorted(Comparator.comparing(PlayerRanking::getNetBalance).reversed())
                .toList();
    }

    public List<PlayerRanking> getTopPlayers(int limit) {
        return playerRankingRepository.findAll().stream()
                .sorted(Comparator.comparing(PlayerRanking::getNetBalance).reversed())
                .limit(limit)
                .toList();
    }
}