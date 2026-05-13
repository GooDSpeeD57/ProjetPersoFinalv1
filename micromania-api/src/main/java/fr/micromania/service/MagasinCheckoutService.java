package fr.micromania.service;

import fr.micromania.entity.Adresse;
import fr.micromania.entity.Magasin;

/**
 * Responsable de la résolution du magasin à utiliser lors du checkout
 * (retrait magasin ou magasin d'expédition le plus proche).
 */
public interface MagasinCheckoutService {

    /**
     * Résout le magasin le plus pertinent pour facturer/expédier
     * en fonction de l'adresse du client.
     */
    Magasin resoudrePourCheckout(Adresse adresse);

    /**
     * Charge le magasin de retrait explicitement demandé,
     * ou en résout un proche si aucun n'est précisé.
     */
    Magasin chargerRetraitOuProche(Long idMagasinRetrait, Adresse adresse);
}
