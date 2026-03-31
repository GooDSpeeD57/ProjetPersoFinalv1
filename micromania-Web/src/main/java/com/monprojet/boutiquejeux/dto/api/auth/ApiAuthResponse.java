package com.monprojet.boutiquejeux.dto.api.auth;

public record ApiAuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String pseudo,
        String email,
        String typeFidelite
) {}
