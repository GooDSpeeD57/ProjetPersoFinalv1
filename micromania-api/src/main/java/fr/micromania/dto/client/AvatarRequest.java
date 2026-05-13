package fr.micromania.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AvatarRequest(
        @NotBlank @Size(max = 100) String  nom,
        @NotBlank @Size(max = 255) String  url,
        @Size(max = 255)           String  alt,
        boolean decorative,
        boolean actif
) {}
