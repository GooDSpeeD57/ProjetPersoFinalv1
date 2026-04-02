package com.monprojet.boutiquejeux.config;

import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import com.monprojet.boutiquejeux.service.ApiService;
import com.monprojet.boutiquejeux.service.RememberMeCookieService;
import com.monprojet.boutiquejeux.service.WebClientSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRememberMeAuthenticationFilter extends OncePerRequestFilter {

    private final ApiService apiService;
    private final RememberMeCookieService rememberMeCookieService;
    private final WebClientSessionService webClientSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null
            && !isStaticResource(request)
            && !isAuthPost(request)) {

            HttpSession existingSession = request.getSession(false);
            if (existingSession == null || existingSession.getAttribute("jwt") == null) {
                rememberMeCookieService.readRememberMeCookie(request).ifPresent(cookieValue -> {
                    try {
                        ApiAuthResponse authResponse = apiService.loginClientWithRememberMe(cookieValue);
                        if (authResponse != null && authResponse.accessToken() != null && !authResponse.accessToken().isBlank()) {
                            HttpSession session = request.getSession(true);
                            webClientSessionService.populateClientSession(session, authResponse);
                            if (authResponse.rememberMeToken() != null && !authResponse.rememberMeToken().isBlank()) {
                                rememberMeCookieService.writeRememberMeCookie(response, authResponse.rememberMeToken());
                            }

                            UserDetails principal = User.builder()
                                .username(authResponse.email())
                                .password("N/A")
                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
                                .build();

                            UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
                            SecurityContextHolder.setContext(context);
                            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
                        }
                    } catch (RuntimeException e) {
                        log.warn("Remember me web refusé : {}", e.getMessage());
                        rememberMeCookieService.clearRememberMeCookie(response);
                    }
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isStaticResource(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/img/")
            || path.startsWith("/favicon");
    }

    private boolean isAuthPost(HttpServletRequest request) {
        return "/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }
}
