package fr.micromania.controller;

import fr.micromania.dto.bibliotheque.BibliothequeResponse;
import fr.micromania.repository.BibliothequeClientRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private final BibliothequeClientRepository bibliothequeClientRepository;

    @GetMapping
    public ResponseEntity<List<BibliothequeResponse>> getBibliotheque(@PathVariable Long idClient) {
        List<BibliothequeResponse> result = bibliothequeClientRepository.findByClientId(idClient).stream()
                .map(b -> new BibliothequeResponse(
                        b.getId(),
                        b.getVariant() != null ? b.getVariant().getId() : null,
                        b.getVariant() != null ? b.getVariant().getNomCommercial() : null,
                        b.getVariant() != null ? b.getVariant().getSku() : null,
                        b.getFacture() != null ? b.getFacture().getId() : null,
                        b.getCleProduit() != null ? b.getCleProduit().getCleActivation() : null,
                        b.getDateAttribution()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }
}
