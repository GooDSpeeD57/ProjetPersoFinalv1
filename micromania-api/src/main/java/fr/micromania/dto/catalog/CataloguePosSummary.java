package fr.micromania.dto.catalog;

import java.math.BigDecimal;

/**
 * Vue catalogue pour le point de vente (POS).
 * Un enregistrement = un variant disponible en magasin.
 * Le champ statut indique l'état commercial : NEUF / OCCASION / LOCATION.
 */
public record CataloguePosSummary(
    Long      idProduit,
    Long      idVariant,
    String    nom,
    String    sku,
    String    statut,        // NEUF / OCCASION / LOCATION
    String    plateforme,
    String    edition,
    String    format,        // PHYSIQUE / DEMAT
    BigDecimal prixNeuf,
    BigDecimal prixOccasion,
    BigDecimal prixLocation,
    BigDecimal prixReprise,
    int       stockNeuf,
    int       stockOccasion,
    boolean   disponible,
    Long      categorieId    // pour filtrer les types de garantie applicables
) {
    /** Prix effectif selon le statut du variant. */
    public BigDecimal prixEffectif() {
        if (statut == null) return prixNeuf;
        return switch (statut.toUpperCase()) {
            case "OCCASION" -> prixOccasion;
            case "LOCATION" -> prixLocation;
            default         -> prixNeuf;
        };
    }
}
