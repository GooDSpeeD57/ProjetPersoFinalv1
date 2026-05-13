package fr.micromania.dto.garantie;

import java.time.LocalDate;

public record GarantieResponse(
        Long      id,
        Long      idVenteUnite,
        /** Null = garantie légale pure. Non-null = extension payante souscrite (ex: ETENDUE_CONSOLE). */
        String    codeTypeGarantie,
        String    descTypeGarantie,
        /** Durée totale en mois (légale + extension si présente). */
        Integer   dureeMois,
        LocalDate dateDebut,
        /** Date de fin réelle — inclut déjà l'extension si présente. */
        LocalDate dateFin,
        String    numeroSerie,
        String    nomProduit,
        /** "LEGALE" si typeGarantie null, "EXTENSION" si typeGarantie non-null. */
        String    typeItem
) {}
