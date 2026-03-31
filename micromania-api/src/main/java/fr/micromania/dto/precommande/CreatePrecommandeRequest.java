package fr.micromania.dto.precommande;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreatePrecommandeRequest(
    @NotNull
    Long idClient,

    @NotNull
    Long idCanalVente,

    Long idModePaiement,

    @NotEmpty @Valid
    List<PrecommandeLigneRequest> lignes,

    @DecimalMin("0.00")
    BigDecimal acompteAVerser,

    LocalDateTime dateDisponibiliteEstimee,

    String commentaireClient
) {}
