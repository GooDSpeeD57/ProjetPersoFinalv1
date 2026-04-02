package com.monprojet.boutiquejeux.config;

import com.monprojet.boutiquejeux.dto.CartItem;
import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import com.monprojet.boutiquejeux.dto.api.panier.ApiPanier;
import com.monprojet.boutiquejeux.service.ApiService;
import com.monprojet.boutiquejeux.service.RememberMeCookieService;
import com.monprojet.boutiquejeux.service.WebClientSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final WebClientSessionService webClientSessionService;
    private final RememberMeCookieService rememberMeCookieService;
    static final ThreadLocal<String> CURRENT_PASSWORD = new ThreadLocal<>();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String motDePasse = CURRENT_PASSWORD.get();
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
            HttpServletResponse response = attrs != null ? attrs.getResponse() : null;
            boolean rememberMe = request != null && "on".equalsIgnoreCase(request.getParameter("remember-me"));

            ApiAuthResponse resp = apiService.loginClient(email, motDePasse, rememberMe);
            if (resp == null || resp.accessToken() == null) {
                throw new UsernameNotFoundException("Réponse API invalide");
            }

            if (attrs != null) {
                HttpSession session = attrs.getRequest().getSession();
                mergeSessionCartToApi(session, resp.accessToken());
                webClientSessionService.populateClientSession(session, resp);

                if (rememberMe && resp.rememberMeToken() != null && !resp.rememberMeToken().isBlank()) {
                    rememberMeCookieService.writeRememberMeCookie(response, resp.rememberMeToken());
                } else {
                    rememberMeCookieService.clearRememberMeCookie(response);
                }
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

    @SuppressWarnings("unchecked")
    private void mergeSessionCartToApi(HttpSession session, String jwt) {
        Object cartObj = session.getAttribute("cart");
        if (!(cartObj instanceof List<?> list) || list.isEmpty()) {
            return;
        }

        ApiPanier panierApi;
        try {
            panierApi = apiService.getPanier(jwt);
        } catch (RuntimeException e) {
            log.warn("Impossible de récupérer le panier API avant fusion : {}", e.getMessage());
            return;
        }

        for (Object obj : list) {
            if (!(obj instanceof CartItem item)) continue;
            if (item.getVariantId() == null) continue;

            boolean dejaPresent = panierApi != null
                    && panierApi.lignes() != null
                    && panierApi.lignes().stream()
                    .anyMatch(l -> l.idVariant().equals(item.getVariantId()));

            if (dejaPresent) {
                continue;
            }

            try {
                apiService.addLignePanier(jwt, item.getVariantId(), item.getQuantite());
            } catch (RuntimeException e) {
                log.warn("Fusion panier ignorée pour variant {} : {}", item.getVariantId(), e.getMessage());
            }
        }

        session.removeAttribute("cart");
    }
}
