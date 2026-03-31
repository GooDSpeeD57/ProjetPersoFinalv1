package fr.micromania.dto.commande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponse(
    Long id,
    String referenceCommande,
    String statut,
    String canalVente,
    String modeLivraison,
    String modePaiement,
    String codePromo,
    BigDecimal sousTotal,
    BigDecimal montantRemise,
    BigDecimal fraisLivraison,
    BigDecimal montantTotal,
    String commentaireClient,
    LocalDateTime dateCommande,
    LocalDateTime datePaiement,
    LocalDateTime dateExpedition,
    LocalDateTime dateLivraisonPrevue,
    LocalDateTime dateLivraisonReelle,
    List<LigneCommandeResponse> lignes
) {}
