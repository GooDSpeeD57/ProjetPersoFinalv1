package fr.micromania.dto.employe;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeResponse(
    Long    id,
    String  nom,
    String  prenom,
    String  email,
    String  telephone,
    String  role,           // code du rôle ex: ROLE_VENDEUR
    String  roleLibelle,
    Long    magasinId,
    String  magasinNom,
    LocalDate    dateEmbauche,
    boolean actif,
    LocalDateTime dateCreation
) {}
