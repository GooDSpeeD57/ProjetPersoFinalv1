package fr.micromania.dto.catalog;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreateVariantRequest(
    @NotNull
    Long idProduit,

    @NotBlank @Size(max = 100)
    String sku,

    @Size(max = 50)
    String ean,

    Long idPlateforme,

    @NotNull
    Long idFormatProduit,

    @NotNull
    Long idStatutProduit,

    @NotBlank @Size(max = 255)
    String nomCommercial,

    @Size(max = 100)
    String edition,

    @Size(max = 100)
    String couleur,

    String langueVente,

    boolean scelle,
    boolean estDemat,
    boolean estTcgUnitaire,
    boolean estReprise,

    Long idTauxTva,

    @DecimalMin("0.01")
    BigDecimal prixWeb,

    @DecimalMin("0.01")
    BigDecimal prixMagasin
) {}
