package fr.micromania.dto.sav;

import java.time.LocalDateTime;

public record DossierSavResponse(
    Long id,
    String referenceSav,
    String statut,
    Long idVenteUnite,
    String numeroSerie,
    String nomCommercial,
    String employe,
    String panneDeclaree,
    String diagnostic,
    String solutionApportee,
    LocalDateTime dateOuverture,
    LocalDateTime dateCloture,
    boolean sousGarantie
) {}
