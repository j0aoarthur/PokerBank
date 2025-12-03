package com.j0aoarthur.pokerbank.infra.context;

import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.infra.security.CustomUserDetails;
import com.j0aoarthur.pokerbank.repositories.ClubRepository;
import com.j0aoarthur.pokerbank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextServiceImpl implements AuthContextService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    @Override
    public User getCurrentUser() {
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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o username: " + username));
    }

    @Override
    public Club getCurrentClub() {
        Long clubId = ClubContext.getCurrentClubId();

        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " + clubId));
    }
}