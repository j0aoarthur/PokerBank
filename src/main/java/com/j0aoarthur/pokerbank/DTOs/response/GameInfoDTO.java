package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.Game;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GameInfoDTO(
        Long gameId,
        LocalDate date,
        BigDecimal totalBalance,
        BigDecimal totalPrize,
        Integer totalPlayers,
        Boolean isFinished,
        String observation
) {}
