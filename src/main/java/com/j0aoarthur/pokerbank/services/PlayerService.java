package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.PlayerRequestDTO;
import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.entities.Role;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public Player createPlayer(PlayerRequestDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.name());
        return playerRepository.save(player);
    }

    @Transactional
    public Player createAdmin(PlayerRequestDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.name());
        player.setRole(Role.ADMIN);
        return playerRepository.save(player);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado com o ID: " + id));
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = playerRepository.findAll().stream().sorted(Comparator.comparing(Player::getName)).toList();
        if (allPlayers.isEmpty()) {
            throw new EntityNotFoundException("Nenhum jogador encontrado.");
        }
        return allPlayers;
    }

    public List<Player> getPlayersNotInGame(Long gameId) {
        return playerRepository.findPlayersNotInGameOrderByName(gameId);
    }
}
