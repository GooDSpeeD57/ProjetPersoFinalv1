package fr.micromania.controller;

import fr.micromania.entity.Entrepot;
import fr.micromania.repository.EntrepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/entrepots")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class EntrepotController {

    private final EntrepotRepository entrepotRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> lister() {
        return ResponseEntity.ok(
                entrepotRepository.findAll().stream().map(this::toMap).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Entrepot e = entrepotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrepôt introuvable"));
        return ResponseEntity.ok(toMap(e));
    }

    private Map<String, Object> toMap(Entrepot e) {
        return Map.of(
                "id",          e.getId(),
                "nom",         e.getNom() != null ? e.getNom() : "",
                "code",        e.getCode() != null ? e.getCode() : "",
                "telephone",   e.getTelephone() != null ? e.getTelephone() : "",
                "email",       e.getEmail() != null ? e.getEmail() : "",
                "responsable", e.getResponsable() != null ? e.getResponsable() : "",
                "actif",       e.isActif()
        );
    }
}
