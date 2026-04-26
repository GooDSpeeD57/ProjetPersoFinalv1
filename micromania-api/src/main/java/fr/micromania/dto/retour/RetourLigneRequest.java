package fr.micromania.dto.retour;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RetourLigneRequest(

    @NotNull
    Long idLigneFacture,

    @Min(1)
    int quantite,

    @Size(max = 255)
    String motif
) {}
