package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Informações detalhadas de um participante em uma partida, incluindo fichas")
public record GameParticipantInfoDTO(
        @Schema(description = "ID do membro/jogador", example = "5")
        Long memberId,

        @Schema(description = "Nome do membro/jogador", example = "Carlos")
        String memberName,

        @Schema(description = "Valor inicial da banca comprada pelo jogador", example = "100.00")
        BigDecimal initialCash,

        @Schema(description = "Saldo resultante (positivo = lucro, negativo = prejuízo)", example = "-50.00")
        BigDecimal balance,

        @Schema(description = "Valor pendente de pagamento ou recebimento", example = "50.00")
        BigDecimal pendingAmount,

        @Schema(description = "Lista de fichas do jogador na partida")
        List<ChipCountDTO> chips
) {
    public GameParticipantInfoDTO(GameParticipant gameParticipant, List<ChipCountDTO> chips) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getInitialCash(),
                gameParticipant.getBalance(),
                gameParticipant.getPendingAmount(),
                chips
        );
    }
}
