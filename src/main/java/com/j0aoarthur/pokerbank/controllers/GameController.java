package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.DTOs.response.*;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@Tag(name = "Game Controller", description = "Endpoints para gerenciar partidas e jogadores em partidas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerService gamePlayerService;

    @PostMapping
    @Operation(summary = "Cria uma nova partida")
    public ResponseEntity<Game> createGame(@RequestBody @Valid GameRequestDTO gameDTO) {
        Game createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping
    @Operation(summary = "Retorna todas as partidas com paginação")
    public ResponseEntity<Page<GameDTO>> getAllGames(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Game> games = gameService.getAllGames(pageable);
        return ResponseEntity.ok(games.map(GameDTO::new));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Retorna informações detalhadas de uma partida específica")
    public ResponseEntity<GameInfoDTO> getGameInfo(@PathVariable Long gameId) {
        GameInfoDTO gameInfo = gameService.getGameInfo(gameId);
        return ResponseEntity.ok(gameInfo);
    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "Deleta uma partida específica")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    @Operation(summary = "Retorna as últimas 3 partidas criadas")
    public ResponseEntity<List<GameDTO>> getLatestGames() {
        List<Game> latestGames = gameService.getLatestGames();
        return ResponseEntity.ok(latestGames.stream().map(GameDTO::new).toList());
    }

    @PostMapping("/add-player")
    @Operation(summary = "Adiciona um jogador a uma partida")
    public ResponseEntity<GamePlayer> addPlayerToGame(@RequestBody @Valid GamePlayerRequestDTO dto) {
        GamePlayer gamePlayer = gamePlayerService.addPlayerToGame(dto);
        return ResponseEntity.ok(gamePlayer);
    }

    @PutMapping("/{gameId}/players/{playerId}")
    @Operation(summary = "Atualiza as informações de um jogador em uma partida")
    public ResponseEntity<GamePlayer> updateGamePlayer(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody @Valid UpdateGamePlayerDTO dto) {
        GamePlayer updatedGamePlayer = gamePlayerService.updateGamePlayer(gameId, playerId, dto);
        return ResponseEntity.ok(updatedGamePlayer);
    }

    @GetMapping("/{gameId}/players")
    @Operation(summary = "Retorna todos os jogadores de uma partida específica")
    public ResponseEntity<List<GamePlayerBalanceDTO>> getGamePlayersByGame(@PathVariable Long gameId) {
        List<GamePlayer> balances = gamePlayerService.getGamePlayersByGame(gameId);
        return ResponseEntity.ok(balances.stream().map(GamePlayerBalanceDTO::new).toList());
    }

    @GetMapping("/{gameId}/players/{playerId}")
    @Operation(summary = "Retorna as informações de um jogador específico em uma partida")
    public ResponseEntity<GamePlayerInfoDTO> getGamePlayerByGameAndPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerByGameAndPlayer(gameId, playerId);
        List<ChipCount> chipCounts = gamePlayerService.getChipCountsByGamePlayer(gamePlayer.getId());

        GamePlayerInfoDTO gamePlayerInfo = new GamePlayerInfoDTO(gamePlayer, chipCounts.stream().map(ChipCountDTO::new).toList());
        return ResponseEntity.ok(gamePlayerInfo);
    }


}
