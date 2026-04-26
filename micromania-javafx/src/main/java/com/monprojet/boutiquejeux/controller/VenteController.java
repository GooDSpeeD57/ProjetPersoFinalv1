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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ToggleGroup;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    @FXML private HBox                   zoneBons;
    @FXML private ComboBox<BonAchatDto>  comboBonAchat;

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
    @FXML private Label                                  labelTotal;

    // ── Paiement ─────────────────────────────────────────────────
    @FXML private ComboBox<ModePaiementDto> comboPaiement;
    @FXML private CheckBox                  checkPointsFidelite;
    @FXML private Label                     labelPointsUtilises;
    @FXML private Button                    btnValiderVente;
    @FXML private ProgressIndicator         spinner;

    // ── État ─────────────────────────────────────────────────────
    private Long clientId        = null;
    private int  clientPoints    = 0;
    private Long idContexteVente = null;

    private final ObservableList<CataloguePosSummaryDto> produits = FXCollections.observableArrayList();
    private final ObservableList<LignePanier>            panier   = FXCollections.observableArrayList();

    // ── Initialisation ───────────────────────────────────────────

    @FXML
    public void initialize() {
        spinner.setVisible(false);

        // ── Colonnes catalogue (1 ligne = 1 variant en stock) ────
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

        // Recharger quand on change Neuf / Occasion / Location
        typeVenteGroup.selectedToggleProperty().addListener((obs, o, n) ->
                chargerProduits(rechercheProduitField.getText().trim()));

        // ── Colonnes panier ──────────────────────────────────────
        colPanierNom.setCellValueFactory(c -> c.getValue().nomProduitProperty());
        colPanierPlateforme.setCellValueFactory(c -> c.getValue().plateformeProperty());
        colPanierType.setCellValueFactory(c -> c.getValue().typeProperty());
        colPanierQte.setCellValueFactory(c -> c.getValue().quantiteProperty().asObject());
        colPanierPrix.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPrixAffiche()));

        // Colonne Garantie : affiche le label du type sélectionné (ou "—")
        colPanierGarantie.setCellValueFactory(c -> c.getValue().extensionGarantieProperty());
        colPanierGarantie.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Boolean val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                LignePanier l = getTableRow().getItem();
                setText(l.getGarantieLabel() != null && !l.getGarantieLabel().isBlank()
                        ? "🛡 " + l.getGarantieLabel() : "—");
            }
        });

        tablePanier.setEditable(true);
        tablePanier.setItems(panier);

        // ── ComboBox modes de paiement ───────────────────────────
        comboPaiement.setConverter(new StringConverter<>() {
            @Override public String toString(ModePaiementDto m)    { return m == null ? "" : m.code; }
            @Override public ModePaiementDto fromString(String s)  { return null; }
        });

        // ── ComboBox bons d'achat ────────────────────────────────
        comboBonAchat.setConverter(new StringConverter<>() {
            @Override public String toString(BonAchatDto b)    { return b == null ? "" : b.getNomAffiche(); }
            @Override public BonAchatDto fromString(String s)  { return null; }
        });

        checkPointsFidelite.setDisable(true);

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
                        // Sélection par défaut : Carte Bleue
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
            clientId     = client.id;
            clientPoints = client.soldePoints;
            Platform.runLater(() -> {
                labelClientNom.setText(client.prenom + " " + client.nom);
                labelClientEmail.setText(client.email != null ? client.email : "");
                labelClientPoints.setText("🏆 " + clientPoints + " pts");
                labelClientFidelite.setText(client.typeFidelite != null ? client.typeFidelite : "Standard");
                carteClient.setVisible(true);
                carteClient.setManaged(true);
                checkPointsFidelite.setDisable(clientPoints < 100);
                chargerBonsAchat(client.id);
            });
        });
        task.setOnFailed(e -> {
            Throwable ex  = task.getException();
            String    msg = ex instanceof ApiException ae ? ae.getMessage() : ex.getMessage();
            AlertHelper.error("Client introuvable", msg);
        });
        new Thread(task).start();
    }

    /** Bouton "✖ Changer" — efface le client sans toucher au panier. */
    @FXML
    private void handleChangerClient() {
        clientId     = null;
        clientPoints = 0;
        nomClientField.clear();
        prenomClientField.clear();
        dateNaissanceField.setValue(null);
        carteClient.setVisible(false);
        carteClient.setManaged(false);
        zoneBons.setVisible(false);
        zoneBons.setManaged(false);
        comboBonAchat.getItems().clear();
        checkPointsFidelite.setSelected(false);
        checkPointsFidelite.setDisable(true);
        labelPointsUtilises.setText("");
        nomClientField.requestFocus();
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
                comboBonAchat.setItems(FXCollections.observableArrayList(disponibles));
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

    /**
     * Charge les variants en stock depuis /produits/catalogue-pos.
     * 1 ligne API = 1 variant = 1 état (NEUF / OCCASION / LOCATION).
     */
    private void chargerProduits(String q) {
        String etat      = getEtatSelectionne();
        Long   magasinId = SessionManager.getInstance().getMagasinId();

        String url = "/produits/catalogue-pos"
                   + "?idMagasin=" + magasinId
                   + "&etat=" + etat
                   + (q != null && !q.isBlank() ? "&q=" + q : "");

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
        if (p.getStockEffectif() <= 0) {
            AlertHelper.warn("Rupture de stock", "Ce variant n'est plus disponible.");
            return;
        }

        double prix       = prixBD.doubleValue();
        String plateforme = p.plateforme != null ? p.plateforme : "";
        String label      = "LOCATION".equalsIgnoreCase(p.statut) ? "LOCATION (mois)" : p.statut;

        // Chercher si une ligne existe déjà pour ce variant
        LignePanier existante = panier.stream()
                .filter(l -> l.getVariantId().equals(p.idVariant))
                .findFirst().orElse(null);

        if (existante != null) {
            existante.setQuantite(existante.getQuantite() + 1);
        } else {
            LignePanier nouvelleLigne = new LignePanier(p.idVariant, p.nom, plateforme, label, 1, prix);
            // Proposer une garantie si la catégorie en a
            proposerGarantie(p, nouvelleLigne);
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
     * Propose une garantie via popup radio si des TypeGarantie existent pour la catégorie du produit.
     * Appel synchrone depuis le thread JavaFX (bloquant jusqu'à fermeture du dialog).
     */
    private void proposerGarantie(CataloguePosSummaryDto produit, LignePanier ligne) {
        if (produit.categorieId == null) return;
        try {
            String url = "/referentiel/types-garantie?categorieId=" + produit.categorieId;
            List<TypeGarantieDto> types = ApiClient.getInstance().get(url,
                    new TypeReference<List<TypeGarantieDto>>() {});
            // Filtrer sur le statut (NEUF → code contient NEUF, OCCASION → OCC)
            String etat = produit.statut != null ? produit.statut.toUpperCase() : "NEUF";
            List<TypeGarantieDto> filtres = types == null ? List.of() : types.stream()
                .filter(tg -> tg.prixExtension != null && tg.prixExtension.signum() > 0)
                .filter(tg -> {
                    String code = tg.code != null ? tg.code.toUpperCase() : "";
                    if (etat.equals("NEUF") || etat.equals("PRECOMMANDE")) return code.contains("NEUF");
                    if (etat.equals("OCCASION"))                           return code.contains("OCC");
                    return !code.contains("NEUF") && !code.contains("OCC");
                })
                .toList();

            if (filtres.isEmpty()) return;

            // Construire le dialog
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
                    ligne.setGarantie(selected.id, selected.description != null ? selected.description : selected.code);
                }
            });
        } catch (Exception e) {
            // Silencieux si l'API ne répond pas — on continue sans garantie
        }
    }

    private void mettreAJourTotal() {
        double totalProduits  = panier.stream().mapToDouble(l -> l.getPrix() * l.getQuantite()).sum();
        double totalGaranties = panier.stream()
                .filter(l -> l.getTypeGarantieId() != null)
                .mapToDouble(l -> {
                    // On ne connaît pas le prix ici — on le récupère depuis le label ou on laisse l'API calculer
                    // Pour l'affichage on fait confiance au prix stocké dans LignePanier (si on le stocke)
                    return 0; // affiché dans le recap API après validation
                }).sum();
        labelTotal.setText(String.format("Total : %.2f €", totalProduits));
    }

    // ── Valider la vente ─────────────────────────────────────────

    @FXML
    private void handleValiderVente() {
        if (panier.isEmpty()) {
            AlertHelper.warn("Panier vide", "Ajoutez au moins un produit."); return;
        }
        if (clientId == null) {
            AlertHelper.warn("Client", "Identifiez d'abord le client."); return;
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
        String recap = labelTotal.getText()
                + (nbGaranties > 0 ? "\n🛡 " + nbGaranties + " extension(s) de garantie incluse(s)" : "");

        if (!AlertHelper.confirm("Confirmer la vente",
                "Valider la vente pour " + labelClientNom.getText() + " ?\n" + recap)) return;

        setLoading(true);

        List<LigneVenteDto> lignes = panier.stream()
            .map(l -> new LigneVenteDto(
                    l.getVariantId(),
                    l.getQuantite(),
                    BigDecimal.valueOf(l.getPrix()),
                    l.getTypeGarantieId()))
            .toList();

        BonAchatDto bonSelectionne = comboBonAchat.getValue();
        Long        idBonAchat     = bonSelectionne != null ? bonSelectionne.id : null;

        CreateVenteDto body = new CreateVenteDto(
            clientId,
            SessionManager.getInstance().getMagasinId(),
            modePaiement.id,
            idContexteVente,
            idBonAchat,
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

    /** "NEUF", "OCCASION" ou "LOCATION" selon le radio sélectionné. */
    private String getEtatSelectionne() {
        if (radioOccasion != null && radioOccasion.isSelected()) return "OCCASION";
        if (radioLocation != null && radioLocation.isSelected()) return "LOCATION";
        return "NEUF";
    }

    private void reinitialiserClient() {
        clientId     = null;
        clientPoints = 0;
        Platform.runLater(() -> {
            nomClientField.clear();
            prenomClientField.clear();
            dateNaissanceField.setValue(null);
            carteClient.setVisible(false);
            carteClient.setManaged(false);
            zoneBons.setVisible(false);
            zoneBons.setManaged(false);
            comboBonAchat.getItems().clear();
            checkPointsFidelite.setSelected(false);
            checkPointsFidelite.setDisable(true);
            labelPointsUtilises.setText("");
        });
    }

    private void setLoading(boolean b) {
        Platform.runLater(() -> { btnValiderVente.setDisable(b); spinner.setVisible(b); });
    }
}
