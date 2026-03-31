package fr.micromania.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50)
    String pseudo,

    @NotBlank @Size(max = 100)
    String nom,

    @NotBlank @Size(max = 100)
    String prenom,

    @NotNull @Past
    LocalDate dateNaissance,

    @NotBlank @Email @Size(max = 150)
    String email,

    @NotBlank @Pattern(regexp = "^0[0-9]{9}$", message = "Numéro de téléphone invalide")
    String telephone,

    @NotBlank @Size(min = 8, max = 128)
    String motDePasse,

    @NotNull
    Boolean rgpdConsent
) {}
