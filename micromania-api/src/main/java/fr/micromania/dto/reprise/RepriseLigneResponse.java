package fr.micromania.dto.reprise;

import java.math.BigDecimal;

public record RepriseLigneResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    String descriptionLibre,
    int quantite,
    String etatGeneral,
    BigDecimal prixEstimeUnitaire,
    BigDecimal prixValideUnitaire,
    boolean creeStockOccasion
) {}
