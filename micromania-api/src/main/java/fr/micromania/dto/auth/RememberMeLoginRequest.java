package fr.micromania.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RememberMeLoginRequest(
    @NotBlank
    String rememberMeToken
) {}
