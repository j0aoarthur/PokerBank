package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static java.util.UUID.randomUUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o username: " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public User createUser(AuthRequestDTO authRequestDTO) {
        var encodedPassword = passwordEncoder.encode(authRequestDTO.password());
        User user = new User(authRequestDTO, encodedPassword);

        // Gerar token de verificação único (24 horas de validade)
        user.generateVerificationToken();

        // Enviar e-mail de verificação (Mudar o link para o seu domínio real em produção)
        String verificationLink = "http://localhost:8080/auth/verify?token=" + user.getVerificationToken();
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de verificação: " + e.getMessage());
        }

        return userRepository.save(user);
    }

    public boolean verifyUserEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token de verificação inválido"));

        if (user.getVerificationTokenExpiration() < System.currentTimeMillis()) {
            throw new RuntimeException("Token de verificação expirado");
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

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));

        // Verifica se o usuário pediu esse e-mail recentemente e está pedindo de novo em menos de 5 minutos
        if (user.getResetTokenExpiration() != null && user.getResetTokenExpiration() > System.currentTimeMillis() - 5 * 60 * 1000) {
            throw new RuntimeException("Você já solicitou uma redefinição de senha recentemente. Espere alguns minutos antes de tentar novamente.");
        }

        // Gerar token de redefinição de senha (15 minutos de validade)
        user.generateResetToken();

        // Enviar e-mail com o link de redefinição de senha
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + user.getResetToken();
        try {
            emailService.sendResetEmail(user.getEmail(), user.getUsername(), resetLink);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de redefinição de senha: " + e.getMessage());
        }

        userRepository.save(user);
    }

    public void resetPassword(NewPasswordDTO newPasswordDTO) {
        User user = userRepository.findByResetToken(newPasswordDTO.passwordToken())
                .orElseThrow(() -> new RuntimeException("Token de redefinição de senha inválido"));

        if (user.getResetTokenExpiration() < System.currentTimeMillis()) {
            throw new RuntimeException("Token de redefinição de senha expirado");
        }

        user.setPassword(passwordEncoder.encode(newPasswordDTO.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }
}