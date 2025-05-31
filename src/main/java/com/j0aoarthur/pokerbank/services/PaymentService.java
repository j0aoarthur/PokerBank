package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.PaymentDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.validators.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private GameService gameService;

    @Autowired
    private PaymentValidator paymentValidator;

    // Classe auxiliar interna para a lógica de sugestão de pagamento
    @Data
    @AllArgsConstructor
    private static class PlayerPaymentData {
        private Long playerId;
        private String playerName;
        private BigDecimal pendingAmount;
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestion(Long gameId) {
        List<GamePlayer> gamePayers = gamePlayerService.getGamePlayersWithBalanceAndPaymentSituation(gameId, PaymentSituation.PAY)
                .stream()
                .filter(gp -> !gp.getPaid() && gp.getPendingAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        List<GamePlayer> gameReceivers = gamePlayerService.getGamePlayersWithBalanceAndPaymentSituation(gameId, PaymentSituation.RECEIVE)
                .stream()
                .filter(gp -> !gp.getPaid() && gp.getPendingAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        List<PlayerPaymentData> payersData = gamePayers.stream()
                .map(gp -> new PlayerPaymentData(
                        gp.getPlayer().getId(),
                        gp.getPlayer().getName(),
                        gp.getPendingAmount()
                ))
                .sorted(Comparator.comparing(PlayerPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PlayerPaymentData> receiversData = gameReceivers.stream()
                .map(gp -> new PlayerPaymentData(
                        gp.getPlayer().getId(),
                        gp.getPlayer().getName(),
                        gp.getPendingAmount()
                ))
                .sorted(Comparator.comparing(PlayerPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PaymentSuggestionDTO> suggestions = new ArrayList<>();

        while (!payersData.isEmpty() && !receiversData.isEmpty()) {
            PlayerPaymentData currentPayer = payersData.get(0);
            PlayerPaymentData currentReceiver = receiversData.get(0);

            BigDecimal payerDebt = currentPayer.getPendingAmount();
            BigDecimal receiverCredit = currentReceiver.getPendingAmount();

            BigDecimal amountToTransfer = payerDebt.min(receiverCredit);

            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new PaymentSuggestionDTO(
                        currentPayer.getPlayerId(),
                        currentPayer.getPlayerName(),
                        currentReceiver.getPlayerId(),
                        currentReceiver.getPlayerName(),
                        amountToTransfer
                ));

                currentPayer.setPendingAmount(payerDebt.subtract(amountToTransfer));
                currentReceiver.setPendingAmount(receiverCredit.subtract(amountToTransfer));
            }

            if (currentPayer.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                payersData.remove(0);
            }

            if (currentReceiver.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                receiversData.remove(0);
            }
        }
        return suggestions;
    }

    @Transactional
    public void payPlayer(PaymentDTO paymentDTO) {
        GamePlayer payerGamePlayer = gamePlayerService.getGamePlayer(paymentDTO.gameId(), paymentDTO.payerId());
        GamePlayer receiverGamePlayer = gamePlayerService.getGamePlayer(paymentDTO.gameId(), paymentDTO.receiverId());

        paymentValidator.validatePayment(paymentDTO, payerGamePlayer, receiverGamePlayer);

        // Atualiza o valor liquidado para o pagador e recebedor
        BigDecimal paymentAmount = paymentDTO.amount();
        payerGamePlayer.setSettledAmount(payerGamePlayer.getSettledAmount().add(paymentAmount));
        receiverGamePlayer.setSettledAmount(receiverGamePlayer.getSettledAmount().add(paymentAmount));

        // Verifica se o pagador quitou sua dívida (balance é negativo)
        if (payerGamePlayer.getSettledAmount().compareTo(payerGamePlayer.getBalance().abs()) >= 0) {
            payerGamePlayer.setPaid(true);
        }

        // Verifica se o recebedor teve seu crédito totalmente atendido (balance é positivo)
        if (receiverGamePlayer.getSettledAmount().compareTo(receiverGamePlayer.getBalance()) >= 0) {
            receiverGamePlayer.setPaid(true);
        }

        gamePlayerService.updateGamePlayer(payerGamePlayer);
        gamePlayerService.updateGamePlayer(receiverGamePlayer);
        gameService.checkGameFinished(payerGamePlayer.getGame().getId());
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestionsByPlayer(Long gameId, Long playerId) {
        return this.getPaymentSuggestion(gameId)
                .stream().filter(payment -> payment.payerId().equals(playerId)).toList();
    }

    public List<Game> getExpiredGames() {
        List<Game> allGames = gameService.getAllGames();
        return allGames.stream()
                .filter(game -> game.getDueDate().isBefore(java.time.LocalDate.now()))
                .filter(game -> !game.getIsFinished())
                .sorted(Comparator.comparing(Game::getDueDate).reversed())
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getExpiredPaymentsByPlayer(Long playerId) {
        List<Game> expiredGames = this.getExpiredGames();
        List<GamePlayer> gamesByPlayer = gamePlayerService.getGamesByPlayer(playerId);
        return gamesByPlayer.stream()
                .filter(gamePlayer -> expiredGames.stream()
                        .anyMatch(game -> game.getId().equals(gamePlayer.getGame().getId()) && !gamePlayer.getPaid()))
                .sorted(Comparator.comparing((GamePlayer gp) -> gp.getGame().getDueDate()).reversed()) // Melhor usar lambda tipado
                .collect(Collectors.toList());
    }
}