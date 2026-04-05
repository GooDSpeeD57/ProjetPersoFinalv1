package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
        List<ApiProduitImage> images,
        Double noteMoyenne,
        long nbAvis,
        List<ApiAvisProduit> avis
) {}
