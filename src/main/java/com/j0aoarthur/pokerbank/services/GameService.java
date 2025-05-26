package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerService gamePlayerService;

    // Criar nova partida
    public Game createGame(GameRequestDTO dto) {
        Game game = new Game();
        game.setDate(dto.date());
        return gameRepository.save(game);
    }

    // Buscar partida por ID
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    // Listar todas as partidas
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Game> getLatestGames() {
        return gameRepository.findTop3ByOrderByDateDesc();
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestion(Long gameId) {
        List<GamePlayer> initialPayers = gamePlayerService.getGamePlayersWithBalanceAndPaymentSituation(gameId, PaymentSituation.PAY);
        List<GamePlayer> initialReceivers = gamePlayerService.getGamePlayersWithBalanceAndPaymentSituation(gameId, PaymentSituation.RECEIVE);

        List<GamePlayer> payers = initialPayers.stream()
                .map(gp -> {
                    gp.setBalance(gp.getBalance().abs());
                    return gp;
                })
                .filter(gp -> gp.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(GamePlayer::getBalance).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<GamePlayer> receivers = initialReceivers.stream()
                .filter(gp -> gp.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(GamePlayer::getBalance).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PaymentSuggestionDTO> suggestions = new ArrayList<>();

        while (!payers.isEmpty() && !receivers.isEmpty()) {
            GamePlayer currentPayer = payers.get(0);
            GamePlayer currentReceiver = receivers.get(0);

            BigDecimal payerDebt = currentPayer.getBalance();
            BigDecimal receiverCredit = currentReceiver.getBalance();

            BigDecimal amountToTransfer = payerDebt.min(receiverCredit);

            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new PaymentSuggestionDTO(
                        currentPayer.getPlayer().getName(),
                        currentReceiver.getPlayer().getName(),
                        amountToTransfer
                ));

                currentPayer.setBalance(payerDebt.subtract(amountToTransfer));
                currentReceiver.setBalance(receiverCredit.subtract(amountToTransfer));
            }

            if (currentPayer.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                payers.remove(0);
            }

            if (currentReceiver.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                receivers.remove(0);
            }
        }
        return suggestions;
    }

    public GameInfoDTO getGameInfo(Long id) {
        Game game = this.getGameById(id);

        List<GamePlayer> gamePlayersWithBalance = gamePlayerService.getGamePlayersWithBalance(id);

        Integer totalPlayers = gamePlayersWithBalance.size();

        BigDecimal totalBalance = BigDecimal.ZERO;
        for (GamePlayer gamePlayer : gamePlayersWithBalance) {
            totalBalance = totalBalance.add(gamePlayer.getBalance());
        }
        String observation;

        BigDecimal totalPrize = BigDecimal.ZERO;
        for (GamePlayer gamePlayer : gamePlayersWithBalance) {
            totalPrize = totalPrize.add(gamePlayer.getInitialCash());
        }

        if (totalBalance.compareTo(BigDecimal.ZERO) == 0) {
            observation = "O saldo do jogo está correto.";
        } else {
            observation = "O saldo do jogo não está correto";
        }

        return new GameInfoDTO(
                game.getId(),
                game.getDate(),
                totalBalance,
                totalPrize,
                totalPlayers,
                game.getIsFinished(),
                observation
        );


    }
}

