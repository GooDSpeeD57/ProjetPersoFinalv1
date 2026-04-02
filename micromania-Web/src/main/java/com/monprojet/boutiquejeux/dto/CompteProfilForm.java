package com.monprojet.boutiquejeux.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompteProfilForm {

    @Size(min = 3, max = 50, message = "Le pseudo doit contenir entre 3 et 50 caractères")
    private String pseudo;

    @Size(max = 100, message = "Le nom est trop long")
    private String nom;

    @Size(max = 100, message = "Le prénom est trop long")
    private String prenom;

    @Email(message = "Adresse email invalide")
    @Size(max = 150, message = "L'email est trop long")
    private String email;

    @Pattern(regexp = "^0[0-9]{9}$", message = "Le téléphone doit être au format 0XXXXXXXXX")
    private String telephone;
}
