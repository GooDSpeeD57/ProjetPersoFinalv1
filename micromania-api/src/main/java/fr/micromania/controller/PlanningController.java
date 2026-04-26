package fr.micromania.controller;

import fr.micromania.dto.planning.CreatePlanningRequest;
import fr.micromania.dto.planning.PlanningEntryResponse;
import fr.micromania.dto.planning.PlanningRowResponse;
import fr.micromania.entity.Employe;
import fr.micromania.entity.PlanningEmploye;
import fr.micromania.entity.referentiel.StatutPlanning;
import fr.micromania.repository.EmployeRepository;
import fr.micromania.repository.PlanningEmployeRepository;
import fr.micromania.repository.StatutPlanningRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@RequestMapping("/api/v1/plannings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class PlanningController {

    private final PlanningEmployeRepository planningRepository;
    private final EmployeRepository         employeRepository;
    private final StatutPlanningRepository  statutPlanningRepository;

    // ── GET vue semaine (tableau par employé) ─────────────────────

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSemaine(
            @RequestParam Long      magasinId,
            @RequestParam(required = false) String semaine) {   // date quelconque dans la semaine

        LocalDate refDate = (semaine != null && !semaine.isBlank())
            ? LocalDate.parse(semaine)
            : LocalDate.now();

        LocalDate lundi    = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate dimanche = lundi.plusDays(6);

        List<PlanningEmploye> entries = planningRepository.findSemaine(magasinId, lundi, dimanche);

        // Grouper par employé
        Map<Long, PlanningRowResponse> rowMap = new LinkedHashMap<>();
        for (PlanningEmploye p : entries) {
            Employe e = p.getEmploye();
            rowMap.computeIfAbsent(e.getId(), id -> new PlanningRowResponse(
                e.getId(),
                e.getPrenom() + " " + e.getNom(),
                e.getRole().getLibelle(),
                "REPOS", "REPOS", "REPOS", "REPOS", "REPOS", "REPOS", "REPOS"
            ));
            // Remplir le bon jour
            String creneau = p.getHeureDebut() + "-" + p.getHeureFin();
            DayOfWeek jour = p.getDateTravail().getDayOfWeek();
            PlanningRowResponse row = rowMap.get(e.getId());
            rowMap.put(e.getId(), switch (jour) {
                case MONDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), creneau,       row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case TUESDAY   -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   creneau,        row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case WEDNESDAY -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   row.mardi(),    creneau,        row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case THURSDAY  -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   row.mardi(),    row.mercredi(), creneau,        row.vendredi(), row.samedi(),   row.dimanche());
                case FRIDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   row.mardi(),    row.mercredi(), row.jeudi(),    creneau,        row.samedi(),   row.dimanche());
                case SATURDAY  -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), creneau,        row.dimanche());
                case SUNDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),   row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   creneau);
            });
        }

        List<PlanningRowResponse> rows = new ArrayList<>(rowMap.values());
        return ResponseEntity.ok(Map.of(
            "semaineDu",   lundi.toString(),
            "semaineAu",   dimanche.toString(),
            "content",     rows,
            "totalElements", rows.size()
        ));
    }

    // ── POST créer une entrée ─────────────────────────────────────

    @PostMapping
    public ResponseEntity<PlanningEntryResponse> creer(@Valid @RequestBody CreatePlanningRequest req) {

        Employe employe = employeRepository.findByIdAndDeletedFalse(req.idEmploye())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé introuvable"));

        // Statut par défaut = premier statut disponible ou celui fourni
        StatutPlanning statut;
        if (req.idStatutPlanning() != null) {
            statut = statutPlanningRepository.findById(req.idStatutPlanning())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut planning introuvable"));
        } else {
            statut = statutPlanningRepository.findByCode("PLANIFIE")
                .or(() -> statutPlanningRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Aucun statut planning configuré"));
        }

        PlanningEmploye planning = PlanningEmploye.builder()
            .employe(employe)
            .statutPlanning(statut)
            .dateTravail(req.dateTravail())
            .heureDebut(req.heureDebut())
            .heureFin(req.heureFin())
            .noteInterne(req.noteInterne())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toEntryResponse(planningRepository.save(planning)));
    }

    // ── PATCH modifier ────────────────────────────────────────────

    @PatchMapping("/{id}")
    public ResponseEntity<PlanningEntryResponse> modifier(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        PlanningEmploye p = planningRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée planning introuvable"));

        if (body.containsKey("heureDebut")) p.setHeureDebut(java.time.LocalTime.parse((String) body.get("heureDebut")));
        if (body.containsKey("heureFin"))   p.setHeureFin(java.time.LocalTime.parse((String) body.get("heureFin")));
        if (body.containsKey("dateTravail"))p.setDateTravail(LocalDate.parse((String) body.get("dateTravail")));
        if (body.containsKey("noteInterne"))p.setNoteInterne((String) body.get("noteInterne"));

        return ResponseEntity.ok(toEntryResponse(planningRepository.save(p)));
    }

    // ── DELETE ────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!planningRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée planning introuvable");
        }
        planningRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Mapper ────────────────────────────────────────────────────

    private PlanningEntryResponse toEntryResponse(PlanningEmploye p) {
        Employe e = p.getEmploye();
        return new PlanningEntryResponse(
            p.getId(),
            e.getId(),
            e.getPrenom() + " " + e.getNom(),
            e.getRole().getLibelle(),
            p.getDateTravail(),
            p.getHeureDebut(),
            p.getHeureFin(),
            p.getStatutPlanning().getCode(),
            p.getNoteInterne()
        );
    }
}
