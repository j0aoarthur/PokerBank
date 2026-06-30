package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ClaimTokenRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.MemberDTO;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.services.MemberService;
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
@RequestMapping("/members")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Club Member Controller", description = "Endpoints para gerenciar membros de um clube")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Cria um novo membro")
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberRequestDTO memberDTO) {
        Member createdMember = memberService.createMember(memberDTO);
        return ResponseEntity.ok(new MemberDTO(createdMember));
    }

    @GetMapping
    @Operation(summary = "Retorna todos os membros")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/not-in-game/{gameId}")
    @Operation(summary = "Retorna todos os membros que não estão em uma partida específica")
    public ResponseEntity<List<Member>> getMembersNotInGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(memberService.getMembersNotInGame(gameId));
    }

    @PostMapping("/claim")
    @Operation(summary = "Reivindica um membro do clube")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberDTO> claimMember(@RequestBody @Valid ClaimTokenRequestDTO request) {
        Member claimedMember = memberService.claimMember(request.claimToken());
        return ResponseEntity.ok(new MemberDTO(claimedMember));
    }
}
