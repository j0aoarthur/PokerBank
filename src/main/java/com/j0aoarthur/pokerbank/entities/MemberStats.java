package com.j0aoarthur.pokerbank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "member_stats")
@Getter
@Setter
@NoArgsConstructor
public class MemberStats extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", insertable = false, updatable = false)
    private Club club;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "rank")
    private Integer rank;

    private BigDecimal totalWon = BigDecimal.ZERO;

    private BigDecimal totalLost = BigDecimal.ZERO;

    private BigDecimal netBalance = BigDecimal.ZERO;

    private Integer gamesPlayed = 0;
}

