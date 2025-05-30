package com.j0aoarthur.pokerbank.infra.validators;

import com.j0aoarthur.pokerbank.DTOs.request.PaymentDTO;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.entities.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.PaymentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator {

    public void validatePayment(PaymentDTO paymentDTO, GamePlayer payerGamePlayer, GamePlayer receiverGamePlayer) {
        if (payerGamePlayer.getGame().getIsFinished()) {
            // Esta validação pode ser revista. Se o jogo terminou, os pagamentos ainda podem ser feitos
            // até que todos estejam 'paid'. Depende da regra de negócio.
            // Se o jogo está 'finished' E todos estão 'paid', então sim, não há mais pagamentos.
            // Por ora, mantenho a lógica original, mas é um ponto de atenção.
            throw new PaymentException("A partida já foi finalizada.");
        }

        if (payerGamePlayer.getPaid()) {
            throw new PaymentException("O jogador pagador " + payerGamePlayer.getPlayer().getName() + " já quitou sua dívida neste jogo.");
        }

        if (receiverGamePlayer.getPaid()) {
            // Esta validação pode ser controversa. Um recebedor pode receber múltiplos pagamentos parciais
            // de diferentes jogadores. Ele só estará "totalmente pago" em relação a ESTA transação específica.
            // A flag 'paid' do GamePlayer indica que ELE não tem mais nada a receber NO GERAL.
            // A questão é: ele pode receber um valor mesmo que JÁ TENHA RECEBIDO TUDO? Provavelmente não.
            // A lógica original está ok: se o recebedor já teve seu crédito total satisfeito, não deveria receber mais.
            throw new PaymentException("O jogador recebedor " + receiverGamePlayer.getPlayer().getName() + " já recebeu todo o seu crédito neste jogo.");
        }

        if (payerGamePlayer.getPaymentSituation() != PaymentSituation.PAY) {
            throw new PaymentException("O jogador " + payerGamePlayer.getPlayer().getName() + " não está na situação de pagamento.");
        }

        if (receiverGamePlayer.getPaymentSituation() != PaymentSituation.RECEIVE) {
            throw new PaymentException("O jogador " + receiverGamePlayer.getPlayer().getName() + " não está na situação de recebimento.");
        }

        BigDecimal amountToPay = paymentDTO.amount();
        if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("O valor do pagamento deve ser positivo.");
        }

        BigDecimal payerPendingDebt = payerGamePlayer.getPendingAmount();
        BigDecimal receiverPendingCredit = receiverGamePlayer.getPendingAmount();

        // Verifica se o valor do pagamento excede o que o recebedor ainda tem para receber
        if (receiverPendingCredit.compareTo(amountToPay) < 0) {
            throw new PaymentException("O valor do pagamento (R$" + amountToPay + ") é maior que o crédito pendente do recebedor (R$" + receiverPendingCredit + ").");
        }

        // Verifica se o valor do pagamento excede o que o pagador ainda deve
        if (payerPendingDebt.compareTo(amountToPay) < 0) {
            throw new PaymentException("O valor do pagamento (R$" + amountToPay + ") é maior que a dívida pendente do pagador (R$" + payerPendingDebt + ").");
        }
    }
}