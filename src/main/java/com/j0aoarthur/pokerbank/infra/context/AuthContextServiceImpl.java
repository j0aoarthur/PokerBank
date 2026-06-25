package com.j0aoarthur.pokerbank.infra.context;

import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.repositories.ClubRepository;
import com.j0aoarthur.pokerbank.repositories.AccountRepository;
import com.j0aoarthur.pokerbank.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextServiceImpl implements AuthContextService {

    private final AccountRepository accountRepository;
    private final ClubRepository clubRepository;

    @Override
    public Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        String username;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getName();
        }
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o username: " + username));
    }

    @Override
    public Club getCurrentClub() {
        Long clubId = ClubContext.getCurrentClubId();

        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " + clubId));
    }
}