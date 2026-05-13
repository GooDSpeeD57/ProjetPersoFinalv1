package fr.micromania.controller;

import fr.micromania.dto.bibliotheque.BibliothequeResponse;
import fr.micromania.service.BibliothequeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients/{idClient}/bibliotheque")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class BibliothequeController {

    private final BibliothequeService bibliothequeService;

    @GetMapping
    public ResponseEntity<List<BibliothequeResponse>> getBibliotheque(@PathVariable Long idClient) {
        return ResponseEntity.ok(bibliothequeService.getByClientId(idClient));
    }
}
