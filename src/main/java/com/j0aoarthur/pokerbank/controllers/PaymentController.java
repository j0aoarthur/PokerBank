package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.PaymentDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GamePlayerDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.infra.security.annotations.RequiresClubContext;
import com.j0aoarthur.pokerbank.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Payment Controller", description = "Endpoints para gerenciar pagamentos e sugestões de pagamento")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/suggestion/{gameId}")
    @Operation(summary = "Retorna sugestões de pagamento para uma partida específica")
    public ResponseEntity<List<PaymentSuggestionDTO>> getPaymentSuggestionByGame(@PathVariable Long gameId) {
        List<PaymentSuggestionDTO> paymentSuggestion = paymentService.getPaymentSuggestion(gameId);
        return ResponseEntity.ok(paymentSuggestion);
    }

    @GetMapping("/expired-games")
    @Operation(summary = "Retorna todas as partidas com pagamentos expirados")
    public ResponseEntity<List<GameDTO>> getExpiredGames() {
        List<Game> expiredGames = paymentService.getExpiredGames();
        return ResponseEntity.ok(expiredGames.stream().map(GameDTO::new).toList());
    }

    @GetMapping("/expired-payments/{clubMemberId}")
    @Operation(summary = "Retorna todos os pagamentos expirados de um jogador específico")
    public ResponseEntity<List<GamePlayerDTO>> getExpiredPaymentsOfPlayer(@PathVariable Long clubMemberId) {
        List<GamePlayer> expiredPayments = paymentService.getExpiredPaymentsByClubMember(clubMemberId);
        return ResponseEntity.ok(expiredPayments.stream().map(GamePlayerDTO::new).toList());
    }

    @PostMapping
    @Operation(summary = "Registra o pagamento de um jogador")
    public ResponseEntity<Void> payPlayer(@RequestBody PaymentDTO paymentDTO) {
        paymentService.payPlayer(paymentDTO);
        return ResponseEntity.ok().build();
    }
}
