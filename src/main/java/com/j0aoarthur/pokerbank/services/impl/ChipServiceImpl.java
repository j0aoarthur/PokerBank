package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.ChipRequestDTO;
import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.infra.context.AuthContextServiceImpl;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipRepository;
import com.j0aoarthur.pokerbank.services.ChipService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChipServiceImpl implements ChipService {

    private final ChipRepository chipRepository;
    private final AuthContextServiceImpl authContextService;

    @Override
    @Transactional
    public Chip createChip(ChipRequestDTO dto) {
        Club club = authContextService.getCurrentClub();
        Chip chip = new Chip(dto, club);
        return chipRepository.save(chip);
    }


    @Override
    public List<Chip> getAllChips() {
        return chipRepository.findAll().stream().sorted(Comparator.comparing(Chip::getValue)).toList();
    }


    @Override
    public Chip getChipById(Long id) {
        return chipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ficha não encontrada com ID: " + id));
    }
}
