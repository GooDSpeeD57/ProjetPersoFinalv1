package fr.micromania.dto.precommande;

import java.math.BigDecimal;

public record PrecommandeLigneResponse(
    Long id,
    Long idVariant,
    String nomCommercial,
    int quantite,
    BigDecimal prixUnitaireEstime,
    BigDecimal montantLigneEstime
) {}
