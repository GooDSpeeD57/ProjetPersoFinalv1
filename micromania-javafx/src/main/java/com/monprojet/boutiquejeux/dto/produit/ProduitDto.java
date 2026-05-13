package com.monprojet.boutiquejeux.dto.produit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Produit dans la liste catalogue (GET /produits → Page<ProduitSummary>)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProduitDto {

    public Long    id;
    public String  nom;
    public String  categorie;
    public String  genre;
    public String  plateforme;
    public String  edition;
    public String  type;              // NEUF / OCCASION / LOCATION (si filtré côté API)
    public Double  prixNeuf;
    public Double  prixOccasion;
    public Double  prixReprise;
    public Double  prixLocation;
    public boolean disponible;
    public boolean misEnAvant;
    public boolean estPreCommande;
    public Integer pegi;
    public String  imageUrl;
    public Long    variantIdNeuf;     // ID du variant NEUF
    public Long    variantIdOccasion; // ID du variant OCCASION
    public Long    variantIdLocation; // ID du variant LOCATION

    // ── Accesseurs par état ──────────────────────────────────────

    /** Retourne le variantId selon l'état sélectionné ("NEUF" / "OCCASION" / "LOCATION"). */
    public Long getVariantId(String etat) {
        if (etat == null) return variantIdNeuf;
        return switch (etat.toUpperCase()) {
            case "OCCASION" -> variantIdOccasion;
            case "LOCATION" -> variantIdLocation;
            default         -> variantIdNeuf;
        };
    }

    /** Retourne le prix selon l'état (0.0 si non disponible). */
    public double getPrixEffectif(String etat) {
        if (etat == null) return prixNeuf != null ? prixNeuf : 0.0;
        return switch (etat.toUpperCase()) {
            case "OCCASION" -> prixOccasion != null ? prixOccasion : 0.0;
            case "LOCATION" -> prixLocation != null ? prixLocation : 0.0;
            default         -> prixNeuf     != null ? prixNeuf     : 0.0;
        };
    }

    /** Prix générique multi-état pour les vues sans sélection explicite. */
    public String getPrixAffiche() {
        boolean hasNeuf  = prixNeuf     != null && prixNeuf     > 0;
        boolean hasOccaz = prixOccasion != null && prixOccasion > 0;
        boolean hasLoc   = prixLocation != null && prixLocation > 0;
        if (hasNeuf && hasOccaz)
            return String.format("N: %.2f € / O: %.2f €", prixNeuf, prixOccasion);
        if (hasNeuf)  return String.format("%.2f €", prixNeuf);
        if (hasOccaz) return String.format("%.2f €", prixOccasion);
        if (hasLoc)   return String.format("%.2f €/mois", prixLocation);
        return "—";
    }

    /** Texte affiché dans la colonne "Dispo". */
    public String getDispoTexte() {
        return disponible ? "✓" : "✗";
    }

    /** Genre ou catégorie selon ce qui est disponible. */
    public String getGenreOuCategorie() {
        if (genre != null && !genre.isBlank()) return genre;
        return categorie != null ? categorie : "";
    }
}
