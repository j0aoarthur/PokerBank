package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.PaymentDTO;
import com.j0aoarthur.pokerbank.dtos.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.validators.PaymentValidator;
import com.j0aoarthur.pokerbank.services.GameParticipantService;
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

    private final GameParticipantService gameParticipantService;
    private final GameService gameService;
    private final PaymentValidator paymentValidator;

    // Classe auxiliar interna para a lógica de sugestão de pagamento
    @Data
    @AllArgsConstructor
    private static class MemberPaymentData {
        private Long memberId;
        private String memberName;
        private BigDecimal pendingAmount;
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestion(Long gameId) {
        List<GameParticipant> gamePayers = gameParticipantService
                .getUnpaidGameParticipantsByPaymentSituation(gameId, PaymentSituation.PAY)
                .stream()
                .filter(gp -> !gp.getPaid() && gp.getPendingAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        List<GameParticipant> gameReceivers = gameParticipantService
                .getUnpaidGameParticipantsByPaymentSituation(gameId, PaymentSituation.RECEIVE)
                .stream()
                .filter(gp -> !gp.getPaid() && gp.getPendingAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        List<MemberPaymentData> payersData = gamePayers.stream()
                .map(gp -> new MemberPaymentData(
                        gp.getMember().getId(),
                        gp.getMember().getName(),
                        gp.getPendingAmount()))
                .sorted(Comparator.comparing(MemberPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<MemberPaymentData> receiversData = gameReceivers.stream()
                .map(gp -> new MemberPaymentData(
                        gp.getMember().getId(),
                        gp.getMember().getName(),
                        gp.getPendingAmount()))
                .sorted(Comparator.comparing(MemberPaymentData::getPendingAmount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PaymentSuggestionDTO> suggestions = new ArrayList<>();

        while (!payersData.isEmpty() && !receiversData.isEmpty()) {
            MemberPaymentData currentPayer = payersData.get(0);
            MemberPaymentData currentReceiver = receiversData.get(0);

            BigDecimal payerDebt = currentPayer.getPendingAmount();
            BigDecimal receiverCredit = currentReceiver.getPendingAmount();

            BigDecimal amountToTransfer = payerDebt.min(receiverCredit);

            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new PaymentSuggestionDTO(
                        currentPayer.getMemberId(),
                        currentPayer.getMemberName(),
                        currentReceiver.getMemberId(),
                        currentReceiver.getMemberName(),
                        amountToTransfer));

                currentPayer.setPendingAmount(payerDebt.subtract(amountToTransfer));
                currentReceiver.setPendingAmount(receiverCredit.subtract(amountToTransfer));
            }

            if (currentPayer.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                payersData.remove(0);
            }

            if (currentReceiver.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                receiversData.remove(0);
            }

            payersData.sort(Comparator.comparing(MemberPaymentData::getPendingAmount).reversed());
            receiversData.sort(Comparator.comparing(MemberPaymentData::getPendingAmount).reversed());
        }
        return suggestions;
    }

    @Transactional
    public void payPlayer(PaymentDTO paymentDTO) {
        GameParticipant payerGameParticipant = gameParticipantService.getGameParticipantByGameAndMember(paymentDTO.gameId(), paymentDTO.payerId());
        GameParticipant receiverGameParticipant = gameParticipantService.getGameParticipantByGameAndMember(paymentDTO.gameId(), paymentDTO.receiverId());

        paymentValidator.validatePayment(paymentDTO, payerGameParticipant, receiverGameParticipant);

        // Atualiza o valor liquidado para o pagador e recebedor
        BigDecimal paymentAmount = paymentDTO.amount();
        payerGameParticipant.setSettledAmount(
                (payerGameParticipant.getSettledAmount() == null ? BigDecimal.ZERO : payerGameParticipant.getSettledAmount())
                        .add(paymentAmount));
        receiverGameParticipant.setSettledAmount((receiverGameParticipant.getSettledAmount() == null ? BigDecimal.ZERO
                : receiverGameParticipant.getSettledAmount()).add(paymentAmount));

        // Verifica se o pagador quitou sua dívida (balance é negativo)
        if (payerGameParticipant.getSettledAmount().compareTo(payerGameParticipant.getBalance().abs()) >= 0) {
            payerGameParticipant.setPaid(true);
        }

        // Verifica se o recebedor teve seu crédito totalmente atendido (balance é
        // positivo)
        if (receiverGameParticipant.getSettledAmount().compareTo(receiverGameParticipant.getBalance()) >= 0) {
            receiverGameParticipant.setPaid(true);
        }

        gameParticipantService.updateGameParticipantPayment(payerGameParticipant);
        gameParticipantService.updateGameParticipantPayment(receiverGameParticipant);
        gameService.checkGameFinished(payerGameParticipant.getGame().getId());
    }

    public List<PaymentSuggestionDTO> getPaymentSuggestionsByMember(Long gameId, Long memberId) {
        return this.getPaymentSuggestion(gameId)
                .stream().filter(payment -> payment.payerId().equals(memberId)).toList();
    }

    public List<Game> getExpiredGames() {
        List<Game> allGames = gameService.getAllGames();
        return allGames.stream()
                .filter(game -> !game.getDueDate().isAfter(java.time.LocalDate.now()))
                .filter(game -> !game.getIsFinished())
                .sorted(Comparator.comparing(Game::getDueDate).reversed())
                .collect(Collectors.toList());
    }

    public List<GameParticipant> getExpiredPaymentsByMember(Long memberId) {
        List<Game> expiredGames = this.getExpiredGames();
        List<GameParticipant> gamesByPlayer = gameParticipantService.getGameParticipantsByMember(memberId);
        return gamesByPlayer.stream()
                .filter(gameParticipant -> expiredGames.stream()
                        .anyMatch(game -> game.getId().equals(gameParticipant.getGame().getId()) && !gameParticipant.getPaid()))
                .sorted(Comparator.comparing((GameParticipant gp) -> gp.getGame().getDueDate()).reversed()) // Melhor usar
                                                                                                       // lambda tipado
                .collect(Collectors.toList());
    }
}