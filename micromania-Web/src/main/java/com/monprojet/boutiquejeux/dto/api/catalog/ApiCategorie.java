package com.monprojet.boutiquejeux.dto.api.catalog;

public record ApiCategorie(
        Long id,
        String nom,
        String description,
        String typeCategorie,
        boolean actif
) {}
