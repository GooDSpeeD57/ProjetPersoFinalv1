package fr.micromania.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Statistiques pour le tableau de bord JavaFX de l'espace employé.
 * Toutes les stats sont calculées à la volée pour le magasin demandé.
 */
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
public class StatsController {

    @PersistenceContext
    private EntityManager em;

    private static final int SEUIL_STOCK_BAS = 5;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(
            @RequestParam Long magasinId) {

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour   = debutJour.plusDays(1);

        // ── Ventes du jour ───────────────────────────────────────
        Long ventesAujourdhui = em.createQuery("""
                SELECT COUNT(f) FROM fr.micromania.entity.commande.Facture f
                WHERE f.magasin.id = :magId
                  AND f.dateFacture >= :debut
                  AND f.dateFacture < :fin
                """, Long.class)
            .setParameter("magId", magasinId)
            .setParameter("debut", debutJour)
            .setParameter("fin",   finJour)
            .getSingleResult();

        // ── CA du jour ──────────────────────────────────────────
        BigDecimal caAujourdhui = em.createQuery("""
                SELECT COALESCE(SUM(f.montantFinal), 0)
                FROM fr.micromania.entity.commande.Facture f
                WHERE f.magasin.id = :magId
                  AND f.dateFacture >= :debut
                  AND f.dateFacture < :fin
                """, BigDecimal.class)
            .setParameter("magId", magasinId)
            .setParameter("debut", debutJour)
            .setParameter("fin",   finJour)
            .getSingleResult();

        // ── Reprises du jour ─────────────────────────────────────
        Long reprisesAujourdhui = em.createQuery("""
                SELECT COUNT(r) FROM fr.micromania.entity.Reprise r
                WHERE r.magasin.id = :magId
                  AND r.dateCreation >= :debut
                  AND r.dateCreation < :fin
                """, Long.class)
            .setParameter("magId", magasinId)
            .setParameter("debut", debutJour)
            .setParameter("fin",   finJour)
            .getSingleResult();

        // ── Stocks bas ──────────────────────────────────────────
        Long stocksBas = em.createQuery("""
                SELECT COUNT(s) FROM fr.micromania.entity.stock.StockMagasin s
                WHERE s.magasin.id = :magId
                  AND (s.quantiteNeuf + s.quantiteOccasion) <= :seuil
                """, Long.class)
            .setParameter("magId", magasinId)
            .setParameter("seuil", SEUIL_STOCK_BAS)
            .getSingleResult();

        // ── Total clients (tous magasins, non supprimés) ──────────
        Long totalClients = em.createQuery("""
                SELECT COUNT(c) FROM fr.micromania.entity.Client c
                WHERE c.deleted = false AND c.actif = true
                """, Long.class)
            .getSingleResult();

        // ── Réponse ──────────────────────────────────────────────
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("ventesAujourdhui",   ventesAujourdhui);
        stats.put("caAujourdhui",       caAujourdhui.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue());
        stats.put("reprisesAujourdhui", reprisesAujourdhui);
        stats.put("stocksBas",          stocksBas);
        stats.put("totalClients",       totalClients);
        stats.put("magasinId",          magasinId);

        return ResponseEntity.ok(stats);
    }
}
