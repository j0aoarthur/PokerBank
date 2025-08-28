package com.j0aoarthur.pokerbank.controllers;


import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.LoginRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.DTOs.response.AuthResponse;
import com.j0aoarthur.pokerbank.infra.security.TokenService;
import com.j0aoarthur.pokerbank.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Realiza o cadastro de um novo usuário")
    public ResponseEntity<String> register(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        authService.createUser(authRequestDTO);
        return ResponseEntity.status(201).body("Usuário registrado com sucesso! Verifique seu e-mail para ativar sua conta.");
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza o login do usuário e retorna um token JWT")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        UserDetails userDetails = authService.loadUserByUsername(loginRequestDTO.username());
        String jwt = tokenService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername()));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verifica o e-mail do usuário usando o token de verificação")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if (authService.verifyUserEmail(token)) {
            return ResponseEntity.ok("E-mail verificado com sucesso!");
        } else {
            return ResponseEntity.status(400).body("Token de verificação inválido ou expirado.");
        }
    }

    @GetMapping("/forgot-password")
    @Operation(summary = "Solicita a redefinição de senha e envia um e-mail com o link de redefinição")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        try {
            authService.requestPasswordReset(email);
            return ResponseEntity.ok("E-mail de redefinição de senha enviado com sucesso! Verifique sua caixa de entrada.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao solicitar redefinição de senha: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefine a senha do usuário usando o token de redefinição")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid NewPasswordDTO newPasswordDTO) {
        try {
            authService.resetPassword(newPasswordDTO);
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao redefinir senha: " + e.getMessage());
        }
    }
}
