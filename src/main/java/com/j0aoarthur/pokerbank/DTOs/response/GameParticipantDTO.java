package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados de um participante com pagamentos em aberto")
public record GameParticipantDTO(
        @Schema(description = "ID do membro/jogador", example = "5")
        Long memberId,

        @Schema(description = "Nome do membro/jogador", example = "Carlos")
        String memberName,

        @Schema(description = "Saldo do jogador na partida (positivo = lucro, negativo = prejuízo)", example = "-50.00")
        BigDecimal balance,

        @Schema(description = "Indica se o débito/crédito foi liquidado", example = "false")
        Boolean paid,

        @Schema(description = "Situação de pagamento: PAY (deve pagar), RECEIVE (deve receber) ou NONE (zerado)")
        PaymentSituation paymentSituation
) {
    public GameParticipantDTO(GameParticipant gameParticipant) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getBalance(),
                gameParticipant.getPaid(),
                gameParticipant.getPaymentSituation()
        );
    }
}
