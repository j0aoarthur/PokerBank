package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
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

        // Gerar token de verificação único
        // Implementar depois uma lógica para tempo de expiração do token
        user.setVerificationToken(randomUUID().toString());

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
                .orElseThrow(() -> new RuntimeException("Token de verificação inválido ou expirado"));

        if (user.getIsVerified()) {
            return false;
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return true;
    }
}