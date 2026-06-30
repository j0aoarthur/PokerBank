package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.Game;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados da partida recém-criada")
public record CreatedGameDTO(

        @Schema(description = "Identificador único da partida", example = "1")
        Long id,

        @Schema(description = "Data em que a partida ocorreu", example = "2025-06-30")
        LocalDate date,

        @Schema(description = "Data limite para pagamentos da partida (date + 7 dias)", example = "2025-07-07")
        LocalDate dueDate,

        @Schema(description = "Indica se todos os pagamentos da partida foram liquidados", example = "false")
        Boolean isFinished

) {
    public CreatedGameDTO(Game game) {
        this(
                game.getId(),
                game.getDate(),
                game.getDueDate(),
                game.getIsFinished()
        );
    }
}
