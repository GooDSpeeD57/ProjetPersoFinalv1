package fr.micromania.dto.planning;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreatePlanningRequest(
    @NotNull Long      idEmploye,
    @NotNull LocalDate dateTravail,
    @NotNull LocalTime heureDebut,
    @NotNull LocalTime heureFin,
    Long   idStatutPlanning,   // optionnel, défaut = PLANIFIE
    String noteInterne
) {}
