package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/** DTO reçu depuis GET /catalogue (1 variant = 1 vignette). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiVariantSummary(
        Long       id,               // variant ID → ajout panier
        Long       idProduit,        // product ID → lien détail
        String     nom,
        String     slug,
        String     categorie,
        String     typeCategorie,
        String     plateforme,
        String     statutProduit,
        String     edition,
        String     imageUrl,
        String     imageAlt,
        BigDecimal prix,
        boolean    misEnAvant,
        boolean    estPreCommande,
        Integer    pegi,
        Double     noteMoyenne,
        long       nbAvis
) {}
