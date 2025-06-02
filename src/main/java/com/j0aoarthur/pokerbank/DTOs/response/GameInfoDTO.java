package com.j0aoarthur.pokerbank.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GameInfoDTO(
        Long gameId,
        LocalDate date,
        LocalDate dueDate,
        BigDecimal totalBalance,
        BigDecimal totalPrize,
        Integer totalPlayers,
        Boolean isFinished,
        String observation
) {}
