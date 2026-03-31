package fr.micromania.dto.commande;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CommandeSummary(
    Long id,
    String referenceCommande,
    String statut,
    String modeLivraison,
    BigDecimal montantTotal,
    int nbArticles,
    LocalDateTime dateCommande
) {}
