package fr.micromania.dto.garantie;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ExtensionGarantieRequest(
        @NotNull Long idTypeGarantie,
        @NotNull LocalDate dateFinEtendue
) {}
