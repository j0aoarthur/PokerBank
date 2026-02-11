package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.repositories.PlayerRankingRepository;
import com.j0aoarthur.pokerbank.services.PlayerRankingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerRankingServiceImpl implements PlayerRankingService {

    private final PlayerRankingRepository playerRankingRepository;
    private final AuthContextService authContextService;

    @Override
    @Transactional
    public void updatePlayerRanking(GamePlayer gamePlayer) {
        Optional<PlayerRanking> playerRanking = playerRankingRepository.findByClubMemberId(gamePlayer.getClubMember().getId());
        if (playerRanking.isPresent()) {
            this.calculateAndSaveRanking(playerRanking.get(), gamePlayer);
        } else {
            PlayerRanking newPlayerRanking = new PlayerRanking();
            newPlayerRanking.setClubMember(gamePlayer.getClubMember());
            newPlayerRanking.setClub(gamePlayer.getClubMember().getClub());
            this.calculateAndSaveRanking(newPlayerRanking, gamePlayer);
        }

        if (!gamePlayer.getClubMember().getClub().equals(authContextService.getCurrentClub())) {
            throw new IllegalArgumentException("O jogador não pertence ao clube selecionado.");
        }
    }

    @Override
    public List<PlayerRanking> getPlayerRankings() {
        return playerRankingRepository.findAllByGamesPlayedAfterOrderByNetBalanceDesc(1);
    }

    @Override
    public List<PlayerRanking> getTopPlayers() {
        return this.getPlayerRankings().stream().limit(3).toList();
    }

    private void calculateAndSaveRanking(PlayerRanking playerRanking, GamePlayer gamePlayer) {
        playerRanking.setGamesPlayed(playerRanking.getGamesPlayed() + 1);

        BigDecimal balance = gamePlayer.getBalance();
        playerRanking.setNetBalance(playerRanking.getNetBalance().add(balance));

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            playerRanking.setTotalWon(playerRanking.getTotalWon().add(balance));
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            playerRanking.setTotalLost(playerRanking.getTotalLost().add(balance.abs()));
        }

        playerRankingRepository.save(playerRanking);

        updateRankingPositions();
    }

    private void updateRankingPositions() {
        List<PlayerRanking> rankings = this.getPlayerRankings();
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
            playerRankingRepository.save(rankings.get(i));
        }
    }
}