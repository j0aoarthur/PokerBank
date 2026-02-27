package com.j0aoarthur.pokerbank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.j0aoarthur.pokerbank.dtos.request.GameRequestDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class Game extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", insertable = false, updatable = false)
    private Club club;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Boolean isFinished = false;

    private LocalDate dueDate;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GamePlayer> players = new ArrayList<>();

    public Game(GameRequestDTO gameRequestDTO, Club currentClub) {
        this.setDate(gameRequestDTO.date());
        this.setDueDate(gameRequestDTO.date().plusWeeks(1));
    }
}

