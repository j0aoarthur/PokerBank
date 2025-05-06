package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChipService {

    @Autowired
    private ChipRepository chipRepository;

    public Chip createChip(ChipRequestDTO dto) {
        Chip chip = new Chip(dto);
        return chipRepository.save(chip);
    }


    public List<Chip> getAllChips() {
        return chipRepository.findAll();
    }


    public Chip getChipById(Long id) {
        return chipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha não encontrada com ID: " + id));
    }


    public Chip updateChip(Long id, ChipRequestDTO dto) {
        Chip chip = getChipById(id);
        chip.setColor(dto.color());
        chip.setValue(dto.value());
        return chipRepository.save(chip);
    }


    public void deleteChip(Long id) {
        Chip chip = getChipById(id);
        chipRepository.delete(chip);
    }
}
