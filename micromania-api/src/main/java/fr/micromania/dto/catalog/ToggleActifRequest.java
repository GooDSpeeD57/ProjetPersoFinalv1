package fr.micromania.dto.catalog;

/** Corps du PATCH /variants/{id}/actif */
public record ToggleActifRequest(boolean actif) {}
