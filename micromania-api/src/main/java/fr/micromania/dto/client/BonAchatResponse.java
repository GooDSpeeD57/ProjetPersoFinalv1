package fr.micromania.dto.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BonAchatResponse(
    Long id,
    String codeBon,
    BigDecimal valeur,
    int pointsUtilises,
    boolean utilise,
    LocalDateTime dateCreation,
    LocalDateTime dateUtilisation,
    Long idFacture
) {}
