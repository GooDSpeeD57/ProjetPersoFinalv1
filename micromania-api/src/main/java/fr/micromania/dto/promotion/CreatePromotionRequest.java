package fr.micromania.dto.promotion;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreatePromotionRequest(
    @NotBlank @Size(max = 50)
    String codePromo,

    String description,

    @NotNull
    Long idTypeReduction,

    @NotNull @DecimalMin("0.01")
    BigDecimal valeur,

    @NotNull
    LocalDateTime dateDebut,

    @NotNull
    LocalDateTime dateFin,

    @DecimalMin("0.00")
    BigDecimal montantMinimumCommande,

    @Min(1)
    Integer nbUtilisationsMax,

    @Min(1)
    Integer nbUtilisationsMaxClient,

    boolean cumulable,

    List<Long> idVariants,
    List<Long> idCategories
) {}
