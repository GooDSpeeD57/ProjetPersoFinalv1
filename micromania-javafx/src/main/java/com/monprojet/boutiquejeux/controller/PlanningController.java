package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.employe.EmployeDto;
import com.monprojet.boutiquejeux.dto.magasin.MagasinAdminDto;
import com.monprojet.boutiquejeux.dto.planning.PlanningEntryDto;
import com.monprojet.boutiquejeux.dto.planning.PlanningRowDto;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class PlanningController {

    // ── Barre supérieure ────────────────────────────────────────
    @FXML private ComboBox<MagasinAdminDto> cbMagasin;
    @FXML private Label                     labelSemaine;
    @FXML private ProgressIndicator         spinner;

    // ── Onglet Vue semaine ───────────────────────────────────────
    @FXML private TableView<PlanningRowDto>           tableGrille;
    @FXML private TableColumn<PlanningRowDto, String> colGEmp;
    @FXML private TableColumn<PlanningRowDto, String> colGRole;
    @FXML private TableColumn<PlanningRowDto, String> colGLun;
    @FXML private TableColumn<PlanningRowDto, String> colGMar;
    @FXML private TableColumn<PlanningRowDto, String> colGMer;
    @FXML private TableColumn<PlanningRowDto, String> colGJeu;
    @FXML private TableColumn<PlanningRowDto, String> colGVen;
    @FXML private TableColumn<PlanningRowDto, String> colGSam;
    @FXML private TableColumn<PlanningRowDto, String> colGDim;

    // ── Onglet Saisie par employé ────────────────────────────────
    @FXML private ComboBox<EmployeDto>   cbEmploye;
    @FXML private VBox                   vboxJours;
    @FXML private Label                  labelSaisieMsg;
    @FXML private ProgressIndicator      spinnerSaisie;
    @FXML private Button                 btnSauvegarderSemaine;

    // ── État interne ─────────────────────────────────────────────
    private final ApiClient     api     = ApiClient.getInstance();
    private final SessionManager session = SessionManager.getInstance();

    private LocalDate semaineRef = LocalDate.now();

    /** Tous les employés chargés une fois, filtrés par magasin ensuite */
    private List<EmployeDto> tousEmployes = List.of();

    /** Lignes des 7 jours, construites dynamiquement */
    private LigneJour[] lignes;

    private static final String[] NOMS_JOURS = {
        "Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi","Dimanche"
    };

    private static final DateTimeFormatter FMT_LABEL = DateTimeFormatter.ofPattern("dd MMM", Locale.FRENCH);
    private static final DateTimeFormatter FMT_JOUR  = DateTimeFormatter.ofPattern("EEE dd/MM", Locale.FRENCH);

    // ─────────────────────────────────────────────────────────────
    // MODÈLE INTERNE : une ligne par jour
    // ─────────────────────────────────────────────────────────────

    /** Représente une ligne "jour" dans le formulaire de saisie. */
    static class LigneJour {
        final Label     labelJour;
        final CheckBox  check;
        final TextField debut;
        final TextField fin;
        final TextField note;
        Long existingId = null;   // null = pas encore en base, non-null = créneau existant

        LigneJour(Label lbl, CheckBox cb, TextField d, TextField f, TextField n) {
            labelJour = lbl;
            check     = cb;
            debut     = d;
            fin       = f;
            note      = n;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // INITIALISATION
    // ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        spinner.setVisible(false);
        spinnerSaisie.setVisible(false);

        configurerColonnesGrille();
        buildJourRows();

        // ComboBox employé : converter pour affichage
        cbEmploye.setConverter(new StringConverter<>() {
            @Override public String toString(EmployeDto e)   { return e == null ? "" : e.getNomComplet() + (e.roleLibelle != null ? "  [" + e.roleLibelle + "]" : ""); }
            @Override public EmployeDto fromString(String s) { return null; }
        });

        // Sélection d'un employé → charger sa semaine
        cbEmploye.valueProperty().addListener((obs, o, emp) -> {
            labelSaisieMsg.setText("");
            if (emp != null) chargerSemaineEmploye(emp);
            else reinitialiserFormSaisie();
        });

        chargerDonnees();
    }

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCTION DYNAMIQUE DES 7 LIGNES JOURS
    // ─────────────────────────────────────────────────────────────

    private void buildJourRows() {
        lignes = new LigneJour[7];
        vboxJours.getChildren().clear();

        for (int i = 0; i < 7; i++) {

            // Label jour + date
            Label labelJour = new Label(NOMS_JOURS[i]);
            labelJour.setMinWidth(115);
            labelJour.getStyleClass().add("field-label");

            // CheckBox "travaillé"
            CheckBox check = new CheckBox();
            check.setMinWidth(82);
            check.getStyleClass().add("checkbox-dark");

            // Heure début
            TextField debut = new TextField();
            debut.setPromptText("09:00");
            debut.setPrefWidth(72);
            debut.setMaxWidth(72);
            debut.getStyleClass().add("text-field-dark");
            debut.setDisable(true);

            // Flèche
            Label arrow = new Label("→");
            arrow.setMinWidth(18);
            arrow.setStyle("-fx-text-fill:#666;");

            // Heure fin
            TextField fin = new TextField();
            fin.setPromptText("18:00");
            fin.setPrefWidth(72);
            fin.setMaxWidth(72);
            fin.getStyleClass().add("text-field-dark");
            fin.setDisable(true);

            // Note
            TextField note = new TextField();
            note.setPromptText("Note...");
            note.getStyleClass().add("text-field-dark");
            note.setDisable(true);
            HBox.setHgrow(note, Priority.ALWAYS);

            // CheckBox → active/désactive les champs
            check.selectedProperty().addListener((obs, o, sel) -> {
                debut.setDisable(!sel);
                fin.setDisable(!sel);
                note.setDisable(!sel);
                if (!sel) { debut.clear(); fin.clear(); note.clear(); }
            });

            // Assemblage de la ligne
            HBox row = new HBox(8, labelJour, check, debut, arrow, fin, note);
            row.setAlignment(Pos.CENTER_LEFT);
            // Légère alternance de fond
            row.setStyle(i % 2 == 1
                    ? "-fx-background-color:rgba(255,255,255,0.025);-fx-padding:3 4 3 4;"
                    : "-fx-padding:3 4 3 4;");

            lignes[i] = new LigneJour(labelJour, check, debut, fin, note);
            vboxJours.getChildren().add(row);
        }

        mettreAJourDateJours();
    }

    /** Met à jour les labels de date (Lun 01/01, Mar 02/01…) selon la semaine courante. */
    private void mettreAJourDateJours() {
        if (lignes == null) return;
        LocalDate lundi = semaineRef.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE dd/MM", Locale.FRENCH);
        for (int i = 0; i < 7; i++) {
            LocalDate jour = lundi.plusDays(i);
            // Capitalise la première lettre (ex : "lun." → "Lun.")
            String raw = jour.format(fmt);
            lignes[i].labelJour.setText(Character.toUpperCase(raw.charAt(0)) + raw.substring(1));
        }
    }

    private void reinitialiserFormSaisie() {
        if (lignes == null) return;
        for (LigneJour l : lignes) {
            l.check.setSelected(false);
            l.debut.clear();
            l.fin.clear();
            l.note.clear();
            l.existingId = null;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CONFIGURATION COLONNES Vue semaine
    // ─────────────────────────────────────────────────────────────

    private void configurerColonnesGrille() {
        colGEmp .setCellValueFactory(new PropertyValueFactory<>("nomEmploye"));
        colGRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colGLun .setCellValueFactory(new PropertyValueFactory<>("lundi"));
        colGMar .setCellValueFactory(new PropertyValueFactory<>("mardi"));
        colGMer .setCellValueFactory(new PropertyValueFactory<>("mercredi"));
        colGJeu .setCellValueFactory(new PropertyValueFactory<>("jeudi"));
        colGVen .setCellValueFactory(new PropertyValueFactory<>("vendredi"));
        colGSam .setCellValueFactory(new PropertyValueFactory<>("samedi"));
        colGDim .setCellValueFactory(new PropertyValueFactory<>("dimanche"));

        // Colorer REPOS en grisé, créneaux en vert
        for (TableColumn<PlanningRowDto, String> col :
                List.of(colGLun, colGMar, colGMer, colGJeu, colGVen, colGSam, colGDim)) {
            col.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(String v, boolean empty) {
                    super.updateItem(v, empty);
                    if (empty || v == null) { setText(""); setStyle(""); return; }
                    setText(v);
                    setStyle("REPOS".equals(v)
                            ? "-fx-text-fill:#444;"
                            : "-fx-text-fill:#2ecc71;-fx-font-weight:bold;");
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CHARGEMENT DONNÉES
    // ─────────────────────────────────────────────────────────────

    private void chargerDonnees() {
        spinner.setVisible(true);
        new Thread(() -> {
            try {
                List<MagasinAdminDto> magasins = api.get("/magasins/admin",
                        new TypeReference<List<MagasinAdminDto>>() {});
                if (magasins == null) magasins = List.of();

                List<EmployeDto> employes = api.get("/employes",
                        new TypeReference<List<EmployeDto>>() {});
                if (employes == null) employes = List.of();

                final List<MagasinAdminDto> mags = magasins;
                final List<EmployeDto>      emps = employes;

                Platform.runLater(() -> {
                    tousEmployes = emps;
                    cbMagasin.setItems(FXCollections.observableArrayList(mags));

                    Long myMagId = session.getMagasinId();
                    mags.stream()
                            .filter(m -> m.id != null && m.id.equals(myMagId))
                            .findFirst()
                            .ifPresent(cbMagasin::setValue);

                    if (!session.isAdmin()) cbMagasin.setDisable(true);

                    cbMagasin.valueProperty().addListener((obs, o, mag) -> {
                        if (mag != null) rafraichirMagasin();
                    });

                    rafraichirMagasin();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    AlertHelper.error("Chargement", e.getMessage());
                });
            }
        }).start();
    }

    private void rafraichirMagasin() {
        MagasinAdminDto mag = cbMagasin.getValue();
        if (mag == null) return;

        // Filtrer les employés du magasin sélectionné
        List<EmployeDto> empsDuMag = tousEmployes.stream()
                .filter(e -> mag.id != null && mag.id.equals(e.magasinId) && e.actif)
                .sorted(Comparator.comparing(EmployeDto::getNomComplet))
                .toList();
        cbEmploye.setItems(FXCollections.observableArrayList(empsDuMag));
        cbEmploye.setValue(null);
        reinitialiserFormSaisie();

        chargerPlanning(mag.id);
    }

    private void chargerPlanning(Long magasinId) {
        spinner.setVisible(true);
        String semaine = semaineRef.toString();
        updateLabelSemaine();

        new Thread(() -> {
            try {
                PageDto<PlanningRowDto> page = api.get(
                        "/plannings?magasinId=" + magasinId + "&semaine=" + semaine,
                        new TypeReference<PageDto<PlanningRowDto>>() {});
                List<PlanningRowDto> rows = (page != null && page.content != null)
                        ? page.content : List.of();

                Platform.runLater(() -> {
                    tableGrille.setItems(FXCollections.observableArrayList(rows));
                    spinner.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    AlertHelper.error("Planning", e.getMessage());
                });
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // NAVIGATION SEMAINE
    // ─────────────────────────────────────────────────────────────

    @FXML void handleSemPrecedente() { semaineRef = semaineRef.minusWeeks(1); recharger(); }
    @FXML void handleSemSuivante()   { semaineRef = semaineRef.plusWeeks(1);  recharger(); }
    @FXML void handleAujourdhui()    { semaineRef = LocalDate.now();          recharger(); }

    private void recharger() {
        mettreAJourDateJours();
        MagasinAdminDto mag = cbMagasin.getValue();
        if (mag != null) chargerPlanning(mag.id);
        else updateLabelSemaine();

        // Rafraîchir aussi la saisie si un employé est sélectionné
        EmployeDto emp = cbEmploye.getValue();
        if (emp != null) chargerSemaineEmploye(emp);
        else reinitialiserFormSaisie();
    }

    private void updateLabelSemaine() {
        LocalDate lundi    = semaineRef.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate dimanche = lundi.plusDays(6);
        labelSemaine.setText(
                "Lun " + lundi.format(FMT_LABEL)
                + " – Dim " + dimanche.format(FMT_LABEL)
                + "  " + lundi.getYear());
    }

    // ─────────────────────────────────────────────────────────────
    // SAISIE PAR EMPLOYÉ — chargement
    // ─────────────────────────────────────────────────────────────

    /** Charge les créneaux existants de l'employé pour la semaine courante et remplit le formulaire. */
    private void chargerSemaineEmploye(EmployeDto emp) {
        MagasinAdminDto mag = cbMagasin.getValue();
        if (mag == null) return;

        reinitialiserFormSaisie();
        spinnerSaisie.setVisible(true);
        labelSaisieMsg.setText("");

        String semaine = semaineRef.toString();
        LocalDate lundi = semaineRef.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        new Thread(() -> {
            try {
                List<PlanningEntryDto> entries = api.get(
                        "/plannings/entries?magasinId=" + mag.id + "&semaine=" + semaine,
                        new TypeReference<List<PlanningEntryDto>>() {});
                if (entries == null) entries = List.of();

                final List<PlanningEntryDto> filtered = entries.stream()
                        .filter(e -> emp.id != null && emp.id.equals(e.employeId))
                        .toList();
                final LocalDate lundiF = lundi;

                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    for (PlanningEntryDto entry : filtered) {
                        if (entry.dateTravail == null) continue;
                        try {
                            LocalDate date = LocalDate.parse(entry.dateTravail);
                            int idx = (int) ChronoUnit.DAYS.between(lundiF, date);
                            if (idx < 0 || idx >= 7) continue;
                            // D'abord cocher (active les champs), ensuite saisir les valeurs
                            lignes[idx].check.setSelected(true);
                            lignes[idx].existingId = entry.id;
                            lignes[idx].debut.setText(entry.getDebutHHMM());
                            lignes[idx].fin  .setText(entry.getFinHHMM());
                            lignes[idx].note .setText(entry.getNote());
                        } catch (Exception ignored) {}
                    }
                    if (filtered.isEmpty()) {
                        labelSaisieMsg.setText("ℹ️ Semaine vierge — cochez les jours travaillés.");
                    } else {
                        labelSaisieMsg.setText(filtered.size() + " créneau(x) chargé(s).");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    labelSaisieMsg.setText("❌ Erreur chargement : " + e.getMessage());
                });
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // SAISIE PAR EMPLOYÉ — sauvegarde
    // ─────────────────────────────────────────────────────────────

    @FXML
    private void handleSauvegarderSemaine() {
        EmployeDto emp = cbEmploye.getValue();
        if (emp == null) {
            labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
            labelSaisieMsg.setText("❌ Sélectionnez un employé.");
            return;
        }

        // Validation des heures saisies pour les jours cochés
        LocalDate lundi = semaineRef.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            LigneJour l = lignes[i];
            if (!l.check.isSelected()) continue;
            if (!l.debut.getText().trim().matches("\\d{1,2}:\\d{2}")) {
                labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
                labelSaisieMsg.setText("❌ " + NOMS_JOURS[i] + " : heure début invalide (HH:mm).");
                return;
            }
            if (!l.fin.getText().trim().matches("\\d{1,2}:\\d{2}")) {
                labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
                labelSaisieMsg.setText("❌ " + NOMS_JOURS[i] + " : heure fin invalide (HH:mm).");
                return;
            }
        }

        labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#2ecc71;");
        labelSaisieMsg.setText("");
        spinnerSaisie.setVisible(true);
        btnSauvegarderSemaine.setDisable(true);

        final Long    empId   = emp.id;
        final LigneJour[] snap = lignes;  // référence stable pour le thread

        new Thread(() -> {
            try {
                int created = 0, updated = 0, deleted = 0;

                for (int i = 0; i < 7; i++) {
                    LigneJour l    = snap[i];
                    LocalDate date = lundi.plusDays(i);

                    if (l.check.isSelected()) {
                        Map<String, Object> body = new LinkedHashMap<>();
                        body.put("dateTravail", date.toString());
                        body.put("heureDebut",  normaliserHeure(l.debut.getText().trim()));
                        body.put("heureFin",    normaliserHeure(l.fin.getText().trim()));
                        body.put("noteInterne", l.note.getText().trim());

                        if (l.existingId != null) {
                            api.patch("/plannings/" + l.existingId, body, PlanningEntryDto.class);
                            updated++;
                        } else {
                            body.put("idEmploye", empId);
                            api.post("/plannings", body, PlanningEntryDto.class);
                            created++;
                        }
                    } else if (l.existingId != null) {
                        api.delete("/plannings/" + l.existingId);
                        deleted++;
                    }
                }

                final int c = created, u = updated, d = deleted;
                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    btnSauvegarderSemaine.setDisable(false);
                    labelSaisieMsg.setText(
                            "✔ " + c + " créé(s)  " + u + " mis à jour  " + (d > 0 ? d + " supprimé(s)" : ""));
                    // Recharger pour mettre à jour les IDs et la grille
                    chargerSemaineEmploye(cbEmploye.getValue());
                    MagasinAdminDto mag = cbMagasin.getValue();
                    if (mag != null) chargerPlanning(mag.id);
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    btnSauvegarderSemaine.setDisable(false);
                    labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
                    labelSaisieMsg.setText("❌ " + ex.getMessage());
                });
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // SAISIE PAR EMPLOYÉ — copie semaine précédente
    // ─────────────────────────────────────────────────────────────

    @FXML
    private void handleCopierSemainePrecedente() {
        EmployeDto emp = cbEmploye.getValue();
        if (emp == null) {
            labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
            labelSaisieMsg.setText("❌ Sélectionnez un employé.");
            return;
        }
        MagasinAdminDto mag = cbMagasin.getValue();
        if (mag == null) return;

        LocalDate semPrec = semaineRef.minusWeeks(1);
        spinnerSaisie.setVisible(true);
        labelSaisieMsg.setText("");

        new Thread(() -> {
            try {
                List<PlanningEntryDto> entries = api.get(
                        "/plannings/entries?magasinId=" + mag.id + "&semaine=" + semPrec,
                        new TypeReference<List<PlanningEntryDto>>() {});
                if (entries == null) entries = List.of();

                LocalDate lundiPrec = semPrec.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                final List<PlanningEntryDto> filtered = entries.stream()
                        .filter(e -> emp.id != null && emp.id.equals(e.employeId))
                        .toList();
                final LocalDate lPrec = lundiPrec;

                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    if (filtered.isEmpty()) {
                        labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e0e0e0;");
                        labelSaisieMsg.setText("ℹ️ Aucun créneau la semaine précédente.");
                        return;
                    }
                    // Réinitialiser les cases cochées (mais garder les existingId de la semaine courante)
                    for (LigneJour l : lignes) {
                        l.check.setSelected(false);
                        l.debut.clear(); l.fin.clear(); l.note.clear();
                    }
                    // Appliquer les horaires de la semaine précédente
                    for (PlanningEntryDto entry : filtered) {
                        if (entry.dateTravail == null) continue;
                        try {
                            LocalDate date = LocalDate.parse(entry.dateTravail);
                            int idx = (int) ChronoUnit.DAYS.between(lPrec, date);
                            if (idx < 0 || idx >= 7) continue;
                            lignes[idx].check.setSelected(true);
                            lignes[idx].debut.setText(entry.getDebutHHMM());
                            lignes[idx].fin  .setText(entry.getFinHHMM());
                            lignes[idx].note .setText(entry.getNote());
                        } catch (Exception ignored) {}
                    }
                    labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#f39c12;");
                    labelSaisieMsg.setText("📋 " + filtered.size()
                            + " créneau(x) copié(s) — cliquez Sauvegarder pour confirmer.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinnerSaisie.setVisible(false);
                    labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e74c3c;");
                    labelSaisieMsg.setText("❌ " + e.getMessage());
                });
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // SAISIE PAR EMPLOYÉ — effacer la semaine
    // ─────────────────────────────────────────────────────────────

    @FXML
    private void handleEffacerSemaine() {
        EmployeDto emp = cbEmploye.getValue();
        if (emp == null) return;

        // Compter les jours cochés
        long nbCoches = Arrays.stream(lignes).filter(l -> l.check.isSelected()).count();
        if (nbCoches == 0) { reinitialiserFormSaisie(); return; }

        if (!AlertHelper.confirm("Effacer la semaine",
                "Décocher les " + nbCoches + " jour(s) sélectionné(s) pour " + emp.getNomComplet() + " ?\n"
                + "Les créneaux ne seront supprimés qu'après avoir cliqué Sauvegarder.")) return;

        reinitialiserFormSaisie();
        labelSaisieMsg.setStyle("-fx-font-size:12;-fx-text-fill:#e0e0e0;");
        labelSaisieMsg.setText("Semaine effacée — cliquez Sauvegarder pour confirmer.");
    }

    // ─────────────────────────────────────────────────────────────
    // UTILITAIRES
    // ─────────────────────────────────────────────────────────────

    /** "9:00" → "09:00",  "14:30" → "14:30" */
    private static String normaliserHeure(String h) {
        if (h == null) return "00:00";
        if (h.length() == 4 && h.charAt(1) == ':') return "0" + h;
        return h;
    }
}
