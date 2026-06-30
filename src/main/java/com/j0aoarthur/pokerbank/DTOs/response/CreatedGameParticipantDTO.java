package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados do jogador recém-adicionado ou atualizado em uma partida")
public record CreatedGameParticipantDTO(

        @Schema(description = "Identificador único do registro de participação", example = "10")
        Long id,

        @Schema(description = "ID da partida", example = "1")
        Long gameId,

        @Schema(description = "ID do membro/jogador", example = "5")
        Long memberId,

        @Schema(description = "Nome do membro/jogador", example = "Carlos")
        String memberName,

        @Schema(description = "Valor inicial da banca comprada pelo jogador", example = "100.00")
        BigDecimal initialCash,

        @Schema(description = "Saldo resultante do jogador na partida (lucro positivo, prejuízo negativo)", example = "0.00")
        BigDecimal balance,

        @Schema(description = "Situação de pagamento do jogador: PAY (deve pagar), RECEIVE (deve receber) ou NONE (zerado)")
        PaymentSituation paymentSituation,

        @Schema(description = "Indica se o débito/crédito do jogador foi totalmente liquidado", example = "false")
        Boolean paid

) {
    public CreatedGameParticipantDTO(GameParticipant gp) {
        this(
                gp.getId(),
                gp.getGame().getId(),
                gp.getMember().getId(),
                gp.getMember().getName(),
                gp.getInitialCash(),
                gp.getBalance(),
                gp.getPaymentSituation(),
                gp.getPaid()
        );
    }
}
