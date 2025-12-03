package com.j0aoarthur.pokerbank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(name = "player_ranking")
@Getter
@Setter
@NoArgsConstructor
public class PlayerRanking extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", insertable = false, updatable = false)
    private Club club;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @Column(name = "rank")
    private Integer rank;

    private BigDecimal totalWon = BigDecimal.ZERO;

    private BigDecimal totalLost = BigDecimal.ZERO;

    private BigDecimal netBalance = BigDecimal.ZERO;

    private Integer gamesPlayed = 0;
}

