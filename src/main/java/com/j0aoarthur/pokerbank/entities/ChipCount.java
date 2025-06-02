package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.DTOs.request.ChipCountRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chip_counts")
@NoArgsConstructor
@Getter
@Setter
public class ChipCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chip_id")
    private Chip chip;

    private Integer quantity;

    public ChipCount(ChipCountRequestDTO chipCountDTO, GamePlayer gamePlayer, Chip chip) {
        this.setGamePlayer(gamePlayer);
        this.setChip(chip);
        this.setQuantity(chipCountDTO.quantity());
    }
}

