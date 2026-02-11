package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.entities.Chip;

import java.util.List;

public interface ChipService {
    Chip createChip(ChipRequestDTO chipRequestDTO);
    List<Chip> getAllChips();
    Chip getChipById(Long id);
}
