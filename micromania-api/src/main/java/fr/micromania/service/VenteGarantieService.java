package fr.micromania.service;

import fr.micromania.dto.facture.LigneFactureRequest;
import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.commande.LignePanier;

import java.util.List;

/**
 * Responsable de la création des VenteUnite et Garantie associées
 * lors d'une vente en magasin ou d'un checkout web.
 */
public interface VenteGarantieService {

    /**
     * Crée les VenteUnite + Garantie pour une vente en magasin (JavaFX).
     * Les numéros de série sont fournis dans les lignes de la requête.
     */
    void creerDepuisVenteMagasin(Facture facture, List<LigneFactureRequest> lignes);

    /**
     * Crée les VenteUnite + Garantie pour un checkout web.
     * Pas de numéro de série (assigné lors de l'expédition).
     */
    void creerDepuisCheckoutWeb(Facture facture, List<LignePanier> lignes);
}
