package fr.micromania.dto.catalog;

import java.math.BigDecimal;

public record ProduitSummary(
        Long id,
        String nom,
        String slug,
        String categorie,
        String typeCategorie,
        String plateforme,
        String imageUrl,
        String imageAlt,
        BigDecimal prixNeuf,
        BigDecimal prixOccasion,
        BigDecimal prixReprise,
        BigDecimal prixLocation,
        boolean disponible,
        boolean misEnAvant,
        Integer pegi,
        Double noteMoyenne,
        long nbAvis,
        Long variantIdNeuf,
        Long variantIdOccasion,
        Long variantIdLocation,
        boolean estPreCommande
) {}
