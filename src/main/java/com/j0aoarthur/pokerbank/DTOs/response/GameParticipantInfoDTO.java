package com.j0aoarthur.pokerbank.dtos.response;

import com.j0aoarthur.pokerbank.entities.GameParticipant;

import java.math.BigDecimal;
import java.util.List;

public record GameParticipantInfoDTO(
        Long memberId,
        String memberName,
        BigDecimal initialCash,
        BigDecimal balance,
        BigDecimal pendingAmount,
        List<ChipCountDTO> chips
) {

    public GameParticipantInfoDTO(GameParticipant gameParticipant, List<ChipCountDTO> chips) {
        this(
                gameParticipant.getMember().getId(),
                gameParticipant.getMember().getName(),
                gameParticipant.getInitialCash(),
                gameParticipant.getBalance(),
                gameParticipant.getPendingAmount(),
                chips
        );
    }
}
