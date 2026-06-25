package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.dtos.request.ChipCountRequestDTO;
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
    @JoinColumn(name = "game_participant_id")
    private GameParticipant gameParticipant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chip_id")
    private Chip chip;

    private Integer quantity;

    public ChipCount(ChipCountRequestDTO chipCountDTO, GameParticipant gameParticipant, Chip chip) {
        this.setGameParticipant(gameParticipant);
        this.setChip(chip);
        this.setQuantity(chipCountDTO.quantity());
    }
}

