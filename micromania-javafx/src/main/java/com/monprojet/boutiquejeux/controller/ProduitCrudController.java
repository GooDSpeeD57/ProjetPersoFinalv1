package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.produit.*;
import com.monprojet.boutiquejeux.dto.referentiel.EditionDto;
import com.monprojet.boutiquejeux.dto.referentiel.FormatProduitDto;
import com.monprojet.boutiquejeux.dto.referentiel.PlateformeDto;
import com.monprojet.boutiquejeux.dto.referentiel.StatutProduitDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProduitCrudController {

    // ── Liste produits (gauche) ──────────────────────────────────
    @FXML private TextField      rechercheField;
    @FXML private ProgressIndicator spinnerListe;
    @FXML private TableView<ProduitDto>           tableProduits;
    @FXML private TableColumn<ProduitDto, String> colNom;
    @FXML private TableColumn<ProduitDto, String> colCat;
    @FXML private TableColumn<ProduitDto, String> colPegi;
    @FXML private TableColumn<ProduitDto, String> colPrixN;
    @FXML private TableColumn<ProduitDto, String> colPrixO;
    @FXML private TableColumn<ProduitDto, String> colVedette;
    @FXML private TableColumn<ProduitDto, String> colPreco;
    @FXML private Button btnNouveau;
    @FXML private Button btnSupprimer;

    // ── Formulaire (droite) ──────────────────────────────────────
    @FXML private Label            labelTitreForm;
    @FXML private ProgressIndicator spinnerForm;
    @FXML private TextField        nomField;
    @FXML private TextField        slugField;
    @FXML private ComboBox<CategorieDto> categorieCb;
    @FXML private ComboBox<Integer>      pegiCb;
    @FXML private TextField        editeurField;
    @FXML private TextField        constructeurField;
    @FXML private TextField        marqueField;
    @FXML private DatePicker       dateSortiePicker;
    @FXML private ComboBox<String> niveauAccesCb;
    @FXML private ComboBox<String> langueCb;
    @FXML private CheckBox         checkMisEnAvant;
    @FXML private CheckBox         checkPreCommande;
    @FXML private TextArea         descriptionArea;
    @FXML private TextField        resumeCourtField;
    @FXML private Button           btnEnregistrer;
    @FXML private Button           btnAnnuler;

    // ── Variants existants ───────────────────────────────────────
    @FXML private TableView<ProduitVariantDto>           tableVariants;
    @FXML private TableColumn<ProduitVariantDto, String> colVSku;
    @FXML private TableColumn<ProduitVariantDto, String> colVNom;
    @FXML private TableColumn<ProduitVariantDto, String> colVPlatf;
    @FXML private TableColumn<ProduitVariantDto, String> colVStatut;
    @FXML private TableColumn<ProduitVariantDto, String> colVPrixWeb;
    @FXML private TableColumn<ProduitVariantDto, String> colVPrixMag;
    @FXML private TableColumn<ProduitVariantDto, String> colVPrixRep;
    @FXML private TableColumn<ProduitVariantDto, String> colVActif;

    // ── Édition variant existant ─────────────────────────────────
    @FXML private VBox                       cardEditVariant;
    @FXML private Label                      labelEditVariant;
    @FXML private TextField                  editVNomField;
    @FXML private ComboBox<EditionDto>       editVEditionCb;
    @FXML private TextField                  editVPrixWebField;
    @FXML private TextField                  editVPrixMagField;
    @FXML private TextField                  editVPrixRepriseField;
    @FXML private TextField                  editVPrixLocationField;
    @FXML private ComboBox<PlateformeDto>    editVPlatfCb;
    @FXML private ComboBox<StatutProduitDto> editVStatutCb;

    // ── Générateur de variants ───────────────────────────────────
    @FXML private ListView<PlateformeDto>    listePlatformes;
    @FXML private CheckBox                   checkStatutNeuf;
    @FXML private CheckBox                   checkStatutOccasion;
    @FXML private ListView<EditionDto>       listeEditions;
    @FXML private ComboBox<FormatProduitDto> comboFormatNouv;
    @FXML private TextField                  platNomField;
    @FXML private TextField                  platPrixWebField;
    @FXML private TextField                  platPrixMagField;
    @FXML private TextField                  platPrixRepriseField;
    @FXML private TextField                  platPrixLocationField;
    @FXML private TextField                  platImgUrlField;
    @FXML private TableView<PendingVariantRow>           tablePending;
    @FXML private TableColumn<PendingVariantRow, String> colPVPlatf;
    @FXML private TableColumn<PendingVariantRow, String> colPVStatut;
    @FXML private TableColumn<PendingVariantRow, String> colPVEdition;
    @FXML private TableColumn<PendingVariantRow, String> colPVNom;
    @FXML private TableColumn<PendingVariantRow, String> colPVPrixW;
    @FXML private TableColumn<PendingVariantRow, String> colPVPrixM;
    @FXML private TableColumn<PendingVariantRow, String> colPVImg;

    // ── Vidéos ────────────────────────────────────────────────────
    @FXML private TableView<VideoRow>              tableVideos;
    @FXML private TableColumn<VideoRow, String>    colVidOrdre;
    @FXML private TableColumn<VideoRow, String>    colVidTitre;
    @FXML private TableColumn<VideoRow, String>    colVidUrl;
    @FXML private TableColumn<VideoRow, String>    colVidLangue;
    @FXML private javafx.scene.layout.VBox         cardEditVideo;
    @FXML private TextField                        vidUrlField;
    @FXML private TextField                        vidTitreField;
    @FXML private TextField                        vidOrdreField;
    @FXML private TextField                        vidLangueField;

    // ── Screenshots ──────────────────────────────────────────────
    @FXML private TableView<ScreenshotRow>              tableScreenshots;
    @FXML private TableColumn<ScreenshotRow, String>    colSsOrdre;
    @FXML private TableColumn<ScreenshotRow, String>    colSsUrl;
    @FXML private TableColumn<ScreenshotRow, String>    colSsAlt;
    @FXML private javafx.scene.image.ImageView          previewScreenshot;
    @FXML private Label                                 labelScreenshotSelectionne;
    @FXML private TextField                             ssAltField;
    @FXML private TextField                             ssOrdreField;

    private java.util.List<java.io.File> selectedScreenshotFiles = new java.util.ArrayList<>();

    /** Dernier dossier utilisé par les FileChooser (mémorisé entre les ouvertures). */
    private static java.io.File lastChooserDir = null;

    // ── Images ───────────────────────────────────────────────────
    @FXML private TableView<ImageRow>           tableImages;
    @FXML private TableColumn<ImageRow, String> colImgPrincip;
    @FXML private TableColumn<ImageRow, String> colImgVariant;
    @FXML private TableColumn<ImageRow, String> colImgUrl;
    @FXML private TableColumn<ImageRow, String> colImgAlt;
    @FXML private javafx.scene.layout.VBox      cardEditImage;
    @FXML private TextField                     editImgUrlField;
    @FXML private TextField                     editImgAltField;
    @FXML private CheckBox                      editImgPrincipale;
    @FXML private javafx.scene.image.ImageView  previewImage;
    @FXML private Label                         labelImageSelectee;
    @FXML private TextField                     imgAltField;
    @FXML private CheckBox                      checkImgPrincipale;

    /** Fichier image sélectionné par le FileChooser (null si rien de choisi). */
    private java.io.File selectedImageFile = null;

    // ── État ─────────────────────────────────────────────────────
    private enum Mode { NONE, NOUVEAU, EDIT }
    private Mode   mode             = Mode.NONE;
    private Long   produitIdSelec   = null;
    private boolean slugModifieMain = false;

    private final ObservableList<ProduitDto>        produits    = FXCollections.observableArrayList();
    private final ObservableList<ProduitVariantDto> variants    = FXCollections.observableArrayList();
    private final ObservableList<CategorieDto>      categories  = FXCollections.observableArrayList();
    private final ObservableList<PlateformeDto>     plateformes = FXCollections.observableArrayList();
    private final ObservableList<FormatProduitDto>  formats     = FXCollections.observableArrayList();
    private final ObservableList<StatutProduitDto>  statuts     = FXCollections.observableArrayList();
    private final ObservableList<EditionDto>        editions    = FXCollections.observableArrayList();
    private final ObservableList<PendingVariantRow> pending     = FXCollections.observableArrayList();
    private final ObservableList<ImageRow>          images      = FXCollections.observableArrayList();
    private final ObservableList<ScreenshotRow>     screenshots = FXCollections.observableArrayList();
    private final ObservableList<VideoRow>          videos      = FXCollections.observableArrayList();
    /** ID de la vidéo sélectionnée pour édition (null = création). */
    private Long selectedVideoId = null;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    // ══════════════════════════════════════════════════════════════
    //  Initialisation
    // ══════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        spinnerListe.setVisible(false);
        spinnerForm.setVisible(false);

        // ── Table produits
        colNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        colCat.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().categorie != null ? c.getValue().categorie : ""));
        colPegi.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().pegi != null ? String.valueOf(c.getValue().pegi) : "—"));
        colPrixN.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixNeuf != null
                        ? String.format("%.2f €", c.getValue().prixNeuf) : "—"));
        colPrixO.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixOccasion != null
                        ? String.format("%.2f €", c.getValue().prixOccasion) : "—"));
        colVedette.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().misEnAvant ? "★" : ""));
        colPreco.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().estPreCommande ? "🕐" : ""));
        tableProduits.setItems(produits);
        tableProduits.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null && mode != Mode.NOUVEAU) chargerDetail(n.id);
        });

        // ── Table variants existants
        colVSku.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().sku != null ? c.getValue().sku : ""));
        colVNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nomCommercial != null ? c.getValue().nomCommercial : ""));
        colVPlatf.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlateformeCode()));
        colVStatut.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().statutProduit != null ? c.getValue().statutProduit : ""));
        colVPrixWeb.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixNeuf != null
                        ? String.format("%.2f €", c.getValue().prixNeuf) : "—"));
        colVPrixMag.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixOccasion != null
                        ? String.format("%.2f €", c.getValue().prixOccasion) : "—"));
        colVPrixRep.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixReprise != null
                        ? String.format("%.2f €", c.getValue().prixReprise) : "—"));
        colVActif.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().actif ? "✓" : "✗"));
        tableVariants.setItems(variants);

        // ── Table pending variants
        colPVPlatf.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().plateformeCode != null ? c.getValue().plateformeCode : ""));
        colPVStatut.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().statutCode != null ? c.getValue().statutCode : ""));
        colPVEdition.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().editionLibelle != null ? c.getValue().editionLibelle : ""));
        colPVNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nomCommercial != null ? c.getValue().nomCommercial : ""));
        colPVPrixW.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixNeuf != null
                        ? String.format("%.2f €", c.getValue().prixNeuf) : "—"));
        colPVPrixM.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().prixOccasion != null
                        ? String.format("%.2f €", c.getValue().prixOccasion) : "—"));
        colPVImg.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().imageUrl != null ? c.getValue().imageUrl : ""));
        tablePending.setItems(pending);

        // ── Table images
        colImgPrincip.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().principale ? "★" : ""));
        colImgVariant.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().variantSku != null ? c.getValue().variantSku : ""));
        colImgUrl.setCellValueFactory(c -> {
            String url = c.getValue().url != null ? c.getValue().url : "";
            // Afficher uniquement le nom de fichier pour la lisibilité
            int lastSlash = url.lastIndexOf('/');
            return new SimpleStringProperty(lastSlash >= 0 ? url.substring(lastSlash + 1) : url);
        });
        colImgAlt.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().alt != null ? c.getValue().alt : ""));
        tableImages.setItems(images);
        tableImages.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel == null) {
                editImgUrlField.clear();
                editImgAltField.clear();
                editImgPrincipale.setSelected(false);
                cardEditImage.setDisable(true);
            } else {
                editImgUrlField.setText(sel.url  != null ? sel.url  : "");
                editImgAltField.setText(sel.alt  != null ? sel.alt  : "");
                editImgPrincipale.setSelected(sel.principale);
                cardEditImage.setDisable(false);
            }
        });

        // ── Table screenshots
        colSsOrdre.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().ordreAffichage)));
        colSsUrl.setCellValueFactory(c -> {
            String url = c.getValue().url != null ? c.getValue().url : "";
            int lastSlash = url.lastIndexOf('/');
            return new SimpleStringProperty(lastSlash >= 0 ? url.substring(lastSlash + 1) : url);
        });
        colSsAlt.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().alt != null ? c.getValue().alt : ""));
        tableScreenshots.setItems(screenshots);

        // ── Table vidéos
        colVidOrdre.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().ordreAffichage)));
        colVidTitre.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().titre != null ? c.getValue().titre : ""));
        colVidUrl.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().url != null ? c.getValue().url : ""));
        colVidLangue.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().langue != null ? c.getValue().langue : "fr"));
        tableVideos.setItems(videos);
        tableVideos.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel == null) {
                viderFormVideo();
            } else {
                selectedVideoId = sel.id;
                vidUrlField.setText(sel.url != null ? sel.url : "");
                vidTitreField.setText(sel.titre != null ? sel.titre : "");
                vidOrdreField.setText(String.valueOf(sel.ordreAffichage));
                vidLangueField.setText(sel.langue != null ? sel.langue : "fr");
            }
        });
        cardEditVideo.setDisable(false);

        // ── ComboBoxes
        categorieCb.setConverter(new StringConverter<>() {
            @Override public String toString(CategorieDto c)   { return c == null ? "" : c.nom; }
            @Override public CategorieDto fromString(String s) { return null; }
        });
        categorieCb.setItems(categories);
        pegiCb.setItems(FXCollections.observableArrayList(3, 7, 12, 16, 18));
        niveauAccesCb.setItems(FXCollections.observableArrayList("NORMAL", "PREMIUM", "ULTIMATE"));
        niveauAccesCb.setValue("NORMAL");
        langueCb.setItems(FXCollections.observableArrayList(
                "fr", "en", "de", "es", "it", "pt", "nl", "ja", "ko", "zh"));
        langueCb.setValue("fr");

        // ── ComboBoxes édition variant existant
        editVPlatfCb.setConverter(new StringConverter<>() {
            @Override public String toString(PlateformeDto p)   { return p == null ? "" : p.toString(); }
            @Override public PlateformeDto fromString(String s) { return null; }
        });
        editVPlatfCb.setItems(plateformes);

        editVStatutCb.setConverter(new StringConverter<>() {
            @Override public String toString(StatutProduitDto s)   { return s == null ? "" : s.toString(); }
            @Override public StatutProduitDto fromString(String s) { return null; }
        });
        editVStatutCb.setItems(statuts);

        // ── ComboBox édition (formulaire de modification)
        editVEditionCb.setConverter(new StringConverter<>() {
            @Override public String toString(EditionDto e)   { return e == null ? "— aucune —" : e.toString(); }
            @Override public EditionDto fromString(String s) { return null; }
        });
        editVEditionCb.setItems(editions);

        // ── ListView éditions (générateur multi-sélection)
        listeEditions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listeEditions.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(EditionDto e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.toString());
            }
        });
        listeEditions.setItems(editions);

        // ── Sélection dans tableVariants → remplir la carte d'édition
        tableVariants.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n == null) {
                labelEditVariant.setText("— sélectionnez une ligne —");
                viderFormEditVariant();
            } else {
                peuplerFormEditVariant(n);
            }
        });

        // ── ListView plateformes (multi-sélection)
        listePlatformes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listePlatformes.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(PlateformeDto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.toString());
            }
        });
        listePlatformes.setItems(plateformes);

        // ── ComboBox format
        comboFormatNouv.setConverter(new StringConverter<>() {
            @Override public String toString(FormatProduitDto f)   { return f == null ? "" : f.toString(); }
            @Override public FormatProduitDto fromString(String s) { return null; }
        });
        comboFormatNouv.setItems(formats);

        // ── Auto-slug depuis le nom
        nomField.textProperty().addListener((obs, o, n) -> {
            if (!slugModifieMain && mode != Mode.EDIT) slugField.setText(generateSlug(n));
        });
        slugField.textProperty().addListener((obs, o, n) ->
                slugModifieMain = !n.equals(generateSlug(nomField.getText())));

        // ── Auto-précommande si date de sortie dans le futur
        dateSortiePicker.valueProperty().addListener((obs, o, n) -> {
            if (n != null && n.isAfter(LocalDate.now())) {
                checkPreCommande.setSelected(true);
            }
        });

        // ── Contrôle d'accès
        boolean canWrite = SessionManager.getInstance().isManager();
        setFormEditable(canWrite);
        if (!canWrite) {
            btnNouveau.setVisible(false);   btnNouveau.setManaged(false);
            btnSupprimer.setVisible(false); btnSupprimer.setManaged(false);
        }

        viderForm();
        chargerCategories();
        chargerPlateformes();
        chargerFormats();
        chargerStatuts();
        chargerEditions();
        chargerProduits("");
    }

    // ══════════════════════════════════════════════════════════════
    //  Navigation
    // ══════════════════════════════════════════════════════════════

    @FXML private void handleRecherche() { chargerProduits(rechercheField.getText().trim()); }
    @FXML private void handleRefresh()   { chargerProduits(rechercheField.getText().trim()); }

    // ══════════════════════════════════════════════════════════════
    //  CRUD handlers
    // ══════════════════════════════════════════════════════════════

    @FXML
    private void handleNouveau() {
        viderForm();
        mode            = Mode.NOUVEAU;
        produitIdSelec  = null;
        slugModifieMain = false;
        labelTitreForm.setText("Nouveau produit");
        tableProduits.getSelectionModel().clearSelection();
        nomField.requestFocus();
    }

    @FXML
    private void handleSupprimer() {
        ProduitDto sel = tableProduits.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez un produit à supprimer."); return; }
        if (!AlertHelper.confirm("Supprimer le produit",
                "Supprimer définitivement « " + sel.nom + " » ?\nSes variants et stocks associés seront également affectés.")) return;

        spinnerListe.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().delete("/produits/" + sel.id);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerListe.setVisible(false);
            AlertHelper.success("Supprimé", "✅ Produit supprimé.");
            viderForm();
            chargerProduits(rechercheField.getText().trim());
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerListe.setVisible(false));
            String msg = msgErr(task.getException());
            AlertHelper.error("Erreur suppression", msg);
        });
        new Thread(task).start();
    }

    @FXML
    private void handleEnregistrer() {
        // ── Gardes d'état ───────────────────────────────────────
        if (mode == Mode.NONE) {
            AlertHelper.warn("Formulaire vide",
                    "Cliquez sur '➕ Nouveau' pour créer un produit,\n"
                  + "ou sélectionnez un produit dans la liste pour le modifier.");
            return;
        }
        if (mode == Mode.EDIT && produitIdSelec == null) {
            AlertHelper.warn("Erreur", "Identifiant produit manquant — rechargez la vue.");
            return;
        }

        CreateProduitDto dto = construireDto();
        if (dto == null) return;

        spinnerForm.setVisible(true);
        boolean isNouveau = mode == Mode.NOUVEAU;
        String  endpoint  = isNouveau ? "/produits" : "/produits/" + produitIdSelec;
        List<PendingVariantRow> pendingSnapshot = new ArrayList<>(pending);

        Task<ProduitDetailDto> task = new Task<>() {
            @Override protected ProduitDetailDto call() throws Exception {
                ProduitDetailDto saved = isNouveau
                    ? ApiClient.getInstance().post(endpoint, dto, ProduitDetailDto.class)
                    : ApiClient.getInstance().put(endpoint, dto, ProduitDetailDto.class);

                if (saved == null || saved.id == null) {
                    throw new IllegalStateException(
                            "L'API n'a pas retourné l'identifiant du produit sauvegardé.");
                }

                // Créer les variants en attente
                boolean premièreImageAjoutée = false;
                for (PendingVariantRow row : pendingSnapshot) {
                    CreateVariantDto v = new CreateVariantDto();
                    v.idProduit       = saved.id;
                    v.idPlateforme    = row.plateformeId;
                    v.idFormatProduit = row.idFormatProduit;
                    v.idStatutProduit = row.idStatutProduit;
                    v.nomCommercial   = row.nomCommercial;
                    v.idEdition       = row.idEdition;
                    v.sku             = genererSku(saved.slug, row.plateformeCode, row.editionLibelle);
                    v.prixNeuf        = row.prixNeuf;
                    v.prixOccasion    = row.prixOccasion;
                    v.prixReprise     = row.prixReprise;
                    v.prixLocation    = row.prixLocation;
                    ProduitVariantDto variantCree = ApiClient.getInstance()
                            .post("/variants", v, ProduitVariantDto.class);

                    // Lier l'image au variant créé
                    if (row.imageUrl != null && !row.imageUrl.isBlank() && variantCree != null) {
                        CreateProduitImageDto img = new CreateProduitImageDto(
                                row.imageUrl, row.plateformeCode, !premièreImageAjoutée);
                        ApiClient.getInstance().post("/variants/" + variantCree.id + "/images",
                                img, Object.class);
                        premièreImageAjoutée = true;
                    }
                }
                // Recharger le détail après création des variants/images
                return ApiClient.getInstance().get("/produits/" + saved.id, ProduitDetailDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            ProduitDetailDto saved = task.getValue();
            Platform.runLater(() -> {
                spinnerForm.setVisible(false);
                AlertHelper.success(isNouveau ? "Produit créé" : "Produit mis à jour",
                        "✅ « " + saved.nom + " » enregistré.");
                mode = Mode.EDIT;
                produitIdSelec = saved.id;
                pending.clear();
                chargerProduits(rechercheField.getText().trim());
                peuplerFormulaire(saved);
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur enregistrement", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleAnnuler() {
        if (mode == Mode.EDIT && produitIdSelec != null) chargerDetail(produitIdSelec);
        else { viderForm(); tableProduits.getSelectionModel().clearSelection(); }
    }

    // ══════════════════════════════════════════════════════════════
    //  Variants – édition variant existant
    // ══════════════════════════════════════════════════════════════

    @FXML
    private void handleSauvegarderVariant() {
        ProduitVariantDto sel = tableVariants.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez d'abord un variant à modifier."); return; }
        if (mode == Mode.NOUVEAU) { AlertHelper.warn("Mode", "Enregistrez d'abord le produit avant de modifier ses variants."); return; }

        String nomComm = editVNomField.getText().trim();
        if (nomComm.isBlank()) { AlertHelper.warn("Validation", "Le nom commercial est obligatoire."); return; }

        PlateformeDto platf    = editVPlatfCb.getValue();
        StatutProduitDto statut = editVStatutCb.getValue();

        // Pour le PUT on reconstruit un CreateVariantDto avec les champs requis
        // (idProduit, sku, idFormatProduit, idStatutProduit, nomCommercial) sont obligatoires côté API
        // On récupère le format courant depuis la liste chargée
        FormatProduitDto formatActuel = formats.stream()
                .filter(f -> f.toString().equalsIgnoreCase(sel.formatProduit))
                .findFirst().orElse(formats.isEmpty() ? null : formats.get(0));

        if (formatActuel == null) { AlertHelper.warn("Format", "Format du variant introuvable — rechargez la vue."); return; }
        if (statut == null)       { AlertHelper.warn("Statut", "Sélectionnez un statut."); return; }

        CreateVariantDto dto = new CreateVariantDto();
        dto.idProduit       = produitIdSelec;
        dto.idPlateforme    = platf != null ? platf.id : null;
        dto.idFormatProduit = formatActuel.id;
        dto.idStatutProduit = statut.id;
        dto.nomCommercial   = nomComm;
        dto.idEdition       = editVEditionCb.getValue() != null ? editVEditionCb.getValue().id : null;
        dto.sku             = sel.sku;   // inchangé
        dto.prixNeuf        = parseDouble(editVPrixWebField.getText());
        dto.prixOccasion    = parseDouble(editVPrixMagField.getText());
        dto.prixReprise     = parseDouble(editVPrixRepriseField.getText());
        dto.prixLocation    = parseDouble(editVPrixLocationField.getText());

        spinnerForm.setVisible(true);
        final Long variantId = sel.id;
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().put("/variants/" + variantId, dto, Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            AlertHelper.success("Variant modifié", "✅ Variant mis à jour.");
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur modification", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleDesactiverVariant() {
        toggleActifVariant(false);
    }

    @FXML
    private void handleReactiverVariant() {
        toggleActifVariant(true);
    }

    private void toggleActifVariant(boolean actif) {
        ProduitVariantDto sel = tableVariants.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez un variant."); return; }

        String action = actif ? "réactiver" : "désactiver";
        if (!AlertHelper.confirm("Confirmer", "Voulez-vous " + action + " le variant « " + sel.nomCommercial + " » ?")) return;

        spinnerForm.setVisible(true);
        final Long variantId = sel.id;
        // Corps JSON minimal : {"actif": true/false}
        record ToggleBody(boolean actif) {}
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().patch("/variants/" + variantId + "/actif",
                        new ToggleBody(actif), Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            AlertHelper.success(actif ? "Réactivé" : "Désactivé",
                    "✅ Variant " + (actif ? "réactivé" : "désactivé") + ".");
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    private void peuplerFormEditVariant(ProduitVariantDto v) {
        labelEditVariant.setText(v.sku);
        editVNomField.setText(v.nomCommercial != null ? v.nomCommercial : "");
        // Sélectionner l'édition dans le ComboBox
        if (v.edition != null) {
            editions.stream().filter(e -> e.id.equals(v.edition.id))
                    .findFirst().ifPresentOrElse(editVEditionCb::setValue, () -> editVEditionCb.setValue(null));
        } else {
            editVEditionCb.setValue(null);
        }
        editVPrixWebField.setText(v.prixNeuf      != null ? v.prixNeuf.toPlainString()      : "");
        editVPrixMagField.setText(v.prixOccasion  != null ? v.prixOccasion.toPlainString()  : "");
        editVPrixRepriseField.setText(v.prixReprise  != null ? v.prixReprise.toPlainString()  : "");
        editVPrixLocationField.setText(v.prixLocation != null ? v.prixLocation.toPlainString() : "");

        // Plateforme : matcher par code
        if (v.plateforme != null) {
            plateformes.stream().filter(p -> p.code.equals(v.plateforme.code))
                    .findFirst().ifPresent(editVPlatfCb::setValue);
        } else {
            editVPlatfCb.setValue(null);
        }
        // Statut : matcher par code
        statuts.stream().filter(s -> s.code.equalsIgnoreCase(v.statutProduit))
                .findFirst().ifPresentOrElse(editVStatutCb::setValue, () -> editVStatutCb.setValue(null));
    }

    private void viderFormEditVariant() {
        editVNomField.clear();
        editVEditionCb.setValue(null);
        editVPrixWebField.clear();
        editVPrixMagField.clear();
        editVPrixRepriseField.clear();
        editVPrixLocationField.clear();
        editVPlatfCb.setValue(null);
        editVStatutCb.setValue(null);
    }

    // ══════════════════════════════════════════════════════════════
    //  Variants – génération / retrait
    // ══════════════════════════════════════════════════════════════

    @FXML
    private void handleAjouterVariant() {
        // 1. Plateformes sélectionnées
        List<PlateformeDto> platsSelec = new ArrayList<>(
                listePlatformes.getSelectionModel().getSelectedItems());
        if (platsSelec.isEmpty()) {
            AlertHelper.warn("Plateformes",
                    "Sélectionnez au moins une plateforme.\n(Ctrl+clic pour sélection multiple)");
            return;
        }

        // 2. Statuts cochés
        List<StatutProduitDto> statutsSelec = new ArrayList<>();
        if (checkStatutNeuf.isSelected())     trouverStatutParCode("NEUF").ifPresent(statutsSelec::add);
        if (checkStatutOccasion.isSelected()) trouverStatutParCode("OCCASION").ifPresent(statutsSelec::add);
        if (statutsSelec.isEmpty()) {
            AlertHelper.warn("Statut", "Cochez au moins un statut (NEUF ou OCCASION).");
            return;
        }

        // 3. Format
        FormatProduitDto format = comboFormatNouv.getValue();
        if (format == null) { AlertHelper.warn("Format", "Sélectionnez un format produit."); return; }

        // 4. Éditions (multi-sélection ; aucune sélection = sans édition)
        List<EditionDto> editionsSelec = new ArrayList<>(
                listeEditions.getSelectionModel().getSelectedItems());
        // Si rien n'est coché : une seule combinaison sans édition (null)
        List<EditionDto> editionsEffectives = editionsSelec.isEmpty()
                ? java.util.Collections.singletonList(null)
                : editionsSelec;

        // 5. Paramètres communs
        Double prixNeuf     = parseDouble(platPrixWebField.getText());
        Double prixOccasion = parseDouble(platPrixMagField.getText());
        Double prixReprise  = parseDouble(platPrixRepriseField.getText());
        Double prixLocation = parseDouble(platPrixLocationField.getText());
        String imgUrl  = platImgUrlField.getText().trim();
        String baseNom = platNomField.getText().trim();
        if (baseNom.isBlank()) baseNom = nomField.getText().trim();

        // 6. Générer toutes les combinaisons plateforme × statut × édition
        List<PendingVariantRow> nouvelles = new ArrayList<>();
        for (PlateformeDto plat : platsSelec) {
            for (StatutProduitDto statut : statutsSelec) {
                for (EditionDto edition : editionsEffectives) {
                    PendingVariantRow row = new PendingVariantRow();
                    row.plateformeId    = plat.id;
                    row.plateformeCode  = plat.code;
                    row.idFormatProduit = format.id;
                    row.idStatutProduit = statut.id;
                    row.statutCode      = statut.code;
                    row.idEdition       = edition != null ? edition.id       : null;
                    row.editionLibelle  = edition != null ? edition.libelle  : null;

                    String nom = baseNom + " " + plat.code;
                    if (edition != null && edition.libelle != null) nom += " " + edition.libelle;
                    row.nomCommercial   = nom.trim();
                    row.prixNeuf        = prixNeuf;
                    row.prixOccasion    = prixOccasion;
                    row.prixReprise     = prixReprise;
                    row.prixLocation    = prixLocation;
                    row.imageUrl        = imgUrl.isBlank() ? null : imgUrl;
                    nouvelles.add(row);
                }
            }
        }

        if (mode == Mode.EDIT && produitIdSelec != null) {
            // Mode édition : créer immédiatement toutes les combinaisons
            final Long   idProduit = produitIdSelec;
            final String slug      = slugField.getText().trim();
            spinnerForm.setVisible(true);
            Task<Void> task = new Task<>() {
                @Override protected Void call() throws Exception {
                    boolean premièreImage = images.isEmpty();
                    for (PendingVariantRow row : nouvelles) {
                        CreateVariantDto v = new CreateVariantDto();
                        v.idProduit       = idProduit;
                        v.idPlateforme    = row.plateformeId;
                        v.idFormatProduit = row.idFormatProduit;
                        v.idStatutProduit = row.idStatutProduit;
                        v.nomCommercial   = row.nomCommercial;
                        v.idEdition       = row.idEdition;
                        v.sku             = genererSku(slug, row.plateformeCode, row.editionLibelle);
                        v.prixNeuf        = row.prixNeuf;
                        v.prixOccasion    = row.prixOccasion;
                        v.prixReprise     = row.prixReprise;
                        v.prixLocation    = row.prixLocation;
                        ProduitVariantDto variantCree = ApiClient.getInstance()
                                .post("/variants", v, ProduitVariantDto.class);

                        if (row.imageUrl != null && !row.imageUrl.isBlank() && variantCree != null) {
                            CreateProduitImageDto img = new CreateProduitImageDto(
                                    row.imageUrl, row.plateformeCode, premièreImage);
                            ApiClient.getInstance().post(
                                    "/variants/" + variantCree.id + "/images", img, Object.class);
                            premièreImage = false;
                        }
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> Platform.runLater(() -> {
                spinnerForm.setVisible(false);
                AlertHelper.success("Variants créés",
                        "✅ " + nouvelles.size() + " variant(s) créé(s).");
                viderFormVariant();
                chargerDetail(produitIdSelec);
            }));
            task.setOnFailed(e -> {
                Platform.runLater(() -> spinnerForm.setVisible(false));
                AlertHelper.error("Erreur variant", msgErr(task.getException()));
            });
            new Thread(task).start();

        } else {
            // Mode NOUVEAU : ajouter à la liste en attente
            pending.addAll(nouvelles);
            viderFormVariant();
        }
    }

    @FXML
    private void handleRetirerVariant() {
        PendingVariantRow sel = tablePending.getSelectionModel().getSelectedItem();
        if (sel != null) pending.remove(sel);
    }

    // ══════════════════════════════════════════════════════════════
    //  Images – ajout / suppression
    // ══════════════════════════════════════════════════════════════

    /** Ouvre le sélecteur de fichier et affiche l'aperçu. */
    @FXML
    private void handleChoisirImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner une image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        if (lastChooserDir != null && lastChooserDir.exists())
            chooser.setInitialDirectory(lastChooserDir);
        java.io.File file = chooser.showOpenDialog(
                previewImage.getScene() != null ? previewImage.getScene().getWindow() : null);
        if (file == null) return;
        lastChooserDir = file.getParentFile();

        selectedImageFile = file;
        labelImageSelectee.setText(file.getName());
        labelImageSelectee.setStyle("-fx-font-size:11;-fx-text-fill:#ccc;");

        // Aperçu local immédiat (sans passer par l'API)
        try {
            Image img = new Image(file.toURI().toString(), 100, 100, true, true);
            previewImage.setImage(img);
        } catch (Exception ignored) { /* aperçu non critique */ }
    }

    /** Upload le fichier sélectionné et l'associe au variant actif. */
    @FXML
    private void handleAjouterImage() {
        if (selectedImageFile == null) {
            AlertHelper.warn("Fichier manquant", "Cliquez sur '📁 Choisir un fichier…' pour sélectionner une image.");
            return;
        }
        if (mode != Mode.EDIT || produitIdSelec == null) {
            AlertHelper.warn("Mode", "Enregistrez d'abord le produit avant d'ajouter des images.");
            return;
        }
        ProduitVariantDto variantSelec = tableVariants.getSelectionModel().getSelectedItem();
        if (variantSelec == null) {
            AlertHelper.warn("Variant", "Sélectionnez un variant dans l'onglet Variants avant d'uploader.");
            return;
        }

        boolean principale = checkImgPrincipale.isSelected();
        String  alt        = imgAltField.getText().trim();
        final java.io.File fileToUpload = selectedImageFile;
        final Long variantId = variantSelec.id;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().uploadImage(
                        "/variants/" + variantId + "/images/upload",
                        fileToUpload, alt, principale,
                        Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            // Réinitialiser le formulaire
            selectedImageFile = null;
            labelImageSelectee.setText("Aucun fichier sélectionné");
            labelImageSelectee.setStyle("-fx-font-size:11;-fx-text-fill:#aaa;");
            previewImage.setImage(null);
            imgAltField.clear();
            checkImgPrincipale.setSelected(false);
            chargerDetail(produitIdSelec);
            AlertHelper.success("Image uploadée", "✅ Image associée au variant « " + variantSelec.sku + " ».");
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur upload", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleModifierImage() {
        ImageRow sel = tableImages.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez une image dans la liste."); return; }

        String url = editImgUrlField.getText().trim();
        if (url.isBlank()) { AlertHelper.warn("URL manquante", "L'URL ne peut pas être vide."); return; }

        // DTO minimal — seuls les champs fournis sont envoyés
        record UpdateImageBody(String url, String alt, Boolean principale) {}
        String altVal = editImgAltField.getText().trim();
        UpdateImageBody body = new UpdateImageBody(
                url,
                altVal.isBlank() ? null : altVal,
                editImgPrincipale.isSelected()
        );

        spinnerForm.setVisible(true);
        final Long variantId = sel.variantId;
        final Long imageId   = sel.id;
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().patch(
                        "/variants/" + variantId + "/images/" + imageId,
                        body, Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            chargerDetail(produitIdSelec);
            AlertHelper.success("Image modifiée", "✅ Chemin mis à jour.");
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur modification image", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleSupprimerImage() {
        ImageRow sel = tableImages.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez une image à supprimer."); return; }
        if (!AlertHelper.confirm("Supprimer l'image", "Supprimer cette image ?")) return;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().delete("/variants/" + sel.variantId + "/images/" + sel.id);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur suppression image", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    // ── Screenshots ───────────────────────────────────────────────

    @FXML
    private void handleChoisirScreenshot() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Choisir un ou plusieurs screenshots");
        fc.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Images", "*.jpg","*.jpeg","*.png","*.webp","*.gif"));
        if (lastChooserDir != null && lastChooserDir.exists())
            fc.setInitialDirectory(lastChooserDir);

        java.util.List<java.io.File> files = fc.showOpenMultipleDialog(tableScreenshots.getScene().getWindow());
        if (files == null || files.isEmpty()) return;

        lastChooserDir = files.get(0).getParentFile();
        selectedScreenshotFiles = new java.util.ArrayList<>(files);

        // Affichage : "3 fichiers sélectionnés" ou nom si un seul
        if (files.size() == 1) {
            labelScreenshotSelectionne.setText(files.get(0).getName());
            try {
                previewScreenshot.setImage(new javafx.scene.image.Image(
                        files.get(0).toURI().toString(), 240, 135, true, true));
            } catch (Exception ignored) {}
        } else {
            labelScreenshotSelectionne.setText(files.size() + " fichiers sélectionnés");
            try {
                previewScreenshot.setImage(new javafx.scene.image.Image(
                        files.get(0).toURI().toString(), 240, 135, true, true));
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleUploadScreenshot() {
        if (produitIdSelec == null) {
            AlertHelper.warn("Produit", "Enregistrez d'abord le produit avant d'ajouter un screenshot."); return;
        }
        if (selectedScreenshotFiles.isEmpty()) {
            AlertHelper.warn("Fichier", "Sélectionnez au moins un fichier image."); return;
        }

        final java.util.List<java.io.File> filesToUpload = new java.util.ArrayList<>(selectedScreenshotFiles);
        final String alt = ssAltField.getText().trim();
        final int ordreBase;
        int tmp = 0;
        try { tmp = Integer.parseInt(ssOrdreField.getText().trim()); } catch (Exception ignored) {}
        ordreBase = tmp;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                for (int i = 0; i < filesToUpload.size(); i++) {
                    ApiClient.getInstance().uploadScreenshot(
                            "/produits/" + produitIdSelec + "/screenshots/upload",
                            filesToUpload.get(i),
                            alt,
                            ordreBase + i,
                            ProduitDetailDto.ScreenshotRefDto.class);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            selectedScreenshotFiles.clear();
            labelScreenshotSelectionne.setText("Aucun fichier sélectionné");
            previewScreenshot.setImage(null);
            ssAltField.clear();
            ssOrdreField.clear();
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur upload screenshot", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleSupprimerScreenshot() {
        ScreenshotRow sel = tableScreenshots.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez un screenshot à supprimer."); return; }
        if (!AlertHelper.confirm("Supprimer le screenshot", "Supprimer ce screenshot ?")) return;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().delete("/produits/" + sel.produitId + "/screenshots/" + sel.id);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur suppression screenshot", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    // ── Vidéos ────────────────────────────────────────────────────

    private void viderFormVideo() {
        selectedVideoId = null;
        vidUrlField.clear();
        vidTitreField.clear();
        vidOrdreField.clear();
        vidLangueField.setText("fr");
        tableVideos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSauvegarderVideo() {
        if (produitIdSelec == null) {
            AlertHelper.warn("Produit", "Enregistrez d'abord le produit."); return;
        }
        String url = vidUrlField.getText().trim();
        String titre = vidTitreField.getText().trim();
        if (url.isEmpty() || titre.isEmpty()) {
            AlertHelper.warn("Champs requis", "URL et titre sont obligatoires."); return;
        }
        int ordre = 0;
        try { ordre = Integer.parseInt(vidOrdreField.getText().trim()); } catch (Exception ignored) {}
        String langue = vidLangueField.getText().trim();
        if (langue.isEmpty()) langue = "fr";

        final java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("url",             url);
        body.put("titre",           titre);
        body.put("ordreAffichage",  ordre);
        body.put("langue",          langue);
        body.put("sousTitresUrl",   null);
        body.put("audioDescUrl",    null);
        body.put("transcription",   null);
        final boolean isEdit = selectedVideoId != null;
        final Long videoId   = selectedVideoId;
        final Long pidSelec  = produitIdSelec;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                if (isEdit) {
                    // PUT /produits/{id}/videos/{idVideo} — si tu l'implémentes plus tard
                    // Pour l'instant : supprimer + recréer
                    ApiClient.getInstance().delete("/produits/" + pidSelec + "/videos/" + videoId);
                }
                ApiClient.getInstance().post("/produits/" + pidSelec + "/videos", body, Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            viderFormVideo();
            chargerDetail(pidSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur vidéo", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleSupprimerVideo() {
        VideoRow sel = tableVideos.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.warn("Sélection", "Sélectionnez une vidéo à supprimer."); return; }
        if (!AlertHelper.confirm("Supprimer la vidéo", "Supprimer \"" + sel.titre + "\" ?")) return;

        spinnerForm.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().delete("/produits/" + sel.produitId + "/videos/" + sel.id);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinnerForm.setVisible(false);
            viderFormVideo();
            chargerDetail(produitIdSelec);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Erreur suppression vidéo", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleAnnulerVideo() {
        viderFormVideo();
    }

    // ══════════════════════════════════════════════════════════════
    //  Chargements API
    // ══════════════════════════════════════════════════════════════

    private void chargerCategories() {
        Task<List<CategorieDto>> task = new Task<>() {
            @Override protected List<CategorieDto> call() throws Exception {
                return ApiClient.getInstance().get("/categories",
                        new TypeReference<List<CategorieDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                categories.setAll(task.getValue() != null ? task.getValue() : List.of())));
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    private void chargerPlateformes() {
        Task<List<PlateformeDto>> task = new Task<>() {
            @Override protected List<PlateformeDto> call() throws Exception {
                return ApiClient.getInstance().get("/referentiel/plateformes",
                        new TypeReference<List<PlateformeDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                plateformes.setAll(task.getValue() != null ? task.getValue() : List.of())));
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    private void chargerFormats() {
        Task<List<FormatProduitDto>> task = new Task<>() {
            @Override protected List<FormatProduitDto> call() throws Exception {
                return ApiClient.getInstance().get("/referentiel/formats-produit",
                        new TypeReference<List<FormatProduitDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                formats.setAll(task.getValue() != null ? task.getValue() : List.of())));
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    private void chargerStatuts() {
        Task<List<StatutProduitDto>> task = new Task<>() {
            @Override protected List<StatutProduitDto> call() throws Exception {
                return ApiClient.getInstance().get("/referentiel/statuts-produit",
                        new TypeReference<List<StatutProduitDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                statuts.setAll(task.getValue() != null ? task.getValue() : List.of())));
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    private void chargerEditions() {
        Task<List<EditionDto>> task = new Task<>() {
            @Override protected List<EditionDto> call() throws Exception {
                return ApiClient.getInstance().get("/referentiel/editions",
                        new TypeReference<List<EditionDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() ->
                editions.setAll(task.getValue() != null ? task.getValue() : List.of())));
        task.setOnFailed(e -> { /* silencieux — le champ sera vide si l'API est indisponible */ });
        new Thread(task).start();
    }

    private void chargerProduits(String q) {
        spinnerListe.setVisible(true);
        Task<PageDto<ProduitDto>> task = new Task<>() {
            @Override protected PageDto<ProduitDto> call() throws Exception {
                return ApiClient.getInstance().get("/produits?q=" + q + "&size=100",
                        new TypeReference<PageDto<ProduitDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            PageDto<ProduitDto> res     = task.getValue();
            List<ProduitDto>    content = res != null && res.content != null ? res.content : List.of();
            Platform.runLater(() -> {
                produits.setAll(content);
                spinnerListe.setVisible(false);
                if (produitIdSelec != null) {
                    produits.stream().filter(p -> p.id.equals(produitIdSelec)).findFirst()
                            .ifPresent(p -> tableProduits.getSelectionModel().select(p));
                }
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerListe.setVisible(false));
            AlertHelper.error("Chargement", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    private void chargerDetail(Long id) {
        spinnerForm.setVisible(true);
        Task<ProduitDetailDto> task = new Task<>() {
            @Override protected ProduitDetailDto call() throws Exception {
                return ApiClient.getInstance().get("/produits/" + id, ProduitDetailDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            ProduitDetailDto detail = task.getValue();
            Platform.runLater(() -> {
                spinnerForm.setVisible(false);
                mode           = Mode.EDIT;
                produitIdSelec = detail.id;
                peuplerFormulaire(detail);
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinnerForm.setVisible(false));
            AlertHelper.error("Détail produit", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    // ══════════════════════════════════════════════════════════════
    //  Helpers formulaire
    // ══════════════════════════════════════════════════════════════

    private void peuplerFormulaire(ProduitDetailDto d) {
        slugModifieMain = true;

        labelTitreForm.setText("Modifier : " + d.nom);
        nomField.setText(d.nom           != null ? d.nom           : "");
        slugField.setText(d.slug          != null ? d.slug          : "");
        editeurField.setText(d.editeur        != null ? d.editeur        : "");
        constructeurField.setText(d.constructeur  != null ? d.constructeur  : "");
        marqueField.setText(d.marque          != null ? d.marque          : "");
        descriptionArea.setText(d.description    != null ? d.description    : "");
        resumeCourtField.setText(d.resumeCourt    != null ? d.resumeCourt    : "");
        checkMisEnAvant.setSelected(d.misEnAvant);
        checkPreCommande.setSelected(d.estPreCommande);
        pegiCb.setValue(d.pegi);
        niveauAccesCb.setValue(d.niveauAccesMin != null ? d.niveauAccesMin : "NORMAL");
        langueCb.setValue(d.langue != null ? d.langue : "fr");

        if (d.dateSortie != null && !d.dateSortie.isBlank()) {
            try { dateSortiePicker.setValue(LocalDate.parse(d.dateSortie, FMT)); }
            catch (Exception ignored) { dateSortiePicker.setValue(null); }
        } else {
            dateSortiePicker.setValue(null);
        }

        if (d.categorie != null) {
            categories.stream().filter(c -> c.id.equals(d.categorie.id)).findFirst()
                    .ifPresent(categorieCb::setValue);
        }

        variants.setAll(d.variants != null ? d.variants : List.of());

        // Peupler images (avec SKU du variant pour affichage)
        // Peupler vidéos (niveau produit)
        videos.clear();
        if (d.videos != null) {
            for (ProduitDetailDto.VideoRefDto vid : d.videos) {
                VideoRow row = new VideoRow();
                row.id             = vid.id;
                row.produitId      = d.id;   // id du produit courant
                row.url            = vid.url;
                row.titre          = vid.titre;
                row.ordreAffichage = vid.ordreAffichage;
                row.langue         = vid.langue;
                videos.add(row);
            }
        }
        viderFormVideo();

        // Peupler screenshots (niveau produit)
        screenshots.clear();
        if (d.screenshots != null) {
            for (ProduitDetailDto.ScreenshotRefDto ss : d.screenshots) {
                ScreenshotRow row = new ScreenshotRow();
                row.id             = ss.id;
                row.produitId      = ss.produitId;
                row.url            = ss.url;
                row.alt            = ss.alt;
                row.ordreAffichage = ss.ordreAffichage;
                screenshots.add(row);
            }
        }

        images.clear();
        if (d.images != null) {
            // Index variantId → SKU pour l'affichage
            java.util.Map<Long, String> skuByVariantId = new java.util.HashMap<>();
            if (d.variants != null) {
                d.variants.forEach(v -> skuByVariantId.put(v.id, v.sku != null ? v.sku : "?"));
            }
            for (ProduitDetailDto.ProduitImageRefDto img : d.images) {
                ImageRow row = new ImageRow();
                row.id         = img.id;
                row.variantId  = img.variantId;
                row.variantSku = skuByVariantId.getOrDefault(img.variantId, "—");
                row.url        = img.url;
                row.alt        = img.alt;
                row.principale = img.principale;
                images.add(row);
            }
        }
    }

    private void viderForm() {
        mode           = Mode.NONE;
        produitIdSelec = null;
        slugModifieMain = false;
        labelTitreForm.setText("Sélectionnez un produit");
        nomField.clear(); slugField.clear(); editeurField.clear();
        constructeurField.clear(); marqueField.clear();
        descriptionArea.clear(); resumeCourtField.clear();
        checkMisEnAvant.setSelected(false);
        checkPreCommande.setSelected(false);
        dateSortiePicker.setValue(null);
        categorieCb.setValue(null);
        pegiCb.setValue(null);
        niveauAccesCb.setValue("NORMAL");
        langueCb.setValue("fr");
        variants.clear();
        pending.clear();
        videos.clear();
        viderFormVideo();
        screenshots.clear();
        selectedScreenshotFiles.clear();
        images.clear();
        editImgUrlField.clear();
        editImgAltField.clear();
        editImgPrincipale.setSelected(false);
        cardEditImage.setDisable(true);
        viderFormVariant();
    }

    private void viderFormVariant() {
        listePlatformes.getSelectionModel().clearSelection();
        checkStatutNeuf.setSelected(true);
        checkStatutOccasion.setSelected(false);
        listeEditions.getSelectionModel().clearSelection();
        comboFormatNouv.setValue(null);
        platNomField.clear();
        platPrixWebField.clear();
        platPrixMagField.clear();
        platPrixRepriseField.clear();
        platPrixLocationField.clear();
        platImgUrlField.clear();
    }

    private CreateProduitDto construireDto() {
        String nom  = nomField.getText().trim();
        String slug = slugField.getText().trim();
        if (nom.isEmpty())  { AlertHelper.warn("Validation", "Le nom est obligatoire."); nomField.requestFocus(); return null; }
        if (slug.isEmpty()) { AlertHelper.warn("Validation", "Le slug est obligatoire."); slugField.requestFocus(); return null; }
        if (categorieCb.getValue() == null) { AlertHelper.warn("Validation", "Sélectionnez une catégorie."); return null; }

        String resumeCourt = resumeCourtField.getText().trim();
        if (resumeCourt.length() > 500) {
            AlertHelper.warn("Validation", "Le résumé court dépasse 500 caractères."); return null;
        }

        CreateProduitDto dto = new CreateProduitDto();
        dto.nom            = nom;
        dto.slug           = slug;
        dto.idCategorie    = categorieCb.getValue().id;
        dto.editeur        = emptyToNull(editeurField.getText());
        dto.constructeur   = emptyToNull(constructeurField.getText());
        dto.marque         = emptyToNull(marqueField.getText());
        dto.description    = emptyToNull(descriptionArea.getText());
        dto.resumeCourt    = emptyToNull(resumeCourt);
        dto.pegi           = pegiCb.getValue();
        dto.niveauAccesMin = niveauAccesCb.getValue();
        dto.langue         = langueCb.getValue() != null ? langueCb.getValue() : "fr";
        dto.misEnAvant     = checkMisEnAvant.isSelected();
        dto.estPreCommande = checkPreCommande.isSelected();
        dto.dateSortie     = dateSortiePicker.getValue() != null
                           ? dateSortiePicker.getValue().format(FMT) : null;
        return dto;
    }

    private void setFormEditable(boolean editable) {
        nomField.setEditable(editable);
        slugField.setEditable(editable);
        editeurField.setEditable(editable);
        constructeurField.setEditable(editable);
        marqueField.setEditable(editable);
        descriptionArea.setEditable(editable);
        resumeCourtField.setEditable(editable);
        categorieCb.setDisable(!editable);
        pegiCb.setDisable(!editable);
        niveauAccesCb.setDisable(!editable);
        langueCb.setDisable(!editable);
        dateSortiePicker.setDisable(!editable);
        checkMisEnAvant.setDisable(!editable);
        checkPreCommande.setDisable(!editable);
        btnEnregistrer.setVisible(editable); btnEnregistrer.setManaged(editable);
        btnAnnuler.setVisible(editable);     btnAnnuler.setManaged(editable);
    }

    // ══════════════════════════════════════════════════════════════
    //  Utilitaires
    // ══════════════════════════════════════════════════════════════

    /** Cherche un StatutProduitDto par code (insensible à la casse). */
    private Optional<StatutProduitDto> trouverStatutParCode(String code) {
        return statuts.stream()
                .filter(s -> code.equalsIgnoreCase(s.code))
                .findFirst();
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static Double parseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.replace(',', '.')); }
        catch (NumberFormatException e) { return null; }
    }

    private static String generateSlug(String nom) {
        if (nom == null || nom.isBlank()) return "";
        return Normalizer.normalize(nom, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Génère un SKU déterministe : SLUG-PLATCODE[-EDITION]-HEX
     * L'édition est réduite à 6 caractères alphanumériques majuscules.
     */
    private static String genererSku(String slug, String platCode, String edition) {
        String edPart = "";
        if (edition != null && !edition.isBlank()) {
            String edClean = edition.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            edPart = "-" + (edClean.length() > 6 ? edClean.substring(0, 6) : edClean);
        }
        String base = (slug != null && !slug.isBlank() ? slug : "prod")
                    + "-" + (platCode != null ? platCode.toLowerCase() : "xx")
                    + edPart
                    + "-" + Long.toHexString(System.currentTimeMillis() & 0xFFFFFFFFL).toUpperCase();
        return base.length() > 50 ? base.substring(0, 50) : base;
    }

    private static String msgErr(Throwable ex) {
        return ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
    }

    // ══════════════════════════════════════════════════════════════
    //  Modèles internes
    // ══════════════════════════════════════════════════════════════

    /** Ligne de variant en attente de création (mode NOUVEAU) ou à créer immédiatement (EDIT). */
    public static class PendingVariantRow {
        public Long   plateformeId;
        public String plateformeCode;
        public Long   idFormatProduit;
        public Long   idStatutProduit;
        public String statutCode;
        public Long   idEdition;        // FK vers edition_produit
        public String editionLibelle;   // pour affichage / SKU
        public String nomCommercial;
        public Double prixNeuf;
        public Double prixOccasion;
        public Double prixReprise;
        public Double prixLocation;
        public String imageUrl;
    }

    /** Ligne vidéo affichée dans tableVideos. */
    public static class VideoRow {
        public Long   id;
        public Long   produitId;
        public String url;
        public String titre;
        public int    ordreAffichage;
        public String langue;
    }

    /** Ligne screenshot affichée dans tableScreenshots. */
    public static class ScreenshotRow {
        public Long   id;
        public Long   produitId;
        public String url;
        public String alt;
        public int    ordreAffichage;
    }

    /** Ligne image affichée dans tableImages. */
    public static class ImageRow {
        public Long    id;
        public Long    variantId;
        public String  variantSku;
        public String  url;
        public String  alt;
        public boolean principale;
    }
}
