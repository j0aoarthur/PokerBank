package com.j0aoarthur.pokerbank.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Informações completas de uma partida, incluindo totais financeiros")
public record GameInfoDTO(
        @Schema(description = "ID da partida", example = "1")
        Long gameId,

        @Schema(description = "Data em que a partida ocorreu", example = "2025-06-30")
        LocalDate date,

        @Schema(description = "Data limite para pagamentos", example = "2025-07-07")
        LocalDate dueDate,

        @Schema(description = "Soma dos saldos de todos os jogadores", example = "-200.00")
        BigDecimal totalBalance,

        @Schema(description = "Valor total em prêmio gerado pela partida", example = "500.00")
        BigDecimal totalPrize,

        @Schema(description = "Número total de participantes", example = "6")
        Integer totalParticipants,

        @Schema(description = "Indica se todos os pagamentos foram liquidados", example = "false")
        Boolean isFinished,

        @Schema(description = "Observação livre sobre a partida", example = "Partida especial de aniversário")
        String observation
) {}
