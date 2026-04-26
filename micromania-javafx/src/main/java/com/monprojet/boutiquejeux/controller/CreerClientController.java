package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.auth.AuthResponseDto;
import com.monprojet.boutiquejeux.dto.client.CreateClientDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class CreerClientController {

    @FXML private TextField     pseudoField;
    @FXML private TextField     nomField;
    @FXML private TextField     prenomField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     telephoneField;
    @FXML private DatePicker    dateNaissancePicker;
    @FXML private CheckBox      rgpdCheck;
    @FXML private Button        btnCreer;
    @FXML private Label         resultLabel;
    @FXML private ProgressIndicator spinner;

    private static final Pattern EMAIL_RE = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PWD_RE   = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&\\-_])[A-Za-z\\d@$!%*?&\\-_]{8,}$");
    private static final Pattern TEL_RE   = Pattern.compile("^(?:0[67]\\d{8}|\\+33[67]\\d{8})$");

    @FXML
    public void initialize() {
        spinner.setVisible(false);
        resultLabel.setVisible(false);
        dateNaissancePicker.setValue(LocalDate.now().minusYears(18));
    }

    @FXML
    private void handleCreer() {
        if (!valider()) return;

        setLoading(true);

        CreateClientDto body = new CreateClientDto();
        body.pseudo        = pseudoField.getText().trim();
        body.nom           = nomField.getText().trim();
        body.prenom        = prenomField.getText().trim();
        body.email         = emailField.getText().trim().toLowerCase();
        body.motDePasse    = passwordField.getText();
        body.telephone     = telephoneField.getText().trim();
        body.dateNaissance = dateNaissancePicker.getValue().toString();
        body.rgpdConsent   = rgpdCheck.isSelected();

        Task<AuthResponseDto> task = new Task<>() {
            @Override protected AuthResponseDto call() throws Exception {
                return ApiClient.getInstance().postPublic("/auth/register", body, AuthResponseDto.class);
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(false);
            AuthResponseDto auth = task.getValue();
            String email = auth.email != null ? auth.email : emailField.getText();
            showSuccess("\u2705 Compte créé pour " + email + " — 10 points de fidélité offerts !");
            clearForm();
        });

        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            if (ex instanceof ApiException ae && ae.isConflict()) {
                showError("\u274C Email ou pseudo déjà utilisé.");
            } else {
                showError("\u274C " + ex.getMessage());
            }
        });

        new Thread(task).start();
    }

    @FXML private void handleReset() { clearForm(); }

    private boolean valider() {
        String email  = emailField.getText().trim();
        String pwd    = passwordField.getText();
        String pseudo = pseudoField.getText().trim();
        LocalDate dob = dateNaissancePicker.getValue();

        if (pseudo.isEmpty() || nomField.getText().isBlank() || prenomField.getText().isBlank()) {
            showError("Pseudo, nom et prénom sont obligatoires."); return false;
        }
        if (!EMAIL_RE.matcher(email).matches()) {
            showError("Email invalide."); return false;
        }
        if (!PWD_RE.matcher(pwd).matches()) {
            showError("Mot de passe : 8 car. min, maj, min, chiffre et caractère spécial (@$!%*?&-_)."); return false;
        }
        String tel = telephoneField.getText().trim();
        if (!tel.isEmpty() && !TEL_RE.matcher(tel).matches()) {
            showError("Téléphone : mobile français requis (06, 07, +336, +337)."); return false;
        }
        if (dob == null || dob.isAfter(LocalDate.now().minusYears(18))) {
            showError("Le client doit avoir au moins 18 ans."); return false;
        }
        if (!rgpdCheck.isSelected()) {
            showError("Le consentement RGPD est obligatoire."); return false;
        }
        return true;
    }

    private void clearForm() {
        pseudoField.clear(); nomField.clear(); prenomField.clear();
        emailField.clear(); passwordField.clear(); telephoneField.clear();
        dateNaissancePicker.setValue(LocalDate.now().minusYears(18));
        rgpdCheck.setSelected(false);
    }

    private void setLoading(boolean b) {
        Platform.runLater(() -> { btnCreer.setDisable(b); spinner.setVisible(b); });
    }
    private void showError(String msg) {
        Platform.runLater(() -> { resultLabel.setText(msg); resultLabel.setStyle("-fx-text-fill: #e74c3c;"); resultLabel.setVisible(true); });
    }
    private void showSuccess(String msg) {
        Platform.runLater(() -> { resultLabel.setText(msg); resultLabel.setStyle("-fx-text-fill: #2ecc71;"); resultLabel.setVisible(true); });
    }
}
