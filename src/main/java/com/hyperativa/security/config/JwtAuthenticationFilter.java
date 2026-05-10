package com.hyperativa.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.hyperativa.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Verifica se o header existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extrai o token e o usuário (o nome de usuário extraído de dentro do JWT)
        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(jwt);

            // LOG para depuração: Mostra se o filtro conseguiu ler o usuário do token
            log.info("DEBUG: Usuário extraído do Token: {}", username);

            // 3. Se temos um usuário e ele ainda não está autenticado no contexto atual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Valida se o token pertence ao usuário e não está expirado
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Autentica o usuário para esta requisição!
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("DEBUG: Usuário autenticado com sucesso!");
                } else {
                    log.error("DEBUG: Token inválido ou expirado.");
                }
            }
        } catch (Exception e) {
            log.error("DEBUG: Falha ao processar o JWT - {}", e.getMessage());
        }

        // 6. Continua a cadeia de filtros e chega no Controller
        filterChain.doFilter(request, response);
    }
}
