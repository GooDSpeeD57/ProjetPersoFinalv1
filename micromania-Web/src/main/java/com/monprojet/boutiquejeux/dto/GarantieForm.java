package com.monprojet.boutiquejeux.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class GarantieForm {
    @NotBlank private String numeroSerie;
    @NotNull  private Long produitId;
    @NotNull  private Long factureId;
    @NotNull  private Integer dureeMois;
}
