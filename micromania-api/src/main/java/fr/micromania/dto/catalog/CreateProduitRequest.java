package fr.micromania.dto.catalog;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record CreateProduitRequest(
    @NotNull
    Long idCategorie,

    @NotBlank @Size(max = 255)
    String nom,

    @NotBlank @Size(max = 255)
    String slug,

    String description,

    @Size(max = 500)
    String resumeCourt,

    LocalDate dateSortie,

    @Size(max = 150)
    String editeur,

    @Size(max = 150)
    String constructeur,

    @Min(3) @Max(18)
    Integer pegi,

    @Size(max = 150)
    String marque,

    @Pattern(regexp = "NORMAL|PREMIUM|ULTIMATE")
    String niveauAccesMin,

    /** Code ISO-639-1 de la langue (ex : "fr", "en", "de"). Défaut : "fr". */
    @Size(max = 10)
    String langue,

    boolean misEnAvant,

    boolean estPreCommande
) {}
