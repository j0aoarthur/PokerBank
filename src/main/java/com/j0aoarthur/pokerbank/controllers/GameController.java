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
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Partidas", description = "Gerenciamento de partidas de poker e seus participantes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameParticipantService gameParticipantService;

    @PostMapping
    @Operation(summary = "Cria uma nova partida", description = "A data de vencimento (dueDate) é calculada automaticamente como date + 7 dias.")
    public ResponseEntity<CreatedGameDTO> createGame(@RequestBody @Valid GameRequestDTO gameDTO) {
        Game createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(new CreatedGameDTO(createdGame));
    }

    @GetMapping
    @Operation(summary = "Lista todas as partidas", description = "Paginado, ordenado por data decrescente por padrão.")
    public ResponseEntity<Page<GameDTO>> getAllGames(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Game> games = gameService.getGames(pageable);
        return ResponseEntity.ok(games.map(GameDTO::new));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Retorna detalhes de uma partida", description = "Inclui saldo total, prêmio total, jogadores e status de finalização.")
    public ResponseEntity<GameInfoDTO> getGameInfo(
            @Parameter(description = "ID da partida", example = "1", required = true)
            @PathVariable Long gameId) {
        GameInfoDTO gameInfo = gameService.getGameInfoById(gameId);
        return ResponseEntity.ok(gameInfo);
    }

    @DeleteMapping("/{gameId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Deleta uma partida", description = "Apenas ADMIN e OWNER podem deletar partidas. A operação é irreversível.")
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "ID da partida", example = "1", required = true)
            @PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    @Operation(summary = "Lista as últimas 3 partidas", description = "Retorna as 3 partidas mais recentes do clube ativo.")
    public ResponseEntity<List<GameDTO>> getLatestGames() {
        List<Game> latestGames = gameService.getLatestGames();
        return ResponseEntity.ok(latestGames.stream().map(GameDTO::new).toList());
    }

    @PostMapping("/add-player")
    @Operation(summary = "Adiciona um jogador à partida", description = "Registra a banca inicial e as fichas do jogador na partida.")
    public ResponseEntity<CreatedGameParticipantDTO> addPlayerToGame(@RequestBody @Valid GameParticipantRequestDTO dto) {
        GameParticipant gameParticipant = gameParticipantService.addParticipantToGame(dto);
        return ResponseEntity.ok(new CreatedGameParticipantDTO(gameParticipant));
    }

    @PutMapping("/{gameId}/players/{memberId}")
    @Operation(summary = "Atualiza dados do jogador na partida", description = "Atualiza banca inicial e fichas — recalcula saldo e situação de pagamento.")
    public ResponseEntity<CreatedGameParticipantDTO> updateGameParticipant(
            @Parameter(description = "ID da partida", example = "1", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID do membro", example = "5", required = true) @PathVariable Long memberId,
            @RequestBody @Valid UpdateGameParticipantDTO dto) {
        GameParticipant updatedGameParticipant = gameParticipantService.updateGameParticipant(gameId, memberId, dto);
        return ResponseEntity.ok(new CreatedGameParticipantDTO(updatedGameParticipant));
    }

    @GetMapping("/{gameId}/participants")
    @Operation(summary = "Lista participantes da partida", description = "Retorna saldo e situação de pagamento de cada jogador.")
    public ResponseEntity<List<GameParticipantBalanceDTO>> getGameParticipantsByGame(
            @Parameter(description = "ID da partida", example = "1", required = true)
            @PathVariable Long gameId) {
        List<GameParticipant> balances = gameParticipantService.getGameParticipantsByGame(gameId);
        return ResponseEntity.ok(balances.stream().map(GameParticipantBalanceDTO::new).toList());
    }

    @GetMapping("/{gameId}/participants/{memberId}")
    @Operation(summary = "Retorna detalhes do participante na partida", description = "Inclui fichas, banca inicial, saldo e valor pendente de pagamento.")
    public ResponseEntity<GameParticipantInfoDTO> getGameParticipantByGameAndMember(
            @Parameter(description = "ID da partida", example = "1", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID do membro", example = "5", required = true) @PathVariable Long memberId) {
        GameParticipant gameParticipant = gameParticipantService.getGameParticipantByGameAndMember(gameId, memberId);
        List<ChipCount> chipCounts = gameParticipant.getChipCounts();

        GameParticipantInfoDTO gameParticipantInfo = new GameParticipantInfoDTO(gameParticipant, chipCounts.stream().map(ChipCountDTO::new).toList());
        return ResponseEntity.ok(gameParticipantInfo);
    }


}
