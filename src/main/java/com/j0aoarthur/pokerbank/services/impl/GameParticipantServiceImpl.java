package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.ChipCountRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.GameParticipantRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.UpdateGameParticipantDTO;
import com.j0aoarthur.pokerbank.entities.*;
import com.j0aoarthur.pokerbank.entities.enums.PaymentSituation;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.ChipCountRepository;
import com.j0aoarthur.pokerbank.repositories.GameParticipantRepository;
import com.j0aoarthur.pokerbank.services.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameParticipantServiceImpl implements GameParticipantService {

    private final GameService gameService;
    private final MemberService memberService;
    private final ChipService chipService;
    private final MemberStatsService memberStatsService;
    private final GameParticipantRepository gameParticipantRepository;
    private final ChipCountRepository chipCountRepository;

    @Override
    @Transactional
    public GameParticipant addParticipantToGame(GameParticipantRequestDTO dto) {
        Game game = gameService.getGameById(dto.gameId());
        Member member = memberService.getMemberById(dto.memberId());

        // Verifica se o participante já está na partida
        if (gameParticipantRepository.existsByGameIdAndMemberId(game.getId(), member.getId())) {
            throw new IllegalArgumentException("Participante já está na partida");
        }

        // Criar relação GameParticipant
        GameParticipant gameParticipant = gameParticipantRepository.save(new GameParticipant(dto, game, member));

        this.addChipCountsToGameParticipant(gameParticipant, dto.chips());
        gameParticipant.recalculateBalance();
        memberStatsService.updateMemberStats(gameParticipant);
        return gameParticipant;
    }

    @Override
    public GameParticipant getGameParticipantByGameAndMember(Long gameId, Long memberId) {
        return gameParticipantRepository.findByGameIdAndMemberId(gameId, memberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "O participante com ID: " + memberId + " não está na partida com ID: " + gameId));
    }

    @Override
    public List<GameParticipant> getGameParticipantsByGame(Long gameId) {
        return gameParticipantRepository.findByGameIdOrderByBalanceDesc(gameId);
    }

    @Override
    public List<GameParticipant> getGameParticipantsByMember(Long memberId) {
        List<GameParticipant> games = gameParticipantRepository.findByMemberId(memberId);
        if (games.isEmpty()) {
            throw new EntityNotFoundException("Membro não possui partidas jogadas");
        }

        return games;
    }

    @Override
    public List<GameParticipant> getUnpaidGameParticipantsByPaymentSituation(Long gameId, PaymentSituation paymentSituation) {
        return gameParticipantRepository.findByGameIdAndPaymentSituationAndPaidIsFalseOrderByBalance(gameId,
                paymentSituation);
    }

    @Override
    public List<ChipCount> getChipCountsByGameParticipant(Long gameParticipantId) {
        return gameParticipantRepository.getReferenceById(gameParticipantId).getChipCounts().stream()
                .filter(chipCount -> chipCount.getQuantity() > 0).toList();
    }

    @Override
    @Transactional
    public GameParticipant updateGameParticipant(Long gameId, Long memberId, UpdateGameParticipantDTO dto) {
        GameParticipant gameParticipant = this.getGameParticipantByGameAndMember(gameId, memberId);

        List<ChipCount> existingChipCounts = gameParticipant.getChipCounts();
        existingChipCounts.clear();
        gameParticipant.setBalance(BigDecimal.ZERO);

        if (dto.initialCash() != null) {
            gameParticipant.setInitialCash(dto.initialCash());
        }

        this.addChipCountsToGameParticipant(gameParticipant, dto.chips());

        gameParticipant.recalculateBalance();
        memberStatsService.updateMemberStats(gameParticipant);

        return gameParticipant;
    }

    @Override
    @Transactional
    public void updateGameParticipantPayment(GameParticipant gameParticipant) {
        gameParticipantRepository.save(gameParticipant);
    }

    @Transactional
    protected void addChipCountsToGameParticipant(GameParticipant gameParticipant, List<ChipCountRequestDTO> chips) {
        for (ChipCountRequestDTO chipCountDTO : chips) {
            Chip chip = chipService.getChipById(chipCountDTO.chipId());

            // Criar relação ChipCount
            ChipCount chipCount = new ChipCount(chipCountDTO, gameParticipant, chip);

            gameParticipant.getChipCounts().add(chipCount);

            chipCountRepository.save(chipCount);
        }
    }
}
