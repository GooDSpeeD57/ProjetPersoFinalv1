package fr.micromania.dto.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateAvisProduitRequest(
    @Min(1) @Max(5)
    byte note,

    @Size(max = 2000)
    String commentaire
) {}
