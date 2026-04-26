package com.monprojet.boutiquejeux.dto.produit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Correspond à CataloguePosSummary côté API.
 * Un enregistrement = un variant disponible en stock dans le magasin.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CataloguePosSummaryDto {

    public Long       idProduit;
    public Long       idVariant;
    public String     nom;
    public String     sku;
    public String     statut;      // NEUF / OCCASION / LOCATION
    public String     plateforme;
    public String     edition;
    public String     format;
    public BigDecimal prixNeuf;
    public BigDecimal prixOccasion;
    public BigDecimal prixLocation;
    public BigDecimal prixReprise;
    public int        stockNeuf;
    public int        stockOccasion;
    public boolean    disponible;
    public Long       categorieId;

    /** Prix à afficher/utiliser selon le statut du variant. */
    public BigDecimal getPrixEffectif() {
        if (statut == null) return prixNeuf;
        return switch (statut.toUpperCase()) {
            case "OCCASION" -> prixOccasion;
            case "LOCATION" -> prixLocation;
            default         -> prixNeuf;
        };
    }

    /** Stock disponible selon le statut. */
    public int getStockEffectif() {
        if (statut == null) return stockNeuf;
        return switch (statut.toUpperCase()) {
            case "OCCASION" -> stockOccasion;
            default         -> stockNeuf;
        };
    }

    public String getPrixAffiche() {
        BigDecimal p = getPrixEffectif();
        if (p == null) return "—";
        return "LOCATION".equalsIgnoreCase(statut)
                ? String.format("%.2f €/mois", p)
                : String.format("%.2f €", p);
    }

    public String getDispoTexte() {
        return getStockEffectif() > 0 ? "✓ " + getStockEffectif() : "✗";
    }
}
