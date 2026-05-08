package com.hyperativa.security.controller;

import com.hyperativa.security.dto.AuthRequest;
import com.hyperativa.security.dto.AuthResponse;
import com.hyperativa.security.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        // Tenta autenticar as credenciais
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        // Se a execução chegar aqui, as credenciais estão corretas
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.username());
            return ResponseEntity.ok(new AuthResponse(token));
        }

        // Lançado apenas por garantia, o AuthenticationManager lança exceção em caso de falha
        throw new RuntimeException("Credenciais inválidas");
    }
}
