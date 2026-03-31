package fr.micromania.dto.promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
    Long id,
    String codePromo,
    String description,
    String typeReduction,
    BigDecimal valeur,
    LocalDateTime dateDebut,
    LocalDateTime dateFin,
    BigDecimal montantMinimumCommande,
    Integer nbUtilisationsMax,
    Integer nbUtilisationsActuel,
    boolean cumulable,
    boolean actif
) {}
