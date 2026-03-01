package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ClaimTokenRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.ClubMemberRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.ClubMemberDTO;
import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.services.ClubMemberService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club-members")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Club Member Controller", description = "Endpoints para gerenciar membros de um clube")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService playerService;

    @PostMapping
    @Operation(summary = "Cria um novo jogador")
    public ResponseEntity<ClubMemberDTO> createClubMember(@RequestBody ClubMemberRequestDTO clubMemberDTO) {
        ClubMember createdClubMember = playerService.createClubMember(clubMemberDTO);
        return ResponseEntity.ok(new ClubMemberDTO(createdClubMember));

    }

    @GetMapping
    @Operation(summary = "Retorna todos os jogadores")
    public ResponseEntity<List<ClubMember>> getAllClubMembers() {
        return ResponseEntity.ok(playerService.getAllClubMembers());
    }

    @GetMapping("/not-in-game/{gameId}")
    @Operation(summary = "Retorna todos os jogadores que não estão em uma partida específica")
    public ResponseEntity<List<ClubMember>> getClubMembersNotInGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(playerService.getClubMembersNotInGame(gameId));
    }

    @PostMapping("/claim")
    @Operation(summary = "Reivindica um jogador")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubMemberDTO> claimClubMember(@RequestBody @Valid ClaimTokenRequestDTO request) {
        ClubMember claimedClubMember = playerService.claimClubMember(request.claimToken());
        return ResponseEntity.ok(new ClubMemberDTO(claimedClubMember));
    }
}
