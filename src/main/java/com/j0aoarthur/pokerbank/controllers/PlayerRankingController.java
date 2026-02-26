package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.response.PlayerRankingDTO;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import com.j0aoarthur.pokerbank.security.annotations.RequiresClubContext;
import com.j0aoarthur.pokerbank.services.PlayerRankingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Player Ranking Controller", description = "Endpoints para gerenciar o ranking dos jogadores")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class PlayerRankingController {

    private final PlayerRankingService playerRankingService;

    @GetMapping
    @Operation(summary = "Retorna o ranking completo dos jogadores")
    public ResponseEntity<List<PlayerRankingDTO>> getRanking() {
        List<PlayerRanking> ranking = playerRankingService.getPlayerRankings();

        return ResponseEntity.ok(ranking.stream().map(PlayerRankingDTO::new).toList());
    }

    @GetMapping("/top")
    @Operation(summary = "Retorna os 3 melhores jogadores do ranking")
    public ResponseEntity<List<PlayerRankingDTO>> getTopPlayers() {
        List<PlayerRanking> topPlayers = playerRankingService.getTopPlayers();
        return ResponseEntity.ok(topPlayers.stream().map(PlayerRankingDTO::new).toList());
    }


}
