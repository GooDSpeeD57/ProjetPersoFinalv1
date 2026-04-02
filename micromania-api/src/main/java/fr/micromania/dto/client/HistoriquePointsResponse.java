package fr.micromania.dto.client;

import java.time.LocalDateTime;

public record HistoriquePointsResponse(
    Long id,
    String typeOperation,
    int points,
    String commentaire,
    LocalDateTime dateOperation,
    Long idFacture
) {}
