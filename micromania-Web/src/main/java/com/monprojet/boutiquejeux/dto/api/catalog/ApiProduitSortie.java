package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO reçu depuis GET /catalogue/sorties : 1 variant = 1 entrée calendrier. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiProduitSortie(
        Long       idVariant,
        Long       idProduit,
        String     nom,
        String     slug,
        String     categorie,
        String     plateforme,
        String     statutProduit,
        String     edition,
        String     imageUrl,
        BigDecimal prix,
        boolean    estPreCommande,
        Integer    pegi,
        LocalDate  dateSortie
) {}
