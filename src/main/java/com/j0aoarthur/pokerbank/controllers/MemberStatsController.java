package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.response.MemberStatsDTO;
import com.j0aoarthur.pokerbank.entities.MemberStats;
import com.j0aoarthur.pokerbank.services.MemberStatsService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
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
public class MemberStatsController {

    private final MemberStatsService memberStatsService;

    @GetMapping
    @Operation(summary = "Retorna o ranking completo dos jogadores")
    public ResponseEntity<List<MemberStatsDTO>> getRanking() {
        List<MemberStats> ranking = memberStatsService.getMemberStatss();

        return ResponseEntity.ok(ranking.stream().map(MemberStatsDTO::new).toList());
    }

    @GetMapping("/top")
    @Operation(summary = "Retorna os 3 melhores jogadores do ranking")
    public ResponseEntity<List<MemberStatsDTO>> getTopPlayers() {
        List<MemberStats> topPlayers = memberStatsService.getTopPlayers();
        return ResponseEntity.ok(topPlayers.stream().map(MemberStatsDTO::new).toList());
    }


}
