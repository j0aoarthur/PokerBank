package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.DTOs.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.entities.Chip;

import java.util.List;

public interface ChipService {
    Chip createChip(ChipRequestDTO chipRequestDTO);
    List<Chip> getAllChips();
    Chip getChipById(Long id);
}
