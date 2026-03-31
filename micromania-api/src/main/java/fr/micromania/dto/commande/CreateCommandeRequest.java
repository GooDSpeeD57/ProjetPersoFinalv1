package fr.micromania.dto.commande;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateCommandeRequest(
    @NotNull
    Long idPanier,

    @NotNull
    Long idModeLivraison,

    Long idAdresseLivraison,
    Long idMagasinRetrait,

    @Size(max = 50)
    String codePromo,

    Long idModePaiement,

    String commentaireClient
) {}
