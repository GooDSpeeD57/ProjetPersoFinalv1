package fr.micromania.dto.reprise;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;

@Builder
public record CreateRepriseRequest(
    Long idClient,

    @NotNull
    Long idMagasin,

    @NotNull
    Long idModeCompensation,

    @NotEmpty @Valid
    List<CreateRepriseeLigneRequest> lignes,

    String commentaire
) {}
