package fr.micromania.security;

import fr.micromania.repository.TokenBlacklistRepository;
import fr.micromania.service.impl.AuthServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthServiceImpl authService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                Claims claims = authService.parserToken(token);

                String jti = claims.getId();
                if (jti != null && tokenBlacklistRepository.existsByJti(jti)) {
                    log.debug("Token JWT révoqué (blacklist) : jti={}", jti);
                    SecurityContextHolder.clearContext();
                    chain.doFilter(request, response);
                    return;
                }

                Long   userId   = Long.parseLong(claims.getSubject());
                String userType = claims.get("userType", String.class);
                String role     = claims.get("role",     String.class);

                // Préfixe ROLE_ pour Spring Security
                String authority = UserType.CLIENT.equals(userType)
                    ? "ROLE_CLIENT"
                    : "ROLE_" + role;       // ROLE_VENDEUR, ROLE_MANAGER, ROLE_ADMIN

                var auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
                );
                auth.setDetails(userType);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException e) {
                log.debug("Token JWT invalide : {}", e.getMessage());
                // On laisse la chaîne continuer — Spring Security renverra 401
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/images/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/assets/")
                || path.startsWith("/webjars/")
                || path.startsWith("/favicon")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".webp")
                || path.endsWith(".svg");
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
