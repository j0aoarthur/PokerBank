package com.j0aoarthur.pokerbank.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClaimTokenRequestDTO(
        @NotNull(message = "O token de reivindicação é obrigatório.")
        UUID claimToken
) {}
