package com.monprojet.boutiquejeux.dto.api.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiAvisProduit(
        Long id,
        String auteur,
        byte note,
        String commentaire,
        LocalDateTime dateCreation
) {}
