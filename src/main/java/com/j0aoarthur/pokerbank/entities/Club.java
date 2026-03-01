package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clubs")
@Getter
@Setter
@NoArgsConstructor
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, name = "public_code")
    private String publicCode;

    private String description;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClubMember> clubMembers = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Game> games = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlayerRanking> playerRankings = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chip> chips = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GamePlayer> gamePlayers = new ArrayList<>();

    public Club(ClubRequestDTO clubRequestDTO) {
        this.setName(clubRequestDTO.name());
        this.setDescription(clubRequestDTO.description());
        this.setPublicCode(generatePublicCode());
    }

    public String generatePublicCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
