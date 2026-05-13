package fr.micromania.controller;

import fr.micromania.entity.referentiel.Role;
import fr.micromania.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> lister() {
        List<Map<String, Object>> roles = roleRepository.findAll().stream()
                .map(r -> Map.<String, Object>of(
                        "id",      r.getId(),
                        "code",    r.getCode(),
                        "libelle", r.getLibelle()))
                .toList();
        return ResponseEntity.ok(roles);
    }
}
