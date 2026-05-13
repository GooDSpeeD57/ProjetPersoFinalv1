package fr.micromania.dto.catalog;

import java.math.BigDecimal;

public record CataloguePosSummary(
    Long      idProduit,
    Long      idVariant,
    String    nom,
    String    sku,
    String    statut,
    String    plateforme,
    String    edition,
    String    format,
    BigDecimal prixNeuf,
    BigDecimal prixOccasion,
    BigDecimal prixLocation,
    BigDecimal prixReprise,
    int       stockNeuf,
    int       stockOccasion,
    boolean   disponible,
    Long      categorieId,
    boolean   necessiteNumeroSerie
) {
    public BigDecimal prixEffectif() {
        if (statut == null) return prixNeuf;
        return switch (statut.toUpperCase()) {
            case "OCCASION" -> prixOccasion;
            case "LOCATION" -> prixLocation;
            default         -> prixNeuf;
        };
    }
}
