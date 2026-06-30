package com.j0aoarthur.pokerbank.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GameInfoDTO(
        Long gameId,
        LocalDate date,
        LocalDate dueDate,
        BigDecimal totalBalance,
        BigDecimal totalPrize,
        Integer totalParticipants,
        Boolean isFinished,
        String observation
) {}
