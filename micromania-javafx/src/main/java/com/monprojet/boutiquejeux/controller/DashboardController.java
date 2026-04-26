package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.dto.stats.StatsDto;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class DashboardController {

    @FXML private Label labelBienvenue;
    @FXML private Label labelDate;
    @FXML private Label labelVentesJour;
    @FXML private Label labelCAJour;
    @FXML private Label labelReprisesJour;
    @FXML private Label labelStockBas;
    @FXML private Label labelNbClients;
    @FXML private Label labelMagasin;

    private MainController mainController;

    /** Injecté par MainController après le chargement FXML */
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @FXML
    public void initialize() {
        SessionManager s = SessionManager.getInstance();
        labelBienvenue.setText("Bonjour, " + s.getPrenom() + " \uD83D\uDC4B");
        labelDate.setText(LocalDate.now().toString());
        labelMagasin.setText(s.getMagasinNom() != null ? s.getMagasinNom() : "—");

        chargerStats();
    }

    // ── Raccourcis accès rapides ──────────────────────────────────

    @FXML private void handleNouveauClient() { if (mainController != null) mainController.handleCreerClient(); }
    @FXML private void handleNouvelleVente() { if (mainController != null) mainController.handleVente(); }
    @FXML private void handleReprise()       { if (mainController != null) mainController.handleReprise(); }
    @FXML private void handleRecherche()     { if (mainController != null) mainController.handleRecherche(); }

    // ── Stats ─────────────────────────────────────────────────────

    private void chargerStats() {
        Long magId = SessionManager.getInstance().getMagasinId();
        if (magId == null) return;

        Task<StatsDto> task = new Task<>() {
            @Override protected StatsDto call() throws Exception {
                return ApiClient.getInstance().get("/stats/dashboard?magasinId=" + magId, StatsDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            StatsDto stats = task.getValue();
            Platform.runLater(() -> {
                labelVentesJour.setText(String.valueOf(stats.ventesAujourdhui));
                labelCAJour.setText(stats.caAujourdhui + " €");
                labelReprisesJour.setText(String.valueOf(stats.reprisesAujourdhui));
                labelStockBas.setText(String.valueOf(stats.stocksBas));
                labelNbClients.setText(String.valueOf(stats.totalClients));
            });
        });
        task.setOnFailed(e -> {
            // Stats non disponibles — pas bloquant
            Platform.runLater(() -> {
                labelVentesJour.setText("—");
                labelCAJour.setText("—");
                labelReprisesJour.setText("—");
                labelStockBas.setText("—");
                labelNbClients.setText("—");
            });
        });
        new Thread(task).start();
    }
}
