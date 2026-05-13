package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDetailDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitVariantDto;
import com.monprojet.boutiquejeux.dto.referentiel.MagasinDto;
import com.monprojet.boutiquejeux.dto.stock.AjustementStockDto;
import com.monprojet.boutiquejeux.dto.stock.StockEntrepotDto;
import com.monprojet.boutiquejeux.dto.stock.StockMagasinDto;
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
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Vue "Gestion des stocks".
 *
 * Flux :
 *  1. Rechercher un produit → sélectionner → ses variants apparaissent en bas
 *  2. Sélectionner un variant → son stock tous lieux s'affiche à droite
 *  3. Choisir Magasin ou Dépôt dans la liste, entrer une quantité → Entrée / Sortie
 */
public class StockController {

    // ── Recherche ────────────────────────────────────────────────
    @FXML private TextField        rechercheField;
    @FXML private ProgressIndicator spinner;
    @FXML private Label            labelInfo;

    // ── Table produits (gauche haut) ─────────────────────────────
    @FXML private TableView<ProduitDto>           tableProduits;
    @FXML private TableColumn<ProduitDto, String> colPNom;
    @FXML private TableColumn<ProduitDto, String> colPCat;

    // ── Table variants (gauche bas) ──────────────────────────────
    @FXML private TableView<ProduitVariantDto>           tableVariants;
    @FXML private TableColumn<ProduitVariantDto, String> colVPlatf;
    @FXML private TableColumn<ProduitVariantDto, String> colVStatut;
    @FXML private TableColumn<ProduitVariantDto, String> colVEdition;
    @FXML private TableColumn<ProduitVariantDto, String> colVSku;

    // ── Table stock (droite haut) ────────────────────────────────
    @FXML private Label                            labelVariantSelec;
    @FXML private TableView<StockLigne>            tableStock;
    @FXML private TableColumn<StockLigne, String>  colLieu;
    @FXML private TableColumn<StockLigne, String>  colType;
    @FXML private TableColumn<StockLigne, String>  colQteNeuf;
    @FXML private TableColumn<StockLigne, String>  colQteOccaz;
    @FXML private TableColumn<StockLigne, String>  colQteDispo;
    @FXML private TableColumn<StockLigne, String>  colAlerte;

    // ── Formulaire mouvement (droite bas) ────────────────────────
    @FXML private ToggleGroup     modeGroup;
    @FXML private RadioButton     radioMagasin;
    @FXML private RadioButton     radioDepot;
    @FXML private ComboBox<LieuItem> comboLieu;
    @FXML private ComboBox<String>   comboSourceStock;
    @FXML private TextField          quantiteField;
    @FXML private TextField          commentaireField;

    // ── État ─────────────────────────────────────────────────────
    private ProduitVariantDto variantSelectionne = null;

    private final ObservableList<ProduitDto>       produits  = FXCollections.observableArrayList();
    private final ObservableList<ProduitVariantDto> variants  = FXCollections.observableArrayList();
    private final ObservableList<StockLigne>        stocks    = FXCollections.observableArrayList();
    private final ObservableList<LieuItem>          lieux     = FXCollections.observableArrayList();

    // ── Initialisation ───────────────────────────────────────────

