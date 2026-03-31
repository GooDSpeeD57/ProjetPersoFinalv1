package fr.micromania.dto.auth;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String pseudo,
    String email,
    String typeFidelite
) {
    public AuthResponse(String accessToken, Long expiresIn, String pseudo, String email, String typeFidelite) {
        this(accessToken, "Bearer", expiresIn, pseudo, email, typeFidelite);
    }
}
