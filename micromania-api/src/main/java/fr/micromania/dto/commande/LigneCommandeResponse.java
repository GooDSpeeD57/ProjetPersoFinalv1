package fr.micromania.dto.commande;

import java.math.BigDecimal;

public record LigneCommandeResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    String sku,
    int quantite,
    BigDecimal prixUnitaire,
    BigDecimal montantLigne
) {}
