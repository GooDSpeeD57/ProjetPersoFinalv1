package fr.micromania.controller;

import fr.micromania.dto.promotion.*;
import fr.micromania.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    /** Validation d'un code promo (accessible au client pour feedback immédiat) */
    @PostMapping("/valider")
    public ResponseEntity<ValidatePromoResponse> valider(
            @Valid @RequestBody ValidatePromoRequest request) {

        return ResponseEntity.ok(promotionService.valider(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Page<PromotionResponse>> getActives(
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(promotionService.getActives(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<PromotionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<PromotionResponse> creer(
            @Valid @RequestBody CreatePromotionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(promotionService.creer(request));
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> desactiver(@PathVariable Long id) {
        promotionService.desactiver(id);
        return ResponseEntity.noContent().build();
    }
}
