package fr.micromania.controller;

import fr.micromania.dto.auth.*;
import fr.micromania.service.AuthService;
import fr.micromania.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService   authService;
    private final ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest http) {

        clientService.creerDepuisInscription(request);
        AuthResponse response = authService.loginClient(
            new LoginRequest(request.email(), request.motDePasse(), false),
            extractClientIp(http),
            http.getHeader("User-Agent")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login/client")
    public ResponseEntity<AuthResponse> loginClient(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {

        return ResponseEntity.ok(authService.loginClient(
            request, extractClientIp(http), http.getHeader("User-Agent")));
    }

    @PostMapping("/remember-me/client")
    public ResponseEntity<AuthResponse> loginClientWithRememberMe(
            @Valid @RequestBody RememberMeLoginRequest request,
            HttpServletRequest http) {

        return ResponseEntity.ok(authService.loginClientWithRememberMe(
            request.rememberMeToken(), extractClientIp(http), http.getHeader("User-Agent")));
    }

    @PostMapping("/login/employe")
    public ResponseEntity<AuthResponse> loginEmploye(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {

        return ResponseEntity.ok(authService.loginEmploye(
            request, extractClientIp(http), http.getHeader("User-Agent")));
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Remember-Me-Token", required = false) String rememberMeToken) {

        authService.logout(authorization, rememberMeToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Authorization") String authorization) {

        return ResponseEntity.ok(authService.refresh(authorization));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        clientService.verifierEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Email @RequestParam String email) {
        authService.demanderResetPassword(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changerMotDePasse(idClient, request);
        return ResponseEntity.noContent().build();
    }
}
