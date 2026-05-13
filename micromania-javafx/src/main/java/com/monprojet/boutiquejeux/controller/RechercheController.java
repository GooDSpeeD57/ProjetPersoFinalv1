package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.client.ClientSummaryDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDto;
import com.monprojet.boutiquejeux.service.ClientService;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RechercheController {

    @FXML private TextField      rechercheField;
    @FXML private ToggleGroup    modeGroup;
    @FXML private RadioButton    radioClient;
    @FXML private RadioButton    radioProduit;
    @FXML private ProgressIndicator spinner;

    // Table clients
    @FXML private TableView<ClientSummaryDto> tableClients;
    @FXML private TableColumn<ClientSummaryDto, String> colCPseudo;
    @FXML private TableColumn<ClientSummaryDto, String> colCNom;
    @FXML private TableColumn<ClientSummaryDto, String> colCEmail;
    @FXML private TableColumn<ClientSummaryDto, String> colCTel;
    @FXML private TableColumn<ClientSummaryDto, String> colCPoints;
    @FXML private TableColumn<ClientSummaryDto, String> colCFidelite;

    // Table produits
    @FXML private TableView<ProduitDto> tableProduits;
    @FXML private TableColumn<ProduitDto, String> colPNom;
    @FXML private TableColumn<ProduitDto, String> colPGenre;
    @FXML private TableColumn<ProduitDto, String> colPPrix;
    @FXML private TableColumn<ProduitDto, String> colPType;

    // Détail client sélectionné
    @FXML private Label labelDetailNom;
    @FXML private Label labelDetailEmail;
    @FXML private Label labelDetailTel;
    @FXML private Label labelDetailPoints;
    @FXML private Label labelDetailFidelite;
    @FXML private Label labelDetailCarteFidelite;

    @FXML
    public void initialize() {
        spinner.setVisible(false);

        // Clients
        colCPseudo.setCellValueFactory(c -> new SimpleStringProperty(""));
        colCNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomComplet()));
        colCEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().email != null ? c.getValue().email : ""));
        colCTel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().telephone != null ? c.getValue().telephone : ""));
        colCPoints.setCellValueFactory(c -> new SimpleStringProperty(""));
        colCFidelite.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().typeFidelite != null ? c.getValue().typeFidelite : ""));

        // Produits
        colPNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        colPGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenreOuCategorie()));
        colPPrix.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrixAffiche()));
        colPType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type != null ? c.getValue().type : ""));

        // Sélection client → afficher détail
        tableClients.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) afficherDetailClient(n);
        });

        // Basculer visibilité selon mode
        modeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isClient = n == radioClient;
            tableClients.setVisible(isClient);
            tableProduits.setVisible(!isClient);
        });
        tableClients.setVisible(true);
        tableProduits.setVisible(false);

        rechercheField.setOnAction(e -> handleRecherche());
    }

    @FXML
    private void handleRecherche() {
        String q = rechercheField.getText().trim();
        if (q.isEmpty()) return;
        spinner.setVisible(true);

        boolean isClient = radioClient.isSelected();

        if (isClient) {
            Task<List<ClientSummaryDto>> task = new Task<>() {
                @Override protected List<ClientSummaryDto> call() throws Exception {
                    return ClientService.getInstance().rechercher(q);
                }
            };
            task.setOnSucceeded(e -> {
                List<ClientSummaryDto> content = task.getValue();
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    tableClients.setItems(FXCollections.observableArrayList(content));
                });
            });
            task.setOnFailed(e -> Platform.runLater(() -> spinner.setVisible(false)));
            new Thread(task).start();
        } else {
            Task<PageDto<ProduitDto>> task = new Task<>() {
                @Override protected PageDto<ProduitDto> call() throws Exception {
                    return ApiClient.getInstance().get(
                        "/produits?q=" + q,
                        new TypeReference<PageDto<ProduitDto>>() {}
                    );
                }
            };
            task.setOnSucceeded(e -> {
                PageDto<ProduitDto> res = task.getValue();
                List<ProduitDto> content = res != null && res.content != null ? res.content : List.of();
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    tableProduits.setItems(FXCollections.observableArrayList(content));
                });
            });
            task.setOnFailed(e -> Platform.runLater(() -> spinner.setVisible(false)));
            new Thread(task).start();
        }
    }

    private void afficherDetailClient(ClientSummaryDto c) {
        labelDetailNom.setText(c.getNomComplet());
        labelDetailEmail.setText(c.email != null ? c.email : "");
        labelDetailTel.setText(c.telephone != null ? c.telephone : "");
        labelDetailPoints.setText("");
        labelDetailFidelite.setText(c.typeFidelite != null ? c.typeFidelite : "");
        labelDetailCarteFidelite.setText("");
    }
}
