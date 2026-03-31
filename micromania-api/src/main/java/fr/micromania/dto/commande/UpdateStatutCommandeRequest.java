package fr.micromania.dto.commande;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateStatutCommandeRequest(
    @NotBlank
    String codeStatut,

    String commentaire
) {}
