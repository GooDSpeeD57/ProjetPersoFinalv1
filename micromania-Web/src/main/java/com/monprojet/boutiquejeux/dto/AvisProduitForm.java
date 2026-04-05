package com.monprojet.boutiquejeux.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AvisProduitForm {

    @NotNull(message = "Choisissez une note entre 1 et 5")
    @Min(value = 1, message = "Choisissez une note entre 1 et 5")
    @Max(value = 5, message = "Choisissez une note entre 1 et 5")
    private Integer note;

    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    private String commentaire;
}
