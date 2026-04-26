package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.client.ClientSummaryDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDto;
import com.monprojet.boutiquejeux.dto.referentiel.ModeCompensationDto;
import com.monprojet.boutiquejeux.dto.reprise.CreateRepriseLigneDto;
import com.monprojet.boutiquejeux.dto.reprise.CreateRepriseDto;
import com.monprojet.boutiquejeux.dto.reprise.RepriseDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.service.ClientService;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.List;

public class RepriseController {

    @FXML private TextField         rechercheClientField;
    @FXML private Label             labelClient;
    @FXML private TextField         rechercheProduitField;
    @FXML private TableView<ProduitDto> tableProduits;
    @FXML private TableColumn<ProduitDto, String> colNom;
    @FXML private TableColumn<ProduitDto, String> colGenre;
    @FXML private TableColumn<ProduitDto, String> colPrixNeuf;

    @FXML private ComboBox<ModeCompensationDto> comboModeCompensation;
    @FXML private ComboBox<String>  comboEtat;
    @FXML private TextArea          noteArea;
    @FXML private Label             labelPrixEstime;
    @FXML private TextField         prixFinalField;
    @FXML private Button            btnValiderReprise;
    @FXML private ProgressIndicator spinner;

    private Long   clientId   = null;
    private Long   variantId  = null;   // variant NEUF du produit sélectionné
    private double prixEstime = 0;

    @FXML
    public void initialize() {
        spinner.setVisible(false);

        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genre != null ? c.getValue().genre : ""));
        colPrixNeuf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrixAffiche()));
        tableProduits.getItems().clear();

        comboEtat.setItems(FXCollections.observableArrayList(
                "NEUF_SCELLE", "TRES_BON_ETAT", "BON_ETAT", "ETAT_MOYEN", "MAUVAIS_ETAT"));
        comboEtat.setValue("BON_ETAT");
        // L'état affecte la description de la ligne mais pas le prix (géré en base)
        comboModeCompensation.setConverter(new StringConverter<>() {
            @Override public String toString(ModeCompensationDto m)   { return m == null ? "" : m.toString(); }
            @Override public ModeCompensationDto fromString(String s) { return null; }
        });

        tableProduits.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> calculerPrixEstime());

        chargerModesCompensation();
    }

    // ── Chargement référentiel ────────────────────────────────────

    private void chargerModesCompensation() {
        Task<List<ModeCompensationDto>> task = new Task<>() {
            @Override protected List<ModeCompensationDto> call() throws Exception {
                return ApiClient.getInstance().get(
                    "/referentiel/modes-compensation-reprise",
                    new TypeReference<List<ModeCompensationDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<ModeCompensationDto> modes = task.getValue() != null ? task.getValue() : List.of();
            Platform.runLater(() -> {
                comboModeCompensation.setItems(FXCollections.observableArrayList(modes));
                if (!modes.isEmpty()) comboModeCompensation.setValue(modes.get(0));
            });
        });
        task.setOnFailed(e -> { /* silencieux — le champ reste vide */ });
        new Thread(task).start();
    }

    // ── Recherche client ──────────────────────────────────────────

    @FXML
    private void handleRechercheClient() {
        String q = rechercheClientField.getText().trim();
        if (q.isEmpty()) return;

        Task<ClientSummaryDto> task = new Task<>() {
            @Override protected ClientSummaryDto call() throws Exception {
                return ClientService.getInstance().rechercherPremier(q);
            }
        };
        task.setOnSucceeded(e -> {
            ClientSummaryDto summary = task.getValue();
            clientId = summary.id;
            Platform.runLater(() ->
                labelClient.setText(summary.getNomComplet() + " (" + summary.email + ")")
            );
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Client introuvable", msg);
        });
        new Thread(task).start();
    }

    // ── Recherche produit ─────────────────────────────────────────

    @FXML
    private void handleRechercheProduit() {
        String q = rechercheProduitField.getText().trim();
        if (q.isEmpty()) return;

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
            Platform.runLater(() -> tableProduits.setItems(FXCollections.observableArrayList(content)));
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Erreur", msg);
        });
        new Thread(task).start();
    }

    // ── Pré-remplissage du prix depuis l'API ──────────────────────

    private void calculerPrixEstime() {
        ProduitDto p = tableProduits.getSelectionModel().getSelectedItem();
        if (p == null) return;

        variantId = p.variantIdNeuf;

        if (p.prixReprise != null && p.prixReprise > 0) {
            prixEstime = p.prixReprise;
            Platform.runLater(() -> {
                labelPrixEstime.setText(String.format("Prix de reprise : %.2f €", prixEstime));
                prixFinalField.setText(String.format("%.2f", prixEstime));
            });
        } else {
            prixEstime = 0;
            Platform.runLater(() -> {
                labelPrixEstime.setText("Prix de reprise : à évaluer");
                prixFinalField.clear();
            });
        }
    }

    // ── Valider la reprise ────────────────────────────────────────

    @FXML
    private void handleValiderReprise() {
        if (variantId == null) { AlertHelper.warn("Produit", "Sélectionnez un produit."); return; }

        ModeCompensationDto modeComp = comboModeCompensation.getValue();
        if (modeComp == null) {
            AlertHelper.warn("Mode de compensation", "Sélectionnez un mode de compensation (avoir, espèces…).");
            return;
        }

        double prixFinal;
        try { prixFinal = Double.parseDouble(prixFinalField.getText().replace(',', '.')); }
        catch (NumberFormatException ex) { AlertHelper.error("Prix", "Prix final invalide."); return; }

        if (!AlertHelper.confirm("Confirmer la reprise",
                String.format("Reprendre ce produit pour %.2f € ?\nCompensation : %s",
                        prixFinal, modeComp))) return;

        setLoading(true);

        CreateRepriseLigneDto ligne = new CreateRepriseLigneDto();
        ligne.idVariant          = variantId;
        ligne.quantite           = 1;
        ligne.etatGeneral        = comboEtat.getValue();
        ligne.prixEstimeUnitaire = BigDecimal.valueOf(prixFinal);
        ligne.commentaires       = noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim();

        CreateRepriseDto body = new CreateRepriseDto();
        body.idClient          = clientId;           // null accepté (client anonyme)
        body.idMagasin         = SessionManager.getInstance().getMagasinId();
        body.idModeCompensation = modeComp.id;
        body.lignes            = List.of(ligne);
        body.commentaire       = noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim();

        Task<RepriseDto> task = new Task<>() {
            @Override protected RepriseDto call() throws Exception {
                return ApiClient.getInstance().post("/reprises", body, RepriseDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            setLoading(false);
            RepriseDto res = task.getValue();
            String montant = res.montantTotalEstime != null
                    ? String.format("%.2f €", res.montantTotalEstime)
                    : String.format("%.2f €", prixFinal);
            AlertHelper.success("Reprise enregistrée",
                    "\u2705 Reprise " + res.referenceReprise + " créée !\nAvoir client : " + montant);
            resetForm();
        });
        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            String msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Erreur reprise", msg);
        });
        new Thread(task).start();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void resetForm() {
        clientId  = null;
        variantId = null;
        Platform.runLater(() -> {
            rechercheClientField.clear();
            labelClient.setText("Aucun client sélectionné");
            rechercheProduitField.clear();
            tableProduits.getItems().clear();
            labelPrixEstime.setText("Prix estimé : —");
            prixFinalField.clear();
            noteArea.clear();
            comboEtat.setValue("BON_ETAT");
        });
    }

    private void setLoading(boolean b) {
        Platform.runLater(() -> { btnValiderReprise.setDisable(b); spinner.setVisible(b); });
    }
}
