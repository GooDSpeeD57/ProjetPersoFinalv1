package com.monprojet.boutiquejeux.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InscriptionForm {
    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 100)
    private String prenom;

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Lettres, chiffres, _ ou -")
    private String pseudo;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String motDePasse;

    @NotBlank
    @Pattern(
        regexp = "^(?:0[67]\\d{8}|\\+33[67]\\d{8})$",
        message = "Numéro mobile invalide : 06, 07, +336 ou +337"
    )
    private String telephone;

    @NotNull
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "Obligatoire")
    @AssertTrue(message = "Vous devez accepter les conditions RGPD")
    private Boolean rgpdConsent;
}
