package fr.micromania.dto.reprise;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RepriseResponse(
    Long id,
    String referenceReprise,
    String statut,
    String modeCompensation,
    String client,
    String employe,
    String magasin,
    /** Montant calculé selon le mode (espèces = avoir × 0,90). */
    BigDecimal montantTotalEstime,
    BigDecimal montantTotalValide,
    /** Valeur brute en avoir (avant décote espèces), utile pour l'affichage. */
    BigDecimal montantTotalAvoir,
    String commentaire,
    LocalDateTime dateCreation,
    LocalDateTime dateValidation,
    List<RepriseLigneResponse> lignes
) {}
