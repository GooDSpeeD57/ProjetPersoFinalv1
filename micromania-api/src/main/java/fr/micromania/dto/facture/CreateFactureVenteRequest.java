package fr.micromania.dto.facture;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;

/** Création d'une facture de vente directe en magasin (sans commande web) */
@Builder
public record CreateFactureVenteRequest(
    Long idClient,
    String nomClient,
    String emailClient,
    String telephoneClient,

    @NotNull
    Long idMagasin,

    Long idEmploye,

    @NotNull
    Long idModePaiement,

    @NotNull
    Long idContexteVente,

    @NotEmpty @Valid
    List<LigneFactureRequest> lignes,

    String codePromo,
    Long idBonAchat
) {}
