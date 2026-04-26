package fr.micromania.dto.garantie;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExtensionGarantieResponse(
        Long id,
        Long idGarantie,
        String codeTypeGarantie,
        LocalDateTime dateAchat,
        LocalDate dateFinEtendue
) {}
