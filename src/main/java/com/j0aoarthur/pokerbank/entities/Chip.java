package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.DTOs.request.ChipRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Chip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;

    private String colorHex;

    @Column(name = "chip_value")
    private BigDecimal value;

    public Chip(ChipRequestDTO dto) {
        this.setColor(dto.color());
        this.setColorHex(dto.colorHex());
        this.setValue(dto.value());
    }
}
