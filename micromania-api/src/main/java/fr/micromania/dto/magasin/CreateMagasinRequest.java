package fr.micromania.dto.magasin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMagasinRequest(
        @NotBlank @Size(max = 100) String nom,
        @Size(max = 20)            String telephone,
        @Size(max = 150)           String email
) {}
