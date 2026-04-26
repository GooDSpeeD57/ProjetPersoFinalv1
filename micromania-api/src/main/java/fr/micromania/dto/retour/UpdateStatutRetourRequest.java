package fr.micromania.dto.retour;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateStatutRetourRequest(

    @NotBlank
    String codeStatut,

    @DecimalMin("0.00")
    BigDecimal montantRembourse
) {}
