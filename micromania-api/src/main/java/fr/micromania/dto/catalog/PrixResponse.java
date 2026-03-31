package fr.micromania.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrixResponse(
    Long id,
    Long idVariant,
    String canalVente,
    BigDecimal prix,
    LocalDateTime dateDebut,
    LocalDateTime dateFin,
    boolean actif
) {}
