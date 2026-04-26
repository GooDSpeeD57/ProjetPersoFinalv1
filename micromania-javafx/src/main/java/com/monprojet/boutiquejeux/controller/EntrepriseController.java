package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.employe.CreateEmployeDto;
import com.monprojet.boutiquejeux.dto.employe.EmployeDto;
import com.monprojet.boutiquejeux.dto.employe.UpdateEmployeDto;
import com.monprojet.boutiquejeux.dto.entreprise.RoleDto;
import com.monprojet.boutiquejeux.dto.magasin.CreateMagasinDto;
import com.monprojet.boutiquejeux.dto.magasin.MagasinAdminDto;
import com.monprojet.boutiquejeux.dto.magasin.UpdateMagasinDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EntrepriseController {

    // ── EMPLOYÉS ────────────────────────────────────────────────
    @FXML private TextField        rechercheEmployeField;
    @FXML private TableView<EmployeDto> tableEmployes;
    @FXML private TableColumn<EmployeDto, String>  colEmpNom;
    @FXML private TableColumn<EmployeDto, String>  colEmpEmail;
    @FXML private TableColumn<EmployeDto, String>  colEmpRole;
    @FXML private TableColumn<EmployeDto, String>  colEmpMagasin;
    @FXML private TableColumn<EmployeDto, Boolean> colEmpActif;
    @FXML private ProgressIndicator spinnerEmployes;
    @FXML private Label             labelTitreEmploye;
    @FXML private Label             labelMdpHint;
    @FXML private TextField         empNomField;
    @FXML private TextField         empPrenomField;
    @FXML private TextField         empEmailField;
    @FXML private TextField         empTelField;
    @FXML private ComboBox<RoleDto>        empRoleCb;
    @FXML private ComboBox<MagasinAdminDto> empMagasinCb;
    @FXML private DatePicker        empDateEmbauche;
    @FXML private CheckBox          empActifCheck;
    @FXML private PasswordField     empMdpField;
    @FXML private Button            btnEnregistrerEmploye;
    @FXML private Button            btnSupprimerEmploye;
    @FXML private Button            btnNouvelEmploye;

    // ── BOUTIQUES ───────────────────────────────────────────────
    @FXML private TableView<MagasinAdminDto> tableMagasins;
    @FXML private TableColumn<MagasinAdminDto, String>  colMagNom;
    @FXML private TableColumn<MagasinAdminDto, String>  colMagTel;
    @FXML private TableColumn<MagasinAdminDto, String>  colMagMail;
    @FXML private TableColumn<MagasinAdminDto, Boolean> colMagActif;
    @FXML private ProgressIndicator spinnerMagasins;
    @FXML private Label             labelTitreMagasin;
    @FXML private TextField         magNomField;
    @FXML private TextField         magTelField;
    @FXML private TextField         magEmailField;
    @FXML private CheckBox          magActifCheck;
    @FXML private Button            btnEnregistrerMagasin;
    @FXML private Button            btnSupprimerMagasin;
    @FXML private Button            btnNouvelMagasin;

    // ── RÔLES ────────────────────────────────────────────────────
    @FXML private TableView<RoleDto>          tableRoles;
    @FXML private TableColumn<RoleDto, Long>   colRoleId;
    @FXML private TableColumn<RoleDto, String> colRoleCode;
    @FXML private TableColumn<RoleDto, String> colRoleLibelle;

    // ── État interne ─────────────────────────────────────────────
    private final ApiClient api = ApiClient.getInstance();
    private EmployeDto      employeSelectionne = null;
    private MagasinAdminDto magasinSelectionne = null;
    private boolean         modeNouvelEmploye  = false;
    private boolean         modeNouvelMagasin  = false;

    // ─────────────────────────────────────────────────────────────
    // INITIALISATION
    // ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurerColonnesEmployes();
        configurerColonnesMagasins();
        configurerColonnesRoles();

        // Sélection dans la table employé
        tableEmployes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, emp) -> { if (emp != null) chargerFormulaireEmploye(emp); });

        // Sélection dans la table boutique
        tableMagasins.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, mag) -> { if (mag != null) chargerFormulaireMagasin(mag); });

        // Visibilité boutons selon rôle
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        btnNouvelEmploye.setDisable(!SessionManager.getInstance().isManager());
        btnSupprimerEmploye.setDisable(!isAdmin);
        btnNouvelMagasin.setDisable(!isAdmin);
        btnSupprimerMagasin.setDisable(!isAdmin);
        btnEnregistrerMagasin.setDisable(!isAdmin);

        viderFormulaireEmploye();
        viderFormulaireMagasin();

        // Chargement initial en arrière-plan
        chargerReferentiels();
        chargerEmployes(null);
        chargerMagasins();
        chargerRoles();
    }

    // ─────────────────────────────────────────────────────────────
    // CONFIGURATION DES COLONNES
    // ─────────────────────────────────────────────────────────────

    private void configurerColonnesEmployes() {
        colEmpNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getNomComplet()));
        colEmpEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmpRole.setCellValueFactory(new PropertyValueFactory<>("roleLibelle"));
        colEmpMagasin.setCellValueFactory(new PropertyValueFactory<>("magasinNom"));
        colEmpActif.setCellValueFactory(new PropertyValueFactory<>("actif"));
        colEmpActif.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "" : v ? "✔" : "✗");
            }
        });
    }

    private void configurerColonnesMagasins() {
        colMagNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colMagTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colMagMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMagActif.setCellValueFactory(new PropertyValueFactory<>("actif"));
        colMagActif.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "" : v ? "✔" : "✗");
            }
        });
    }

    private void configurerColonnesRoles() {
        colRoleId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRoleCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colRoleLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
    }

    // ─────────────────────────────────────────────────────────────
    // CHARGEMENT DONNÉES
    // ─────────────────────────────────────────────────────────────

    private void chargerReferentiels() {
        new Thread(() -> {
            try {
                List<RoleDto> roles = api.get("/roles", new TypeReference<>() {});
                List<MagasinAdminDto> mags = api.get("/magasins/admin", new TypeReference<>() {});
                final List<RoleDto>        r = roles != null ? roles : List.of();
                final List<MagasinAdminDto> m = mags  != null ? mags  : List.of();
                Platform.runLater(() -> {
                    empRoleCb.setItems(FXCollections.observableArrayList(r));
                    empMagasinCb.setItems(FXCollections.observableArrayList(m));
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Référentiels", e.getMessage()));
            }
        }).start();
    }

    private void chargerEmployes(String q) {
        spinnerEmployes.setVisible(true);
        new Thread(() -> {
            try {
                String path = q != null && !q.isBlank()
                        ? "/employes?q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8)
                        : "/employes";
                List<EmployeDto> list = api.get(path, new TypeReference<>() {});
                final List<EmployeDto> safe = list != null ? list : List.of();
                Platform.runLater(() -> {
                    tableEmployes.setItems(FXCollections.observableArrayList(safe));
                    spinnerEmployes.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinnerEmployes.setVisible(false);
                    AlertHelper.error("Employés", e.getMessage());
                });
            }
        }).start();
    }

    private void chargerMagasins() {
        spinnerMagasins.setVisible(true);
        new Thread(() -> {
            try {
                List<MagasinAdminDto> list = api.get("/magasins/admin", new TypeReference<>() {});
                final List<MagasinAdminDto> safe = list != null ? list : List.of();
                Platform.runLater(() -> {
                    tableMagasins.setItems(FXCollections.observableArrayList(safe));
                    spinnerMagasins.setVisible(false);
                    // Mettre aussi à jour le ComboBox boutiques dans le formulaire employé
                    empMagasinCb.setItems(FXCollections.observableArrayList(safe));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinnerMagasins.setVisible(false);
                    AlertHelper.error("Boutiques", e.getMessage());
                });
            }
        }).start();
    }

    private void chargerRoles() {
        new Thread(() -> {
            try {
                List<RoleDto> list = api.get("/roles", new TypeReference<>() {});
                final List<RoleDto> safe = list != null ? list : List.of();
                Platform.runLater(() -> {
                    tableRoles.setItems(FXCollections.observableArrayList(safe));
                    empRoleCb.setItems(FXCollections.observableArrayList(safe));
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Rôles", e.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ACTIONS EMPLOYÉS
    // ─────────────────────────────────────────────────────────────

    @FXML void handleRechercheEmploye() { chargerEmployes(rechercheEmployeField.getText()); }
    @FXML void handleRefreshEmployes()  { rechercheEmployeField.clear(); chargerEmployes(null); }

    @FXML
    void handleNouvelEmploye() {
        modeNouvelEmploye = true;
        employeSelectionne = null;
        tableEmployes.getSelectionModel().clearSelection();
        labelTitreEmploye.setText("Nouvel employé");
        labelMdpHint.setText("(Obligatoire)");
        viderFormulaireEmploye();
        empMdpField.setPromptText("Minimum 8 caractères *");
    }

    @FXML
    void handleAnnulerEmploye() {
        modeNouvelEmploye = false;
        employeSelectionne = null;
        tableEmployes.getSelectionModel().clearSelection();
        labelTitreEmploye.setText("Sélectionnez un employé");
        labelMdpHint.setText("(Laisser vide pour ne pas modifier)");
        viderFormulaireEmploye();
    }

    @FXML
    void handleEnregistrerEmploye() {
        String nom    = empNomField.getText().trim();
        String prenom = empPrenomField.getText().trim();
        String email  = empEmailField.getText().trim();
        String mdp    = empMdpField.getText();
        RoleDto role  = empRoleCb.getValue();
        MagasinAdminDto mag = empMagasinCb.getValue();

        // Validation minimale
        if (nom.isBlank() || prenom.isBlank() || email.isBlank()) {
            AlertHelper.warn("Validation", "Nom, prénom et email sont obligatoires.");
            return;
        }
        if (role == null) { AlertHelper.warn("Validation", "Veuillez sélectionner un rôle."); return; }
        if (mag  == null) { AlertHelper.warn("Validation", "Veuillez sélectionner une boutique."); return; }
        if (modeNouvelEmploye && mdp.isBlank()) {
            AlertHelper.warn("Validation", "Le mot de passe est obligatoire pour un nouvel employé.");
            return;
        }
        if (!mdp.isBlank() && mdp.length() < 8) {
            AlertHelper.warn("Validation", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        new Thread(() -> {
            try {
                EmployeDto result;
                if (modeNouvelEmploye) {
                    CreateEmployeDto req = new CreateEmployeDto();
                    req.nom         = nom;
                    req.prenom      = prenom;
                    req.email       = email;
                    req.telephone   = empTelField.getText().trim();
                    req.motDePasse  = mdp;
                    req.idRole      = role.id;
                    req.idMagasin   = mag.id;
                    req.dateEmbauche = empDateEmbauche.getValue() != null
                            ? empDateEmbauche.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
                    result = api.post("/employes", req, EmployeDto.class);
                } else {
                    if (employeSelectionne == null) return;
                    UpdateEmployeDto req = new UpdateEmployeDto();
                    req.nom         = nom;
                    req.prenom      = prenom;
                    req.email       = email;
                    req.telephone   = empTelField.getText().trim();
                    req.motDePasse  = mdp.isBlank() ? null : mdp;
                    req.idRole      = role.id;
                    req.idMagasin   = mag.id;
                    req.dateEmbauche = empDateEmbauche.getValue() != null
                            ? empDateEmbauche.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
                    req.actif       = empActifCheck.isSelected();
                    result = api.patch("/employes/" + employeSelectionne.id, req, EmployeDto.class);
                }
                final EmployeDto saved = result;
                Platform.runLater(() -> {
                    AlertHelper.success("Employé", "Enregistré : " + saved.getNomComplet());
                    modeNouvelEmploye = false;
                    labelMdpHint.setText("(Laisser vide pour ne pas modifier)");
                    chargerEmployes(null);
                    chargerReferentiels(); // rafraîchir aussi le CB boutiques
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void handleSupprimerEmploye() {
        EmployeDto sel = tableEmployes.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Suppression", "Sélectionnez un employé."); return; }
        if (!AlertHelper.confirm("Supprimer", "Supprimer l'employé " + sel.getNomComplet() + " ?")) return;

        new Thread(() -> {
            try {
                api.delete("/employes/" + sel.id);
                Platform.runLater(() -> {
                    AlertHelper.success("Employé", "Employé supprimé.");
                    handleAnnulerEmploye();
                    chargerEmployes(null);
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ACTIONS BOUTIQUES
    // ─────────────────────────────────────────────────────────────

    @FXML void handleRefreshMagasins() { chargerMagasins(); }

    @FXML
    void handleNouvelMagasin() {
        modeNouvelMagasin = true;
        magasinSelectionne = null;
        tableMagasins.getSelectionModel().clearSelection();
        labelTitreMagasin.setText("Nouvelle boutique");
        viderFormulaireMagasin();
    }

    @FXML
    void handleAnnulerMagasin() {
        modeNouvelMagasin = false;
        magasinSelectionne = null;
        tableMagasins.getSelectionModel().clearSelection();
        labelTitreMagasin.setText("Sélectionnez une boutique");
        viderFormulaireMagasin();
    }

    @FXML
    void handleEnregistrerMagasin() {
        String nom = magNomField.getText().trim();
        if (nom.isBlank()) { AlertHelper.warn("Validation", "Le nom de la boutique est obligatoire."); return; }

        new Thread(() -> {
            try {
                MagasinAdminDto result;
                if (modeNouvelMagasin) {
                    CreateMagasinDto req = new CreateMagasinDto();
                    req.nom       = nom;
                    req.telephone = magTelField.getText().trim();
                    req.email     = magEmailField.getText().trim();
                    result = api.post("/magasins", req, MagasinAdminDto.class);
                } else {
                    if (magasinSelectionne == null) return;
                    UpdateMagasinDto req = new UpdateMagasinDto();
                    req.nom       = nom;
                    req.telephone = magTelField.getText().trim();
                    req.email     = magEmailField.getText().trim();
                    req.actif     = magActifCheck.isSelected();
                    result = api.patch("/magasins/" + magasinSelectionne.id, req, MagasinAdminDto.class);
                }
                final MagasinAdminDto saved = result;
                Platform.runLater(() -> {
                    AlertHelper.success("Boutique", "Enregistrée : " + saved.nom);
                    modeNouvelMagasin = false;
                    chargerMagasins();
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void handleSupprimerMagasin() {
        MagasinAdminDto sel = tableMagasins.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Désactivation", "Sélectionnez une boutique."); return; }
        if (!AlertHelper.confirm("Désactiver", "Désactiver la boutique « " + sel.nom + " » ?")) return;

        new Thread(() -> {
            try {
                api.delete("/magasins/" + sel.id);
                Platform.runLater(() -> {
                    AlertHelper.success("Boutique", "Boutique désactivée.");
                    handleAnnulerMagasin();
                    chargerMagasins();
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ACTIONS RÔLES
    // ─────────────────────────────────────────────────────────────

    @FXML void handleRefreshRoles() { chargerRoles(); }

    // ─────────────────────────────────────────────────────────────
    // HELPERS : remplissage / vidage des formulaires
    // ─────────────────────────────────────────────────────────────

    private void chargerFormulaireEmploye(EmployeDto e) {
        modeNouvelEmploye = false;
        employeSelectionne = e;
        labelTitreEmploye.setText("✏️  " + e.getNomComplet());
        labelMdpHint.setText("(Laisser vide pour ne pas modifier)");

        empNomField.setText(e.nom    != null ? e.nom    : "");
        empPrenomField.setText(e.prenom != null ? e.prenom : "");
        empEmailField.setText(e.email   != null ? e.email   : "");
        empTelField.setText(e.telephone != null ? e.telephone : "");
        empMdpField.clear();
        empActifCheck.setSelected(e.actif);

        if (e.dateEmbauche != null) {
            try { empDateEmbauche.setValue(LocalDate.parse(e.dateEmbauche)); }
            catch (Exception ignored) { empDateEmbauche.setValue(null); }
        } else {
            empDateEmbauche.setValue(null);
        }

        // Sélectionner le bon rôle dans le CB
        empRoleCb.getItems().stream()
                .filter(r -> r.code.equals(e.roleCode))
                .findFirst()
                .ifPresent(empRoleCb::setValue);

        // Sélectionner la bonne boutique dans le CB
        empMagasinCb.getItems().stream()
                .filter(m -> m.id != null && m.id.equals(e.magasinId))
                .findFirst()
                .ifPresent(empMagasinCb::setValue);
    }

    private void viderFormulaireEmploye() {
        empNomField.clear();
        empPrenomField.clear();
        empEmailField.clear();
        empTelField.clear();
        empMdpField.clear();
        empRoleCb.setValue(null);
        empMagasinCb.setValue(null);
        empDateEmbauche.setValue(null);
        empActifCheck.setSelected(true);
    }

    private void chargerFormulaireMagasin(MagasinAdminDto m) {
        modeNouvelMagasin = false;
        magasinSelectionne = m;
        labelTitreMagasin.setText("✏️  " + m.nom);

        magNomField.setText(m.nom       != null ? m.nom       : "");
        magTelField.setText(m.telephone != null ? m.telephone : "");
        magEmailField.setText(m.email   != null ? m.email     : "");
        magActifCheck.setSelected(m.actif);
    }

    private void viderFormulaireMagasin() {
        magNomField.clear();
        magTelField.clear();
        magEmailField.clear();
        magActifCheck.setSelected(true);
    }
}
