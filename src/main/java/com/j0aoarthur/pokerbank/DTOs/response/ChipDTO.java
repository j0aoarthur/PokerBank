package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.Chip;

import java.math.BigDecimal;

public record ChipDTO(Long id, String color, String colorHex, BigDecimal value) {
    public ChipDTO(Chip chip) {
        this(chip.getId(), chip.getColor(), chip.getColorHex(), chip.getValue());
    }
}
