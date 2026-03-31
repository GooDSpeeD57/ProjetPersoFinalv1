package fr.micromania.service.impl;

import fr.micromania.dto.auth.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.Employe;
import fr.micromania.entity.ResetPasswordToken;
import fr.micromania.repository.*;
import fr.micromania.service.AuthService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final ClientRepository           clientRepository;
    private final EmployeRepository          employeRepository;
    private final ResetPasswordTokenRepository resetTokenRepository;
    private final PasswordEncoder            passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public AuthResponse loginClient(LoginRequest request, String ip, String userAgent) {
        Client client = clientRepository.findByEmailAndDeletedFalse(request.email())
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        verifierBlocage("CLIENT", request.email());

        if (!passwordEncoder.matches(request.motDePasse(), client.getMotDePasse())) {
            enregistrerEchec("CLIENT", client.getId(), request.email(), ip, "MOT_DE_PASSE_INCORRECT");
            throw new BadCredentialsException("Identifiants invalides");
        }
        if (!client.isCompteActive()) {
            throw new IllegalStateException("Compte non activé — vérifiez votre email");
        }
        if (!client.isEmailVerifie()) {
            throw new IllegalStateException("Email non vérifié");
        }

        enregistrerSucces("CLIENT", client.getId(), request.email(), ip, userAgent);
        String token = genererToken(client.getId(), client.getEmail(), "CLIENT",
                                    client.getTypeFidelite().getCode());
        log.info("Login client : email={} ip={}", client.getEmail(), ip);
        client.setDateDerniereConnexion(LocalDateTime.now());
        clientRepository.save(client);
        return new AuthResponse(token, jwtExpirationMs / 1000,
                                client.getPseudo(), client.getEmail(),
                                client.getTypeFidelite().getCode());
    }

    @Override
    @Transactional
    public AuthResponse loginEmploye(LoginRequest request, String ip, String userAgent) {
        Employe employe = employeRepository.findByEmailAndDeletedFalse(request.email())
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        verifierBlocage("EMPLOYE", request.email());

        if (!passwordEncoder.matches(request.motDePasse(), employe.getMotDePasse())) {
            enregistrerEchec("EMPLOYE", employe.getId(), request.email(), ip, "MOT_DE_PASSE_INCORRECT");
            throw new BadCredentialsException("Identifiants invalides");
        }
        if (!employe.isActif()) {
            throw new IllegalStateException("Compte employé désactivé");
        }

        enregistrerSucces("EMPLOYE", employe.getId(), request.email(), ip, userAgent);
        String token = genererToken(employe.getId(), employe.getEmail(), "EMPLOYE",
                                    employe.getRole().getCode());
        log.info("Login employé : email={} role={}", employe.getEmail(), employe.getRole().getCode());
        return new AuthResponse(token, jwtExpirationMs / 1000,
                                employe.getPrenom() + " " + employe.getNom(),
                                employe.getEmail(),
                                employe.getRole().getCode());
    }

    @Override
    @Transactional
    public void logout(String token) {
        log.info("Logout demandé — token invalidé côté client");
    }

    @Override
    @Transactional
    public void demanderResetPassword(String email) {
        clientRepository.findByEmailAndDeletedFalse(email).ifPresent(client -> {
            resetTokenRepository.deleteExpired(LocalDateTime.now());

            ResetPasswordToken token = ResetPasswordToken.builder()
                .client(client)
                .token(UUID.randomUUID().toString())
                .expireLe(LocalDateTime.now().plusHours(2))
                .utilise(false)
                .build();
            resetTokenRepository.save(token);
            log.info("Reset password demandé : email={} token={}", email, token.getToken());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        ResetPasswordToken resetToken = resetTokenRepository
            .findByTokenAndUtiliseFalseAndExpireLeAfter(request.token(), LocalDateTime.now())
            .orElseThrow(() -> new IllegalArgumentException("Token invalide ou expiré"));

        Client client = resetToken.getClient();
        client.setMotDePasse(passwordEncoder.encode(request.nouveauMotDePasse()));
        clientRepository.save(client);

        resetToken.setUtilise(true);
        resetToken.setDateUtilisation(LocalDateTime.now());
        resetTokenRepository.save(resetToken);

        log.info("Mot de passe réinitialisé pour client id={}", client.getId());
    }

    @Override
    @Transactional
    public void changerMotDePasse(Long idClient, ChangePasswordRequest request) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        if (!passwordEncoder.matches(request.ancienMotDePasse(), client.getMotDePasse())) {
            throw new BadCredentialsException("Ancien mot de passe incorrect");
        }
        client.setMotDePasse(passwordEncoder.encode(request.nouveauMotDePasse()));
        clientRepository.save(client);
        log.info("Mot de passe changé pour client id={}", idClient);
    }

    @Override
    public AuthResponse refresh(String bearerToken) {
        String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
        Claims claims = parserToken(token);

        String userType = claims.get("userType", String.class);
        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String role  = claims.get("role",  String.class);

        String newToken = genererToken(userId, email, userType, role);

        if ("CLIENT".equals(userType)) {
            Client client = clientRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + userId));
            return new AuthResponse(newToken, jwtExpirationMs / 1000,
                                    client.getPseudo(), email,
                                    client.getTypeFidelite().getCode());
        } else {
            Employe emp = employeRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + userId));
            return new AuthResponse(newToken, jwtExpirationMs / 1000,
                                    emp.getPrenom() + " " + emp.getNom(), email, role);
        }
    }

    private String genererToken(Long userId, String email, String userType, String role) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("email",    email)
            .claim("userType", userType)
            .claim("role",     role)
            .issuedAt(now)
            .expiration(expiry)
            .id(UUID.randomUUID().toString())
            .signWith(getSigningKey())
            .compact();
    }

    public Claims parserToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private void verifierBlocage(String userType, String email) {
    }

    private void enregistrerEchec(String userType, Long userId, String email, String ip, String motif) {
        log.warn("Échec connexion {} email={} ip={} motif={}", userType, email, ip, motif);
    }

    private void enregistrerSucces(String userType, Long userId, String email, String ip, String userAgent) {
        log.info("Connexion réussie {} email={} ip={}", userType, email, ip);
    }

    public static class BadCredentialsException extends RuntimeException {
        public BadCredentialsException(String msg) { super(msg); }
    }
}
