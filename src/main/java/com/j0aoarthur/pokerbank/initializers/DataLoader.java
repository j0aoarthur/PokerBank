package com.j0aoarthur.pokerbank.initializers;

import com.j0aoarthur.pokerbank.DTOs.request.*;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.Player;
import com.j0aoarthur.pokerbank.services.ChipService;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;
import com.j0aoarthur.pokerbank.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile({"test"})
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ChipService chipService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerService gamePlayerService;

    @Override
    public void run(String... args) throws Exception {


        // Adicionar fichas de poker
        chipService.createChip(new ChipRequestDTO("preta","#000", new BigDecimal("0.10")));
        chipService.createChip(new ChipRequestDTO("verde", "#0F0", new BigDecimal("0.25")));
        chipService.createChip(new ChipRequestDTO("azul", "#00F", new BigDecimal("0.50")));
        chipService.createChip(new ChipRequestDTO("amarela", "#FF0", new BigDecimal("1.00")));
        chipService.createChip(new ChipRequestDTO("vermelha","#F00", new BigDecimal("5.00")));

        // Adicionar Jogadores
        Player player1 = playerService.createPlayer(new PlayerRequestDTO("Cauã"));
        Player player2 = playerService.createAdmin(new PlayerRequestDTO("João Arthur"));
        Player player3 = playerService.createPlayer(new PlayerRequestDTO("Kawk"));
        Player player4 = playerService.createPlayer(new PlayerRequestDTO("Lanche"));
        Player player5 = playerService.createPlayer(new PlayerRequestDTO("Rafa"));

        // Adicionar Partida
        Game game = gameService.createGame(new GameRequestDTO(LocalDate.now().minusWeeks(2)));

        // Adicionar Mão de cada jogador da partida
        List<GamePlayerRequestDTO> players = List.of(
                new GamePlayerRequestDTO(
                        game.getId(),
                        player1.getId(),
                        new BigDecimal("20.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 1),
                                new ChipCountRequestDTO(4L, 6),
                                new ChipCountRequestDTO(3L, 9)
                        )
                ),
                new GamePlayerRequestDTO(
                        game.getId(),
                        player2.getId(),
                        new BigDecimal("12.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 2),
                                new ChipCountRequestDTO(4L, 5),
                                new ChipCountRequestDTO(2L, 10)
                        )
                ),
                new GamePlayerRequestDTO(
                        game.getId(),
                        player3.getId(),
                        new BigDecimal("8.00"),
                        List.of(
                                new ChipCountRequestDTO(4L, 4),
                                new ChipCountRequestDTO(3L, 1),
                                new ChipCountRequestDTO(2L, 4),
                                new ChipCountRequestDTO(1L, 5)
                        )
                ),
                new GamePlayerRequestDTO(
                        game.getId(),
                        player4.getId(),
                        new BigDecimal("19.50"),
                        List.of(
                                new ChipCountRequestDTO(5L, 3),
                                new ChipCountRequestDTO(3L, 9),
                                new ChipCountRequestDTO(1L, 20)
                        )
                ),
                new GamePlayerRequestDTO(
                        game.getId(),
                        player5.getId(),
                        new BigDecimal("10.25"),
                        List.of(
                                new ChipCountRequestDTO(5L, 1),
                                new ChipCountRequestDTO(4L, 2),
                                new ChipCountRequestDTO(2L, 5),
                                new ChipCountRequestDTO(1L, 10)
                        )
                )
        );

        for (GamePlayerRequestDTO player : players) {
            gamePlayerService.addPlayerToGame(player);
        }

        Game game2 = gameService.createGame(new GameRequestDTO(LocalDate.now().minusDays(1)));

        List<GamePlayerRequestDTO> players2 = List.of(
                new GamePlayerRequestDTO(
                        game2.getId(),
                        player1.getId(),
                        new BigDecimal("10.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 3)
                        )
                ),
                new GamePlayerRequestDTO(
                        game2.getId(),
                        player2.getId(),
                        new BigDecimal("10.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 1)
                        )
                ));

        for (GamePlayerRequestDTO player : players2) {
            gamePlayerService.addPlayerToGame(player);
        }

        Game game3 = gameService.createGame(new GameRequestDTO(LocalDate.now().minusDays(2)));

        List<GamePlayerRequestDTO> players3 = List.of(
                new GamePlayerRequestDTO(
                        game3.getId(),
                        player1.getId(),
                        new BigDecimal("10.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 3)
                        )
                ),
                new GamePlayerRequestDTO(
                        game3.getId(),
                        player2.getId(),
                        new BigDecimal("10.00"),
                        List.of(
                                new ChipCountRequestDTO(5L, 1)
                        )
                ));

        for (GamePlayerRequestDTO player : players3) {
            gamePlayerService.addPlayerToGame(player);
        }
    }
}
