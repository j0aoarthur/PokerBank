package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.ChipDTO;
import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import com.j0aoarthur.pokerbank.services.ChipService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chips")
@Tag(name = "Chip Controller", description = "Endpoints para gerenciar as fichas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class ChipController {

    private final ChipService chipService;

    @PostMapping
    @Transactional
    // ADMINS PODEM CRIAR FICHAS
    @Operation(summary = "Cria uma nova ficha")
    public ResponseEntity<ChipDTO> createChip(@RequestBody @Valid ChipRequestDTO chipDTO) {
        Chip createdChip = chipService.createChip(chipDTO);
        return ResponseEntity.ok(new ChipDTO(createdChip));
    }

    @GetMapping
    @Operation(summary = "Retorna todas as fichas")
    public ResponseEntity<List<ChipDTO>> getAllChips() {
        List<Chip> chipList = chipService.getAllChips();
        return ResponseEntity.ok(chipList.stream().map(ChipDTO::new).toList());
    }
}
