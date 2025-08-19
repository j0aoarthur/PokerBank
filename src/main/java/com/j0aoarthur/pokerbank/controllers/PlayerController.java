package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.PlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PlayerDTO;
import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Player Controller", description = "Endpoints para gerenciar jogadores")
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    @Operation(summary = "Cria um novo jogador")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerRequestDTO playerDTO) {
        Player createdPlayer = playerService.createPlayer(playerDTO);
        return ResponseEntity.ok(new PlayerDTO(createdPlayer));
    }

    @PostMapping("/admin")
    @Operation(summary = "Cria um novo administrador")
    public ResponseEntity<PlayerDTO> createAdmin(@RequestBody PlayerRequestDTO playerDTO) {
        Player createdAdmin = playerService.createAdmin(playerDTO);
        return ResponseEntity.ok(new PlayerDTO(createdAdmin));
    }

    @GetMapping
    @Operation(summary = "Retorna todos os jogadores")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/not-in-game/{gameId}")
    @Operation(summary = "Retorna todos os jogadores que não estão em uma partida específica")
    public ResponseEntity<List<Player>> getPlayersNotInGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(playerService.getPlayersNotInGame(gameId));
    }
}
