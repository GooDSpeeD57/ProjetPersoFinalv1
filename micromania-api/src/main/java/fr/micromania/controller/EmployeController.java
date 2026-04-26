package fr.micromania.controller;

import fr.micromania.dto.employe.CreateEmployeRequest;
import fr.micromania.dto.employe.EmployeResponse;
import fr.micromania.dto.employe.UpdateEmployeRequest;
import fr.micromania.entity.Employe;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.referentiel.Role;
import fr.micromania.repository.EmployeRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class EmployeController {

    private final EmployeRepository    employeRepository;
    private final RoleRepository       roleRepository;
    private final MagasinRepository    magasinRepository;
    private final PasswordEncoder      passwordEncoder;

    // ── GET liste ────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<EmployeResponse>> lister(
            @RequestParam(required = false) Long   magasinId,
            @RequestParam(required = false) String q) {

        List<Employe> employes;

        if (q != null && !q.isBlank()) {
            // recherche textuelle (tous magasins ou filtré après)
            employes = employeRepository.search(q,
                    org.springframework.data.domain.Pageable.unpaged()).getContent();
            if (magasinId != null) {
                Long id = magasinId;
                employes = employes.stream()
                    .filter(e -> id.equals(e.getMagasin().getId()))
                    .toList();
            }
        } else if (magasinId != null) {
            employes = employeRepository.findByMagasinIdAndDeletedFalse(magasinId);
        } else {
            employes = employeRepository.findAll().stream()
                .filter(e -> !e.isDeleted())
                .toList();
        }

        return ResponseEntity.ok(employes.stream().map(this::toResponse).toList());
    }

    // ── GET moi ──────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<EmployeResponse> me(@AuthenticationPrincipal Long id) {
        Employe e = employeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé introuvable"));
        return ResponseEntity.ok(toResponse(e));
    }

    // ── GET un ───────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<EmployeResponse> getById(@PathVariable Long id) {
        Employe e = employeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé introuvable"));
        return ResponseEntity.ok(toResponse(e));
    }

    // ── POST créer ───────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<EmployeResponse> creer(@Valid @RequestBody CreateEmployeRequest req) {

        if (employeRepository.existsByEmailAndDeletedFalse(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }

        Role role = roleRepository.findById(req.idRole())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle introuvable"));

        Magasin magasin = magasinRepository.findById(req.idMagasin())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Magasin introuvable"));

        Employe employe = Employe.builder()
            .nom(req.nom())
            .prenom(req.prenom())
            .email(req.email())
            .telephone(req.telephone())
            .motDePasse(passwordEncoder.encode(req.motDePasse()))
            .role(role)
            .magasin(magasin)
            .dateEmbauche(req.dateEmbauche())
            .actif(true)
            .deleted(false)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(employeRepository.save(employe)));
    }

    // ── PATCH modifier ───────────────────────────────────────────

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<EmployeResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeRequest req) {

        Employe employe = employeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé introuvable"));

        if (req.nom()       != null) employe.setNom(req.nom());
        if (req.prenom()    != null) employe.setPrenom(req.prenom());
        if (req.telephone() != null) employe.setTelephone(req.telephone());
        if (req.dateEmbauche() != null) employe.setDateEmbauche(req.dateEmbauche());
        if (req.actif()     != null) employe.setActif(req.actif());

        if (req.email() != null && !req.email().equals(employe.getEmail())) {
            if (employeRepository.existsByEmailAndDeletedFalse(req.email())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
            }
            employe.setEmail(req.email());
        }

        if (req.motDePasse() != null && !req.motDePasse().isBlank()) {
            employe.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        }

        if (req.idRole() != null) {
            Role role = roleRepository.findById(req.idRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle introuvable"));
            employe.setRole(role);
        }

        if (req.idMagasin() != null) {
            Magasin magasin = magasinRepository.findById(req.idMagasin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Magasin introuvable"));
            employe.setMagasin(magasin);
        }

        return ResponseEntity.ok(toResponse(employeRepository.save(employe)));
    }

    // ── DELETE (soft) ─────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé introuvable");
        }
        employeRepository.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Mapper ───────────────────────────────────────────────────

    private EmployeResponse toResponse(Employe e) {
        return new EmployeResponse(
            e.getId(),
            e.getNom(),
            e.getPrenom(),
            e.getEmail(),
            e.getTelephone(),
            e.getRole().getCode(),
            e.getRole().getLibelle(),
            e.getMagasin().getId(),
            e.getMagasin().getNom(),
            e.getDateEmbauche(),
            e.isActif(),
            e.getDateCreation()
        );
    }
}
