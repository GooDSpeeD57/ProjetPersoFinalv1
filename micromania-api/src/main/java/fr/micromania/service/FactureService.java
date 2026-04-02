package fr.micromania.service;

import fr.micromania.dto.facture.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FactureService {

    /** Génère une facture à partir d'une commande validée */
    FactureResponse genererDepuisCommande(Long idCommande);

    /** Vente directe en magasin sans commande préalable */
    FactureResponse creerVenteMagasin(CreateFactureVenteRequest request);

    /** Checkout web fictif depuis le panier actif du client */
    FactureResponse checkoutPanierClient(Long idClient, CheckoutPanierRequest request);

    FactureResponse getById(Long id);

    FactureResponse getByIdForClient(Long idClient, Long id);

    FactureResponse getByReference(String reference);

    Page<FactureSummary> getByClient(Long idClient, Pageable pageable);

    Page<FactureSummary> getByMagasin(Long idMagasin, Pageable pageable);

    void annuler(Long id);
}
