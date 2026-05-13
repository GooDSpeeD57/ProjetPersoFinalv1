package fr.micromania.controller;

import fr.micromania.dto.planning.CreatePlanningRequest;
import fr.micromania.dto.planning.PlanningEntryResponse;
import fr.micromania.service.PlanningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plannings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSemaine(
            @RequestParam Long magasinId,
            @RequestParam(required = false) String semaine) {

        return ResponseEntity.ok(planningService.getSemaine(magasinId, semaine));
    }

    @GetMapping("/entries")
    public ResponseEntity<List<PlanningEntryResponse>> getEntries(
            @RequestParam Long magasinId,
            @RequestParam(required = false) String semaine) {

        return ResponseEntity.ok(planningService.getEntries(magasinId, semaine));
    }

    @PostMapping
    public ResponseEntity<PlanningEntryResponse> creer(@Valid @RequestBody CreatePlanningRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planningService.creer(req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlanningEntryResponse> modifier(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        return ResponseEntity.ok(planningService.modifier(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        planningService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
