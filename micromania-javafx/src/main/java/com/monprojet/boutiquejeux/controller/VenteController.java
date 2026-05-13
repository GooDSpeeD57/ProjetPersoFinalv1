package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.client.BonAchatDto;
import com.monprojet.boutiquejeux.dto.client.ClientDetailDto;
import com.monprojet.boutiquejeux.dto.produit.CataloguePosSummaryDto;
import com.monprojet.boutiquejeux.dto.referentiel.ContexteVenteDto;
import com.monprojet.boutiquejeux.dto.referentiel.ModePaiementDto;
import com.monprojet.boutiquejeux.dto.referentiel.TypeGarantieDto;
import com.monprojet.boutiquejeux.dto.vente.CreateVenteDto;
import com.monprojet.boutiquejeux.dto.vente.FactureDto;
import com.monprojet.boutiquejeux.dto.vente.LigneVenteDto;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.model.LignePanier;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VenteController {

    // ── Identification client ────────────────────────────────────
    @FXML private TextField              nomClientField;
    @FXML private TextField              prenomClientField;
    @FXML private DatePicker             dateNaissanceField;
    @FXML private VBox                   carteClient;
    @FXML private Label                  labelClientNom;
    @FXML private Label                  labelClientEmail;
    @FXML private Label                  labelClientPoints;
    @FXML private Label                  labelClientFidelite;
    @FXML private VBox                   zoneBons;
    @FXML private ListView<BonAchatDto>  listeBonsAchat;

    // ── Catalogue produits ───────────────────────────────────────
    @FXML private TextField                                    rechercheProduitField;
    @FXML private ToggleGroup                                  typeVenteGroup;
    @FXML private RadioButton                                  radioNeuf;
    @FXML private RadioButton                                  radioOccasion;
    @FXML private RadioButton                                  radioLocation;
    @FXML private TableView<CataloguePosSummaryDto>            tableProduits;
    @FXML private TableColumn<CataloguePosSummaryDto, String>  colNom;
    @FXML private TableColumn<CataloguePosSummaryDto, String>  colPlateforme;
    @FXML private TableColumn<CataloguePosSummaryDto, String>  colEdition;
    @FXML private TableColumn<CataloguePosSummaryDto, String>  colStock;
    @FXML private TableColumn<CataloguePosSummaryDto, String>  colPrix;

    // ── Panier ───────────────────────────────────────────────────
    @FXML private TableView<LignePanier>                 tablePanier;
    @FXML private TableColumn<LignePanier, String>       colPanierNom;
    @FXML private TableColumn<LignePanier, String>       colPanierPlateforme;
    @FXML private TableColumn<LignePanier, String>       colPanierType;
    @FXML private TableColumn<LignePanier, Integer>      colPanierQte;
    @FXML private TableColumn<LignePanier, String>       colPanierPrix;
    @FXML private TableColumn<LignePanier, Boolean>      colPanierGarantie;
    @FXML private Label                                  labelTotalProduits;
    @FXML private Label                                  labelTotalGaranties;
    @FXML private Label                                  labelTotalBons;
    @FXML private Label                                  labelTotal;

    // ── Paiement ─────────────────────────────────────────────────
    @FXML private ComboBox<ModePaiementDto> comboPaiement;
    @FXML private Button                    btnValiderVente;
    @FXML private ProgressIndicator         spinner;

    // ── État ─────────────────────────────────────────────────────
    private Long   clientId     = null;
    private String clientNom    = "";
    private Long   idContexteVente = null;

    /** Checkboxes des bons d'achat : bon → propriété cochée/décochée */
    private final Map<BonAchatDto, BooleanProperty> bonsChecked = new LinkedHashMap<>();

    private final ObservableList<CataloguePosSummaryDto> produits = FXCollections.observableArrayList();
    private final ObservableList<LignePanier>            panier   = FXCollections.observableArrayList();

    // ── Initialisation ───────────────────────────────────────────

    @FXML
    public void initialize() {
        spinner.setVisible(false);

        // ── Colonnes catalogue ───────────────────────────────────
        colNom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().nom != null ? c.getValue().nom : ""));
        colPlateforme.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().plateforme != null ? c.getValue().plateforme : ""));
        colEdition.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().edition != null ? c.getValue().edition : ""));
        colStock.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDispoTexte()));
        colPrix.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPrixAffiche()));
        tableProduits.setItems(produits);

        // Colorier en rouge si stock = 0
        tableProduits.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(CataloguePosSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getStockEffectif() <= 0) {
                    setStyle("-fx-text-fill: #ff6b6b; -fx-opacity: 0.7;");
                } else {
                    setStyle("");
                }
            }
        });

        typeVenteGroup.selectedToggleProperty().addListener((obs, o, n) ->
                chargerProduits(rechercheProduitField.getText().trim()));

        // ── Colonnes panier ──────────────────────────────────────
        colPanierNom.setCellValueFactory(c -> c.getValue().nomProduitProperty());
        colPanierPlateforme.setCellValueFactory(c -> c.getValue().plateformeProperty());
        colPanierType.setCellValueFactory(c -> c.getValue().typeProperty());
        colPanierQte.setCellValueFactory(c -> c.getValue().quantiteProperty().asObject());
        colPanierPrix.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPrixAffiche()));

        colPanierGarantie.setCellValueFactory(c -> c.getValue().extensionGarantieProperty());
        colPanierGarantie.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                LignePanier l = getTableRow().getItem();
                if (l.getGarantieLabel() != null && !l.getGarantieLabel().isBlank()) {
                    double totalGarantie = l.getGarantiePrix() * l.getQuantite();
                    String prixStr = totalGarantie > 0
                            ? (l.getQuantite() > 1
                                ? String.format(" +%.2f€ (%.2f€×%d)", totalGarantie, l.getGarantiePrix(), l.getQuantite())
                                : String.format(" +%.2f€", totalGarantie))
                            : "";
                    setText("🛡 " + l.getGarantieLabel() + prixStr);
                } else {
                    setText("—");
                }
            }
        });

        tablePanier.setEditable(true);
        tablePanier.setItems(panier);

        // ── ListView bons d'achat — CheckBoxListCell ─────────────
        listeBonsAchat.setCellFactory(CheckBoxListCell.forListView(
                bon -> bonsChecked.getOrDefault(bon, new SimpleBooleanProperty(false)),
                new StringConverter<>() {
                    @Override public String toString(BonAchatDto b)    { return b == null ? "" : b.getNomAffiche(); }
                    @Override public BonAchatDto fromString(String s)  { return null; }
                }
        ));

        // ── ComboBox modes de paiement ───────────────────────────
        comboPaiement.setConverter(new StringConverter<>() {
            @Override public String toString(ModePaiementDto m)    { return m == null ? "" : m.code; }
            @Override public ModePaiementDto fromString(String s)  { return null; }
        });

        chargerReferentiels();
        chargerProduits("");
    }

    // ── Référentiels ─────────────────────────────────────────────

    private void chargerReferentiels() {
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                List<ModePaiementDto> modes = ApiClient.getInstance().get(
                        "/referentiel/modes-paiement",
                        new TypeReference<List<ModePaiementDto>>() {});

                List<ContexteVenteDto> contextes = ApiClient.getInstance().get(
                        "/referentiel/contextes-vente",
                        new TypeReference<List<ContexteVenteDto>>() {});

                Long ctxId = contextes == null ? null : contextes.stream()
                        .filter(c -> "EN_MAGASIN".equalsIgnoreCase(c.code))
                        .map(c -> c.id)
                        .findFirst().orElse(null);

                Platform.runLater(() -> {
                    if (modes != null && !modes.isEmpty()) {
                        comboPaiement.setItems(FXCollections.observableArrayList(modes));
                        ModePaiementDto carteBleu = modes.stream()
                                .filter(m -> m.code != null && (
                                        m.code.equalsIgnoreCase("CB") ||
                                        m.code.equalsIgnoreCase("CARTE_BLEUE") ||
                                        m.code.toUpperCase().contains("CARTE")))
                                .findFirst()
                                .orElse(modes.get(0));
                        comboPaiement.setValue(carteBleu);
                    }
                    idContexteVente = ctxId;
                });
                return null;
            }
        };
        task.setOnFailed(e -> AlertHelper.warn("Référentiels",
                "Impossible de charger les modes de paiement depuis l'API."));
        new Thread(task).start();
    }

    // ── Identification client ────────────────────────────────────

    @FXML
    private void handleRechercheClient() {
        String    nom           = nomClientField.getText().trim();
        String    prenom        = prenomClientField.getText().trim();
        LocalDate dateNaissance = dateNaissanceField.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null) {
            AlertHelper.warn("Champs manquants", "Saisissez le nom, le prénom et la date de naissance.");
            return;
        }

        String url = "/clients/identifier?nom=" + nom
                   + "&prenom=" + prenom
                   + "&dateNaissance=" + dateNaissance;

        Task<ClientDetailDto> task = new Task<>() {
            @Override protected ClientDetailDto call() throws Exception {
                return ApiClient.getInstance().get(url, ClientDetailDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            ClientDetailDto client = task.getValue();
            clientId  = client.id;
            clientNom = client.prenom + " " + client.nom;
            Platform.runLater(() -> {
                labelClientNom.setText(clientNom);
                labelClientEmail.setText(client.email != null ? client.email : "");
                labelClientPoints.setText("🏆 " + client.soldePoints + " pts");
                labelClientFidelite.setText(client.typeFidelite != null ? client.typeFidelite : "Standard");
                carteClient.setVisible(true);
                carteClient.setManaged(true);
                chargerBonsAchat(client.id);
                // Recalculer le panier si des produits sont déjà dedans
                mettreAJourTotal();
            });
        });
        task.setOnFailed(e -> {
            Throwable ex  = task.getException();
            String    msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Client introuvable", msg);
        });
        new Thread(task).start();
    }

    @FXML
    private void handleChangerClient() {
        clientId  = null;
        clientNom = "";
        nomClientField.clear();
        prenomClientField.clear();
        dateNaissanceField.setValue(null);
        carteClient.setVisible(false);
        carteClient.setManaged(false);
        zoneBons.setVisible(false);
        zoneBons.setManaged(false);
        bonsChecked.clear();
        listeBonsAchat.getItems().clear();
        nomClientField.requestFocus();
        mettreAJourTotal();
    }

    private void chargerBonsAchat(Long idClient) {
        Task<List<BonAchatDto>> task = new Task<>() {
            @Override protected List<BonAchatDto> call() throws Exception {
                return ApiClient.getInstance().get(
                        "/clients/" + idClient + "/bons-achat",
                        new TypeReference<List<BonAchatDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<BonAchatDto> bons        = task.getValue();
            List<BonAchatDto> disponibles = bons == null ? List.of()
                    : bons.stream().filter(b -> !b.utilise).toList();
            Platform.runLater(() -> {
                // Reconstruire la map de checkboxes
                bonsChecked.clear();
                for (BonAchatDto b : disponibles) {
                    SimpleBooleanProperty prop = new SimpleBooleanProperty(false);
                    prop.addListener((obs, o, n) -> mettreAJourTotal());
                    bonsChecked.put(b, prop);
                }
                listeBonsAchat.setItems(FXCollections.observableArrayList(disponibles));
                zoneBons.setVisible(!disponibles.isEmpty());
                zoneBons.setManaged(!disponibles.isEmpty());
            });
        });
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    // ── Catalogue produits ───────────────────────────────────────

    @FXML
    private void handleRechercheProduit() {
        chargerProduits(rechercheProduitField.getText().trim());
    }

    private void chargerProduits(String q) {
        String etat      = getEtatSelectionne();
        Long   magasinId = SessionManager.getInstance().getMagasinId();

        String qEnc = q != null && !q.isBlank()
                ? "&q=" + java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8) : "";

        String url = "/produits/catalogue-pos"
                   + "?idMagasin=" + magasinId
                   + "&etat=" + etat
                   + qEnc;

        Task<PageDto<CataloguePosSummaryDto>> task = new Task<>() {
            @Override protected PageDto<CataloguePosSummaryDto> call() throws Exception {
                return ApiClient.getInstance().get(url,
                        new TypeReference<PageDto<CataloguePosSummaryDto>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            PageDto<CataloguePosSummaryDto> res     = task.getValue();
            List<CataloguePosSummaryDto>    content = res != null && res.content != null
                    ? res.content : List.of();
            Platform.runLater(() -> { produits.clear(); produits.addAll(content); });
        });
        task.setOnFailed(e -> { /* silencieux */ });
        new Thread(task).start();
    }

    // ── Panier ───────────────────────────────────────────────────

    @FXML
    private void handleAjouterPanier() {
        CataloguePosSummaryDto p = tableProduits.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertHelper.warn("Sélection", "Sélectionnez un produit dans le catalogue.");
            return;
        }

        BigDecimal prixBD = p.getPrixEffectif();
        if (prixBD == null || prixBD.signum() == 0) {
            AlertHelper.warn("Prix indisponible",
                    "Ce variant n'a pas de prix « " + p.statut + " » renseigné.");
            return;
        }

        // Vérification stock en magasin
        if (p.getStockEffectif() <= 0) {
            AlertHelper.warn("Rupture de stock",
                    "Ce variant n'est plus disponible dans ce magasin.");
            return;
        }

        // Vérifier que la quantité demandée ne dépasse pas le stock
        LignePanier existante = panier.stream()
                .filter(l -> l.getVariantId().equals(p.idVariant))
                .findFirst().orElse(null);

        int qteDejaAuPanier = existante != null ? existante.getQuantite() : 0;
        if (qteDejaAuPanier >= p.getStockEffectif()) {
            AlertHelper.warn("Stock insuffisant",
                    "Stock disponible : " + p.getStockEffectif() + " — déjà " + qteDejaAuPanier + " au panier.");
            return;
        }

        double prix       = prixBD.doubleValue();
        String plateforme = p.plateforme != null ? p.plateforme : "";
        String label      = "LOCATION".equalsIgnoreCase(p.statut) ? "LOCATION (mois)" : p.statut;

        if (p.necessiteNumeroSerie) {
            // ── Produit sérialisé (console, accessoire) ───────────────────────
            // Chaque unité = une ligne distincte avec son propre n° de série.
            // On ne regroupe jamais plusieurs unités sur la même ligne.
            String numeroSerie = demanderNumeroSerie(p.nom);
            if (numeroSerie == null) return; // utilisateur a annulé

            String nomAvecSerie = p.nom + "  (N/S: " + numeroSerie + ")";
            LignePanier nouvelleLigne = new LignePanier(p.idVariant, nomAvecSerie, plateforme, label, 1, prix);
            nouvelleLigne.setNumeroSerie(numeroSerie);
            if (clientId != null) {
                proposerGarantie(p, nouvelleLigne);
            }
            panier.add(nouvelleLigne);

        } else if (existante != null) {
            existante.setQuantite(existante.getQuantite() + 1);
        } else {
            LignePanier nouvelleLigne = new LignePanier(p.idVariant, p.nom, plateforme, label, 1, prix);
            // Proposer garantie uniquement si un client est identifié
            if (clientId != null) {
                proposerGarantie(p, nouvelleLigne);
            }
            panier.add(nouvelleLigne);
        }

        tablePanier.refresh();
        mettreAJourTotal();
    }

    @FXML
    private void handleRetirerPanier() {
        LignePanier ligne = tablePanier.getSelectionModel().getSelectedItem();
        if (ligne != null) { panier.remove(ligne); mettreAJourTotal(); }
    }

    /**
     * Affiche une boîte de dialogue pour saisir le numéro de série.
     * Redemande si le champ est vide.
     *
     * @return le numéro de série saisi (trimé), ou null si l'utilisateur annule.
     */
    private String demanderNumeroSerie(String nomProduit) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Numéro de série");
            dialog.setHeaderText("📋 Scanner ou saisir le numéro de série");
            dialog.setContentText("N° série de « " + nomProduit + " » :");
            dialog.getDialogPane().setPrefWidth(460);

            java.util.Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return null;  // annulé

            String ns = result.get().trim();
            if (!ns.isEmpty()) return ns;

            AlertHelper.warn("Numéro de série manquant",
                    "Le numéro de série est obligatoire pour ce produit.\nScannez ou saisissez-le.");
        }
    }

    /**
     * Propose une garantie via popup radio.
     * Appelé uniquement lorsqu'un client est identifié.
     */
    private void proposerGarantie(CataloguePosSummaryDto produit, LignePanier ligne) {
        try {
            // Appeler avec categorieId si disponible, sinon récupérer tous les types
            String url = produit.categorieId != null
                    ? "/referentiel/types-garantie?categorieId=" + produit.categorieId
                    : "/referentiel/types-garantie";
            List<TypeGarantieDto> types = ApiClient.getInstance().get(url,
                    new TypeReference<List<TypeGarantieDto>>() {});

            if (types == null || types.isEmpty()) return;

            // Garder uniquement ceux avec un prix > 0
            List<TypeGarantieDto> avecPrix = types.stream()
                .filter(tg -> tg.prixExtension != null && tg.prixExtension.signum() > 0)
                .toList();

            if (avecPrix.isEmpty()) return;

            // Logique identique au site web :
            // - NEUF/PRECOMMANDE  → afficher TOUT sauf ceux explicitement marqués OCCASION
            // - OCCASION          → afficher TOUT sauf ceux explicitement marqués NEUF
            // - LOCATION/autres   → afficher uniquement les génériques (ni NEUF ni OCC)
            // Les types génériques (ex. ANTICASSE, EXT_1AN) s'affichent pour NEUF et OCCASION.
            String etat = produit.statut != null ? produit.statut.toUpperCase() : "NEUF";
            List<TypeGarantieDto> filtres = avecPrix.stream()
                .filter(tg -> {
                    String code = tg.code != null ? tg.code.toUpperCase() : "";
                    boolean hasNeufMarker = code.contains("NEUF") || code.contains("NEW");
                    boolean hasOccMarker  = code.contains("OCC")  || code.contains("OCCASION");
                    if (etat.equals("NEUF") || etat.equals("PRECOMMANDE")) return !hasOccMarker;
                    if (etat.equals("OCCASION"))                           return !hasNeufMarker;
                    return !hasNeufMarker && !hasOccMarker;   // LOCATION, REPRISE…
                })
                .toList();

            if (filtres.isEmpty()) return;

            Dialog<TypeGarantieDto> dialog = new Dialog<>();
            dialog.setTitle("Garantie optionnelle");
            dialog.setHeaderText("Ajouter une garantie pour « " + produit.nom + " » ?");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            ToggleGroup tg = new ToggleGroup();
            VBox box = new VBox(8);
            RadioButton rbNone = new RadioButton("Sans garantie");
            rbNone.setToggleGroup(tg);
            rbNone.setSelected(true);
            rbNone.setUserData(null);
            box.getChildren().add(rbNone);
            for (TypeGarantieDto t : filtres) {
                RadioButton rb = new RadioButton(t.getLibelle());
                rb.setToggleGroup(tg);
                rb.setUserData(t);
                box.getChildren().add(rb);
            }
            dialog.getDialogPane().setContent(box);
            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK && tg.getSelectedToggle() != null) {
                    return (TypeGarantieDto) tg.getSelectedToggle().getUserData();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(selected -> {
                if (selected != null) {
                    double prixGarantie = selected.prixExtension != null
                            ? selected.prixExtension.doubleValue() : 0.0;
                    String libelle = selected.description != null ? selected.description : selected.code;
                    ligne.setGarantie(selected.id, libelle, prixGarantie);
                }
            });
        } catch (Exception e) {
            System.err.println("[VenteController] proposerGarantie erreur : " + e.getMessage());
        }
    }

    private void mettreAJourTotal() {
        double totalProduits  = panier.stream().mapToDouble(l -> l.getPrix() * l.getQuantite()).sum();
        double totalGaranties = panier.stream().mapToDouble(l -> l.getGarantiePrix() * l.getQuantite()).sum();

        // Bons d'achat cochés (CheckBoxListCell)
        double totalBons = bonsChecked.entrySet().stream()
                .filter(entry -> entry.getValue().get())
                .filter(entry -> entry.getKey().valeur != null)
                .mapToDouble(entry -> entry.getKey().valeur.doubleValue())
                .sum();

        double sousTotal  = totalProduits + totalGaranties;
        double totalFinal = Math.max(0, sousTotal - totalBons);

        // Mise à jour des labels
        if (!panier.isEmpty()) {
            labelTotalProduits.setText(String.format("Produits : %.2f €", totalProduits));
            labelTotalProduits.setVisible(true);
            labelTotalProduits.setManaged(true);
        } else {
            labelTotalProduits.setVisible(false);
            labelTotalProduits.setManaged(false);
        }

        if (totalGaranties > 0) {
            labelTotalGaranties.setText(String.format("🛡 Garanties : +%.2f €", totalGaranties));
            labelTotalGaranties.setVisible(true);
            labelTotalGaranties.setManaged(true);
        } else {
            labelTotalGaranties.setVisible(false);
            labelTotalGaranties.setManaged(false);
        }

        if (totalBons > 0) {
            labelTotalBons.setText(String.format("🎟 Bons : -%.2f €", totalBons));
            labelTotalBons.setVisible(true);
            labelTotalBons.setManaged(true);
        } else {
            labelTotalBons.setVisible(false);
            labelTotalBons.setManaged(false);
        }

        labelTotal.setText(String.format("Total : %.2f €", totalFinal));
    }

    // ── Valider la vente ─────────────────────────────────────────

    @FXML
    private void handleValiderVente() {
        if (panier.isEmpty()) {
            AlertHelper.warn("Panier vide", "Ajoutez au moins un produit."); return;
        }
        ModePaiementDto modePaiement = comboPaiement.getValue();
        if (modePaiement == null) {
            AlertHelper.warn("Paiement", "Sélectionnez un mode de paiement."); return;
        }
        if (idContexteVente == null) {
            AlertHelper.warn("Configuration",
                    "Contexte de vente non chargé. Patientez ou relancez l'application.");
            return;
        }

        long nbGaranties = panier.stream().filter(LignePanier::isExtensionGarantie).count();
        List<BonAchatDto> bonsSelec = bonsChecked.entrySet().stream()
                .filter(entry -> entry.getValue().get())
                .map(Map.Entry::getKey)
                .toList();

        String nomPourConfirm = (clientId != null) ? labelClientNom.getText() : "client anonyme";
        String recapGaranties = nbGaranties > 0 ? "\n🛡 " + nbGaranties + " extension(s) de garantie" : "";
        String recapBons      = !bonsSelec.isEmpty()
                ? "\n🎟 " + bonsSelec.size() + " bon(s) d'achat (-"
                  + bonsSelec.stream().filter(b -> b.valeur != null)
                             .mapToDouble(b -> b.valeur.doubleValue()).sum()
                  + " €)" : "";
        String recap = labelTotal.getText() + recapGaranties + recapBons;

        if (!AlertHelper.confirm("Confirmer la vente",
                "Valider la vente pour " + nomPourConfirm + " ?\n" + recap)) return;

        setLoading(true);

        List<LigneVenteDto> lignes = panier.stream()
            .map(l -> new LigneVenteDto(
                    l.getVariantId(),
                    l.getQuantite(),
                    BigDecimal.valueOf(l.getPrix()),
                    l.getTypeGarantieId(),
                    l.getNumeroSerie()))
            .toList();

        List<Long> idsBons = bonsSelec.stream().map(b -> b.id).toList();

        CreateVenteDto body = new CreateVenteDto(
            clientId,
            SessionManager.getInstance().getMagasinId(),
            modePaiement.id,
            idContexteVente,
            idsBons.isEmpty() ? null : idsBons,
            lignes
        );

        Task<FactureDto> task = new Task<>() {
            @Override protected FactureDto call() throws Exception {
                return ApiClient.getInstance().post("/factures/vente-magasin", body, FactureDto.class);
            }
        };
        task.setOnSucceeded(e -> {
            setLoading(false);
            FactureDto facture = task.getValue();
            String ref = facture.referenceFacture != null
                    ? facture.referenceFacture : String.valueOf(facture.id);
            AlertHelper.success("Vente enregistrée",
                    "✅ Facture " + ref + " créée !\nMontant : "
                    + String.format("%.2f €", facture.montantFinal));
            panier.clear();
            mettreAJourTotal();
            reinitialiserClient();
            // Recharger catalogue pour refléter le nouveau stock
            chargerProduits(rechercheProduitField.getText().trim());
        });
        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex  = task.getException();
            String    msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Erreur vente", msg);
        });
        new Thread(task).start();
    }

    // ── Helpers ──────────────────────────────────────────────────

    private String getEtatSelectionne() {
        if (radioOccasion != null && radioOccasion.isSelected()) return "OCCASION";
        if (radioLocation != null && radioLocation.isSelected()) return "LOCATION";
        return "NEUF";
    }

    private void reinitialiserClient() {
        clientId  = null;
        clientNom = "";
        Platform.runLater(() -> {
            nomClientField.clear();
            prenomClientField.clear();
            dateNaissanceField.setValue(null);
            carteClient.setVisible(false);
            carteClient.setManaged(false);
            zoneBons.setVisible(false);
            zoneBons.setManaged(false);
            bonsChecked.clear();
            listeBonsAchat.getItems().clear();
        });
    }

    private void setLoading(boolean b) {
        Platform.runLater(() -> { btnValiderVente.setDisable(b); spinner.setVisible(b); });
    }
}
