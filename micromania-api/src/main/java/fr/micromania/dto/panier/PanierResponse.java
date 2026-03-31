package fr.micromania.dto.panier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PanierResponse(
    Long id,
    String statutPanier,
    String canalVente,
    String codePromo,
    List<LignePanierResponse> lignes,
    BigDecimal sousTotal,
    BigDecimal remise,
    BigDecimal total,
    LocalDateTime dateDerniereActivite
) {}
