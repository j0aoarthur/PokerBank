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
@Tag(name = "Member Stats Controller", description = "Endpoints para gerenciar as estatísticas dos membros")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class MemberStatsController {

    private final MemberStatsService memberStatsService;

    @GetMapping
    @Operation(summary = "Retorna as estatísticas completas dos membros")
    public ResponseEntity<List<MemberStatsDTO>> getAllMemberStats() {
        List<MemberStats> stats = memberStatsService.getAllMemberStats();

        return ResponseEntity.ok(stats.stream().map(MemberStatsDTO::new).toList());
    }

    @GetMapping("/top")
    @Operation(summary = "Retorna os 3 melhores membros")
    public ResponseEntity<List<MemberStatsDTO>> getTopMembers() {
        List<MemberStats> topMembers = memberStatsService.getTopMembers();
        return ResponseEntity.ok(topMembers.stream().map(MemberStatsDTO::new).toList());
    }


}
