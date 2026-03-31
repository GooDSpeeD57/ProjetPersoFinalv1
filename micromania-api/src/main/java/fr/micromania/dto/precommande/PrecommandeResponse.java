package fr.micromania.dto.precommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PrecommandeResponse(
    Long id,
    String referencePrecommande,
    String statut,
    String canalVente,
    String modePaiement,
    String clientPseudo,
    BigDecimal acomptePaye,
    BigDecimal montantTotalEstime,
    LocalDateTime datePrecommande,
    LocalDateTime dateDisponibiliteEstimee,
    LocalDateTime dateConversionCommande,
    String commentaireClient,
    List<PrecommandeLigneResponse> lignes
) {}
