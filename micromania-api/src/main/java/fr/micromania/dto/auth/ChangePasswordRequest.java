package fr.micromania.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequest(
    @NotBlank
    String ancienMotDePasse,

    @NotBlank @Size(min = 8, max = 128)
    String nouveauMotDePasse
) {}
