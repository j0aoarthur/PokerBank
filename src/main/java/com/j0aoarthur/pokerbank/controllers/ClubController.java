package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.ClubMemberDTO;
import com.j0aoarthur.pokerbank.dtos.response.ClubResponseDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.security.annotations.RequiresClubContext;
import com.j0aoarthur.pokerbank.services.ClubService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    public ResponseEntity<?> createClub(@RequestBody @Valid ClubRequestDTO clubRequestDTO) {
        try {
            Club club = clubService.createClub(clubRequestDTO);
            return ResponseEntity.status(201).body(new ClubResponseDTO(club));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Clube já existe ou dados inválidos. " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ClubResponseDTO>> getAllClubs() {
        List<Club> clubList = clubService.getAllClubs();
        return ResponseEntity.ok(clubList.stream().map(ClubResponseDTO::new).toList());
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClubById(@PathVariable Long clubId) {
        try {
            Club club = clubService.getClubById(clubId);
            return ResponseEntity.ok(new ClubResponseDTO(club));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Clube não encontrado. " + e.getMessage());
        }
    }

    @PutMapping("/{clubId}")
    // ADMINS PODEM ATUALIZAR O CLUBE
    public ResponseEntity<?> updateClub(@PathVariable Long clubId, @RequestBody @Valid ClubRequestDTO clubRequestDTO) {
        try {
            Club newClub = clubService.updateClub(clubId, clubRequestDTO);
            return ResponseEntity.ok(new ClubResponseDTO(newClub));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao atualizar o clube. " + e.getMessage());
        }
    }

    @DeleteMapping("/{clubId}")
    // OWNER PODE DELETAR O CLUBE
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId) {
        try {
            clubService.deleteClub(clubId);
            return ResponseEntity.ok("Clube deletado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao deletar o clube. " + e.getMessage());
        }
    }

    @GetMapping("/members")
    @RequiresClubContext
    public ResponseEntity<?> getClubMembers() {
        try {
            return ResponseEntity.ok(clubService.getClubMembers().stream().map(ClubMemberDTO::new).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Clube não encontrado. " + e.getMessage());
        }
    }

    @PostMapping("/join/{publicCode}")
    public ResponseEntity<String> joinClub(@PathVariable String publicCode) {
        try {
            clubService.joinClub(publicCode);
            return ResponseEntity.ok("Entrada no clube realizada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao entrar no clube. " + e.getMessage());
        }
    }

    @GetMapping("/my-clubs")
    public ResponseEntity<?> getMyClubs() {
        try {
            List<Club> clubs = clubService.getMyClubs();
            return ResponseEntity.ok(clubs.stream().map(ClubResponseDTO::new).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Clube não encontrado. " + e.getMessage());
        }
    }
}
