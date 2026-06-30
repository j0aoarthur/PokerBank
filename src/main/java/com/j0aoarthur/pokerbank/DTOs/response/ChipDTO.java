package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Chip;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados de uma ficha do clube")
public record ChipDTO(
        @Schema(description = "ID da ficha", example = "1")
        Long id,

        @Schema(description = "Nome da cor da ficha", example = "Vermelha")
        String color,

        @Schema(description = "Código hexadecimal da cor", example = "#FF0000")
        String colorHex,

        @Schema(description = "Valor monetário unitário da ficha", example = "5.00")
        BigDecimal value
) {
    public ChipDTO(Chip chip) {
        this(chip.getId(), chip.getColor(), chip.getColorHex(), chip.getValue());
    }
}
