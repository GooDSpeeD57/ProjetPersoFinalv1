package fr.micromania.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entrée du calendrier des sorties : 1 variant + date de sortie du produit.
 */
public record ProduitSortieItem(
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
