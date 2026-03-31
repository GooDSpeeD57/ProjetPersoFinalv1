package fr.micromania.dto.sav;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateDossierSavRequest(
    @NotNull
    Long idVenteUnite,

    Long idGarantie,
    Long idEmploye,

    @NotBlank
    String panneDeclaree
) {}
