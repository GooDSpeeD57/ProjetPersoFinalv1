package fr.micromania.dto.client;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateClientRequest(
    @Size(min = 3, max = 50)
    String pseudo,

    @Size(max = 100)
    String nom,

    @Size(max = 100)
    String prenom,

    @Past
    LocalDate dateNaissance,

    @Email @Size(max = 150)
    String email,

    @Pattern(regexp = "^0[0-9]{9}$")
    String telephone,

    Long idAvatar
) {}
