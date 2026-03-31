package fr.micromania.dto.facture;

import java.math.BigDecimal;

public record LigneFactureResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    String sku,
    int quantite,
    BigDecimal prixUnitaire,
    BigDecimal tauxTvaApplique,
    BigDecimal montantHtLigne,
    BigDecimal montantTvaLigne,
    BigDecimal montantLigne
) {}
