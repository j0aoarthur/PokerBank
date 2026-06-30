package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.PaymentDTO;
import com.j0aoarthur.pokerbank.dtos.response.GameDTO;
import com.j0aoarthur.pokerbank.dtos.response.GameParticipantDTO;
import com.j0aoarthur.pokerbank.dtos.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.services.impl.PaymentService;
import com.j0aoarthur.pokerbank.tenancy.annotations.RequiresClubContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Pagamentos", description = "Sugestões e registro de pagamentos entre jogadores")
@SecurityRequirement(name = "bearerAuth")
@RequiresClubContext
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/suggestion/{gameId}")
    @Operation(summary = "Sugestões de pagamento da partida", description = "Calcula o menor número de transferências para liquidar todos os débitos da partida.")
    public ResponseEntity<List<PaymentSuggestionDTO>> getPaymentSuggestionByGame(
            @Parameter(description = "ID da partida", example = "1", required = true)
            @PathVariable Long gameId) {
        List<PaymentSuggestionDTO> paymentSuggestion = paymentService.getPaymentSuggestion(gameId);
        return ResponseEntity.ok(paymentSuggestion);
    }

    @GetMapping("/expired-games")
    @Operation(summary = "Lista partidas com pagamentos expirados", description = "Partidas cujo dueDate já passou e ainda possuem débitos não liquidados.")
    public ResponseEntity<List<GameDTO>> getExpiredGames() {
        List<Game> expiredGames = paymentService.getExpiredGames();
        return ResponseEntity.ok(expiredGames.stream().map(GameDTO::new).toList());
    }

    @GetMapping("/expired-payments/{memberId}")
    @Operation(summary = "Lista pagamentos expirados de um membro", description = "Partidas onde o membro tem débito não pago após o dueDate.")
    public ResponseEntity<List<GameParticipantDTO>> getExpiredPaymentsOfMember(
            @Parameter(description = "ID do membro", example = "5", required = true)
            @PathVariable Long memberId) {
        List<GameParticipant> expiredPayments = paymentService.getExpiredPaymentsByMember(memberId);
        return ResponseEntity.ok(expiredPayments.stream().map(GameParticipantDTO::new).toList());
    }

    @PostMapping
    @Operation(summary = "Registra o pagamento entre jogadores", description = "Marca a transferência entre pagador e recebedor como liquidada na partida.")
    public ResponseEntity<Void> payParticipant(@RequestBody PaymentDTO paymentDTO) {
        paymentService.payPlayer(paymentDTO);
        return ResponseEntity.ok().build();
    }
}
