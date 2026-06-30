package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.GameParticipantRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGameParticipantDTO;
import com.j0aoarthur.pokerbank.dtos.response.*;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.services.GameParticipantService;
import com.j0aoarthur.pokerbank.services.GameService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@Tag(name = "Game Controller", description = "Endpoints para gerenciar partidas e participantes em partidas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameParticipantService gameParticipantService;

    @PostMapping
    @Operation(summary = "Cria uma nova partida")
    public ResponseEntity<Game> createGame(@RequestBody @Valid GameRequestDTO gameDTO) {
        Game createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping
    @Operation(summary = "Retorna todas as partidas com paginação")
    public ResponseEntity<Page<GameDTO>> getAllGames(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Game> games = gameService.getGames(pageable);
        return ResponseEntity.ok(games.map(GameDTO::new));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Retorna informações detalhadas de uma partida específica")
    public ResponseEntity<GameInfoDTO> getGameInfo(@PathVariable Long gameId) {
        GameInfoDTO gameInfo = gameService.getGameInfoById(gameId);
        return ResponseEntity.ok(gameInfo);
    }

    @DeleteMapping("/{gameId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
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

    @PostMapping("/add-participant")
    @Operation(summary = "Adiciona um participante a uma partida")
    public ResponseEntity<GameParticipant> addParticipantToGame(@RequestBody @Valid GameParticipantRequestDTO dto) {
        GameParticipant gameParticipant = gameParticipantService.addParticipantToGame(dto);
        return ResponseEntity.ok(gameParticipant);
    }

    @PutMapping("/{gameId}/participants/{memberId}")
    @Operation(summary = "Atualiza as informações de um participante em uma partida")
    public ResponseEntity<GameParticipant> updateGameParticipant(@PathVariable Long gameId, @PathVariable Long memberId, @RequestBody @Valid UpdateGameParticipantDTO dto) {
        GameParticipant updatedGameParticipant = gameParticipantService.updateGameParticipant(gameId, memberId, dto);
        return ResponseEntity.ok(updatedGameParticipant);
    }

    @GetMapping("/{gameId}/participants")
    @Operation(summary = "Retorna todos os participantes de uma partida específica")
    public ResponseEntity<List<GameParticipantBalanceDTO>> getGameParticipantsByGame(@PathVariable Long gameId) {
        List<GameParticipant> balances = gameParticipantService.getGameParticipantsByGame(gameId);
        return ResponseEntity.ok(balances.stream().map(GameParticipantBalanceDTO::new).toList());
    }

    @GetMapping("/{gameId}/participants/{memberId}")
    @Operation(summary = "Retorna as informações de um participante específico em uma partida")
    public ResponseEntity<GameParticipantInfoDTO> getGameParticipantByGameAndMember(@PathVariable Long gameId, @PathVariable Long memberId) {
        GameParticipant gameParticipant = gameParticipantService.getGameParticipantByGameAndMember(gameId, memberId);
        List<ChipCount> chipCounts = gameParticipant.getChipCounts();

        GameParticipantInfoDTO gameParticipantInfo = new GameParticipantInfoDTO(gameParticipant, chipCounts.stream().map(ChipCountDTO::new).toList());
        return ResponseEntity.ok(gameParticipantInfo);
    }


}
