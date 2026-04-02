package com.monprojet.boutiquejeux.service;

import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientSessionService {

    private final ApiService apiService;

    public void populateClientSession(HttpSession session, ApiAuthResponse authResponse) {
        if (session == null || authResponse == null) {
            return;
        }

        session.setAttribute("jwt", authResponse.accessToken());
        session.setAttribute("userEmail", authResponse.email());
        session.setAttribute("userPseudo", authResponse.pseudo());
        session.setAttribute("userTypeFidelite", authResponse.typeFidelite());

        try {
            var panier = apiService.getPanier(authResponse.accessToken());
            int count = (panier != null && panier.lignes() != null)
                ? panier.lignes().stream().mapToInt(l -> l.quantite()).sum()
                : 0;
            session.setAttribute("cartCount", count);
        } catch (RuntimeException e) {
            log.warn("Impossible de recharger le panier dans la session : {}", e.getMessage());
            session.setAttribute("cartCount", 0);
        }
    }
}
