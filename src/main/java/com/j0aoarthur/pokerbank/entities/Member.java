package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", insertable = false, updatable = false)
    private Club club;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role = Role.PLAYER;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "claim_token", unique = true)
    private UUID claimToken;

    public Member(MemberRequestDTO memberDTO, Club club, Account account) {
        this.setName(memberDTO.name());
        this.setClub(club);
        this.setClubId(club.getId());
        this.setAccount(account);
        this.setRole(memberDTO.role() != null ? memberDTO.role() : Role.PLAYER);
    }
}
