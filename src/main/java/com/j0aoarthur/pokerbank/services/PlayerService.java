package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.PlayerRequestDTO;
import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public Player createPlayer(PlayerRequestDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.name());
        player.setCreatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com o ID: " + id));
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersNotInGame(Long gameId) {
        return playerRepository.findPlayersNotInGame(gameId);
    }
}
