package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.PaymentDTO;
import com.j0aoarthur.pokerbank.dtos.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.validators.PaymentValidator;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final GamePlayerService gamePlayerService;
    private final GameService gameService;
    private final PaymentValidator paymentValidator;

    // Classe auxiliar interna para a lógica de sugestão de pagamento
    @Data
    @AllArgsConstructor
    private static class ClubMemberPaymentData {
        private Long clubMemberId;
        private String clubMemberName;
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

        List<ClubMemberPaymentData> payersData = gamePayers.stream()
                .map(gp -> new ClubMemberPaymentData(
                        gp.getClubMember().getId(),
                        gp.getClubMember().getName(),
                        gp.getPendingAmount()
                ))
                .sorted(Comparator.comparing(ClubMemberPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<ClubMemberPaymentData> receiversData = gameReceivers.stream()
                .map(gp -> new ClubMemberPaymentData(
                        gp.getClubMember().getId(),
                        gp.getClubMember().getName(),
                        gp.getPendingAmount()
                ))
                .sorted(Comparator.comparing(ClubMemberPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PaymentSuggestionDTO> suggestions = new ArrayList<>();

        while (!payersData.isEmpty() && !receiversData.isEmpty()) {
            ClubMemberPaymentData currentPayer = payersData.get(0);
            ClubMemberPaymentData currentReceiver = receiversData.get(0);

            BigDecimal payerDebt = currentPayer.getPendingAmount();
            BigDecimal receiverCredit = currentReceiver.getPendingAmount();

            BigDecimal amountToTransfer = payerDebt.min(receiverCredit);

            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new PaymentSuggestionDTO(
                        currentPayer.getClubMemberId(),
                        currentPayer.getClubMemberName(),
                        currentReceiver.getClubMemberId(),
                        currentReceiver.getClubMemberName(),
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

            payersData.sort(Comparator.comparing(ClubMemberPaymentData::getPendingAmount).reversed());
            receiversData.sort(Comparator.comparing(ClubMemberPaymentData::getPendingAmount).reversed());
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
        payerGamePlayer.setSettledAmount((payerGamePlayer.getSettledAmount() == null ? BigDecimal.ZERO : payerGamePlayer.getSettledAmount()).add(paymentAmount));
        receiverGamePlayer.setSettledAmount((receiverGamePlayer.getSettledAmount() == null ? BigDecimal.ZERO : receiverGamePlayer.getSettledAmount()).add(paymentAmount));

        // Verifica se o pagador quitou sua dívida (balance é negativo)
        if (payerGamePlayer.getSettledAmount().compareTo(payerGamePlayer.getBalance().abs()) >= 0) {
            payerGamePlayer.setPaid(true);
        }

        // Verifica se o recebedor teve seu crédito totalmente atendido (balance é positivo)
        if (receiverGamePlayer.getSettledAmount().compareTo(receiverGamePlayer.getBalance()) >= 0) {
            receiverGamePlayer.setPaid(true);
        }

        gamePlayerService.updateGamePlayerPayment(payerGamePlayer);
        gamePlayerService.updateGamePlayerPayment(receiverGamePlayer);
        gameService.checkGameFinished(payerGamePlayer.getGame().getId());
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestionsByClubMember(Long gameId, Long clubMemberId) {
        return this.getPaymentSuggestion(gameId)
                .stream().filter(payment -> payment.payerId().equals(clubMemberId)).toList();
    }

    public List<Game> getExpiredGames() {
        List<Game> allGames = gameService.getAllGames();
        return allGames.stream()
                .filter(game -> !game.getDueDate().isAfter(java.time.LocalDate.now()))
                .filter(game -> !game.getIsFinished())
                .sorted(Comparator.comparing(Game::getDueDate).reversed())
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getExpiredPaymentsByClubMember(Long clubMemberId) {
        List<Game> expiredGames = this.getExpiredGames();
        List<GamePlayer> gamesByPlayer = gamePlayerService.getGamePlayersByPlayer(clubMemberId);
        return gamesByPlayer.stream()
                .filter(gamePlayer -> expiredGames.stream()
                        .anyMatch(game -> game.getId().equals(gamePlayer.getGame().getId()) && !gamePlayer.getPaid()))
                .sorted(Comparator.comparing((GamePlayer gp) -> gp.getGame().getDueDate()).reversed()) // Melhor usar lambda tipado
                .collect(Collectors.toList());
    }
}