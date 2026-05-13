package fr.micromania.service.impl;

import fr.micromania.dto.planning.CreatePlanningRequest;
import fr.micromania.dto.planning.PlanningEntryResponse;
import fr.micromania.dto.planning.PlanningRowResponse;
import fr.micromania.entity.Employe;
import fr.micromania.entity.PlanningEmploye;
import fr.micromania.entity.referentiel.StatutPlanning;
import fr.micromania.repository.EmployeRepository;
import fr.micromania.repository.PlanningEmployeRepository;
import fr.micromania.repository.StatutPlanningRepository;
import fr.micromania.service.PlanningService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanningServiceImpl implements PlanningService {

    private final PlanningEmployeRepository planningRepository;
    private final EmployeRepository         employeRepository;
    private final StatutPlanningRepository  statutPlanningRepository;

    @Override
    public Map<String, Object> getSemaine(Long magasinId, String semaine) {
        LocalDate refDate  = (semaine != null && !semaine.isBlank()) ? LocalDate.parse(semaine) : LocalDate.now();
        LocalDate lundi    = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate dimanche = lundi.plusDays(6);

        List<PlanningEmploye> entries = planningRepository.findSemaine(magasinId, lundi, dimanche);

        Map<Long, PlanningRowResponse> rowMap = new LinkedHashMap<>();
        for (PlanningEmploye p : entries) {
            Employe e = p.getEmploye();
            rowMap.computeIfAbsent(e.getId(), id -> new PlanningRowResponse(
                    e.getId(),
                    e.getPrenom() + " " + e.getNom(),
                    e.getRole().getLibelle(),
                    "REPOS", "REPOS", "REPOS", "REPOS", "REPOS", "REPOS", "REPOS"
            ));
            String creneau = p.getHeureDebut() + "-" + p.getHeureFin();
            DayOfWeek jour = p.getDateTravail().getDayOfWeek();
            PlanningRowResponse row = rowMap.get(e.getId());
            rowMap.put(e.getId(), switch (jour) {
                case MONDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), creneau,      row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case TUESDAY   -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  creneau,        row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case WEDNESDAY -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  row.mardi(),    creneau,        row.jeudi(),    row.vendredi(), row.samedi(),   row.dimanche());
                case THURSDAY  -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  row.mardi(),    row.mercredi(), creneau,        row.vendredi(), row.samedi(),   row.dimanche());
                case FRIDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  row.mardi(),    row.mercredi(), row.jeudi(),    creneau,        row.samedi(),   row.dimanche());
                case SATURDAY  -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), creneau,        row.dimanche());
                case SUNDAY    -> new PlanningRowResponse(row.employeId(), row.nomEmploye(), row.role(), row.lundi(),  row.mardi(),    row.mercredi(), row.jeudi(),    row.vendredi(), row.samedi(),   creneau);
            });
        }

        List<PlanningRowResponse> rows = new ArrayList<>(rowMap.values());
        return Map.of(
                "semaineDu",     lundi.toString(),
                "semaineAu",     dimanche.toString(),
                "content",       rows,
                "totalElements", rows.size()
        );
    }

    @Override
    public List<PlanningEntryResponse> getEntries(Long magasinId, String semaine) {
        LocalDate refDate  = (semaine != null && !semaine.isBlank()) ? LocalDate.parse(semaine) : LocalDate.now();
        LocalDate lundi    = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate dimanche = lundi.plusDays(6);
        return planningRepository.findSemaine(magasinId, lundi, dimanche)
                .stream().map(this::toEntryResponse).toList();
    }

    @Override
    @Transactional
    public PlanningEntryResponse creer(CreatePlanningRequest req) {
        Employe employe = employeRepository.findByIdAndDeletedFalse(req.idEmploye())
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + req.idEmploye()));

        StatutPlanning statut;
        if (req.idStatutPlanning() != null) {
            statut = statutPlanningRepository.findById(req.idStatutPlanning())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Statut planning introuvable : " + req.idStatutPlanning()));
        } else {
            statut = statutPlanningRepository.findByCode("PLANIFIE")
                    .or(() -> statutPlanningRepository.findAll().stream().findFirst())
                    .orElseThrow(() -> new IllegalStateException("Aucun statut planning configuré"));
        }

        PlanningEmploye planning = PlanningEmploye.builder()
                .employe(employe)
                .statutPlanning(statut)
                .dateTravail(req.dateTravail())
                .heureDebut(req.heureDebut())
                .heureFin(req.heureFin())
                .noteInterne(req.noteInterne())
                .build();
        return toEntryResponse(planningRepository.save(planning));
    }

    @Override
    @Transactional
    public PlanningEntryResponse modifier(Long id, Map<String, Object> body) {
        PlanningEmploye p = planningRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrée planning introuvable : " + id));

        if (body.containsKey("heureDebut"))  p.setHeureDebut(LocalTime.parse((String) body.get("heureDebut")));
        if (body.containsKey("heureFin"))    p.setHeureFin(LocalTime.parse((String) body.get("heureFin")));
        if (body.containsKey("dateTravail")) p.setDateTravail(LocalDate.parse((String) body.get("dateTravail")));
        if (body.containsKey("noteInterne")) p.setNoteInterne((String) body.get("noteInterne"));

        return toEntryResponse(planningRepository.save(p));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!planningRepository.existsById(id)) {
            throw new EntityNotFoundException("Entrée planning introuvable : " + id);
        }
        planningRepository.deleteById(id);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

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
