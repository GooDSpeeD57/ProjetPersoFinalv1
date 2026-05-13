package fr.micromania.controller;

import fr.micromania.dto.entrepot.EntrepotResponse;
import fr.micromania.service.EntrepotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrepots")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class EntrepotController {

    private final EntrepotService entrepotService;

    @GetMapping
    public ResponseEntity<List<EntrepotResponse>> lister() {
        return ResponseEntity.ok(entrepotService.lister());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrepotService.getById(id));
    }
}
