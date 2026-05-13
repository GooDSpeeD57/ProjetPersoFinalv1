package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.MainApp;
import com.monprojet.boutiquejeux.dto.auth.AuthResponseDto;
import com.monprojet.boutiquejeux.dto.auth.LoginRequestDto;
import com.monprojet.boutiquejeux.dto.employe.EmployeDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.util.prefs.Preferences;

public class LoginController {

    private static final String PREF_JWT        = "rememberedJwt";
    private static final String PREF_EMAIL      = "rememberedEmail";
    private static final String PREF_ROLE       = "rememberedRole";
    private static final String PREF_EXPIRES_AT = "rememberedExpiresAt";

    private static final Preferences PREFS =
        Preferences.userNodeForPackage(LoginController.class);

    @FXML private TextField          emailField;
    @FXML private PasswordField      passwordField;
    @FXML private CheckBox           rememberMeCheck;
    @FXML private Button             loginButton;
    @FXML private Label              errorLabel;
    @FXML private ProgressIndicator  spinner;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        spinner.setVisible(false);
        passwordField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) handleLogin(); });

        // ── Auto-login si token encore valide ───────────────────
        String savedJwt   = PREFS.get(PREF_JWT, null);
        String savedEmail = PREFS.get(PREF_EMAIL, null);
        String savedRole  = PREFS.get(PREF_ROLE, null);
        long   expiresAt  = PREFS.getLong(PREF_EXPIRES_AT, 0);

        if (savedJwt != null && savedEmail != null && expiresAt > System.currentTimeMillis()) {
            emailField.setText(savedEmail);
            rememberMeCheck.setSelected(true);
            setLoading(true);
            SessionManager.getInstance().login(savedJwt, savedEmail, savedRole);

            Task<EmployeDto> checkTask = new Task<>() {
                @Override protected EmployeDto call() throws Exception {
                    return ApiClient.getInstance().get("/employes/me", EmployeDto.class);
                }
            };
            checkTask.setOnSucceeded(e -> {
                setLoading(false);
                EmployeDto me = checkTask.getValue();
                SessionManager.getInstance().setInfos(me.prenom, me.magasinId, me.magasinNom);
                try { MainApp.showMain(); }
                catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
            });
            checkTask.setOnFailed(e -> {
                setLoading(false);
                clearSavedCredentials();
                SessionManager.getInstance().logout();
                Platform.runLater(() -> emailField.setText(savedEmail));
            });
            new Thread(checkTask).start();

        } else if (savedEmail != null) {
            emailField.setText(savedEmail);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String pwd   = passwordField.getText();

        if (email.isEmpty() || pwd.isEmpty()) {
            showError("Email et mot de passe obligatoires.");
            return;
        }

        setLoading(true);
        boolean rememberMe = rememberMeCheck.isSelected();

        Task<AuthResponseDto> task = new Task<>() {
            @Override protected AuthResponseDto call() throws Exception {
                return ApiClient.getInstance().postPublic(
                    "/auth/login/employe",
                    new LoginRequestDto(email, pwd, rememberMe),
                    AuthResponseDto.class
                );
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            AuthResponseDto auth = task.getValue();

            if (!auth.isEmploye()) {
                showError("Accès réservé aux employés.");
                return;
            }

            // Sauvegarder si "Se souvenir de moi" coché
            if (rememberMe && auth.expiresIn != null) {
                long exp = System.currentTimeMillis() + auth.expiresIn * 1000L;
                PREFS.put(PREF_JWT,        auth.accessToken);
                PREFS.put(PREF_EMAIL,      email);
                PREFS.put(PREF_ROLE,       auth.typeFidelite);
                PREFS.putLong(PREF_EXPIRES_AT, exp);
            } else {
                clearSavedCredentials();
            }

            SessionManager.getInstance().login(auth.accessToken, email, auth.typeFidelite);

            // Récupérer magasinId via /employes/me
            Task<EmployeDto> meTask = new Task<>() {
                @Override protected EmployeDto call() throws Exception {
                    return ApiClient.getInstance().get("/employes/me", EmployeDto.class);
                }
            };
            meTask.setOnSucceeded(ev -> {
                EmployeDto me = meTask.getValue();
                SessionManager.getInstance().setInfos(me.prenom, me.magasinId, me.magasinNom);
                try { MainApp.showMain(); }
                catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
            });
            meTask.setOnFailed(ev -> {
                // magasinId manquant mais login OK — on ouvre quand même
                try { MainApp.showMain(); }
                catch (Exception ex) { showError("Erreur : " + ex.getMessage()); }
            });
            new Thread(meTask).start();
        });

        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            if (ex instanceof ApiException ae) {
                showError(switch (ae.getStatus()) {
                    case 401 -> "Email ou mot de passe incorrect.";
                    case 429 -> "Compte temporairement bloqué (trop de tentatives).";
                    case 0   -> "Serveur injoignable. Vérifiez que l'API est démarrée.";
                    default  -> ae.getMessage();
                });
            } else {
                showError(ex.getMessage() != null ? ex.getMessage() : "Connexion impossible.");
            }
        });

        new Thread(task).start();
    }

    public static void clearSavedCredentials() {
        PREFS.remove(PREF_JWT);
        PREFS.remove(PREF_EMAIL);
        PREFS.remove(PREF_ROLE);
        PREFS.remove(PREF_EXPIRES_AT);
    }

    private void showError(String msg) {
        Platform.runLater(() -> { errorLabel.setText(msg); errorLabel.setVisible(true); });
    }

    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            loginButton.setDisable(loading);
            spinner.setVisible(loading);
            errorLabel.setVisible(false);
        });
    }
}
