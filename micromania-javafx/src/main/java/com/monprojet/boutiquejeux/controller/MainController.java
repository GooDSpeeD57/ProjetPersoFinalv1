package com.monprojet.boutiquejeux.controller;

import com.monprojet.boutiquejeux.MainApp;
import com.monprojet.boutiquejeux.util.AlertHelper;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private Label     labelEmploye;
    @FXML private Label     labelRole;
    @FXML private Label     labelMagasin;
    @FXML private StackPane contentArea;

    // Boutons de navigation sidebar
    @FXML private Button btnDashboard;
    @FXML private Button btnCreerClient;
    @FXML private Button btnVente;
    @FXML private Button btnReprise;
    @FXML private Button btnStock;
    @FXML private Button btnProduits;
    @FXML private Button btnGaranties;
    @FXML private Button btnPlanning;
    @FXML private Button btnRecherche;
    @FXML private Button btnEntreprise;
    @FXML private Button btnDepot;

    private Button activeButton;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        labelEmploye.setText(session.getPrenom());
        labelRole.setText(formatRole(session.getRole()));
        labelMagasin.setText(session.getMagasinNom() != null ? session.getMagasinNom() : "");

        // Masquer les boutons selon le rôle
        if (!session.isManager()) {
            btnPlanning.setVisible(false);
            btnPlanning.setManaged(false);
            btnEntreprise.setVisible(false);
            btnEntreprise.setManaged(false);
        }

        // Charger le dashboard par défaut
        handleDashboard();
    }

    @FXML void handleDashboard()   { navigate("/fxml/Dashboard.fxml",    btnDashboard); }
    @FXML void handleCreerClient() { navigate("/fxml/CreerClient.fxml",   btnCreerClient); }
    @FXML void handleVente()       { navigate("/fxml/Vente.fxml",         btnVente); }
    @FXML void handleReprise()     { navigate("/fxml/Reprise.fxml",       btnReprise); }
    @FXML void handleStock()       { navigate("/fxml/Stock.fxml",         btnStock); }
    @FXML void handleProduits()    { navigate("/fxml/Produits.fxml",      btnProduits); }
    @FXML void handleGaranties()   { navigate("/fxml/Garanties.fxml",     btnGaranties); }
    @FXML void handlePlanning()    { navigate("/fxml/Planning.fxml",      btnPlanning); }
    @FXML void handleRecherche()    { navigate("/fxml/Recherche.fxml",     btnRecherche); }
    @FXML void handleEntreprise()  { navigate("/fxml/Entreprise.fxml",    btnEntreprise); }
    @FXML void handleDepot()       { navigate("/fxml/Depot.fxml",         btnDepot); }

    @FXML
    private void handleLogout() {
        if (AlertHelper.confirm("Déconnexion", "Voulez-vous vraiment vous déconnecter ?")) {
            SessionManager.getInstance().logout();
            try {
                MainApp.showLogin();
            } catch (IOException e) {
                AlertHelper.error("Erreur", e.getMessage());
            }
        }
    }

    private void navigate(String fxmlPath, Button button) {
        try {
            // Highlight bouton actif
            if (activeButton != null) activeButton.getStyleClass().remove("nav-active");
            button.getStyleClass().add("nav-active");
            activeButton = button;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            // Injecter la référence MainController dans DashboardController
            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardController dc) {
                dc.setMainController(this);
            }

            // Appliquer le redimensionnement proportionnel sur tous les TableView
            appliquerContrainteColonnes(view);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            AlertHelper.error("Navigation", "Impossible de charger : " + fxmlPath + "\n" + e.getMessage());
        }
    }

    /**
     * Parcourt récursivement l'arbre de nœuds et applique
     * CONSTRAINED_RESIZE_POLICY sur chaque TableView trouvé,
     * ce qui remplit l'espace disponible de façon proportionnelle
     * tout en respectant le minWidth de chaque colonne.
     */
    @SuppressWarnings("unchecked")
    private void appliquerContrainteColonnes(Node node) {
        if (node instanceof TableView<?> table) {
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
        // Tab n'est pas un Node : on passe par TabPane.getTabs() pour descendre dans chaque onglet
        if (node instanceof TabPane tabPane) {
            for (Tab tab : tabPane.getTabs()) {
                if (tab.getContent() != null) {
                    appliquerContrainteColonnes(tab.getContent());
                }
            }
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                appliquerContrainteColonnes(child);
            }
        }
    }

    private String formatRole(String role) {
        if (role == null) return "";
        return switch (role) {
            case "ROLE_ADMIN"   -> "Administrateur";
            case "ROLE_MANAGER" -> "Manager";
            case "ROLE_VENDEUR" -> "Vendeur";
            default             -> role;
        };
    }
}
