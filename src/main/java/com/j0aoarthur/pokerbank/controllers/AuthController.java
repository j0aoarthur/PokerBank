package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.dtos.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.LoginRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.dtos.request.RefreshTokenRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.AuthResponse;
import com.j0aoarthur.pokerbank.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Registro, login, verificação de conta e gestão de tokens JWT")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Cadastra um novo usuário",
            description = "Cria uma conta e envia e-mail de verificação. A conta fica inativa até a verificação.",
            security = {}
    )
    public ResponseEntity<String> register(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        authService.createUser(authRequestDTO);
        return ResponseEntity.status(201).body("Usuário registrado com sucesso! Verifique seu e-mail para ativar sua conta.");
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autentica o usuário",
            description = "Valida as credenciais e retorna o par de tokens de acesso e refresh.",
            security = {}
    )
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        // BadCredentialsException propagará para GlobalExceptionHandler → 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password())
        );
        AuthResponse tokens = authService.generateTokens(loginRequestDTO.username());
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/verify")
    @Operation(
            summary = "Verifica o e-mail do usuário",
            description = "Ativa a conta a partir do token enviado por e-mail durante o cadastro.",
            security = {}
    )
    public ResponseEntity<String> verifyEmail(
            @Parameter(description = "Token de verificação recebido por e-mail", required = true)
            @RequestParam String token) {
        if (authService.verifyUserEmail(token)) {
            return ResponseEntity.ok("E-mail verificado com sucesso!");
        } else {
            return ResponseEntity.status(400).body("Token de verificação inválido ou expirado.");
        }
    }

    @GetMapping("/forgot-password")
    @Operation(
            summary = "Solicita redefinição de senha",
            description = "Envia um e-mail com link para redefinir a senha. O link expira em 1 hora.",
            security = {}
    )
    public ResponseEntity<String> requestPasswordReset(
            @Parameter(description = "E-mail cadastrado na conta", example = "user@email.com", required = true)
            @RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("E-mail de redefinição de senha enviado com sucesso! Verifique sua caixa de entrada.");
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Redefine a senha",
            description = "Define uma nova senha usando o token recebido por e-mail.",
            security = {}
    )
    public ResponseEntity<String> resetPassword(@RequestBody @Valid NewPasswordDTO newPasswordDTO) {
        authService.resetPassword(newPasswordDTO);
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }

    @PostMapping("/select-club/{clubId}")
    @Operation(
            summary = "Seleciona o clube ativo",
            description = "Gera novos tokens com o contexto do clube selecionado embutido no JWT.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AuthResponse> selectClub(
            @Parameter(description = "ID do clube a selecionar", example = "1", required = true)
            @PathVariable Long clubId) {
        AuthResponse tokens = authService.selectClub(clubId);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Renova o token de acesso",
            description = "Gera um novo par de tokens usando o refresh token ainda válido.",
            security = {}
    )
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        AuthResponse tokens = authService.refreshTokens(request.refreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Realiza o logout",
            description = "Invalida o refresh token, impedindo a geração de novos tokens de acesso.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok("Logout realizado com sucesso.");
    }
}
