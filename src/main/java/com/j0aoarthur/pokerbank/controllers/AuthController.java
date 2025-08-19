package com.j0aoarthur.pokerbank.controllers;


import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.LoginRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.AuthResponse;
import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.infra.security.TokenService;
import com.j0aoarthur.pokerbank.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "Realiza o cadastro de um novo usuário")
    public ResponseEntity register(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        authService.createUser(authRequestDTO);
        return ResponseEntity.status(201).body("Usuário registrado com sucesso!");
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
}
