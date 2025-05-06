package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_players")
@Setter
@NoArgsConstructor
@Getter
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "initial_cash")
    private BigDecimal initialCash;

    private BigDecimal balance;

    private Boolean paid = false;

    @Enumerated(EnumType.STRING)
    private PaymentSituation paymentSituation;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public GamePlayer(GamePlayerRequestDTO dto, Game game, Player player) {
        this.setInitialCash(dto.initialCash());
        this.setBalance(BigDecimal.ZERO);
        this.setGame(game);
        this.setPlayer(player);
    }
}

