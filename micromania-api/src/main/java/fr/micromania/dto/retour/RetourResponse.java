package fr.micromania.dto.retour;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RetourResponse(
    Long id,
    String referenceRetour,
    Long idFacture,
    Long idClient,
    String statutRetour,
    String typeRetour,
    String motifRetour,
    LocalDateTime dateDemande,
    LocalDateTime dateTraitement,
    BigDecimal montantRembourse,
    List<RetourLigneResponse> lignes
) {}
