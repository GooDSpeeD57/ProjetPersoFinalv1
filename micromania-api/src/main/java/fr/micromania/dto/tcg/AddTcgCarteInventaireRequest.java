package fr.micromania.dto.tcg;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record AddTcgCarteInventaireRequest(
    @NotNull
    Long idTcgCarteReference,

    @NotNull
    Long idMagasin,

    @NotNull
    Long idEtatCarteTcg,

    @NotBlank
    String langue,

    boolean foil,
    boolean reverseFoil,
    boolean alternateArt,

    String gradation,

    @NotNull @DecimalMin("0.00")
    BigDecimal prixVente,

    BigDecimal prixAchat,

    @NotBlank
    String provenance
) {}
