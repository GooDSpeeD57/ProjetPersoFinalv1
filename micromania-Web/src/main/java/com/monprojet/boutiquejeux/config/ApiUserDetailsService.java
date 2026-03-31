package com.monprojet.boutiquejeux.config;

import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService {

    private final ApiService apiService;
    static final ThreadLocal<String> CURRENT_PASSWORD = new ThreadLocal<>();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String motDePasse = CURRENT_PASSWORD.get();
        try {
            ApiAuthResponse resp = apiService.loginClient(email, motDePasse);
            if (resp == null || resp.accessToken() == null) {
                throw new UsernameNotFoundException("Réponse API invalide");
            }

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpSession session = attrs.getRequest().getSession();
                session.setAttribute("jwt", resp.accessToken());
                session.setAttribute("userEmail", resp.email());
                session.setAttribute("userPseudo", resp.pseudo());
                session.setAttribute("userTypeFidelite", resp.typeFidelite());
            }

            return User.builder()
                    .username(email)
                    .password("{noop}" + motDePasse)
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_CLIENT")))
                    .build();
        } catch (RuntimeException e) {
            log.warn("Login API échoué pour {} : {}", email, e.getMessage());
            throw new UsernameNotFoundException("Identifiants invalides");
        } finally {
            CURRENT_PASSWORD.remove();
        }
    }
}
