package com.monprojet.boutiquejeux.exception;

/**
 * Exception levée par ApiClient quand l'API renvoie un code HTTP >= 400.
 * Porte le status HTTP pour permettre une gestion fine côté contrôleur.
 */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }

    public boolean isUnauthorized()  { return status == 401; }
    public boolean isForbidden()     { return status == 403; }
    public boolean isNotFound()      { return status == 404; }
    public boolean isConflict()      { return status == 409; }
    public boolean isValidation()    { return status == 400; }
    public boolean isLocked()        { return status == 429; }
    public boolean isServerError()   { return status >= 500; }

    @Override
    public String toString() {
        return "ApiException[" + status + "] " + getMessage();
    }
}
