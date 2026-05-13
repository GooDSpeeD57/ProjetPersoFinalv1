package com.monprojet.boutiquejeux.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Miroir de AuthResponse (Spring Boot API).
 * Champs : accessToken, tokenType, expiresIn, pseudo, email, typeFidelite, rememberMeToken
 *
 * Pour un employé :
 *   typeFidelite = code du rôle  (VENDEUR / MANAGER / ADMIN)
 *   pseudo       = prenom + " " + nom
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponseDto {

    public String accessToken;
    public String tokenType;
    public Long   expiresIn;
    public String pseudo;          // prénom + nom pour un employé
    public String email;
    public String typeFidelite;    // rôle : VENDEUR / MANAGER / ADMIN (ou ROLE_xxx)
    public String rememberMeToken;

    /** Retourne le code rôle normalisé sans préfixe ROLE_ */
    public String getRoleNorm() {
        if (typeFidelite == null) return null;
        return typeFidelite.replace("ROLE_", "").toUpperCase();
    }

    public boolean isEmploye() {
        String r = getRoleNorm();
        return "VENDEUR".equals(r) || "MANAGER".equals(r) || "ADMIN".equals(r);
    }
}
