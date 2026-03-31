package com.monprojet.boutiquejeux.dto.api.catalog;

import java.time.LocalDate;
import java.util.List;

public record ApiProduitDetail(
        Long id,
        String nom,
        String slug,
        String description,
        String resumeCourt,
        LocalDate dateSortie,
        String editeur,
        String constructeur,
        Integer pegi,
        String marque,
        String niveauAccesMin,
        String langue,
        boolean misEnAvant,
        ApiCodeDescription categorie,
        List<ApiProduitVariant> variants,
        List<ApiProduitImage> images
) {}
