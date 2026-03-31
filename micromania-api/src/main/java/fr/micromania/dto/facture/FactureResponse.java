package fr.micromania.dto.facture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FactureResponse(
    Long id,
    String referenceFacture,
    String statutFacture,
    String contexteVente,
    String modePaiement,
    String magasin,
    String nomClientAffiche,
    String emailClientAffiche,
    BigDecimal montantHtTotal,
    BigDecimal montantTvaTotal,
    BigDecimal montantTotal,
    BigDecimal montantRemise,
    BigDecimal montantFinal,
    LocalDateTime dateFacture,
    List<LigneFactureResponse> lignes
) {}
