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
    BigDecimal montantTotalEstime,
    BigDecimal montantTotalValide,
    String commentaire,
    LocalDateTime dateCreation,
    LocalDateTime dateValidation,
    List<RepriseLigneResponse> lignes
) {}
