package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.avatar.AvatarAdminDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvatarsController {

    // ── Liste ──────────────────────────────────────────────────────────────────
    @FXML private TableView<AvatarAdminDto>         tableAvatars;
    @FXML private TableColumn<AvatarAdminDto,String> colId;
    @FXML private TableColumn<AvatarAdminDto,String> colNom;
    @FXML private TableColumn<AvatarAdminDto,String> colAlt;
    @FXML private TableColumn<AvatarAdminDto,String> colDeco;
    @FXML private TableColumn<AvatarAdminDto,String> colActif;
    @FXML private Label             labelCount;
    @FXML private ProgressIndicator spinnerListe;

    // ── Formulaire ─────────────────────────────────────────────────────────────
    @FXML private Label             labelTitreForm;
    @FXML private TextField         fieldNom;
    @FXML private TextField         fieldUrl;
    @FXML private TextField         fieldAlt;
    @FXML private CheckBox          checkDecorative;
    @FXML private CheckBox          checkActif;
    @FXML private Button            btnSauvegarder;
    @FXML private Label             labelFormMsg;
    @FXML private ProgressIndicator spinnerForm;

    // ── Upload ─────────────────────────────────────────────────────────────────
    @FXML private Label             labelFichiersChoisis;
    @FXML private ListView<String>  listeFichiersUpload;
    @FXML private TextArea          textResultatUpload;
    @FXML private ProgressIndicator spinnerUpload;

    private final ApiClient api = ApiClient.getInstance();

    /** Avatar en cours d'édition (null = création) */
    private AvatarAdminDto avatarEnEdition = null;

    /** Fichiers sélectionnés pour l'upload */
    private final List<File> fichiersSelectionnes = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════════════
    //  INITIALISATION
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        spinnerListe.setVisible(false);
        spinnerForm.setVisible(false);
        spinnerUpload.setVisible(false);

        // Colonnes table
        colId   .setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().id != null ? String.valueOf(c.getValue().id) : ""));
        colNom  .setCellValueFactory(c -> new SimpleStringProperty(nullSafe(c.getValue().nom)));
        colAlt  .setCellValueFactory(c -> new SimpleStringProperty(nullSafe(c.getValue().alt)));
        colDeco .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().decorative ? "✔" : ""));
        colActif.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().actif ? "✔" : "✖"));

        // Sélection → remplir le formulaire
        tableAvatars.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) remplirFormulaire(sel);
        });

        chargerListe();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LISTE
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleRefresh() { chargerListe(); }

    private void chargerListe() {
        spinnerListe.setVisible(true);
        tableAvatars.getItems().clear();

        Task<List<AvatarAdminDto>> task = new Task<>() {
            @Override protected List<AvatarAdminDto> call() throws Exception {
                return api.get("/admin/avatars", new TypeReference<List<AvatarAdminDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<AvatarAdminDto> list = task.getValue();
            if (list == null) list = List.of();
            tableAvatars.setItems(FXCollections.observableArrayList(list));
            labelCount.setText(list.size() + " avatar(s)");
            spinnerListe.setVisible(false);
        });
        task.setOnFailed(e -> {
            spinnerListe.setVisible(false);
            AlertHelper.error("Erreur chargement", task.getException().getMessage());
        });
        new Thread(task).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FORMULAIRE
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleNouveau() {
        avatarEnEdition = null;
        labelTitreForm.setText("Nouvel avatar");
        viderFormulaire();
        labelFormMsg.setText("");
    }

    @FXML
    private void handleAnnuler() {
        avatarEnEdition = null;
        viderFormulaire();
        labelTitreForm.setText("Sélectionnez un avatar ou créez-en un");
        labelFormMsg.setText("");
    }

    @FXML
    private void handleSauvegarder() {
        String nom = fieldNom.getText().trim();
        String url = fieldUrl.getText().trim();
        String alt = fieldAlt.getText().trim();

        if (nom.isBlank()) { labelFormMsg.setText("❌ Le nom est obligatoire."); return; }
        if (url.isBlank()) { labelFormMsg.setText("❌ L'URL est obligatoire."); return; }

        Map<String, Object> body = Map.of(
                "nom",       nom,
                "url",       url,
                "alt",       alt.isBlank() ? "Avatar utilisateur" : alt,
                "decorative", checkDecorative.isSelected(),
                "actif",      checkActif.isSelected()
        );

        spinnerForm.setVisible(true);
        labelFormMsg.setText("");
        btnSauvegarder.setDisable(true);

        final AvatarAdminDto enEdition = avatarEnEdition;
        Task<AvatarAdminDto> task = new Task<>() {
            @Override protected AvatarAdminDto call() throws Exception {
                if (enEdition == null) {
                    return api.post("/admin/avatars", body, AvatarAdminDto.class);
                } else {
                    return api.put("/admin/avatars/" + enEdition.id, body, AvatarAdminDto.class);
                }
            }
        };
        task.setOnSucceeded(e -> {
            spinnerForm.setVisible(false);
            btnSauvegarder.setDisable(false);
            AvatarAdminDto saved = task.getValue();
            labelFormMsg.setText("✔ Avatar \"" + saved.nom + "\" sauvegardé (id " + saved.id + ")");
            avatarEnEdition = saved;
            labelTitreForm.setText("Modifier — " + saved.nom);
            chargerListe();
        });
        task.setOnFailed(e -> {
            spinnerForm.setVisible(false);
            btnSauvegarder.setDisable(false);
            String msg = task.getException() instanceof ApiException ae
                    ? ae.getMessage() : task.getException().getMessage();
            labelFormMsg.setText("❌ " + msg);
        });
        new Thread(task).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SUPPRESSION
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleSupprimer() {
        AvatarAdminDto sel = tableAvatars.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.warn("Sélection", "Veuillez sélectionner un avatar à supprimer.");
            return;
        }
        if (!AlertHelper.confirm("Supprimer", "Supprimer l'avatar \"" + sel.nom + "\" ?")) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                api.delete("/admin/avatars/" + sel.id);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (avatarEnEdition != null && avatarEnEdition.id.equals(sel.id)) handleAnnuler();
                chargerListe();
            });
        });
        task.setOnFailed(e -> AlertHelper.error("Erreur suppression", task.getException().getMessage()));
        new Thread(task).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UPLOAD MULTIPLE
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleChoisirFichiers() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Sélectionner des images d'avatars");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );

        List<File> files = fc.showOpenMultipleDialog(tableAvatars.getScene().getWindow());
        if (files == null || files.isEmpty()) return;

        fichiersSelectionnes.clear();
        fichiersSelectionnes.addAll(files);

        listeFichiersUpload.setItems(FXCollections.observableArrayList(
                files.stream().map(File::getName).toList()
        ));
        labelFichiersChoisis.setText(files.size() + " fichier(s) sélectionné(s)");
        textResultatUpload.clear();
    }

    @FXML
    private void handleViderUpload() {
        fichiersSelectionnes.clear();
        listeFichiersUpload.getItems().clear();
        labelFichiersChoisis.setText("Aucun fichier sélectionné");
        textResultatUpload.clear();
    }

    @FXML
    private void handleUploader() {
        if (fichiersSelectionnes.isEmpty()) {
            AlertHelper.warn("Upload", "Veuillez sélectionner au moins un fichier.");
            return;
        }

        spinnerUpload.setVisible(true);
        textResultatUpload.clear();

        List<File> aUploader = new ArrayList<>(fichiersSelectionnes);

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                StringBuilder sb = new StringBuilder();
                int ok = 0, err = 0;

                for (File f : aUploader) {
                    try {
                        AvatarAdminDto created = api.uploadAvatar("/admin/avatars/upload", f);
                        sb.append("✔ ").append(f.getName())
                          .append(" → id ").append(created.id)
                          .append(" | ").append(created.url).append("\n");
                        ok++;
                    } catch (Exception ex) {
                        sb.append("❌ ").append(f.getName())
                          .append(" : ").append(ex.getMessage()).append("\n");
                        err++;
                    }
                }
                sb.append("\n").append(ok).append(" succès, ").append(err).append(" erreur(s).");
                final String result = sb.toString();

                Platform.runLater(() -> {
                    textResultatUpload.setText(result);
                    spinnerUpload.setVisible(false);
                    chargerListe();
                });
                return null;
            }
        };
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                spinnerUpload.setVisible(false);
                AlertHelper.error("Erreur upload", task.getException().getMessage());
            });
        });
        new Thread(task).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UTILITAIRES
    // ══════════════════════════════════════════════════════════════════════════

    private void remplirFormulaire(AvatarAdminDto a) {
        avatarEnEdition = a;
        labelTitreForm.setText("Modifier — " + a.nom);
        fieldNom.setText(nullSafe(a.nom));
        fieldUrl.setText(nullSafe(a.url));
        fieldAlt.setText(nullSafe(a.alt));
        checkDecorative.setSelected(a.decorative);
        checkActif.setSelected(a.actif);
        labelFormMsg.setText("");
    }

    private void viderFormulaire() {
        fieldNom.clear(); fieldUrl.clear(); fieldAlt.clear();
        checkDecorative.setSelected(false);
        checkActif.setSelected(true);
    }

    private static String nullSafe(String s) { return s != null ? s : ""; }
}
