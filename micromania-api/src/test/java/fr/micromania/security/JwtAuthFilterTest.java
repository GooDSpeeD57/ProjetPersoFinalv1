package fr.micromania.security;

import fr.micromania.service.impl.AuthServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter — tests unitaires")
class JwtAuthFilterTest {

    @Mock AuthServiceImpl authService;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain filterChain;

    @InjectMocks JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Sans header Authorization — filtre passe sans authentifier")
    void sansHeader_laissePasserSansAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Header sans préfixe Bearer — filtre passe sans authentifier")
    void headerSansBearer_laissePasserSansAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Token client valide — injecte ROLE_CLIENT dans le contexte")
    void tokenClientValide_injecteAuthentification() throws Exception {
        Claims claims = Jwts.claims()
                .subject("1")
                .add("email", "alice@test.fr")
                .add("userType", "CLIENT")
                .add("role", "NORMAL")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(authService.parserToken("valid.jwt.token")).thenReturn(claims);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_CLIENT");
        assertThat(auth.getPrincipal()).isEqualTo(1L);
        assertThat(auth.getDetails()).isEqualTo("CLIENT");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Token employé MANAGER — injecte ROLE_MANAGER")
    void tokenEmployeManager_injecteRoleManager() throws Exception {
        Claims claims = Jwts.claims()
                .subject("99")
                .add("email", "manager@test.fr")
                .add("userType", "EMPLOYE")
                .add("role", "MANAGER")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer employe.jwt.token");
        when(authService.parserToken("employe.jwt.token")).thenReturn(claims);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_MANAGER");
        assertThat(auth.getPrincipal()).isEqualTo(99L);
        assertThat(auth.getDetails()).isEqualTo("EMPLOYE");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Token JWT invalide — vide le contexte et laisse continuer la chaîne")
    void tokenInvalide_retourne401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
        when(authService.parserToken("invalid.token.here"))
                .thenThrow(new io.jsonwebtoken.JwtException("Token invalide"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(response);
    }
}