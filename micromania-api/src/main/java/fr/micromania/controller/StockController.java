package fr.micromania.controller;

import fr.micromania.dto.stock.*;
import fr.micromania.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class StockController {

    private final StockService stockService;

    @GetMapping("/magasin/variant/{idVariant}")
    public ResponseEntity<List<StockMagasinResponse>> getStockVariantTousMagasins(
            @PathVariable Long idVariant) {

        return ResponseEntity.ok(stockService.getStockVariantTousMagasins(idVariant));
    }

    @GetMapping("/magasin/{idMagasin}/variant/{idVariant}")
    public ResponseEntity<StockMagasinResponse> getStockMagasin(
            @PathVariable Long idVariant,
            @PathVariable Long idMagasin) {

        return ResponseEntity.ok(stockService.getStockVariantMagasin(idVariant, idMagasin));
    }

    @GetMapping("/entrepot/variant/{idVariant}")
    public ResponseEntity<List<StockEntrepotResponse>> getStockEntrepot(
            @PathVariable Long idVariant) {

        return ResponseEntity.ok(stockService.getStockVariantTousEntrepots(idVariant));
    }

    @GetMapping("/magasin/{idMagasin}/rupture")
    public ResponseEntity<List<StockMagasinResponse>> getRupture(
            @PathVariable Long idMagasin) {

        return ResponseEntity.ok(stockService.getRuptureMagasin(idMagasin));
    }

    @PostMapping("/ajustement/magasin")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<StockMagasinResponse> ajusterMagasin(
            @Valid @RequestBody AjustementStockRequest request) {

        return ResponseEntity.ok(stockService.ajusterStockMagasin(request));
    }

    @PostMapping("/ajustement/entrepot")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<StockEntrepotResponse> ajusterEntrepot(
            @Valid @RequestBody AjustementStockRequest request) {

        return ResponseEntity.ok(stockService.ajusterStockEntrepot(request));
    }

    @GetMapping("/mouvements")
    public ResponseEntity<List<MouvementStockResponse>> getMouvements(
            @RequestParam(required = false) Long idVariant,
            @RequestParam(required = false) Long idMagasin,
            @PageableDefault(size = 50) Pageable pageable) {

        return ResponseEntity.ok(stockService.getMouvements(idVariant, idMagasin, pageable));
    }
}
