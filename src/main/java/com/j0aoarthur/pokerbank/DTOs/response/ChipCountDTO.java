package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.ChipCount;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Quantidade de fichas de uma determinada cor que um jogador possui")
public record ChipCountDTO(
        @Schema(description = "ID da ficha", example = "1")
        Long chipId,

        @Schema(description = "Nome da cor da ficha", example = "Vermelha")
        String color,

        @Schema(description = "Quantidade desta ficha com o jogador", example = "10")
        Integer quantity,

        @Schema(description = "Valor monetário unitário da ficha", example = "5.00")
        BigDecimal value
) {
    public ChipCountDTO(ChipCount chipCount) {
        this(chipCount.getChip().getId(), chipCount.getChip().getColor(), chipCount.getQuantity(), chipCount.getChip().getValue());
    }
}
