package fr.micromania.dto.employe;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateEmployeRequest(
    @Size(max = 100) String nom,
    @Size(max = 100) String prenom,
    @Email @Size(max = 150) String email,
    @Size(max = 20) String telephone,
    @Size(min = 8, max = 255) String motDePasse,   // null = ne pas changer
    Long idRole,
    Long idMagasin,
    LocalDate dateEmbauche,
    Boolean actif
) {}
