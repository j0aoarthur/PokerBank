package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.PlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PlayerDTO;
import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerRequestDTO playerDTO) {
        Player createdPlayer = playerService.createPlayer(playerDTO);
        return ResponseEntity.ok(new PlayerDTO(createdPlayer));
    }

    @PostMapping("/admin")
    public ResponseEntity<PlayerDTO> createAdmin(@RequestBody PlayerRequestDTO playerDTO) {
        Player createdAdmin = playerService.createAdmin(playerDTO);
        return ResponseEntity.ok(new PlayerDTO(createdAdmin));
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/not-in-game/{gameId}")
    public ResponseEntity<List<Player>> getPlayersNotInGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(playerService.getPlayersNotInGame(gameId));
    }
}
