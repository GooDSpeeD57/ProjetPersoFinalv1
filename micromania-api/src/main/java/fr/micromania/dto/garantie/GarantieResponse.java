package fr.micromania.dto.garantie;

import java.time.LocalDate;

public record GarantieResponse(
        Long id,
        Long idVenteUnite,
        String codeTypeGarantie,
        String descTypeGarantie,
        Integer dureeMois,
        LocalDate dateDebut,
        LocalDate dateFin,
        boolean estEtendue,
        LocalDate dateExtension
) {}
