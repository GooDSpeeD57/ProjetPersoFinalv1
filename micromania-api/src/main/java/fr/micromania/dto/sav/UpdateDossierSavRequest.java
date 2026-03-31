package fr.micromania.dto.sav;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateDossierSavRequest(
    String diagnostic,
    String solutionApportee,

    @NotBlank
    String codeStatut,

    Long idEmploye
) {}
