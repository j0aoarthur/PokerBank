package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Game;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados resumidos de uma partida")
public record GameDTO(
        @Schema(description = "ID da partida", example = "1")
        Long id,

        @Schema(description = "Data em que a partida ocorreu", example = "2025-06-30")
        LocalDate date,

        @Schema(description = "Data limite para pagamentos (date + 7 dias)", example = "2025-07-07")
        LocalDate dueDate,

        @Schema(description = "Indica se todos os pagamentos foram liquidados", example = "false")
        Boolean isFinished
) {
    public GameDTO(Game game) {
        this(game.getId(), game.getDate(), game.getDueDate(), game.getIsFinished());
    }
}
