package fr.micromania.dto.planning;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlanningEntryResponse(
    Long      id,
    Long      employeId,
    String    nomEmploye,
    String    role,
    LocalDate dateTravail,
    LocalTime heureDebut,
    LocalTime heureFin,
    String    statut,
    String    noteInterne
) {}
