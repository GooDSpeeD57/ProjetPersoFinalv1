package fr.micromania.dto.retour;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRetourRequest(

    @NotNull
    Long idFacture,

    @NotNull
    Long idTypeRetour,

    @Size(max = 255)
    String motifRetour,

    @NotNull
    @NotEmpty
    @Valid
    List<RetourLigneRequest> lignes
) {}
