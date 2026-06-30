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
@Tag(name = "Ranking", description = "Estatísticas e ranking dos jogadores do clube ativo")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class MemberStatsController {

    private final MemberStatsService memberStatsService;

    @GetMapping
    @Operation(summary = "Lista estatísticas completas de todos os membros", description = "Ordenado por saldo líquido decrescente.")
    public ResponseEntity<List<MemberStatsDTO>> getAllMemberStats() {
        List<MemberStats> stats = memberStatsService.getAllMemberStats();
        return ResponseEntity.ok(stats.stream().map(MemberStatsDTO::new).toList());
    }

    @GetMapping("/top")
    @Operation(summary = "Lista o top 3 do ranking", description = "Retorna os 3 membros com maior saldo líquido acumulado.")
    public ResponseEntity<List<MemberStatsDTO>> getTopMembers() {
        List<MemberStats> topMembers = memberStatsService.getTopMembers();
        return ResponseEntity.ok(topMembers.stream().map(MemberStatsDTO::new).toList());
    }


}
