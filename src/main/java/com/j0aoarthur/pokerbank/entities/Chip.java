package com.j0aoarthur.pokerbank.entities;

import java.math.BigDecimal;

import com.j0aoarthur.pokerbank.dtos.request.ChipRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chips")
@NoArgsConstructor
public class Chip extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", insertable = false, updatable = false)
    private Club club;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;

    private String colorHex;

    @Column(name = "chip_value")
    private BigDecimal value;

    public Chip(ChipRequestDTO dto, Club club) {
        this.club = club;
        this.setColor(dto.color());
        this.setColorHex(dto.colorHex());
        this.setValue(dto.value());
    }
}
