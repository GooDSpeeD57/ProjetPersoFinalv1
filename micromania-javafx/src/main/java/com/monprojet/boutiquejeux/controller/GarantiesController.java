package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.client.ClientSummaryDto;
import com.monprojet.boutiquejeux.dto.garantie.GarantieDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.service.ClientService;
import com.monprojet.boutiquejeux.util.AlertHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class GarantiesController {

    @FXML private TextField rechercheClientField;
    @FXML private Label     labelClient;
    @FXML private TableView<GarantieDto> tableGaranties;
    @FXML private TableColumn<GarantieDto, String> colProduit;
    @FXML private TableColumn<GarantieDto, String> colDateAchat;
    @FXML private TableColumn<GarantieDto, String> colDateFin;
    @FXML private TableColumn<GarantieDto, String> colStatut;
    @FXML private TableColumn<GarantieDto, String> colDuree;
    @FXML private ProgressIndicator spinner;
    @FXML private Label labelNbGaranties;

    @FXML
    public void initialize() {
        spinner.setVisible(false);
        tableGaranties.getItems().clear();

        // API field names: typeDescription, dateDebut, dateFin, dureeMois
        colProduit.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().typeDescription != null ? c.getValue().typeDescription : ""));
        colDateAchat.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().dateDebut != null ? c.getValue().dateDebut : ""));
        colDateFin.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().dateFin != null ? c.getValue().dateFin : ""));
        colDuree.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().dureeMois != null ? c.getValue().dureeMois + " mois" : "—"));
        colStatut.setCellValueFactory(c -> {
            String fin = c.getValue().dateFin;
            boolean active;
            try { active = fin != null && LocalDate.parse(fin).isAfter(LocalDate.now()); }
            catch (Exception ex) { active = false; }
            return new SimpleStringProperty(active ? "\u2705 Active" : "\u274C Expirée");
        });

        // Couleur selon statut
        tableGaranties.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(GarantieDto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                String fin = item.dateFin;
                try {
                    boolean active = fin != null && LocalDate.parse(fin).isAfter(LocalDate.now());
                    setStyle(active ? "-fx-background-color: #1a3d1a;" : "-fx-background-color: #2d2d2d;");
                } catch (Exception ex) { setStyle(""); }
            }
        });
    }

    @FXML
    private void handleRechercheClient() {
        String q = rechercheClientField.getText().trim();
        if (q.isEmpty()) return;
        spinner.setVisible(true);

        Task<ClientSummaryDto> clientTask = new Task<>() {
            @Override protected ClientSummaryDto call() throws Exception {
                return ClientService.getInstance().rechercherPremier(q);
            }
        };
        clientTask.setOnSucceeded(e -> {
            ClientSummaryDto summary = clientTask.getValue();
            Platform.runLater(() -> labelClient.setText(summary.getNomComplet()));
            chargerGaranties(summary.id);
        });
        clientTask.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            Throwable ex = clientTask.getException();
            String msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Client introuvable", msg);
        });
        new Thread(clientTask).start();
    }

    private void chargerGaranties(Long clientId) {
        Task<List<GarantieDto>> task = new Task<>() {
            @Override protected List<GarantieDto> call() throws Exception {
                return ApiClient.getInstance().get(
                    "/garanties?clientId=" + clientId,
                    new TypeReference<List<GarantieDto>>() {}
                );
            }
        };
        task.setOnSucceeded(e -> {
            List<GarantieDto> garanties = task.getValue() != null ? task.getValue() : List.of();
            Platform.runLater(() -> {
                tableGaranties.setItems(FXCollections.observableArrayList(garanties));
                labelNbGaranties.setText(garanties.size() + " garantie(s) trouvée(s)");
                spinner.setVisible(false);
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                spinner.setVisible(false);
                labelNbGaranties.setText("0 garantie(s) trouvée(s)");
            });
        });
        new Thread(task).start();
    }
}
