package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "gamePlayer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChipCount> chipCounts = new ArrayList<>();

    private BigDecimal initialCash;

    // Representa o resultado financeiro do jogador no jogo (lucro/prejuízo).
    // Ex: +100.00 se ganhou, -50.00 se perdeu.
    // Este valor NÃO será alterado pelas operações de pagamento.
    private BigDecimal balance;

    // Novo campo: Valor já pago (se 'balance' < 0) ou já recebido (se 'balance' > 0).
    // Inicializado como BigDecimal.ZERO.
    private BigDecimal settledAmount = BigDecimal.ZERO;

    private Boolean paid = false; // True se settledAmount cobre totalmente o 'balance'.

    @Enumerated(EnumType.STRING)
    private PaymentSituation paymentSituation; // PAY, RECEIVE, ou talvez NEUTRAL/SETTLED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public GamePlayer(GamePlayerRequestDTO dto, Game game, Player player) {
        this.setInitialCash(dto.initialCash());
        this.setBalance(BigDecimal.ZERO);
        this.setSettledAmount(BigDecimal.ZERO);
        this.setPaid(false);
        this.setGame(game);
        this.setPlayer(player);
    }

    /**
     * Calcula o valor pendente com base no 'balance' e no 'settledAmount'.
     * Se paymentSituation == PAY (balance é negativo), retorna o valor absoluto da dívida restante.
     * Se paymentSituation == RECEIVE (balance é positivo), retorna o crédito restante a receber.
     * @return O valor pendente. Retorna BigDecimal.ZERO se não houver pendência ou situação indefinida.
     */
    @Transient
    public BigDecimal getPendingAmount() {
        if (this.balance == null || this.paymentSituation == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentSettledAmount = (this.settledAmount == null) ? BigDecimal.ZERO : this.settledAmount;

        BigDecimal pending = BigDecimal.ZERO;
        if (this.paymentSituation == PaymentSituation.PAY) {
            pending = this.balance.abs().subtract(currentSettledAmount);
        } else if (this.paymentSituation == PaymentSituation.RECEIVE) {
            pending = this.balance.subtract(currentSettledAmount);
        }

        return pending.max(BigDecimal.ZERO);
    }
}