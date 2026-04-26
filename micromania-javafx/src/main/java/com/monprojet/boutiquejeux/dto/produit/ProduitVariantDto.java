package com.monprojet.boutiquejeux.dto.produit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/** Correspond à ProduitVariantResponse côté API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProduitVariantDto {
    public Long         id;
    public String       sku;
    public String       nomCommercial;
    public String       formatProduit;
    public String       statutProduit;
    public EditionRef   edition;
    public boolean      actif;
    public BigDecimal   prixNeuf;
    public BigDecimal   prixOccasion;
    public BigDecimal   prixReprise;
    public BigDecimal   prixLocation;
    public PlateformeDto plateforme;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlateformeDto {
        public Long   id;
        public String code;
        public String libelle;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EditionRef {
        public Long   id;
        public String code;
        public String libelle;
    }

    public String getPlateformeCode() {
        return plateforme != null ? plateforme.code : "";
    }

    /** Libellé de l'édition, ou chaîne vide si aucune. */
    public String getEditionLibelle() {
        return edition != null && edition.libelle != null ? edition.libelle : "";
    }

    @Override
    public String toString() {
        String label = nomCommercial != null ? nomCommercial : sku;
        if (plateforme != null && plateforme.code != null) label += " [" + plateforme.code + "]";
        if (statutProduit != null) label += " — " + statutProduit;
        return label;
    }

    /** Prix à afficher dans les tableaux : neuf si dispo, sinon occasion. */
    public BigDecimal getPrixAffichage() {
        if (prixNeuf != null)     return prixNeuf;
        if (prixOccasion != null) return prixOccasion;
        return null;
    }
}
