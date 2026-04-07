package fr.micromania.exception;

import fr.micromania.service.impl.AuthServiceImpl.AccountLockedException;
import fr.micromania.service.impl.AuthServiceImpl.BadCredentialsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.NoSuchFileException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 400 — Validation @Valid ────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Valeur invalide",
                (a, b) -> a   // conserve le premier message en cas de doublon
            ));

        return ResponseEntity.badRequest().body(
            new ErrorResponse(400, "Données invalides", request.getRequestURI(), errors));
    }

    // ── 400 — Règle métier violée ──────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.debug("Argument invalide : {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
            new ErrorResponse(400, ex.getMessage(), request.getRequestURI()));
    }

    // ── 400 — État invalide ────────────────────────────────────
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        log.debug("État invalide : {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
            new ErrorResponse(400, ex.getMessage(), request.getRequestURI()));
    }

    // ── 429 — Compte bloqué (trop de tentatives) ─────────────
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLocked(
            AccountLockedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(429).body(
            new ErrorResponse(429, ex.getMessage(), request.getRequestURI()));
    }

    // ── 401 — Token JWT invalide / expiré ────────────────────
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(
            io.jsonwebtoken.JwtException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse(401, "Token invalide ou expiré", request.getRequestURI()));
    }

    // ── 401 — Mauvaises credentials ───────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse(401, ex.getMessage(), request.getRequestURI()));
    }

    // ── 403 — Accès refusé ────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorResponse(403, "Accès refusé", request.getRequestURI()));
    }

    // ── 403 — Violation sécurité applicative ──────────────────
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurity(
            SecurityException ex,
            HttpServletRequest request) {

        log.warn("Violation sécurité : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorResponse(403, "Accès refusé", request.getRequestURI()));
    }

    // ── 404 — Entité introuvable ───────────────────────────────
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse(404, ex.getMessage(), request.getRequestURI()));
    }

    // ── 409 — Conflit (doublon, état incompatible) ─────────────
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn("Violation contrainte BDD : {}", ex.getMostSpecificCause().getMessage());
        String message = detecterConflitMessage(ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse(409, message, request.getRequestURI()));
    }

    @ExceptionHandler({NoResourceFoundException.class, NoSuchFileException.class})
    public ResponseEntity<ErrorResponse> handleStaticNotFound(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(404, "Ressource introuvable", request.getRequestURI()));
    }

    // ── 500 — Erreur inattendue ────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Erreur inattendue sur {} : {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(
            new ErrorResponse(500, "Erreur interne — veuillez réessayer", request.getRequestURI()));
    }

    // ── Helper ────────────────────────────────────────────────
    private String detecterConflitMessage(String cause) {
        if (cause == null) return "Conflit de données";
        if (cause.contains("email"))    return "Cet email est déjà utilisé";
        if (cause.contains("pseudo"))   return "Ce pseudo est déjà utilisé";
        if (cause.contains("telephone")) return "Ce téléphone est déjà utilisé";
        if (cause.contains("sku"))      return "Ce SKU existe déjà";
        if (cause.contains("ean"))      return "Cet EAN existe déjà";
        if (cause.contains("slug"))     return "Ce slug existe déjà";
        return "Conflit de données — vérifiez les informations saisies";
    }
}
