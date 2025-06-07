package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.response.PlayerRankingDTO;
import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import com.j0aoarthur.pokerbank.services.PlayerRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlayerRankingController {

    @Autowired
    private PlayerRankingService playerRankingService;

    @GetMapping
    public ResponseEntity<List<PlayerRankingDTO>> getRanking() {
        List<PlayerRanking> ranking = playerRankingService.getPlayerRankings();

        return ResponseEntity.ok(ranking.stream().map(PlayerRankingDTO::new).toList());
    }

    @GetMapping("/top")
    public ResponseEntity<List<PlayerRankingDTO>> getTopPlayers() {
        List<PlayerRanking> topPlayers = playerRankingService.getTopPlayers();
        return ResponseEntity.ok(topPlayers.stream().map(PlayerRankingDTO::new).toList());
    }


}
