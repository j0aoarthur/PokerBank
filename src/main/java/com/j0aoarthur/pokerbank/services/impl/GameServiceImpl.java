package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GameParticipant;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.GameRepository;
import com.j0aoarthur.pokerbank.services.GameService;
import com.j0aoarthur.pokerbank.services.MemberStatsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final MemberStatsService memberStatsService;
    private final AuthContextService authContextService;

    @Override
    @Transactional
    public Game createGame(GameRequestDTO gameRequestDTO) {
        Club currentClub = authContextService.getCurrentClub();
        Game game = new Game(gameRequestDTO, currentClub);
        return gameRepository.save(game);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        Game game = this.getGameById(id);
        if (game.getIsFinished()) {
            throw new IllegalArgumentException("Não é possível excluir uma partida já finalizada.");
        }

        if (!game.getClub().equals(authContextService.getCurrentClub())) {
            throw new AccessDeniedException("Você não tem permissão para excluir esta partida.");
        }

        // Obter todos os participantes da partida antes de excluír a partida
        List<GameParticipant> gameParticipants = game.getParticipants();

        gameRepository.delete(game);

        // Atualizar as estatísticas dos participantes da partida
        for (GameParticipant gameParticipant : gameParticipants) {
            memberStatsService.updateMemberStats(gameParticipant);
        }
    }

    // Listar todas as partidas com paginação
    @Override
    public Page<Game> getGames(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    // Listar todas as partidas
    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll().stream().sorted(Comparator.comparing(Game::getDate).reversed()).toList();
    }

    // Buscar partida por ID
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    // Buscar as 3 últimas partidas
    @Override
    public List<Game> getLatestGames() {
        return gameRepository.findTop3ByOrderByDateDesc();
    }

    @Override
    public GameInfoDTO getGameInfoById(Long id) {
        Game game = this.getGameById(id);

        List<GameParticipant> gameParticipantsWithBalance = game.getParticipants();

        Integer totalParticipants = gameParticipantsWithBalance.size();

        BigDecimal totalBalance = BigDecimal.ZERO;
        for (GameParticipant gameParticipant : gameParticipantsWithBalance) {
            totalBalance = totalBalance.add(gameParticipant.getBalance());
        }
        String observation;

        BigDecimal totalPrize = BigDecimal.ZERO;
        for (GameParticipant gameParticipant : gameParticipantsWithBalance) {
            totalPrize = totalPrize.add(gameParticipant.getInitialCash());
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
                totalParticipants,
                game.getIsFinished(),
                observation
        );
    }

    @Override
    @Transactional
    public void checkGameFinished(Long gameId) {
        Game game = this.getGameById(gameId);
        List<GameParticipant> gameParticipants = game.getParticipants();

        if (gameParticipants.isEmpty()) {
            throw new EntityNotFoundException("Nenhum participante encontrado na partida de ID: " + gameId);
        }

        boolean allPaid = gameParticipants.stream().allMatch(GameParticipant::getPaid);

        if (allPaid) {
            game.setIsFinished(true);
            gameRepository.save(game);
        }
    }
}

