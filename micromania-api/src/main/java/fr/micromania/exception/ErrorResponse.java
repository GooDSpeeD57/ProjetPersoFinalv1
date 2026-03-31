package fr.micromania.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String message,
    String path,
    LocalDateTime timestamp,
    Map<String, String> errors   // champs invalides (@Valid)
) {
    /** Constructeur sans détails de validation */
    public ErrorResponse(int status, String message, String path) {
        this(status, message, path, LocalDateTime.now(), null);
    }

    /** Constructeur avec erreurs de validation */
    public ErrorResponse(int status, String message, String path, Map<String, String> errors) {
        this(status, message, path, LocalDateTime.now(), errors);
    }
}
