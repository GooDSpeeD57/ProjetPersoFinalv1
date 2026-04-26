package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.commande.CommandeDetailDto;
import com.monprojet.boutiquejeux.dto.commande.CommandeSummaryDto;
import com.monprojet.boutiquejeux.dto.commande.LigneCommandeDto;
import com.monprojet.boutiquejeux.dto.commande.UpdateStatutCommandeDto;
import com.monprojet.boutiquejeux.dto.depot.EntrepotDto;
import com.monprojet.boutiquejeux.dto.depot.TransfertStockDto;
import com.monprojet.boutiquejeux.dto.magasin.MagasinAdminDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDetailDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitDto;
import com.monprojet.boutiquejeux.dto.produit.ProduitVariantDto;
import com.monprojet.boutiquejeux.dto.stock.StockEntrepotDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class DepotController {

    // ══ ONGLET 1 — STOCK DÉPÔT ══════════════════════════════════
    @FXML private ComboBox<EntrepotDto>     depotEntrepotFilterCb;
    @FXML private TextField                 depotRechercheField;
    @FXML private TableView<ProduitDto>     depotTableProduits;
    @FXML private TableColumn<ProduitDto, String> depotColPNom;
    @FXML private TableColumn<ProduitDto, String> depotColPCat;
    @FXML private TableView<ProduitVariantDto>  depotTableVariants;
    @FXML private TableColumn<ProduitVariantDto, String> depotColVSku;
    @FXML private TableColumn<ProduitVariantDto, String> depotColVPlatf;
    @FXML private TableColumn<ProduitVariantDto, String> depotColVStatut;
    @FXML private Label                         depotLabelVariant;
    @FXML private TableView<StockEntrepotDto>   depotTableStock;
    @FXML private TableColumn<StockEntrepotDto, String>  depotColLieu;
    @FXML private TableColumn<StockEntrepotDto, Integer> depotColNeuf;
    @FXML private TableColumn<StockEntrepotDto, Integer> depotColReservee;
    @FXML private TableColumn<StockEntrepotDto, Integer> depotColDispo;
    @FXML private ComboBox<EntrepotDto>     depotAjustEntrepotCb;
    @FXML private TextField                 depotAjustQteField;
    @FXML private TextField                 depotAjustCommentField;
    @FXML private ProgressIndicator         spinnerDepot;

    // ══ ONGLET 2 — COMMANDES ════════════════════════════════════
    @FXML private ComboBox<String>                      cmdStatutCb;
    @FXML private TableView<CommandeSummaryDto>         tableCommandes;
    @FXML private TableColumn<CommandeSummaryDto, String>     colCmdRef;
    @FXML private TableColumn<CommandeSummaryDto, String>     colCmdStatut;
    @FXML private TableColumn<CommandeSummaryDto, String>     colCmdLivraison;
    @FXML private TableColumn<CommandeSummaryDto, BigDecimal> colCmdMontant;
    @FXML private TableColumn<CommandeSummaryDto, Integer>    colCmdArticles;
    @FXML private TableColumn<CommandeSummaryDto, String>     colCmdDate;
    @FXML private ProgressIndicator                           spinnerCmd;
    @FXML private Label                     labelCmdTitre;
    @FXML private Label                     lblCmdRef;
    @FXML private Label                     lblCmdStatut;
    @FXML private Label                     lblCmdLivraison;
    @FXML private Label                     lblCmdPaiement;
    @FXML private Label                     lblCmdTotal;
    @FXML private Label                     lblCmdDate;
    @FXML private Label                     lblCmdCommentaire;
    @FXML private TableView<LigneCommandeDto>   tableCmdLignes;
    @FXML private TableColumn<LigneCommandeDto, String>     colLigneNom;
    @FXML private TableColumn<LigneCommandeDto, String>     colLigneSku;
    @FXML private TableColumn<LigneCommandeDto, Integer>    colLigneQte;
    @FXML private TableColumn<LigneCommandeDto, BigDecimal> colLignePrix;
    @FXML private TableColumn<LigneCommandeDto, BigDecimal> colLigneMontant;
    @FXML private Button btnPrendreEnCharge;
    @FXML private Button btnMarquerExpediee;
    @FXML private Button btnMarquerLivree;
    @FXML private Button btnMarquerRetirable;
    @FXML private Button btnAnnulerCommande;

    // ══ ONGLET 3 — EXPÉDITIONS ══════════════════════════════════
    @FXML private TableView<CommandeSummaryDto>          tableExpedDomicile;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpDRef;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpDStatut;
    @FXML private TableColumn<CommandeSummaryDto, BigDecimal> colExpDTotal;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpDDate;
    @FXML private TableView<CommandeSummaryDto>          tableExpedCollect;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpCRef;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpCStatut;
    @FXML private TableColumn<CommandeSummaryDto, BigDecimal> colExpCTotal;
    @FXML private TableColumn<CommandeSummaryDto, String>     colExpCDate;
    @FXML private ProgressIndicator spinnerExped;
    @FXML private Label             labelExpedTitre;
    @FXML private Label             lblExpedRef;
    @FXML private Label             lblExpedMode;
    @FXML private Label             lblExpedDate;
    @FXML private Label             lblExpedNbArt;
    @FXML private Label             lblExpedMontant;
    @FXML private TableView<LigneCommandeDto>   tableExpedLignes;
    @FXML private TableColumn<LigneCommandeDto, String>  colExpLigneNom;
    @FXML private TableColumn<LigneCommandeDto, String>  colExpLigneSku;
    @FXML private TableColumn<LigneCommandeDto, Integer> colExpLigneQte;
    @FXML private Button btnExpedExpedier;
    @FXML private Button btnExpedLivrer;

    // ══ ONGLET 4 — TRANSFERTS ═══════════════════════════════════
    @FXML private ComboBox<EntrepotDto>    transfertEntrepotCb;
    @FXML private ComboBox<MagasinAdminDto> transfertMagasinCb;
    @FXML private TextField                transfertRechercheVariant;
    @FXML private ComboBox<ProduitVariantDto> transfertVariantCb;
    @FXML private TextField                transfertQteField;
    @FXML private TextField                transfertCommentField;
    @FXML private Label                    labelStockDispoDepot;
    @FXML private TableView<Map<String, Object>> tableTransferts;
    @FXML private TableColumn<Map<String, Object>, String>  colTrfProduit;
    @FXML private TableColumn<Map<String, Object>, String>  colTrfLieu;
    @FXML private TableColumn<Map<String, Object>, String>  colTrfQte;
    @FXML private TableColumn<Map<String, Object>, String>  colTrfCommentaire;
    @FXML private TableColumn<Map<String, Object>, String>  colTrfDate;
    @FXML private ProgressIndicator spinnerTransfert;

    // ── État interne ─────────────────────────────────────────────
    private final ApiClient api = ApiClient.getInstance();
    private CommandeSummaryDto       commandeSelectionnee;
    private CommandeSummaryDto       expedSelectionnee;
    private ProduitVariantDto        variantDepotSelectionne;
    private List<EntrepotDto>        entrepots;
    private List<MagasinAdminDto>    magasins;

    // ─────────────────────────────────────────────────────────────
    // INIT
    // ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurerColonnesDepot();
        configurerColonnesCommandes();
        configurerColonnesExpeditions();
        configurerColonnesTransferts();

        cmdStatutCb.setItems(FXCollections.observableArrayList(
                "PAYEE", "PREPARATION", "EXPEDIEE", "LIVREE",
                "RETIRABLE", "RETIREE", "ANNULEE"));

        // Sélections
        depotTableProduits.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, p) -> { if (p != null) chargerVariantsDepot(p.id); });
        depotTableVariants.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, v) -> { if (v != null) chargerStockDepot(v); });
        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, c) -> { if (c != null) chargerDetailCommande(c); });
        tableExpedDomicile.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, c) -> { if (c != null) chargerDetailExpedition(c); });
        tableExpedCollect.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, c) -> { if (c != null) chargerDetailExpedition(c); });
        transfertVariantCb.valueProperty().addListener(
                (obs, old, v) -> afficherStockDispoDepot(v));
        transfertEntrepotCb.valueProperty().addListener(
                (obs, old, e) -> afficherStockDispoDepot(transfertVariantCb.getValue()));

        chargerReferentiels();
        chargerCommandes(null);
        chargerExpeditions();
        chargerHistoriqueTransferts();
    }

    // ─────────────────────────────────────────────────────────────
    // CONFIGURATION COLONNES
    // ─────────────────────────────────────────────────────────────

    private void configurerColonnesDepot() {
        depotColPNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        depotColPCat.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().categorie != null ? c.getValue().categorie : ""));
        depotColVSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        depotColVPlatf.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlateformeCode()));
        depotColVStatut.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().statutProduit != null ? c.getValue().statutProduit : ""));
        depotColLieu.setCellValueFactory(new PropertyValueFactory<>("nomEntrepot"));
        depotColNeuf.setCellValueFactory(new PropertyValueFactory<>("quantiteNeuf"));
        depotColReservee.setCellValueFactory(new PropertyValueFactory<>("quantiteReservee"));
        depotColDispo.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
    }

    private void configurerColonnesCommandes() {
        colCmdRef.setCellValueFactory(new PropertyValueFactory<>("referenceCommande"));
        colCmdStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colCmdLivraison.setCellValueFactory(new PropertyValueFactory<>("modeLivraison"));
        colCmdMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colCmdArticles.setCellValueFactory(new PropertyValueFactory<>("nbArticles"));
        colCmdDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colorierLignesStatut(tableCommandes);

        colLigneNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colLigneSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colLigneQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colLignePrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colLigneMontant.setCellValueFactory(new PropertyValueFactory<>("montantLigne"));
    }

    private void configurerColonnesExpeditions() {
        colExpDRef.setCellValueFactory(new PropertyValueFactory<>("referenceCommande"));
        colExpDStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colExpDTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colExpDDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colorierLignesStatut(tableExpedDomicile);

        colExpCRef.setCellValueFactory(new PropertyValueFactory<>("referenceCommande"));
        colExpCStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colExpCTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colExpCDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colorierLignesStatut(tableExpedCollect);

        colExpLigneNom.setCellValueFactory(new PropertyValueFactory<>("nomCommercial"));
        colExpLigneSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colExpLigneQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
    }

    private void configurerColonnesTransferts() {
        colTrfProduit.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "nomCommercial")
                        + " (" + str(c.getValue(), "sku") + ")"));
        colTrfLieu.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "lieu")));
        colTrfQte.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "quantite")));
        colTrfCommentaire.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "commentaire")));
        colTrfDate.setCellValueFactory(c ->
                new SimpleStringProperty(str(c.getValue(), "dateMouvement")));
    }

    @SuppressWarnings("unchecked")
    private <T> void colorierLignesStatut(TableView<T> table) {
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    String statut = "";
                    if (item instanceof CommandeSummaryDto c) statut = c.statut != null ? c.statut : "";
                    setStyle(switch (statut) {
                        case "PAYEE" -> "-fx-background-color:#1a2a1a;";
                        case "PREPARATION" -> "-fx-background-color:#2a2a0a;";
                        case "EXPEDIEE"    -> "-fx-background-color:#0a1a2a;";
                        case "LIVREE", "RETIREE" -> "-fx-background-color:#0a1a0a;";
                        case "ANNULEE"     -> "-fx-background-color:#2a0a0a;";
                        default            -> "";
                    });
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // CHARGEMENTS RÉFÉRENTIELS
    // ─────────────────────────────────────────────────────────────

    private void chargerReferentiels() {
        new Thread(() -> {
            try {
                entrepots = api.get("/entrepots", new TypeReference<>() {});
                // /magasins/admin requiert MANAGER ou ADMIN — ne pas appeler pour les VENDEUR
                if (SessionManager.getInstance().isManager()) {
                    magasins = api.get("/magasins/admin", new TypeReference<>() {});
                }
                final List<EntrepotDto>     depots = entrepots != null ? entrepots : List.of();
                final List<MagasinAdminDto> mags   = magasins  != null ? magasins  : List.of();
                Platform.runLater(() -> {
                    depotEntrepotFilterCb.setItems(FXCollections.observableArrayList(depots));
                    depotAjustEntrepotCb.setItems(FXCollections.observableArrayList(depots));
                    transfertEntrepotCb.setItems(FXCollections.observableArrayList(depots));
                    transfertMagasinCb.setItems(FXCollections.observableArrayList(
                            mags.stream().filter(m -> m.actif).toList()));
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Référentiels", e.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ONGLET 1 — STOCK DÉPÔT
    // ─────────────────────────────────────────────────────────────

    @FXML void handleDepotRecherche() {
        chargerProduitsDepot(depotRechercheField.getText());
    }

    @FXML void handleDepotRefresh() {
        depotRechercheField.clear();
        chargerProduitsDepot(null);
    }

    private void chargerProduitsDepot(String q) {
        spinnerDepot.setVisible(true);
        new Thread(() -> {
            try {
                String path = (q != null && !q.isBlank())
                        ? "/produits?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&size=100"
                        : "/produits?size=100";
                PageDto<ProduitDto> page = api.get(path, new TypeReference<>() {});
                List<ProduitDto> liste = page != null && page.content != null ? page.content : List.of();
                Platform.runLater(() -> {
                    depotTableProduits.setItems(FXCollections.observableArrayList(liste));
                    spinnerDepot.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { spinnerDepot.setVisible(false); AlertHelper.error("Stock", e.getMessage()); });
            }
        }).start();
    }

    private void chargerVariantsDepot(Long idProduit) {
        new Thread(() -> {
            try {
                ProduitDetailDto detail = api.get("/produits/" + idProduit, ProduitDetailDto.class);
                List<ProduitVariantDto> variants = detail.variants != null ? detail.variants : List.of();
                Platform.runLater(() -> depotTableVariants.setItems(FXCollections.observableArrayList(variants)));
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Variants", e.getMessage()));
            }
        }).start();
    }

    private void chargerStockDepot(ProduitVariantDto variant) {
        variantDepotSelectionne = variant;
        depotLabelVariant.setText("Stock : " + variant.nomCommercial + " (" + variant.sku + ")");
        new Thread(() -> {
            try {
                List<StockEntrepotDto> stocks = api.get(
                        "/stock/entrepot/variant/" + variant.id, new TypeReference<>() {});
                Platform.runLater(() -> depotTableStock.setItems(FXCollections.observableArrayList(stocks)));
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Stock dépôt", e.getMessage()));
            }
        }).start();
    }

    @FXML void handleDepotEntree() { ajusterStockDepot(true); }
    @FXML void handleDepotSortie() { ajusterStockDepot(false); }

    private void ajusterStockDepot(boolean entree) {
        if (variantDepotSelectionne == null) { AlertHelper.warn("Stock", "Sélectionnez un variant."); return; }
        EntrepotDto depot = depotAjustEntrepotCb.getValue();
        if (depot == null) { AlertHelper.warn("Stock", "Sélectionnez un dépôt."); return; }
        String qteStr = depotAjustQteField.getText().trim();
        int qte;
        try { qte = Integer.parseInt(qteStr); if (qte <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { AlertHelper.warn("Stock", "Quantité invalide."); return; }

        int delta = entree ? qte : -qte;
        Map<String, Object> body = Map.of(
                "idVariant",   variantDepotSelectionne.id,
                "idEntrepot",  depot.id,
                "sourceStock", "NEUF",
                "delta",       delta,
                "commentaire", depotAjustCommentField.getText().trim()
        );
        new Thread(() -> {
            try {
                api.post("/stock/ajustement/entrepot", body, Object.class);
                Platform.runLater(() -> {
                    AlertHelper.success("Stock", (entree ? "Entrée" : "Sortie") + " enregistrée.");
                    depotAjustQteField.clear();
                    depotAjustCommentField.clear();
                    chargerStockDepot(variantDepotSelectionne);
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ONGLET 2 — COMMANDES
    // ─────────────────────────────────────────────────────────────

    @FXML void handleCmdFilter()  { chargerCommandes(cmdStatutCb.getValue()); }
    @FXML void handleCmdRefresh() { cmdStatutCb.setValue(null); chargerCommandes(null); }

    private void chargerCommandes(String statut) {
        spinnerCmd.setVisible(true);
        new Thread(() -> {
            try {
                String path = "/commandes?size=100"
                        + (statut != null ? "&statut=" + statut : "");
                PageDto<CommandeSummaryDto> page = api.get(path, new TypeReference<>() {});
                // page.content() renvoie maintenant le vrai contenu (bug PageDto corrigé)
                List<CommandeSummaryDto> liste = page != null ? page.content() : List.of();
                Platform.runLater(() -> {
                    tableCommandes.setItems(FXCollections.observableArrayList(liste));
                    spinnerCmd.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { spinnerCmd.setVisible(false); AlertHelper.error("Commandes", e.getMessage()); });
            }
        }).start();
    }

    private void chargerDetailCommande(CommandeSummaryDto cmd) {
        commandeSelectionnee = cmd;
        labelCmdTitre.setText("Commande " + cmd.referenceCommande);
        new Thread(() -> {
            try {
                CommandeDetailDto detail = api.get("/commandes/" + cmd.id, CommandeDetailDto.class);
                Platform.runLater(() -> {
                    lblCmdRef.setText(detail.referenceCommande != null ? detail.referenceCommande : "");
                    lblCmdStatut.setText(detail.statut != null ? detail.statut : "");
                    lblCmdLivraison.setText(detail.modeLivraison != null ? detail.modeLivraison : "");
                    lblCmdPaiement.setText(detail.modePaiement != null ? detail.modePaiement : "");
                    lblCmdTotal.setText(detail.montantTotal != null ? detail.montantTotal + " €" : "");
                    lblCmdDate.setText(detail.dateCommande != null ? detail.dateCommande.replace("T", " ") : "");
                    lblCmdCommentaire.setText(detail.commentaireClient != null ? detail.commentaireClient : "—");
                    if (detail.lignes != null)
                        tableCmdLignes.setItems(FXCollections.observableArrayList(detail.lignes));
                    actualiserBoutonsCmde(detail.statut);
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Commande", e.getMessage()));
            }
        }).start();
    }

    private void actualiserBoutonsCmde(String statut) {
        if (statut == null) statut = "";
        btnPrendreEnCharge.setDisable(!"PAYEE".equals(statut));
        btnMarquerExpediee.setDisable(!"PREPARATION".equals(statut));
        btnMarquerLivree.setDisable(!"EXPEDIEE".equals(statut));
        btnMarquerRetirable.setDisable(!("PAYEE".equals(statut) || "PREPARATION".equals(statut)));
        btnAnnulerCommande.setDisable("ANNULEE".equals(statut) || "LIVREE".equals(statut) || "RETIREE".equals(statut));
    }

    @FXML void handlePrendreEnCharge() { changerStatut("PREPARATION", "Commande prise en charge."); }
    @FXML void handleMarquerExpediee() { changerStatut("EXPEDIEE",    "Commande marquée expédiée."); }
    @FXML void handleMarquerLivree()   { changerStatut("LIVREE",      "Commande marquée livrée."); }
    @FXML void handleMarquerRetirable(){ changerStatut("RETIRABLE",   "Commande prête au retrait."); }

    @FXML
    void handleAnnulerCommande() {
        if (commandeSelectionnee == null) return;
        if (!AlertHelper.confirm("Annuler", "Annuler la commande " + commandeSelectionnee.referenceCommande + " ?"))
            return;
        new Thread(() -> {
            try {
                api.delete("/commandes/" + commandeSelectionnee.id + "?motif=Annulée+depuis+le+dépôt");
                Platform.runLater(() -> {
                    AlertHelper.success("Commande", "Commande annulée.");
                    chargerCommandes(cmdStatutCb.getValue());
                    chargerExpeditions();
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    private void changerStatut(String codeStatut, String successMsg) {
        if (commandeSelectionnee == null) { AlertHelper.warn("Action", "Sélectionnez une commande."); return; }
        UpdateStatutCommandeDto body = new UpdateStatutCommandeDto(codeStatut, null);
        new Thread(() -> {
            try {
                api.patch("/commandes/" + commandeSelectionnee.id + "/statut", body, CommandeDetailDto.class);
                Platform.runLater(() -> {
                    AlertHelper.success("Commande", successMsg);
                    chargerCommandes(cmdStatutCb.getValue());
                    chargerExpeditions();
                    labelCmdTitre.setText("← Sélectionnez une commande");
                    tableCmdLignes.getItems().clear();
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ONGLET 3 — EXPÉDITIONS
    // ─────────────────────────────────────────────────────────────

    @FXML void handleExpedRefresh() { chargerExpeditions(); }

    private void chargerExpeditions() {
        spinnerExped.setVisible(true);
        new Thread(() -> {
            try {
                // Livraison domicile : commandes en PREPARATION ou PAYEE avec livraison NON click-and-collect
                PageDto<CommandeSummaryDto> pagePrepaDom = api.get(
                        "/commandes?size=200&statut=PREPARATION", new TypeReference<>() {});
                PageDto<CommandeSummaryDto> pageExpeDom = api.get(
                        "/commandes?size=200&statut=EXPEDIEE", new TypeReference<>() {});
                PageDto<CommandeSummaryDto> pageRetirable = api.get(
                        "/commandes?size=200&statut=RETIRABLE", new TypeReference<>() {});

                List<CommandeSummaryDto> domicile = mergeListes(pagePrepaDom, pageExpeDom)
                        .stream().filter(c -> !"RETRAIT_MAGASIN".equals(c.modeLivraison)).toList();
                List<CommandeSummaryDto> collect = getContent(pageRetirable)
                        .stream().filter(c -> "RETRAIT_MAGASIN".equals(c.modeLivraison)
                                || c.modeLivraison == null
                                || c.modeLivraison.contains("RETRAIT")).toList();

                // Ajouter aussi les PAYEE avec livraison domicile
                PageDto<CommandeSummaryDto> pagePayee = api.get(
                        "/commandes?size=200&statut=PAYEE", new TypeReference<>() {});
                List<CommandeSummaryDto> domicileFull = new java.util.ArrayList<>(domicile);
                domicileFull.addAll(getContent(pagePayee).stream()
                        .filter(c -> !"RETRAIT_MAGASIN".equals(c.modeLivraison)).toList());

                Platform.runLater(() -> {
                    tableExpedDomicile.setItems(FXCollections.observableArrayList(domicileFull));
                    tableExpedCollect.setItems(FXCollections.observableArrayList(collect));
                    spinnerExped.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { spinnerExped.setVisible(false); AlertHelper.error("Expéditions", e.getMessage()); });
            }
        }).start();
    }

    private void chargerDetailExpedition(CommandeSummaryDto cmd) {
        expedSelectionnee = cmd;
        labelExpedTitre.setText("Expédition " + cmd.referenceCommande);
        lblExpedRef.setText(cmd.referenceCommande != null ? cmd.referenceCommande : "");
        lblExpedMode.setText(cmd.modeLivraison != null ? cmd.modeLivraison : "");
        lblExpedDate.setText(cmd.dateCommande != null ? cmd.dateCommande.replace("T", " ") : "");
        lblExpedNbArt.setText(String.valueOf(cmd.nbArticles));
        lblExpedMontant.setText(cmd.montantTotal != null ? cmd.montantTotal + " €" : "");

        // Charger les lignes
        new Thread(() -> {
            try {
                CommandeDetailDto detail = api.get("/commandes/" + cmd.id, CommandeDetailDto.class);
                Platform.runLater(() -> {
                    if (detail.lignes != null)
                        tableExpedLignes.setItems(FXCollections.observableArrayList(detail.lignes));
                    boolean estPrepou = "PREPARATION".equals(cmd.statut) || "PAYEE".equals(cmd.statut);
                    boolean estExped  = "EXPEDIEE".equals(cmd.statut);
                    boolean estRetirable = "RETIRABLE".equals(cmd.statut);
                    btnExpedExpedier.setDisable(!estPrepou);
                    btnExpedLivrer.setDisable(!estExped && !estRetirable);
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Expédition", e.getMessage()));
            }
        }).start();
    }

    @FXML
    void handleExpedConfirmer() {
        if (expedSelectionnee == null) { AlertHelper.warn("Expédition", "Sélectionnez une commande."); return; }
        UpdateStatutCommandeDto body = new UpdateStatutCommandeDto("EXPEDIEE", "Expédiée depuis le dépôt");
        new Thread(() -> {
            try {
                api.patch("/commandes/" + expedSelectionnee.id + "/statut", body, CommandeDetailDto.class);
                Platform.runLater(() -> {
                    AlertHelper.success("Expédition", "Commande marquée comme expédiée.");
                    chargerExpeditions();
                    labelExpedTitre.setText("← Sélectionnez une commande");
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    @FXML
    void handleExpedLivrer() {
        if (expedSelectionnee == null) { AlertHelper.warn("Livraison", "Sélectionnez une commande."); return; }
        boolean estRetrait = "RETIRABLE".equals(expedSelectionnee.statut);
        String codeStatut  = estRetrait ? "RETIREE" : "LIVREE";
        String msg         = estRetrait ? "Commande retirée en magasin." : "Commande marquée livrée.";
        UpdateStatutCommandeDto body = new UpdateStatutCommandeDto(codeStatut, null);
        new Thread(() -> {
            try {
                api.patch("/commandes/" + expedSelectionnee.id + "/statut", body, CommandeDetailDto.class);
                Platform.runLater(() -> {
                    AlertHelper.success("Livraison", msg);
                    chargerExpeditions();
                    labelExpedTitre.setText("← Sélectionnez une commande");
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> AlertHelper.error("Erreur", ex.getMessage()));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // ONGLET 4 — TRANSFERTS
    // ─────────────────────────────────────────────────────────────

    @FXML
    void handleTransfertRechercheVariant() {
        String q = transfertRechercheVariant.getText().trim();
        if (q.isBlank()) return;
        new Thread(() -> {
            try {
                PageDto<ProduitDto> page = api.get(
                        "/produits?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&size=50",
                        new TypeReference<>() {});
                if (page == null || page.content == null || page.content.isEmpty()) {
                    Platform.runLater(() -> AlertHelper.warn("Recherche", "Aucun produit trouvé."));
                    return;
                }
                // Charger les variants de tous les produits trouvés
                List<ProduitVariantDto> allVariants = new java.util.ArrayList<>();
                for (ProduitDto p : page.content) {
                    try {
                        ProduitDetailDto d = api.get("/produits/" + p.id, ProduitDetailDto.class);
                        if (d.variants != null) allVariants.addAll(d.variants);
                    } catch (Exception ignored) {}
                }
                Platform.runLater(() -> {
                    transfertVariantCb.setItems(FXCollections.observableArrayList(allVariants));
                    if (!allVariants.isEmpty()) transfertVariantCb.show();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.error("Recherche", e.getMessage()));
            }
        }).start();
    }

    private void afficherStockDispoDepot(ProduitVariantDto variant) {
        EntrepotDto depot = transfertEntrepotCb.getValue();
        if (variant == null || depot == null) {
            labelStockDispoDepot.setText("");
            return;
        }
        new Thread(() -> {
            try {
                List<StockEntrepotDto> stocks = api.get(
                        "/stock/entrepot/variant/" + variant.id, new TypeReference<>() {});
                int dispo = stocks.stream()
                        .filter(s -> depot.id.equals(s.idEntrepot))
                        .mapToInt(s -> s.quantiteDisponible)
                        .sum();
                Platform.runLater(() ->
                        labelStockDispoDepot.setText("Stock disponible dans ce dépôt : " + dispo + " unité(s)"));
            } catch (Exception ignored) {
                Platform.runLater(() -> labelStockDispoDepot.setText(""));
            }
        }).start();
    }

    @FXML
    void handleEffectuerTransfert() {
        EntrepotDto depot = transfertEntrepotCb.getValue();
        MagasinAdminDto mag = transfertMagasinCb.getValue();
        ProduitVariantDto variant = transfertVariantCb.getValue();
        String qteStr = transfertQteField.getText().trim();

        if (depot == null) { AlertHelper.warn("Transfert", "Sélectionnez un dépôt source."); return; }
        if (mag   == null) { AlertHelper.warn("Transfert", "Sélectionnez un magasin destination."); return; }
        if (variant == null) { AlertHelper.warn("Transfert", "Sélectionnez un variant."); return; }
        int qte;
        try { qte = Integer.parseInt(qteStr); if (qte <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { AlertHelper.warn("Transfert", "Quantité invalide."); return; }

        if (!AlertHelper.confirm("Transfert",
                "Transférer " + qte + " × " + variant.sku
                + "\nde « " + depot.nom + " » vers « " + mag.nom + " » ?")) return;

        TransfertStockDto body = new TransfertStockDto();
        body.idVariant             = variant.id;
        body.idEntrepotSource      = depot.id;
        body.idMagasinDestination  = mag.id;
        body.quantite              = qte;
        body.commentaire           = transfertCommentField.getText().trim();

        spinnerTransfert.setVisible(true);
        new Thread(() -> {
            try {
                api.post("/stock/transfert", body, Object.class);
                Platform.runLater(() -> {
                    spinnerTransfert.setVisible(false);
                    AlertHelper.success("Transfert", "Transfert effectué avec succès !");
                    handleResetTransfert();
                    chargerHistoriqueTransferts();
                    afficherStockDispoDepot(variant);
                });
            } catch (ApiException ex) {
                Platform.runLater(() -> {
                    spinnerTransfert.setVisible(false);
                    AlertHelper.error("Erreur transfert", ex.getMessage());
                });
            }
        }).start();
    }

    @FXML
    void handleResetTransfert() {
        transfertEntrepotCb.setValue(null);
        transfertMagasinCb.setValue(null);
        transfertRechercheVariant.clear();
        transfertVariantCb.getItems().clear();
        transfertVariantCb.setValue(null);
        transfertQteField.clear();
        transfertCommentField.clear();
        labelStockDispoDepot.setText("");
    }

    @FXML void handleTransfertHistRefresh() { chargerHistoriqueTransferts(); }

    @SuppressWarnings("unchecked")
    private void chargerHistoriqueTransferts() {
        spinnerTransfert.setVisible(true);
        new Thread(() -> {
            try {
                // Mouvements de type TRANSFERT récents
                List<Map<String, Object>> mouvements = api.get(
                        "/stock/mouvements?size=100", new TypeReference<>() {});
                // Filtrer uniquement les TRANSFERT
                List<Map<String, Object>> transferts = mouvements.stream()
                        .filter(m -> "TRANSFERT".equals(m.get("typeMouvement")))
                        .toList();
                Platform.runLater(() -> {
                    tableTransferts.setItems(FXCollections.observableArrayList(transferts));
                    spinnerTransfert.setVisible(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    spinnerTransfert.setVisible(false);
                    // Silencieux si l'endpoint renvoie une Page plutôt qu'une liste
                });
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────
    // UTILS
    // ─────────────────────────────────────────────────────────────

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }

    private List<CommandeSummaryDto> getContent(PageDto<CommandeSummaryDto> page) {
        return page != null && page.content != null ? page.content : List.of();
    }

    @SafeVarargs
    private List<CommandeSummaryDto> mergeListes(PageDto<CommandeSummaryDto>... pages) {
        List<CommandeSummaryDto> result = new java.util.ArrayList<>();
        for (PageDto<CommandeSummaryDto> p : pages) result.addAll(getContent(p));
        return result;
    }
}
