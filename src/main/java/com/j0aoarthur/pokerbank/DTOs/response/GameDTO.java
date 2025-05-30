package com.j0aoarthur.pokerbank.DTOs.response;

import com.j0aoarthur.pokerbank.entities.Game;

import java.time.LocalDate;

public record GameDTO(Long id, LocalDate date, LocalDate dueDate, Boolean isFinished) {

    public GameDTO(Game game) {
        this(game.getId(), game.getDate(), game.getDueDate(), game.getIsFinished());
    }
}
