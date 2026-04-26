package com.monprojet.boutiquejeux.dto.api.garantie;

import java.time.LocalDate;

public record ApiGarantie(
        Long      id,
        Long      idVenteUnite,
        String    codeTypeGarantie,
        String    descTypeGarantie,
        Integer   dureeMois,
        LocalDate dateDebut,
        LocalDate dateFin,
        boolean   estEtendue,
        LocalDate dateExtension
) {}
