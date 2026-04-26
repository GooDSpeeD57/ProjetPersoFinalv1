package fr.micromania.dto.catalog;

import java.math.BigDecimal;

/**
 * Une entrée du catalogue public : 1 variant = 1 vignette.
 * idProduit sert pour le lien vers la page détail produit.
 */
public record VariantCatalogueSummary(
        Long       id,               // variant ID → ajout panier
        Long       idProduit,        // product ID → lien détail
        String     nom,              // nom du produit
        String     slug,
        String     categorie,
        String     typeCategorie,
        String     plateforme,       // libelle plateforme
        String     statutProduit,    // NEUF / OCCASION / …
        String     edition,          // libelle édition (Standard, DayOne…) ou null
        String     imageUrl,
        String     imageAlt,
        BigDecimal prix,             // prix web
        boolean    misEnAvant,
        boolean    estPreCommande,
        Integer    pegi,
        Double     noteMoyenne,
        long       nbAvis
) {}
