package com.j0aoarthur.pokerbank.security;

import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import com.j0aoarthur.pokerbank.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(7);

        try {
            if (tokenService.validateToken(jwt)) {
                String username = tokenService.extractUsername(jwt);

                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o username: " + username));

                Claims claims = tokenService.extractAllClaims(jwt);

                Long clubId = claims.get("clubId", Long.class);
                Role role = claims.get("role", String.class) != null ? Role.valueOf(claims.get("role", String.class))
                        : null;

                UserDetails userDetails = new CustomUserDetails(user, role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                if (clubId != null) {
                    ClubContext.setCurrentClubId(clubId);
                }
            }
        } catch (Exception e) {
            // Logar erro de autenticação se necessário, mas não impedir a requisição (pode
            // ser pública)
            // Ou lançar exceção se e a intenção for falhar autenticação
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ClubContext.clear();
        }
    }
}
