package com.monprojet.boutiquejeux.dto.api.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApiBonAchat(
    Long id,
    String codeBon,
    BigDecimal valeur,
    int pointsUtilises,
    boolean utilise,
    LocalDateTime dateCreation,
    LocalDateTime dateExpiration,
    LocalDateTime dateUtilisation,
    Long idFacture
) {}
