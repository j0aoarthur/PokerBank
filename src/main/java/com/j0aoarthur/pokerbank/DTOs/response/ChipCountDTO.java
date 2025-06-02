package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.ChipCount;

import java.math.BigDecimal;

public record ChipCountDTO(Long chipId, String color, Integer quantity, BigDecimal value) {

    public ChipCountDTO(ChipCount chipCount) {
        this( chipCount.getChip().getId(), chipCount.getChip().getColor(), chipCount.getQuantity(), chipCount.getChip().getValue());
    }
}
