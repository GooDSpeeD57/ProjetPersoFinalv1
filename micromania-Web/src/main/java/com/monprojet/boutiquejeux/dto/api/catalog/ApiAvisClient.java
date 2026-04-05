package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiAvisClient(
        Long id,
        Long idProduit,
        byte note,
        String commentaire,
        String statut,
        String motifModeration,
        LocalDateTime dateCreation,
        LocalDateTime dateModification,
        LocalDateTime dateModeration
) {}
