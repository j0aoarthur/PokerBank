package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.ChipDTO;
import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.services.ChipService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chips")
@Tag(name = "Fichas", description = "Gerenciamento de fichas de poker do clube ativo")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class ChipController {

    private final ChipService chipService;

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Cria uma nova ficha", description = "Apenas ADMIN e OWNER podem criar fichas. O valor da ficha é fixo por cor.")
    public ResponseEntity<ChipDTO> createChip(@RequestBody @Valid ChipRequestDTO chipDTO) {
        Chip createdChip = chipService.createChip(chipDTO);
        return ResponseEntity.ok(new ChipDTO(createdChip));
    }

    @GetMapping
    @Operation(summary = "Lista todas as fichas do clube ativo")
    public ResponseEntity<List<ChipDTO>> getAllChips() {
        List<Chip> chipList = chipService.getAllChips();
        return ResponseEntity.ok(chipList.stream().map(ChipDTO::new).toList());
    }
}
