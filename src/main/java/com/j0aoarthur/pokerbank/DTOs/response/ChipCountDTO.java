package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.ChipCount;

public record ChipCountDTO(String color, Integer quantity) {

    public ChipCountDTO(ChipCount chipCount) {
        this(chipCount.getChip().getColor(), chipCount.getQuantity());
    }
}
