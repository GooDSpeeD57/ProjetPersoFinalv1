package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiProduitSummary(
        Long id,
        String nom,
        String slug,
        String categorie,
        @JsonAlias({"typeCategorie", "type_categorie"})
        String typeCategorie,
        String plateforme,
        @JsonAlias({"formatProduit", "format_produit"})
        String formatProduit,
        String imageUrl,
        String imageAlt,
        BigDecimal prixNeuf,
        BigDecimal prixOccasion,
        boolean disponible,
        boolean misEnAvant,
        Integer pegi,
        Double noteMoyenne,
        long nbAvis,
        Long variantIdNeuf,
        Long variantIdOccasion,
        boolean estPreCommande
) {}
