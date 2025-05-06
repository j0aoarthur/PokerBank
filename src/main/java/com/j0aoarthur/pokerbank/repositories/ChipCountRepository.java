package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Chip;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChipCountRepository extends JpaRepository<ChipCount, Long> {
    List<ChipCount> findByGamePlayerId(Long gamePlayerId);
}
