package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ClaimTokenRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.MemberDTO;
import com.j0aoarthur.pokerbank.services.MemberService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Membros", description = "Gerenciamento de membros do clube ativo")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Cria um novo membro", description = "Cria um membro no clube ativo e gera um token de vinculação com conta de usuário.")
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberRequestDTO memberDTO) {
        return ResponseEntity.ok(new MemberDTO(memberService.createMember(memberDTO)));
    }

    @GetMapping
    @Operation(summary = "Lista todos os membros do clube ativo")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers().stream().map(MemberDTO::new).toList());
    }

    @GetMapping("/not-in-game/{gameId}")
    @Operation(summary = "Lista membros fora de uma partida", description = "Retorna membros do clube que ainda não foram adicionados à partida especificada.")
    public ResponseEntity<List<MemberDTO>> getMembersNotInGame(
            @Parameter(description = "ID da partida", example = "1", required = true)
            @PathVariable Long gameId) {
        return ResponseEntity.ok(memberService.getMembersNotInGame(gameId).stream().map(MemberDTO::new).toList());
    }

    @PostMapping("/claim")
    @Operation(summary = "Vincula conta de usuário ao membro", description = "Usa o token de claim para associar a conta autenticada ao membro do clube.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberDTO> claimMember(@RequestBody @Valid ClaimTokenRequestDTO request) {
        return ResponseEntity.ok(new MemberDTO(memberService.claimMember(request.claimToken())));
    }
}
