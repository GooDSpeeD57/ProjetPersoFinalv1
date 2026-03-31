package fr.micromania.dto.catalog;

import java.math.BigDecimal;

public record ProduitSummary(
    Long id,
    String nom,
    String slug,
    String categorie,
    String imageUrl,
    String imageAlt,
    BigDecimal prixMinimal,
    boolean disponible,
    boolean misEnAvant,
    Integer pegi
) {}
