package fr.micromania.controller;

import fr.micromania.dto.employe.CreateEmployeRequest;
import fr.micromania.dto.employe.EmployeResponse;
import fr.micromania.dto.employe.UpdateEmployeRequest;
import fr.micromania.service.EmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class EmployeController {

    private final EmployeService employeService;

    @GetMapping
    public ResponseEntity<List<EmployeResponse>> lister(
            @RequestParam(required = false) Long   magasinId,
            @RequestParam(required = false) String q) {

        return ResponseEntity.ok(employeService.lister(magasinId, q));
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeResponse> me(@AuthenticationPrincipal Long id) {
        return ResponseEntity.ok(employeService.me(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<EmployeResponse> creer(@Valid @RequestBody CreateEmployeRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(employeService.creer(req));
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<EmployeResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeRequest req) {

        try {
            return ResponseEntity.ok(employeService.modifier(id, req));
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        employeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
