package fr.micromania.service;

import fr.micromania.dto.planning.CreatePlanningRequest;
import fr.micromania.dto.planning.PlanningEntryResponse;

import java.util.List;
import java.util.Map;

/**
 * Gestion du planning hebdomadaire des employés.
 */
public interface PlanningService {

    Map<String, Object> getSemaine(Long magasinId, String semaine);

    List<PlanningEntryResponse> getEntries(Long magasinId, String semaine);

    PlanningEntryResponse creer(CreatePlanningRequest request);

    PlanningEntryResponse modifier(Long id, Map<String, Object> body);

    void supprimer(Long id);
}