    @FXML
    public void initialize() {
        spinner.setVisible(false);

        // ── Table produits
        colPNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        colPCat.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().categorie != null ? c.getValue().categorie : ""));
        tableProduits.setItems(produits);
        tableProduits.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            variants.clear();
            stocks.clear();
            variantSelectionne = null;
            labelVariantSelec.setText("← Sélectionnez un variant");
            if (n != null) chargerVariants(n.id);
        });

        // ── Table variants
        colVPlatf.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlateformeCode()));
        colVStatut.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().statutProduit != null ? c.getValue().statutProduit : ""));
        colVEdition.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEditionLibelle()));
        colVSku.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().sku != null ? c.getValue().sku : ""));
        tableVariants.setItems(variants);
        tableVariants.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                variantSelectionne = n;
                String label = n.nomCommercial != null ? n.nomCommercial : n.sku;
                String edLib = n.getEditionLibelle();
                if (!edLib.isBlank()) label += " — " + edLib;
                labelVariantSelec.setText("📦 " + label);
                chargerStockVariant(n.id);
            }
        });

        // ── Table stock
        colLieu.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().lieu));
        colType.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDepot ? "Dépôt" : "Mag."));
        colQteNeuf.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().qteNeuf)));
        colQteOccaz.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().qteOccaz >= 0
                        ? String.valueOf(c.getValue().qteOccaz) : "—"));
        colQteDispo.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().qteDispo)));
        colAlerte.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().qteDispo == 0 ? "🔴 Rupture"
                        : c.getValue().qteDispo <= 2 ? "🟠 Bas" : "🟢 OK"));
        tableStock.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(StockLigne item, boolean empty) {
                super.updateItem(item, empty);
                setStyle(!empty && item != null && item.qteDispo == 0
                        ? "-fx-background-color:#3d1a1a;"
                        : !empty && item != null && item.qteDispo <= 2
                        ? "-fx-background-color:#3d2e1a;" : "");
            }
        });
        tableStock.setItems(stocks);

        // ── ComboBox lieu
        comboLieu.setConverter(new StringConverter<>() {
            @Override public String toString(LieuItem l)    { return l == null ? "" : l.toString(); }
            @Override public LieuItem fromString(String s)  { return null; }
        });
        comboLieu.setItems(lieux);

        // ── Source stock
        comboSourceStock.setItems(FXCollections.observableArrayList("NEUF", "OCCASION", "REPRISE"));
        comboSourceStock.setValue("NEUF");

        // ── Bascule Magasin / Dépôt → recharger la liste des lieux
        modeGroup.selectedToggleProperty().addListener((obs, o, n) -> chargerLieux());

        // Chargements initiaux
        chargerMagasins();   // déclenche chargerLieux() après
        chargerProduits("");
    }

    // ── Chargements API ──────────────────────────────────────────

    private final ObservableList<MagasinDto> magasins = FXCollections.observableArrayList();

    private void chargerMagasins() {
        Task<List<MagasinDto>> task = new Task<>() {
            @Override protected List<MagasinDto> call() throws Exception {
                return ApiClient.getInstance().get("/magasins",
                        new TypeReference<List<MagasinDto>>() {});
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            magasins.setAll(task.getValue() != null ? task.getValue() : List.of());
            chargerLieux();
        }));
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    /** Remplit comboLieu avec les magasins ou les dépôts selon le radio sélectionné. */
    private void chargerLieux() {
        lieux.clear();
        boolean depotMode = radioDepot != null && radioDepot.isSelected();

        if (depotMode) {
            // Dépôts : on appelle l'API seulement si un variant est sélectionné,
            // sinon on utilise des libellés statiques ou on attend.
            // Pour une UX simple : un seul "Dépôt central" si pas d'entrepôts distincts.
            // On ajoutera les vrais entrepôts quand un variant est chargé.
            if (variantSelectionne != null) {
                chargerEntrepotsVariant(variantSelectionne.id);
            }
        } else {
            // Magasins
            Long sessionMagId = SessionManager.getInstance().getMagasinId();
            for (MagasinDto m : magasins) {
                lieux.add(new LieuItem(m.id, m.toString(), false));
            }
            // Pré-sélectionner le magasin de la session
            lieux.stream()
                    .filter(l -> l.id.equals(sessionMagId))
                    .findFirst()
                    .ifPresentOrElse(
                            comboLieu::setValue,
                            () -> { if (!lieux.isEmpty()) comboLieu.setValue(lieux.get(0)); }
                    );
        }
    }

    private void chargerEntrepotsVariant(Long idVariant) {
        Task<List<StockEntrepotDto>> task = new Task<>() {
            @Override protected List<StockEntrepotDto> call() throws Exception {
                return ApiClient.getInstance().get("/stock/entrepot/variant/" + idVariant,
                        new TypeReference<List<StockEntrepotDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<StockEntrepotDto> entrepots = task.getValue();
            Platform.runLater(() -> {
                lieux.clear();
                if (entrepots != null) {
                    for (StockEntrepotDto s : entrepots) {
                        String nom = s.nomEntrepot != null ? s.nomEntrepot : "Dépôt #" + s.idEntrepot;
                        lieux.add(new LieuItem(s.idEntrepot, nom, true));
                    }
                }
                if (lieux.isEmpty()) {
                    // Pas encore de stock en dépôt → proposer "Dépôt central" par défaut
                    lieux.add(new LieuItem(1L, "Dépôt central", true));
                }
                if (!lieux.isEmpty()) comboLieu.setValue(lieux.get(0));
            });
        });
        task.setOnFailed(e -> Platform.runLater(() -> {
            lieux.clear();
            lieux.add(new LieuItem(1L, "Dépôt central", true));
            comboLieu.setValue(lieux.get(0));
        }));
        new Thread(task).start();
    }

    // ── Recherche / refresh ───────────────────────────────────────

    @FXML private void handleRecherche() { chargerProduits(rechercheField.getText().trim()); }
    @FXML private void handleRefresh()   { chargerProduits(rechercheField.getText().trim()); }

    private void chargerProduits(String q) {
        spinner.setVisible(true);
        Task<PageDto<ProduitDto>> task = new Task<>() {
            @Override protected PageDto<ProduitDto> call() throws Exception {
                return ApiClient.getInstance().get("/produits?q=" + q + "&size=100",
                        new TypeReference<PageDto<ProduitDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            PageDto<ProduitDto> res = task.getValue();
            List<ProduitDto> content = res != null && res.content != null ? res.content : List.of();
            Platform.runLater(() -> {
                produits.setAll(content);
                labelInfo.setText(content.size() + " produit(s)");
                spinner.setVisible(false);
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            AlertHelper.error("Recherche", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    private void chargerVariants(Long idProduit) {
        spinner.setVisible(true);
        Task<ProduitDetailDto> task = new Task<>() {
            @Override protected ProduitDetailDto call() throws Exception {
                return ApiClient.getInstance().get("/produits/" + idProduit, ProduitDetailDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            ProduitDetailDto detail = task.getValue();
            Platform.runLater(() -> {
                spinner.setVisible(false);
                if (detail != null && detail.variants != null) {
                    // On n'affiche que les variants actifs
                    variants.setAll(detail.variants.stream()
                            .filter(v -> v.actif)
                            .toList());
                    labelInfo.setText(variants.size() + " variant(s)");
                } else {
                    variants.clear();
                    labelInfo.setText("0 variant(s)");
                }
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            AlertHelper.error("Variants", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    private void chargerStockVariant(Long idVariant) {
        spinner.setVisible(true);
        stocks.clear();

        Task<List<StockLigne>> task = new Task<>() {
            @Override protected List<StockLigne> call() throws Exception {
                List<StockLigne> lignes = new ArrayList<>();

                // Stock magasins
                try {
                    List<StockMagasinDto> magStocks = ApiClient.getInstance().get(
                            "/stock/magasin/variant/" + idVariant,
                            new TypeReference<List<StockMagasinDto>>() {});
                    if (magStocks != null) {
                        for (StockMagasinDto s : magStocks) lignes.add(StockLigne.fromMagasin(s));
                    }
                } catch (ApiException ex) {
                    if (!ex.isNotFound()) throw ex;
                }

                // Stock dépôts
                try {
                    List<StockEntrepotDto> depotStocks = ApiClient.getInstance().get(
                            "/stock/entrepot/variant/" + idVariant,
                            new TypeReference<List<StockEntrepotDto>>() {});
                    if (depotStocks != null) {
                        for (StockEntrepotDto s : depotStocks) lignes.add(StockLigne.fromEntrepot(s));
                    }
                } catch (ApiException ex) {
                    if (!ex.isNotFound()) throw ex;
                }

                return lignes;
            }
        };
        task.setOnSucceeded(e -> {
            List<StockLigne> lignes = task.getValue();
            Platform.runLater(() -> {
                stocks.setAll(lignes);
                spinner.setVisible(false);
                // Si mode dépôt actif, mettre à jour la liste des entrepôts
                if (radioDepot.isSelected()) chargerEntrepotsVariant(idVariant);
            });
        });
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            AlertHelper.error("Stock", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    // ── Mouvements de stock ───────────────────────────────────────

    @FXML
    private void handleEntree() {
        effectuerMouvement(true);
    }

    @FXML
    private void handleSortie() {
        effectuerMouvement(false);
    }

    private void effectuerMouvement(boolean estEntree) {
        if (variantSelectionne == null) {
            AlertHelper.warn("Variant", "Sélectionnez d'abord un variant dans la liste.");
            return;
        }
        LieuItem lieu = comboLieu.getValue();
        if (lieu == null) {
            AlertHelper.warn("Emplacement", "Choisissez un magasin ou un dépôt.");
            return;
        }

        // Quantité
        String qRaw = quantiteField.getText().trim();
        int quantite;
        try {
            quantite = Integer.parseInt(qRaw);
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            AlertHelper.warn("Quantité", "Entrez un nombre entier positif (ex : 5).");
            quantiteField.requestFocus();
            return;
        }

        String source     = comboSourceStock.getValue() != null ? comboSourceStock.getValue() : "NEUF";
        String commentaire = commentaireField.getText().trim();
        int    delta       = estEntree ? quantite : -quantite;

        String label = (estEntree ? "Entrée" : "Sortie") + " de " + quantite + " unité(s) ["
                + source + "] → " + lieu.nom
                + "\nVariant : " + variantSelectionne.nomCommercial;
        if (!AlertHelper.confirm("Confirmer le mouvement", label)) return;

        AjustementStockDto body = new AjustementStockDto(
                variantSelectionne.id,
                lieu.isDepot ? null : lieu.id,
                lieu.isDepot ? lieu.id : null,
                source, delta,
                commentaire.isEmpty() ? null : commentaire
        );
        String endpoint = lieu.isDepot ? "/stock/ajustement/entrepot" : "/stock/ajustement/magasin";

        spinner.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                ApiClient.getInstance().post(endpoint, body, Object.class);
                return null;
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            spinner.setVisible(false);
            AlertHelper.success("Mouvement enregistré",
                    "✅ " + (estEntree ? "Entrée" : "Sortie") + " de " + quantite + " unité(s) confirmée.");
            quantiteField.clear();
            commentaireField.clear();
            chargerStockVariant(variantSelectionne.id);
        }));
        task.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            AlertHelper.error("Erreur mouvement", msgErr(task.getException()));
        });
        new Thread(task).start();
    }

    // ── Utilitaires ──────────────────────────────────────────────

    private static String msgErr(Throwable ex) {
        return ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
    }

    // ══════════════════════════════════════════════════════════════
    //  Modèles internes
    // ══════════════════════════════════════════════════════════════

    /** Représente un magasin ou un entrepôt dans la ComboBox "Emplacement". */
    public static class LieuItem {
        public final Long    id;
        public final String  nom;
        public final boolean isDepot;

        public LieuItem(Long id, String nom, boolean isDepot) {
            this.id = id; this.nom = nom; this.isDepot = isDepot;
        }

        @Override public String toString() { return nom; }
    }

    /** Vue aplatie commune aux réponses magasin et entrepôt. */
    public static class StockLigne {
        public Long   variantId;
        public Long   locationId;
        public String lieu;
        public int    qteNeuf;
        public int    qteOccaz;    // -1 si non applicable (dépôt)
        public int    qteDispo;
        public boolean isDepot;

        static StockLigne fromMagasin(StockMagasinDto s) {
            StockLigne l = new StockLigne();
            l.variantId  = s.idVariant;
            l.locationId = s.idMagasin;
            l.lieu       = s.nomMagasin != null ? s.nomMagasin : "Magasin #" + s.idMagasin;
            l.qteNeuf    = s.quantiteNeuf;
            l.qteOccaz   = s.quantiteOccasion;
            l.qteDispo   = s.quantiteDisponible;
            l.isDepot    = false;
            return l;
        }

        static StockLigne fromEntrepot(StockEntrepotDto s) {
            StockLigne l = new StockLigne();
            l.variantId  = s.idVariant;
            l.locationId = s.idEntrepot;
            l.lieu       = s.nomEntrepot != null ? s.nomEntrepot : "Dépôt #" + s.idEntrepot;
            l.qteNeuf    = s.quantiteNeuf;
            l.qteOccaz   = -1;
            l.qteDispo   = s.quantiteDisponible;
            l.isDepot    = true;
            return l;
        }
    }
}
