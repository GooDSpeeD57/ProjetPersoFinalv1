package fr.micromania.dto.employe;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateEmployeRequest(
    @NotBlank @Size(max = 100) String nom,
    @NotBlank @Size(max = 100) String prenom,
    @NotBlank @Email  @Size(max = 150) String email,
    @Size(max = 20) String telephone,
    @NotBlank @Size(min = 8, max = 255) String motDePasse,
    @NotNull Long idRole,
    @NotNull Long idMagasin,
    LocalDate dateEmbauche
) {}
