package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Saldo e situação de pagamento de um jogador em uma partida")
public record GameParticipantBalanceDTO(
        @Schema(description = "ID do membro/jogador", example = "5")
        Long memberId,

        @Schema(description = "Nome do membro/jogador", example = "Carlos")
        String memberName,

        @Schema(description = "Saldo do jogador na partida (positivo = lucro, negativo = prejuízo)", example = "-50.00")
        BigDecimal balance,

        @Schema(description = "Situação de pagamento: PAY (deve pagar), RECEIVE (deve receber) ou NONE (zerado)")
        PaymentSituation paymentSituation,

        @Schema(description = "Indica se o débito/crédito foi liquidado", example = "false")
        Boolean paid
) {
    public GameParticipantBalanceDTO(GameParticipant gameParticipant) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getBalance(),
                gameParticipant.getPaymentSituation(),
                gameParticipant.getPaid()
        );
    }
}
