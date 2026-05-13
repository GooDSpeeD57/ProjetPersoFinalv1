package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.client.ClientSummaryDto;
import com.monprojet.boutiquejeux.dto.garantie.GarantieDto;
import com.monprojet.boutiquejeux.dto.referentiel.TypeGarantieDto;
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
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class GarantiesController {

    // ── Barre recherche ───────────────────────────────────────────────────────
    @FXML private TextField          rechercheClientField;
    @FXML private Label              labelClient;
    @FXML private ProgressIndicator  spinner;
    @FXML private Label              labelNbGaranties;

    // ── Tableau ───────────────────────────────────────────────────────────────
    @FXML private TableView<GarantieDto>           tableGaranties;
    @FXML private TableColumn<GarantieDto, String> colProduit;
    @FXML private TableColumn<GarantieDto, String> colNumeroSerie;
    @FXML private TableColumn<GarantieDto, String> colType;
    @FXML private TableColumn<GarantieDto, String> colDateAchat;
    @FXML private TableColumn<GarantieDto, String> colDuree;
    @FXML private TableColumn<GarantieDto, String> colDateFin;
    @FXML private TableColumn<GarantieDto, String> colStatut;

    // ── Panneau détail ────────────────────────────────────────────────────────
    @FXML private VBox  panelDetail;
    @FXML private Label detailProduit;
    @FXML private Label detailTypeGarantie;
    @FXML private Label detailDateDebut;
    @FXML private Label detailDateFin;
    @FXML private Label detailDuree;
    @FXML private Label detailExtension;
    @FXML private Label detailNumeroSerie;

    // ── Section extension ─────────────────────────────────────────────────────
    @FXML private VBox              sectionExtension;
    @FXML private ComboBox<TypeGarantieDto> cbTypeExtension;
    @FXML private DatePicker        dpFinExtension;
    @FXML private Label             labelExtensionMsg;
    @FXML private ProgressIndicator spinnerExtension;

    // ── État ──────────────────────────────────────────────────────────────────
    private Long currentClientId;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ═════════════════════════════════════════════════════════════════════════
    //  Initialisation
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        spinner.setVisible(false);
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);

        configurerColonnes();
        configurerCouleurLignes();
        configurerSelectionDetail();
        configurerComboExtension();
        chargerTypesGarantie();
    }

    // ── Colonnes ──────────────────────────────────────────────────────────────

    private void configurerColonnes() {
        // Produit : nomProduit si disponible, sinon typeDescription
        colProduit.setCellValueFactory(c -> {
            GarantieDto g = c.getValue();
            String v = g.nomProduit != null && !g.nomProduit.isBlank()
                    ? g.nomProduit
                    : (g.typeDescription != null ? g.typeDescription : "—");
            return new SimpleStringProperty(v);
        });

        // N° série : "—" si produit non sérialisé (jeux, etc.)
        colNumeroSerie.setCellValueFactory(c -> {
            String ns = c.getValue().numeroSerie;
            return new SimpleStringProperty(ns != null && !ns.isBlank() ? ns : "—");
        });
        colNumeroSerie.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                // N° série en gras cyan si présent, gris sinon
                setStyle("—".equals(item)
                        ? "-fx-text-fill:#666666;"
                        : "-fx-text-fill:#5dade2;-fx-font-weight:bold;");
            }
        });

        // Type : Légale de conformité (bleu) / Additionnelle (orange)
        colType.setCellValueFactory(c -> new SimpleStringProperty(libelleType(c.getValue())));
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.contains("Légale"))
                    setStyle("-fx-text-fill:#5dade2;-fx-font-weight:bold;");
                else
                    setStyle("-fx-text-fill:#f0a500;-fx-font-weight:bold;");
            }
        });

        colDateAchat.setCellValueFactory(c ->
                new SimpleStringProperty(formatDate(c.getValue().dateDebut)));

        colDuree.setCellValueFactory(c -> {
            GarantieDto g = c.getValue();
            if (g.dureeMois == null) return new SimpleStringProperty("—");
            String prefix = "EXTENSION".equals(g.typeItem) ? "+" : "";
            return new SimpleStringProperty(prefix + g.dureeMois + " mois");
        });

        // dateFin est déjà la date réelle finale (inclut l'extension si présente)
        colDateFin.setCellValueFactory(c ->
                new SimpleStringProperty(formatDate(c.getValue().dateFin)));

        // Statut : Active (vert) / Expire bientôt (orange) / Expirée (rouge)
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(libelleStatut(c.getValue())));
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); return; }
                setText(item);
                if (item.contains("bientôt"))   setStyle("-fx-text-fill:#f39c12;-fx-font-weight:bold;");
                else if (item.contains("Active")) setStyle("-fx-text-fill:#2ecc71;-fx-font-weight:bold;");
                else if (item.contains("Expirée")) setStyle("-fx-text-fill:#e74c3c;-fx-font-weight:bold;");
                else setStyle("-fx-text-fill:#aaaaaa;");
            }
        });
    }

    // ── Couleur des lignes ────────────────────────────────────────────────────

    private void configurerCouleurLignes() {
        tableGaranties.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(GarantieDto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                try {
                    LocalDate fin = item.dateFin != null ? LocalDate.parse(item.dateFin) : null;
                    if (fin == null || fin.isBefore(LocalDate.now())) {
                        setStyle("-fx-background-color:#2d1f1f;");          // rouge sombre — expirée
                    } else if (fin.isBefore(LocalDate.now().plusDays(30))) {
                        setStyle("-fx-background-color:#2d2500;");          // orange sombre — bientôt
                    } else {
                        setStyle("-fx-background-color:#1a2d1a;");          // vert sombre — active
                    }
                } catch (Exception ex) { setStyle(""); }
            }
        });
    }

    // ── Sélection → panneau détail ────────────────────────────────────────────

    private void configurerSelectionDetail() {
        tableGaranties.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> afficherDetail(sel));
    }

    // ── ComboBox extension ────────────────────────────────────────────────────

    private void configurerComboExtension() {
        cbTypeExtension.setConverter(new StringConverter<>() {
            @Override public String toString(TypeGarantieDto t) {
                return t == null ? "" : t.getLibelle();
            }
            @Override public TypeGarantieDto fromString(String s) { return null; }
        });

        // Auto-calcul : dureeMois du type extension = durée TOTALE depuis dateDebut
        // Ex : ETENDUE_CONSOLE = 36 mois → fin = dateDebut + 36 mois
        cbTypeExtension.valueProperty().addListener((obs, old, type) -> {
            if (type == null || type.dureeMois == null) return;
            GarantieDto sel = tableGaranties.getSelectionModel().getSelectedItem();
            if (sel == null || sel.dateDebut == null) return;
            try {
                LocalDate debut      = LocalDate.parse(sel.dateDebut);
                LocalDate finEtendue = debut.plusMonths(type.dureeMois);
                dpFinExtension.setValue(finEtendue);
                labelExtensionMsg.setText("📅 Fin totale : " + formatDate(sel.dateDebut)
                        + " + " + type.dureeMois + " mois = " + formatDate(finEtendue.toString()));
                labelExtensionMsg.setStyle("-fx-text-fill:#5dade2;");
            } catch (Exception ignored) { }
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Chargement des types de garantie (pour le ComboBox extension)
    // ═════════════════════════════════════════════════════════════════════════

    private void chargerTypesGarantie() {
        Task<List<TypeGarantieDto>> task = new Task<>() {
            @Override protected List<TypeGarantieDto> call() throws Exception {
                return ApiClient.getInstance().get(
                        "/referentiel/types-garantie",
                        new TypeReference<List<TypeGarantieDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<TypeGarantieDto> types = task.getValue();
            if (types != null) {
                // Ne proposer que les extensions payantes (légales exclues)
                List<TypeGarantieDto> extensions = types.stream()
                        .filter(t -> t.prixExtension != null
                                && t.prixExtension.compareTo(java.math.BigDecimal.ZERO) > 0)
                        .toList();
                Platform.runLater(() -> cbTypeExtension.setItems(
                        FXCollections.observableArrayList(extensions)));
            }
        });
        task.setOnFailed(e -> { /* silencieux — la liste sera vide */ });
        new Thread(task, "th-types-garantie").start();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Recherche client
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleRechercheClient() {
        String q = rechercheClientField.getText().trim();
        if (q.isEmpty()) return;

        spinner.setVisible(true);
        tableGaranties.getItems().clear();
        labelNbGaranties.setText("");
        panelDetail.setVisible(false);
        panelDetail.setManaged(false);

        Task<ClientSummaryDto> clientTask = new Task<>() {
            @Override protected ClientSummaryDto call() throws Exception {
                return ClientService.getInstance().rechercherPremier(q);
            }
        };
        clientTask.setOnSucceeded(e -> {
            ClientSummaryDto summary = clientTask.getValue();
            currentClientId = summary.id;
            Platform.runLater(() -> labelClient.setText(summary.getNomComplet()));
            chargerGaranties(summary.id);
        });
        clientTask.setOnFailed(e -> {
            Throwable ex = clientTask.getException();
            Platform.runLater(() -> {
                spinner.setVisible(false);
                labelClient.setText("Aucun client sélectionné");
            });
            String msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Client introuvable", msg);
        });
        new Thread(clientTask, "th-recherche-client").start();
    }

    private void chargerGaranties(Long clientId) {
        Task<List<GarantieDto>> task = new Task<>() {
            @Override protected List<GarantieDto> call() throws Exception {
                return ApiClient.getInstance().get(
                        "/garanties?clientId=" + clientId,
                        new TypeReference<List<GarantieDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<GarantieDto> list = task.getValue() != null ? task.getValue() : List.of();
            Platform.runLater(() -> {
                tableGaranties.setItems(FXCollections.observableArrayList(list));
                labelNbGaranties.setText(list.size() + " garantie(s)");
                spinner.setVisible(false);
            });
        });
        task.setOnFailed(e -> Platform.runLater(() -> {
            spinner.setVisible(false);
            labelNbGaranties.setText("0 garantie(s)");
        }));
        new Thread(task, "th-garanties").start();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Panneau de détail
    // ═════════════════════════════════════════════════════════════════════════

    private void afficherDetail(GarantieDto g) {
        if (g == null) {
            panelDetail.setVisible(false);
            panelDetail.setManaged(false);
            return;
        }

        // Infos générales
        detailProduit.setText(g.nomProduit != null && !g.nomProduit.isBlank()
                ? g.nomProduit : "—");
        detailTypeGarantie.setText(g.typeDescription != null ? g.typeDescription
                : (g.typeCode != null ? g.typeCode : "—"));
        detailDateDebut.setText(formatDate(g.dateDebut));
        detailDateFin.setText(formatDate(g.dateFin));
        detailDateFin.setStyle("EXTENSION".equals(g.typeItem) ? "-fx-text-fill:#f0a500;" : "");
        detailDuree.setText(g.dureeMois != null ? g.dureeMois + " mois" : "—");

        // N° de série
        if (g.numeroSerie != null && !g.numeroSerie.isBlank()) {
            detailNumeroSerie.setText(g.numeroSerie);
            detailNumeroSerie.setStyle("-fx-text-fill:#5dade2;-fx-font-size:12;-fx-font-weight:bold;");
        } else {
            detailNumeroSerie.setText("—");
            detailNumeroSerie.setStyle("-fx-text-fill:#666666;-fx-font-size:12;");
        }

        // Extension
        if ("EXTENSION".equals(g.typeItem)) {
            detailExtension.setText("✅ " + (g.typeDescription != null ? g.typeDescription : "Additionnelle"));
            detailExtension.setStyle("-fx-text-fill:#2ecc71;-fx-font-size:12;");
        } else {
            detailExtension.setText("Aucune");
            detailExtension.setStyle("-fx-text-fill:#888888;-fx-font-size:12;");
        }

        // Section ajout extension : visible uniquement si pas encore étendue
        boolean peutEtendre = "LEGALE".equals(g.typeItem);
        sectionExtension.setVisible(peutEtendre);
        sectionExtension.setManaged(peutEtendre);
        if (peutEtendre) {
            labelExtensionMsg.setText("");
            labelExtensionMsg.setStyle("");
            dpFinExtension.setValue(null);
            cbTypeExtension.setValue(null);   // reset ComboBox → efface l'auto-calcul du précédent
        }

        panelDetail.setVisible(true);
        panelDetail.setManaged(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Ajout d'une extension
    // ═════════════════════════════════════════════════════════════════════════

    @FXML
    private void handleAjouterExtension() {
        GarantieDto sel = tableGaranties.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        TypeGarantieDto type = cbTypeExtension.getValue();
        if (type == null) {
            labelExtensionMsg.setStyle("-fx-text-fill:#e74c3c;");
            labelExtensionMsg.setText("⚠ Sélectionner un type d'extension.");
            return;
        }
        if (dpFinExtension.getValue() == null) {
            labelExtensionMsg.setStyle("-fx-text-fill:#e74c3c;");
            labelExtensionMsg.setText("⚠ Saisir la date de fin.");
            return;
        }

        labelExtensionMsg.setText("");
        spinnerExtension.setVisible(true);

        final long garantieId = sel.id;
        final long typeId      = type.id;
        final String dateFin   = dpFinExtension.getValue().toString(); // ISO-8601 yyyy-MM-dd

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                Map<String, Object> body = Map.of(
                        "idTypeGarantie", typeId,
                        "dateFinEtendue", dateFin
                );
                ApiClient.getInstance().post("/garanties/" + garantieId + "/extensions", body);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerExtension.setVisible(false);
            labelExtensionMsg.setStyle("-fx-text-fill:#2ecc71;");
            labelExtensionMsg.setText("✅ Extension enregistrée !");
            // Rafraîchir la liste du client
            if (currentClientId != null) chargerGaranties(currentClientId);
        }));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            Platform.runLater(() -> {
                spinnerExtension.setVisible(false);
                labelExtensionMsg.setStyle("-fx-text-fill:#e74c3c;");
                labelExtensionMsg.setText("Erreur : " + (ex != null ? ex.getMessage() : "inconnue"));
            });
        });
        new Thread(task, "th-extension").start();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═════════════════════════════════════════════════════════════════════════

    /** Retourne le libellé du type basé sur typeItem (LEGALE / EXTENSION). */
    private String libelleType(GarantieDto g) {
        if ("EXTENSION".equals(g.typeItem)) return "🛡️ Additionnelle";
        return "⚖️ Légale de conformité";
    }

    /** Retourne le libellé du statut avec emoji. */
    private String libelleStatut(GarantieDto g) {
        if (g.dateFin == null) return "❓ Inconnue";
        String dateCible = g.dateFin;
        try {
            LocalDate d = LocalDate.parse(dateCible);
            if (d.isBefore(LocalDate.now()))               return "❌ Expirée";
            if (d.isBefore(LocalDate.now().plusDays(30)))  return "⚠️ Expire bientôt";
            return "✅ Active";
        } catch (Exception ex) {
            return "❓ Inconnue";
        }
    }

    /** Formate une date ISO-8601 en dd/MM/yyyy. */
    private String formatDate(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try { return LocalDate.parse(iso).format(FMT); }
        catch (Exception e) { return iso; }
    }
}
