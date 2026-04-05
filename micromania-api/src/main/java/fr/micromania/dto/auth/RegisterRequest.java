package fr.micromania.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank
    @Size(min = 3, max = 50)
    String pseudo,

    @NotBlank
    @Size(max = 100)
    String nom,

    @NotBlank
    @Size(max = 100)
    String prenom,

    @NotNull
    @Past
    LocalDate dateNaissance,

    @NotBlank
    @Email
    @Size(max = 150)
    String email,

    @NotBlank
    @Pattern(
        regexp = "^(?:0[67]\\d{8}|\\+33[67]\\d{8})$",
        message = "Le téléphone doit être un mobile français valide : 06, 07, +336 ou +337"
    )
    String telephone,

    @NotBlank
    @Size(min = 8, max = 128)
    String motDePasse,

    @NotNull
    Boolean rgpdConsent,

    Long idAvatar
) {}
