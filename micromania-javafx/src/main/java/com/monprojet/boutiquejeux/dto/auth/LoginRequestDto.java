package com.monprojet.boutiquejeux.dto.auth;

public class LoginRequestDto {
    public String email;
    public String motDePasse;
    public boolean rememberMe;

    public LoginRequestDto(String email, String motDePasse, boolean rememberMe) {
        this.email      = email;
        this.motDePasse = motDePasse;
        this.rememberMe = rememberMe;
    }
}
