package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.services.ChipService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chips")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChipController {

    @Autowired
    private ChipService chipService;

    @PostMapping
    @Transactional
    public ResponseEntity<Chip> createChip(@RequestBody @Valid ChipRequestDTO chipDTO) {
        Chip createdChip = chipService.createChip(chipDTO);
        return ResponseEntity.ok(createdChip);
    }

    @GetMapping
    public ResponseEntity getAllChips() {
        List<Chip> chipList = chipService.getAllChips();
        return ResponseEntity.ok(chipList);
    }
}
