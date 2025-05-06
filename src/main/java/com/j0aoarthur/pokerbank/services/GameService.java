package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // Criar nova partida
    public Game createGame(GameRequestDTO dto) {
        Game game = new Game();
        game.setDate(dto.date());
        return gameRepository.save(game);
    }

    // Buscar partida por ID
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    // Listar todas as partidas
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Game> getLatestGames() {
        return gameRepository.findTop3ByOrderByDateDesc();
    }
}

