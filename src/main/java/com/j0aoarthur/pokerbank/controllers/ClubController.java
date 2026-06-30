package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.ClubResponseDTO;
import com.j0aoarthur.pokerbank.dtos.response.MemberDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import com.j0aoarthur.pokerbank.services.ClubService;
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
@RequestMapping("/clubs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Clubes", description = "Criação, gerenciamento e participação em clubes de poker")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    @Operation(summary = "Cria um novo clube", description = "O usuário autenticado se torna o dono (OWNER) do clube criado.")
    public ResponseEntity<ClubResponseDTO> createClub(@RequestBody @Valid ClubRequestDTO clubRequestDTO) {
        Club club = clubService.createClub(clubRequestDTO);
        return ResponseEntity.status(201).body(new ClubResponseDTO(club));
    }

    @GetMapping
    @Operation(summary = "Lista todos os clubes", description = "Retorna todos os clubes cadastrados no sistema.")
    public ResponseEntity<List<ClubResponseDTO>> getAllClubs() {
        List<Club> clubList = clubService.getAllClubs();
        return ResponseEntity.ok(clubList.stream().map(ClubResponseDTO::new).toList());
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "Busca clube por ID")
    public ResponseEntity<ClubResponseDTO> getClubById(
            @Parameter(description = "ID do clube", example = "1", required = true)
            @PathVariable Long clubId) {
        Club club = clubService.getClubById(clubId);
        return ResponseEntity.ok(new ClubResponseDTO(club));
    }

    @PutMapping("/{clubId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Atualiza dados de um clube", description = "Apenas ADMIN e OWNER podem atualizar o clube.")
    public ResponseEntity<ClubResponseDTO> updateClub(
            @Parameter(description = "ID do clube", example = "1", required = true)
            @PathVariable Long clubId,
            @RequestBody @Valid ClubRequestDTO clubRequestDTO) {
        Club newClub = clubService.updateClub(clubId, clubRequestDTO);
        return ResponseEntity.ok(new ClubResponseDTO(newClub));
    }

    @DeleteMapping("/{clubId}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Deleta um clube", description = "Apenas o OWNER pode deletar o clube. A operação é irreversível.")
    public ResponseEntity<Void> deleteClub(
            @Parameter(description = "ID do clube", example = "1", required = true)
            @PathVariable Long clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members")
    @RequiresClubContext
    @Operation(summary = "Lista membros do clube ativo", description = "Requer clube selecionado no JWT (select-club).")
    public ResponseEntity<List<MemberDTO>> getMembers() {
        return ResponseEntity.ok(clubService.getMembers().stream().map(MemberDTO::new).toList());
    }

    @PostMapping("/join/{publicCode}")
    @Operation(summary = "Entra em um clube via código público", description = "Vincula o usuário autenticado ao clube correspondente ao código.")
    public ResponseEntity<MemberDTO> joinClub(
            @Parameter(description = "Código público do clube (visível na tela do clube)", example = "AB1C2D", required = true)
            @PathVariable String publicCode) {
        Member member = clubService.joinClub(publicCode);
        return ResponseEntity.ok(new MemberDTO(member));
    }

    @GetMapping("/my-clubs")
    @Operation(summary = "Lista os clubes do usuário autenticado")
    public ResponseEntity<List<ClubResponseDTO>> getMyClubs() {
        List<Club> clubs = clubService.getMyClubs();
        return ResponseEntity.ok(clubs.stream().map(ClubResponseDTO::new).toList());
    }

    @DeleteMapping("/members/{memberId}")
    @RequiresClubContext
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Remove um membro do clube ativo", description = "ADMIN e OWNER podem remover qualquer membro, exceto o próprio OWNER.")
    public ResponseEntity<Void> removeMemberFromClub(
            @Parameter(description = "ID do membro a remover", example = "5", required = true)
            @PathVariable Long memberId) {
        Long clubId = ClubContext.getCurrentClubId();
        clubService.removeMemberFromClub(clubId, memberId);
        return ResponseEntity.noContent().build();
    }
}
