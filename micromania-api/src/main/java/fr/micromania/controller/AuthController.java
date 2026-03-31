package fr.micromania.controller;

import fr.micromania.dto.auth.*;
import fr.micromania.service.AuthService;
import fr.micromania.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService   authService;
    private final ClientService clientService;

    /** Inscription client public */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest http) {

        clientService.creerDepuisInscription(request);
        AuthResponse response = authService.loginClient(
            new LoginRequest(request.email(), request.motDePasse()),
            http.getRemoteAddr(),
            http.getHeader("User-Agent")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Login client */
    @PostMapping("/login/client")
    public ResponseEntity<AuthResponse> loginClient(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {

        return ResponseEntity.ok(authService.loginClient(
            request, http.getRemoteAddr(), http.getHeader("User-Agent")));
    }

    /** Login employé (back-office) */
    @PostMapping("/login/employe")
    public ResponseEntity<AuthResponse> loginEmploye(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {

        return ResponseEntity.ok(authService.loginEmploye(
            request, http.getRemoteAddr(), http.getHeader("User-Agent")));
    }

    /** Logout (invalide le token côté client) */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization) {

        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    /** Refresh token */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Authorization") String authorization) {

        return ResponseEntity.ok(authService.refresh(authorization));
    }

    /** Vérification email */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        clientService.verifierEmail(token);
        return ResponseEntity.noContent().build();
    }

    /** Demande de reset mot de passe */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        authService.demanderResetPassword(email);
        return ResponseEntity.noContent().build(); // Toujours 204 pour ne pas révéler l'existence du compte
    }

    /** Reset mot de passe avec token */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /** Changement de mot de passe (utilisateur connecté) */
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changerMotDePasse(idClient, request);
        return ResponseEntity.noContent().build();
    }
}
