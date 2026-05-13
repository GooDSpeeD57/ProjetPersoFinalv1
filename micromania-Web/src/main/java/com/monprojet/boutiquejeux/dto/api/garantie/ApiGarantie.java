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
        String    nomProduit,
        String    numeroSerie,
        /** "LEGALE" = conformité légale / "EXTENSION" = extension payante souscrite */
        String    typeItem
) {}
