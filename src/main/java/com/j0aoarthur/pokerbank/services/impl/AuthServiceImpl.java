package com.j0aoarthur.pokerbank.services.impl;

import com.j0aoarthur.pokerbank.dtos.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.dtos.response.AuthResponse;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.email.EmailService;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.AccountRepository;
import com.j0aoarthur.pokerbank.security.CustomUserDetails;
import com.j0aoarthur.pokerbank.security.TokenService;
import com.j0aoarthur.pokerbank.services.AuthService;
import com.j0aoarthur.pokerbank.services.MemberService;
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

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private final EmailService emailService;
    private final AuthContextService authContextService;
    private final MemberService memberService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.getUserByUsername(username);

        return CustomUserDetails.create(account);
    }

    private Account getUserByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o username: " + username));
    }

    @Override
    public AuthResponse generateTokens(String username) {
        Account account = this.getUserByUsername(username);
        String accessToken = tokenService.generateUserToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshTokens(String refreshToken) {
        if (!tokenService.validateRefreshToken(refreshToken)) {
            throw new IllegalStateException("Token de atualização inválido ou expirado.");
        }

        String username = tokenService.extractUsername(refreshToken);
        Account account = getUserByUsername(username);

        Claims claims = tokenService.extractAllClaims(refreshToken);
        Long clubId = claims.get("clubId", Long.class);
        String roleStr = claims.get("role", String.class);

        String newAccessToken;
        String newRefreshToken;

        if (clubId != null && roleStr != null) {
            Role role = Role.valueOf(roleStr);
            newAccessToken = tokenService.generateClubToken(account, clubId, role);
            newRefreshToken = tokenService.generateClubRefreshToken(account, clubId, role);
        } else {
            newAccessToken = tokenService.generateUserToken(account);
            newRefreshToken = tokenService.generateRefreshToken(account);
        }

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public Account createUser(AuthRequestDTO authRequestDTO) {
        var encodedPassword = passwordEncoder.encode(authRequestDTO.password());
        Account account = new Account(authRequestDTO, encodedPassword);

        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já está em uso.");
        }

        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        // Gerar token de verificação único (24 horas de validade)
        account.generateVerificationToken();

        // Enviar e-mail de verificação (Mudar o link para o seu domínio real em produção)
        String verificationLink = frontendBaseUrl + "/auth/verify?token=" + account.getVerificationToken();
        try {
            emailService.sendVerificationEmail(account.getEmail(), account.getUsername(), verificationLink);
        } catch (Exception e) {
//            throw new RuntimeException("Erro ao enviar e-mail de verificação: " + e.getMessage());
            logger.error("Falha ao enviar e-mail de verificação para {}: {}", account.getEmail(), e.getMessage());
        }

        return accountRepository.save(account);
    }

    @Override
    public boolean verifyUserEmail(String token) {
        Account account = accountRepository.findByVerificationToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token de verificação inválido"));

        if (account.getVerificationTokenExpiration() < System.currentTimeMillis()) {
            throw new IllegalStateException("Token de verificação expirado");
        }

        if (account.getIsVerified()) {
            return false;
        }

        account.setIsVerified(true);
        account.setVerificationToken(null);
        account.setVerificationTokenExpiration(null);
        accountRepository.save(account);
        return true;
    }

    @Override
    public void requestPasswordReset(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o e-mail: " + email));

        // Verifica se o usuário pediu esse e-mail recentemente e está pedindo de novo em menos de 5 minutos
        if (account.getResetTokenExpiration() != null && account.getResetTokenExpiration() > System.currentTimeMillis() - 5 * 60 * 1000) {
            throw new IllegalStateException("Você já solicitou uma redefinição de senha recentemente. Espere alguns minutos antes de tentar novamente.");
        }

        // Gerar token de redefinição de senha (15 minutos de validade)
        account.generateResetToken();

        // Enviar e-mail com o link de redefinição de senha
        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + account.getResetToken();
        try {
            emailService.sendResetEmail(account.getEmail(), account.getUsername(), resetLink);
        } catch (Exception e) {
//            throw new RuntimeException("Erro ao enviar e-mail de redefinição de senha: " + e.getMessage());
            logger.error("Falha ao enviar e-mail de redefinição de senha para {}: {}", account.getEmail(), e.getMessage());

        }

        accountRepository.save(account);
    }

    @Override
    public void resetPassword(NewPasswordDTO newPasswordDTO) {
        Account account = accountRepository.findByResetToken(newPasswordDTO.passwordToken())
                .orElseThrow(() -> new EntityNotFoundException("Token de redefinição de senha inválido"));

        if (account.getResetTokenExpiration() < System.currentTimeMillis()) {
            throw new IllegalStateException("Token de redefinição de senha expirado");
        }

        account.setPassword(passwordEncoder.encode(newPasswordDTO.newPassword()));
        account.setResetToken(null);
        account.setResetTokenExpiration(null);
        accountRepository.save(account);
    }

    @Override
    public AuthResponse selectClub(Long clubId) {
        Account currentUser = authContextService.getCurrentUser();

        var roleOpt = memberService.getMemberByUserAndClub(currentUser.getId(), clubId);

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