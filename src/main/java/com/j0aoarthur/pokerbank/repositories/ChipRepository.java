package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Chip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChipRepository extends JpaRepository<Chip, Long> {
    Optional<Chip> findByColor(String color);
}
