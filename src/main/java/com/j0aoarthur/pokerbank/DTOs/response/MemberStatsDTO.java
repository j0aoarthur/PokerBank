package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.MemberStats;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Estatísticas acumuladas de um membro no clube")
public record MemberStatsDTO(
        @Schema(description = "ID do membro", example = "5")
        Long memberId,

        @Schema(description = "Posição no ranking geral do clube", example = "1")
        Integer rank,

        @Schema(description = "Nome do membro", example = "Carlos")
        String memberName,

        @Schema(description = "Número total de partidas jogadas", example = "12")
        int gamesPlayed,

        @Schema(description = "Saldo líquido acumulado (lucro - prejuízo ao longo de todas as partidas)", example = "350.00")
        BigDecimal netBalance
) {
    public MemberStatsDTO(MemberStats pr) {
        this(
                pr.getMember().getId(),
                pr.getRank(),
                pr.getMember().getName(),
                pr.getGamesPlayed(),
                pr.getNetBalance()
        );
    }
}
