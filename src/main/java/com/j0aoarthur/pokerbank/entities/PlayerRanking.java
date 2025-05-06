package com.j0aoarthur.pokerbank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "player_ranking")
@Getter
@Setter
@NoArgsConstructor
public class PlayerRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "total_won")
    private BigDecimal totalWon = BigDecimal.ZERO;

    @Column(name = "total_lost")
    private BigDecimal totalLost = BigDecimal.ZERO;

    @Column(name = "net_balance")
    private BigDecimal netBalance = BigDecimal.ZERO;

    @Column(name = "games_played")
    private Integer gamesPlayed = 0;
}

