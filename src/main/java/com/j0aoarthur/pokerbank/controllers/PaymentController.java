package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.PaymentDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GamePlayerDTO;
import com.j0aoarthur.pokerbank.DTOs.response.PaymentSuggestionDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/suggestion/{gameId}")
    public ResponseEntity<List<PaymentSuggestionDTO>> getPaymentSuggestionByGame(@PathVariable Long gameId) {
        List<PaymentSuggestionDTO> paymentSuggestion = paymentService.getPaymentSuggestion(gameId);
        return ResponseEntity.ok(paymentSuggestion);
    }

    @GetMapping("/expired-games")
    public ResponseEntity<List<GameDTO>> getExpiredGames() {
        List<Game> expiredGames = paymentService.getExpiredGames();
        return ResponseEntity.ok(expiredGames.stream().map(GameDTO::new).toList());
    }

    @GetMapping("/expired-payments/{playerId}")
    public ResponseEntity<List<GamePlayerDTO>> getExpiredPaymentsOfPlayer(@PathVariable Long playerId) {
        List<GamePlayer> expiredPayments = paymentService.getExpiredPaymentsByPlayer(playerId);
        return ResponseEntity.ok(expiredPayments.stream().map(GamePlayerDTO::new).toList());
    }

    @PostMapping
    public ResponseEntity payPlayer(@RequestBody PaymentDTO paymentDTO) {
        paymentService.payPlayer(paymentDTO);
        return ResponseEntity.ok().build();
    }
}
