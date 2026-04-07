package fr.micromania.service.impl;

import fr.micromania.dto.auth.*;
import fr.micromania.entity.Client;
import fr.micromania.entity.Employe;
import fr.micromania.entity.RememberMeToken;
import fr.micromania.entity.ResetPasswordToken;
import fr.micromania.entity.securite.ConnexionLog;
import fr.micromania.entity.securite.TentativeConnexionEchec;
import fr.micromania.entity.securite.TokenBlacklist;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.ConnexionLogRepository;
import fr.micromania.repository.EmployeRepository;
import fr.micromania.repository.RememberMeTokenRepository;
import fr.micromania.repository.ResetPasswordTokenRepository;
import fr.micromania.repository.TentativeConnexionEchecRepository;
import fr.micromania.repository.TokenBlacklistRepository;
import fr.micromania.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final String USER_TYPE_CLIENT  = fr.micromania.security.UserType.CLIENT;
    private static final String USER_TYPE_EMPLOYE = fr.micromania.security.UserType.EMPLOYE;

    private static final int MAX_TENTATIVES = 5;
    private static final int BLOCAGE_MINUTES = 15;

    private final ClientRepository clientRepository;
    private final EmployeRepository employeRepository;
    private final ResetPasswordTokenRepository resetTokenRepository;
    private final RememberMeTokenRepository rememberMeTokenRepository;
    private final TentativeConnexionEchecRepository tentativeRepository;
    private final ConnexionLogRepository connexionLogRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Value("${app.remember-me.validity-days:30}")
    private long rememberMeValidityDays;

    @Override
    @Transactional
    public AuthResponse loginClient(LoginRequest request, String ip, String userAgent) {
        cleanupRememberMeTokens();

        Client client = clientRepository.findByEmailAndDeletedFalse(request.email())
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        verifierBlocage(USER_TYPE_CLIENT, request.email());

        if (!passwordEncoder.matches(request.motDePasse(), client.getMotDePasse())) {
            enregistrerEchec(USER_TYPE_CLIENT, client.getId(), request.email(), ip, "MOT_DE_PASSE_INCORRECT");
            throw new BadCredentialsException("Identifiants invalides");
        }
        if (!client.isCompteActive()) {
            throw new IllegalStateException("Compte non activé — vérifiez votre email");
        }
        if (!client.isEmailVerifie()) {
            throw new IllegalStateException("Email non vérifié");
        }

        enregistrerSucces(USER_TYPE_CLIENT, client.getId(), request.email(), ip, userAgent);
        client.setDateDerniereConnexion(LocalDateTime.now());
        clientRepository.save(client);

        String jwtToken = genererToken(client.getId(), client.getEmail(), USER_TYPE_CLIENT,
                                       client.getTypeFidelite().getCode());
        String rememberMeToken = request != null && request.rememberMeEnabled()
            ? creerRememberMeToken(client.getEmail(), USER_TYPE_CLIENT)
            : null;

        log.info("Login client : email={} ip={} rememberMe={}", client.getEmail(), ip,
            request != null && request.rememberMeEnabled());

        return new AuthResponse(jwtToken, jwtExpirationMs / 1000,
                                client.getPseudo(), client.getEmail(),
                                client.getTypeFidelite().getCode(), rememberMeToken);
    }

    @Override
    @Transactional
    public AuthResponse loginClientWithRememberMe(String rememberMeToken, String ip, String userAgent) {
        cleanupRememberMeTokens();

        ParsedRememberMeToken parsedToken = parseRememberMeToken(rememberMeToken);
        RememberMeToken persistedToken = rememberMeTokenRepository.findBySerieAndUserType(parsedToken.serie(), USER_TYPE_CLIENT)
            .orElseThrow(() -> new BadCredentialsException("Remember me invalide"));

        if (isRememberMeExpired(persistedToken)) {
            rememberMeTokenRepository.delete(persistedToken);
            throw new BadCredentialsException("Remember me expiré");
        }

        if (!persistedToken.getTokenValue().equals(parsedToken.tokenValue())) {
            rememberMeTokenRepository.delete(persistedToken);
            throw new BadCredentialsException("Remember me invalide");
        }

        Client client = clientRepository.findByEmailAndDeletedFalse(persistedToken.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Compte introuvable"));

        if (!client.isCompteActive() || !client.isEmailVerifie()) {
            rememberMeTokenRepository.delete(persistedToken);
            throw new IllegalStateException("Compte non disponible pour le remember me");
        }

        persistedToken.setTokenValue(generateOpaqueToken());
        persistedToken.setDateDerniere(LocalDateTime.now());
        rememberMeTokenRepository.save(persistedToken);

        client.setDateDerniereConnexion(LocalDateTime.now());
        clientRepository.save(client);

        enregistrerSucces(USER_TYPE_CLIENT, client.getId(), client.getEmail(), ip, userAgent);

        String jwtToken = genererToken(client.getId(), client.getEmail(), USER_TYPE_CLIENT,
                                       client.getTypeFidelite().getCode());
        String renewedRememberMeToken = buildRememberMeCookieValue(persistedToken.getSerie(), persistedToken.getTokenValue());

        log.info("Remember me client : email={} ip={}", client.getEmail(), ip);

        return new AuthResponse(jwtToken, jwtExpirationMs / 1000,
            client.getPseudo(), client.getEmail(), client.getTypeFidelite().getCode(), renewedRememberMeToken);
    }

    @Override
    @Transactional
    public AuthResponse loginEmploye(LoginRequest request, String ip, String userAgent) {
        Employe employe = employeRepository.findByEmailAndDeletedFalse(request.email())
            .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        verifierBlocage(USER_TYPE_EMPLOYE, request.email());

        if (!passwordEncoder.matches(request.motDePasse(), employe.getMotDePasse())) {
            enregistrerEchec(USER_TYPE_EMPLOYE, employe.getId(), request.email(), ip, "MOT_DE_PASSE_INCORRECT");
            throw new BadCredentialsException("Identifiants invalides");
        }
        if (!employe.isActif()) {
            throw new IllegalStateException("Compte employé désactivé");
        }

        enregistrerSucces(USER_TYPE_EMPLOYE, employe.getId(), request.email(), ip, userAgent);
        String token = genererToken(employe.getId(), employe.getEmail(), USER_TYPE_EMPLOYE,
                                    employe.getRole().getCode());
        log.info("Login employé : email={} role={}", employe.getEmail(), employe.getRole().getCode());
        return new AuthResponse(token, jwtExpirationMs / 1000,
                                employe.getPrenom() + " " + employe.getNom(),
                                employe.getEmail(),
                                employe.getRole().getCode());
    }

    @Override
    @Transactional
    public void logout(String token, String rememberMeToken) {
        if (rememberMeToken != null && !rememberMeToken.isBlank()) {
            try {
                ParsedRememberMeToken parsedToken = parseRememberMeToken(rememberMeToken);
                rememberMeTokenRepository.deleteBySerie(parsedToken.serie());
            } catch (RuntimeException e) {
                log.warn("Logout remember me ignoré : {}", e.getMessage());
            }
        }
        if (token != null && !token.isBlank()) {
            try {
                String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
                Claims claims = parserToken(rawToken);
                String jti = claims.getId();
                if (jti != null) {
                    tokenBlacklistRepository.save(TokenBlacklist.builder()
                        .jti(jti)
                        .expireLe(claims.getExpiration().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                        .build());
                    log.info("Token JWT révoqué : jti={}", jti);
                }
            } catch (RuntimeException e) {
                log.warn("Logout JWT blacklist ignoré : {}", e.getMessage());
            }
        }
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

        if (USER_TYPE_CLIENT.equals(userType)) {
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
        tentativeRepository.findByUserTypeAndEmailTente(userType, email).ifPresent(t -> {
            if (t.getBloqueJusquAu() != null && t.getBloqueJusquAu().isAfter(LocalDateTime.now())) {
                throw new AccountLockedException(
                    "Compte temporairement bloqué après " + MAX_TENTATIVES +
                    " tentatives. Réessayez dans " + BLOCAGE_MINUTES + " minutes."
                );
            }
        });
    }

    private void enregistrerEchec(String userType, Long userId, String email, String ip, String motif) {
        log.warn("Échec connexion {} email={} ip={} motif={}", userType, email, ip, motif);

        TentativeConnexionEchec tentative = tentativeRepository
            .findByUserTypeAndEmailTente(userType, email)
            .orElseGet(() -> TentativeConnexionEchec.builder()
                .userType(userType)
                .userId(userId)
                .emailTente(email)
                .ip(ip)
                .nbTentatives(0)
                .build());

        tentative.setNbTentatives(tentative.getNbTentatives() + 1);
        tentative.setIp(ip);
        if (tentative.getNbTentatives() >= MAX_TENTATIVES) {
            tentative.setBloqueJusquAu(LocalDateTime.now().plusMinutes(BLOCAGE_MINUTES));
            log.warn("Compte bloqué {} email={} jusqu'à {}", userType, email, tentative.getBloqueJusquAu());
        }
        tentativeRepository.save(tentative);

        connexionLogRepository.save(ConnexionLog.builder()
            .userType(userType)
            .userId(userId)
            .emailTente(email)
            .ip(ip)
            .succes(false)
            .motifEchec(motif)
            .build());
    }

    private void enregistrerSucces(String userType, Long userId, String email, String ip, String userAgent) {
        log.info("Connexion réussie {} email={} ip={}", userType, email, ip);

        tentativeRepository.deleteByUserTypeAndEmailTente(userType, email);

        connexionLogRepository.save(ConnexionLog.builder()
            .userType(userType)
            .userId(userId)
            .emailTente(email)
            .ip(ip)
            .userAgent(userAgent)
            .succes(true)
            .build());
    }

    private String creerRememberMeToken(String username, String userType) {
        rememberMeTokenRepository.deleteByUsernameAndUserType(username, userType);

        String serie = generateOpaqueToken();
        String tokenValue = generateOpaqueToken();

        RememberMeToken rememberMeToken = RememberMeToken.builder()
            .serie(serie)
            .tokenValue(tokenValue)
            .dateDerniere(LocalDateTime.now())
            .username(username)
            .userType(userType)
            .build();

        rememberMeTokenRepository.save(rememberMeToken);
        return buildRememberMeCookieValue(serie, tokenValue);
    }

    private String generateOpaqueToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String buildRememberMeCookieValue(String serie, String tokenValue) {
        return serie + ":" + tokenValue;
    }

    private ParsedRememberMeToken parseRememberMeToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank() || !rawToken.contains(":")) {
            throw new IllegalArgumentException("Remember me mal formé");
        }

        String[] parts = rawToken.split(":", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Remember me mal formé");
        }
        return new ParsedRememberMeToken(parts[0], parts[1]);
    }

    private boolean isRememberMeExpired(RememberMeToken rememberMeToken) {
        return rememberMeToken.getDateDerniere().isBefore(LocalDateTime.now().minusDays(rememberMeValidityDays));
    }

    private void cleanupRememberMeTokens() {
        rememberMeTokenRepository.deleteOlderThan(LocalDateTime.now().minusDays(rememberMeValidityDays));
    }

    private record ParsedRememberMeToken(String serie, String tokenValue) {}

    public static class BadCredentialsException extends RuntimeException {
        public BadCredentialsException(String msg) { super(msg); }
    }

    public static class AccountLockedException extends RuntimeException {
        public AccountLockedException(String msg) { super(msg); }
    }
}
