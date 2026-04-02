package fr.micromania.service;

import fr.micromania.dto.auth.*;

public interface AuthService {

    AuthResponse loginClient(LoginRequest request, String ip, String userAgent);

    AuthResponse loginClientWithRememberMe(String rememberMeToken, String ip, String userAgent);

    AuthResponse loginEmploye(LoginRequest request, String ip, String userAgent);

    void logout(String token, String rememberMeToken);

    void demanderResetPassword(String email);

    void resetPassword(ResetPasswordRequest request);

    void changerMotDePasse(Long idClient, ChangePasswordRequest request);

    AuthResponse refresh(String bearerToken);
}
