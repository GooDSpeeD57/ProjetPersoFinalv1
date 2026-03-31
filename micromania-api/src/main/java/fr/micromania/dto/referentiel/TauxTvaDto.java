package fr.micromania.dto.referentiel;

import java.math.BigDecimal;

public record TauxTvaDto(Long id, String code, BigDecimal taux) {}
