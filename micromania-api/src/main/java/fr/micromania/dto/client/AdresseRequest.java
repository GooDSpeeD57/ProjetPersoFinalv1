package fr.micromania.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AdresseRequest(
    @NotNull
    Long idTypeAdresse,

    @NotBlank @Size(max = 255)
    String rue,

    @Size(max = 255)
    String complement,

    @NotBlank @Size(max = 100)
    String ville,

    @NotBlank @Size(max = 15)
    String codePostal,

    @Size(max = 100)
    String pays,

    boolean estDefaut
) {}
