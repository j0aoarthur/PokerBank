package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.dtos.response.AuthResponse;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.email.EmailService;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.UserRepository;
import com.j0aoarthur.pokerbank.security.CustomUserDetails;
import com.j0aoarthur.pokerbank.security.TokenService;
import com.j0aoarthur.pokerbank.services.AuthService;
import com.j0aoarthur.pokerbank.services.ClubMemberService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsService, AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private final EmailService emailService;
    private final AuthContextService authContextService;
    private final ClubMemberService clubMemberService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.getUserByUsername(username);

        return CustomUserDetails.create(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o username: " + username));
    }

    @Override
    public AuthResponse generateTokens(String username) {
        User user = this.getUserByUsername(username);
        String accessToken = tokenService.generateUserToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshTokens(String refreshToken) {
        if (!tokenService.validateRefreshToken(refreshToken)) {
            throw new IllegalStateException("Token de atualização inválido ou expirado.");
        }

        String username = tokenService.extractUsername(refreshToken);
        User user = getUserByUsername(username);

        Claims claims = tokenService.extractAllClaims(refreshToken);
        Long clubId = claims.get("clubId", Long.class);
        String roleStr = claims.get("role", String.class);

        String newAccessToken;
        String newRefreshToken;

        if (clubId != null && roleStr != null) {
            Role role = Role.valueOf(roleStr);
            newAccessToken = tokenService.generateClubToken(user, clubId, role);
            newRefreshToken = tokenService.generateClubRefreshToken(user, clubId, role);
        } else {
            newAccessToken = tokenService.generateUserToken(user);
            newRefreshToken = tokenService.generateRefreshToken(user);
        }

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public User createUser(AuthRequestDTO authRequestDTO) {
        var encodedPassword = passwordEncoder.encode(authRequestDTO.password());
        User user = new User(authRequestDTO, encodedPassword);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já está em uso.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        // Gerar token de verificação único (24 horas de validade)
        user.generateVerificationToken();

        // Enviar e-mail de verificação (Mudar o link para o seu domínio real em produção)
        String verificationLink = frontendBaseUrl + "/auth/verify?token=" + user.getVerificationToken();
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
        } catch (Exception e) {
//            throw new RuntimeException("Erro ao enviar e-mail de verificação: " + e.getMessage());
            logger.error("Falha ao enviar e-mail de verificação para {}: {}", user.getEmail(), e.getMessage());
        }

        return userRepository.save(user);
    }

    @Override
    public boolean verifyUserEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token de verificação inválido"));

        if (user.getVerificationTokenExpiration() < System.currentTimeMillis()) {
            throw new IllegalStateException("Token de verificação expirado");
        }

        if (user.getIsVerified()) {
            return false;
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiration(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o e-mail: " + email));

        // Verifica se o usuário pediu esse e-mail recentemente e está pedindo de novo em menos de 5 minutos
        if (user.getResetTokenExpiration() != null && user.getResetTokenExpiration() > System.currentTimeMillis() - 5 * 60 * 1000) {
            throw new IllegalStateException("Você já solicitou uma redefinição de senha recentemente. Espere alguns minutos antes de tentar novamente.");
        }

        // Gerar token de redefinição de senha (15 minutos de validade)
        user.generateResetToken();

        // Enviar e-mail com o link de redefinição de senha
        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + user.getResetToken();
        try {
            emailService.sendResetEmail(user.getEmail(), user.getUsername(), resetLink);
        } catch (Exception e) {
//            throw new RuntimeException("Erro ao enviar e-mail de redefinição de senha: " + e.getMessage());
            logger.error("Falha ao enviar e-mail de redefinição de senha para {}: {}", user.getEmail(), e.getMessage());

        }

        userRepository.save(user);
    }

    @Override
    public void resetPassword(NewPasswordDTO newPasswordDTO) {
        User user = userRepository.findByResetToken(newPasswordDTO.passwordToken())
                .orElseThrow(() -> new EntityNotFoundException("Token de redefinição de senha inválido"));

        if (user.getResetTokenExpiration() < System.currentTimeMillis()) {
            throw new IllegalStateException("Token de redefinição de senha expirado");
        }

        user.setPassword(passwordEncoder.encode(newPasswordDTO.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }

    @Override
    public AuthResponse selectClub(Long clubId) {
        User currentUser = authContextService.getCurrentUser();

        var roleOpt = clubMemberService.getMemberByUserAndClub(currentUser.getId(), clubId);

        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não é membro do clube com ID: " + clubId);
        }

        Role userRole = roleOpt.get().getRole();

        // Gera um novo token com as informações do clube e do papel (role)
        String accessToken = tokenService.generateClubToken(currentUser, clubId, userRole);
        String refreshToken = tokenService.generateClubRefreshToken(currentUser, clubId, userRole);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(String token) {
        tokenService.deleteRefreshTokenByToken(token);
    }


}