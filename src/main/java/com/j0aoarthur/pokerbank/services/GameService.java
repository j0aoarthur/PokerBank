package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerService gamePlayerService;

    @Transactional
    public Game createGame(GameRequestDTO dto) {
        Game game = new Game();
        game.setDate(dto.date());
        game.setDueDate(game.getDate().plusWeeks(1));
        return gameRepository.save(game);
    }

    // Buscar partida por ID
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    // Listar todas as partidas com paginação
    public Page<Game> getAllGames(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    // Listar todas as partidas
    public List<Game> getAllGames() {
        return gameRepository.findAll().stream().sorted(Comparator.comparing(Game::getDate).reversed()).toList();
    }

    public List<Game> getLatestGames() {
        return gameRepository.findTop3ByOrderByDateDesc();
    }

    public GameInfoDTO getGameInfo(Long id) {
        Game game = this.getGameById(id);

        List<GamePlayer> gamePlayersWithBalance = gamePlayerService.getGamePlayersByGame(id);

        Integer totalPlayers = gamePlayersWithBalance.size();

        BigDecimal totalBalance = BigDecimal.ZERO;
        for (GamePlayer gamePlayer : gamePlayersWithBalance) {
            totalBalance = totalBalance.add(gamePlayer.getBalance());
        }
        String observation;

        BigDecimal totalPrize = BigDecimal.ZERO;
        for (GamePlayer gamePlayer : gamePlayersWithBalance) {
            totalPrize = totalPrize.add(gamePlayer.getInitialCash());
        }

        if (totalBalance.compareTo(BigDecimal.ZERO) == 0) {
            observation = "O saldo do jogo está correto.";
        } else if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            observation = "Falta dinheiro no jogo. O saldo total faltando é: " + totalBalance;
        }
        else {
            observation = "Dinheiro a mais no jogo. O saldo total a mais é: " + totalBalance;
        }

        return new GameInfoDTO(
                game.getId(),
                game.getDate(),
                game.getDueDate(),
                totalBalance,
                totalPrize,
                totalPlayers,
                game.getIsFinished(),
                observation
        );


    }

    @Transactional
    public void checkGameFinished(Long gameId) {
        Game game = this.getGameById(gameId);
        List<GamePlayer> gamePlayers = gamePlayerService.getGamePlayersByGame(gameId);

        if (gamePlayers.isEmpty()) {
            throw new EntityNotFoundException("Nenhum jogador encontrado na partida de ID: " + gameId);
        }

        boolean allPaid = gamePlayers.stream().allMatch(GamePlayer::getPaid);

        if (allPaid) {
            game.setIsFinished(true);
            gameRepository.save(game);
        }
    }
}

