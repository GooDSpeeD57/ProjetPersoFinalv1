package com.monprojet.boutiquejeux.dto.api.panier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ApiPanier(
        Long id,
        String statutPanier,
        String canalVente,
        String codePromo,
        List<ApiLignePanier> lignes,
        BigDecimal sousTotal,
        BigDecimal remise,
        BigDecimal total,
        LocalDateTime dateDerniereActivite
) {}
