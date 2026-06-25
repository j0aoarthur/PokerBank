package com.j0aoarthur.pokerbank.infra.validators;

import com.j0aoarthur.pokerbank.dtos.request.PaymentDTO;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.PaymentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator {

    public void validatePayment(PaymentDTO paymentDTO, GameParticipant payerGameParticipant, GameParticipant receiverGameParticipant) {
        if (payerGameParticipant.getGame().getIsFinished()) {
            // Esta validação pode ser revista. Se o jogo terminou, os pagamentos ainda podem ser feitos
            // até que todos estejam 'paid'. Depende da regra de negócio.
            // Se o jogo está 'finished' E todos estão 'paid', então sim, não há mais pagamentos.
            // Por ora, mantenho a lógica original, mas é um ponto de atenção.
            throw new PaymentException("A partida já foi finalizada.");
        }

        if (payerGameParticipant.getPaid()) {
            throw new PaymentException("O jogador pagador " + payerGameParticipant.getMember().getName() + " já quitou sua dívida neste jogo.");
        }

        if (receiverGameParticipant.getPaid()) {
            // Esta validação pode ser controversa. Um recebedor pode receber múltiplos pagamentos parciais
            // de diferentes jogadores. Ele só estará "totalmente pago" em relação a ESTA transação específica.
            // A flag 'paid' do GameParticipant indica que ELE não tem mais nada a receber NO GERAL.
            // A questão é: ele pode receber um valor mesmo que JÁ TENHA RECEBIDO TUDO? Provavelmente não.
            // A lógica original está ok: se o recebedor já teve seu crédito total satisfeito, não deveria receber mais.
            throw new PaymentException("O jogador recebedor " + receiverGameParticipant.getMember().getName() + " já recebeu todo o seu crédito neste jogo.");
        }

        if (payerGameParticipant.getPaymentSituation() != PaymentSituation.PAY) {
            throw new PaymentException("O jogador " + payerGameParticipant.getMember().getName() + " não está na situação de pagamento.");
        }

        if (receiverGameParticipant.getPaymentSituation() != PaymentSituation.RECEIVE) {
            throw new PaymentException("O jogador " + receiverGameParticipant.getMember().getName() + " não está na situação de recebimento.");
        }

        BigDecimal amountToPay = paymentDTO.amount();
        if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("O valor do pagamento deve ser positivo.");
        }

        BigDecimal payerPendingDebt = payerGameParticipant.getPendingAmount();
        BigDecimal receiverPendingCredit = receiverGameParticipant.getPendingAmount();

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