package fr.micromania.controller;

import fr.micromania.dto.client.*;
import fr.micromania.dto.garantie.GarantieResponse;
import fr.micromania.entity.commande.LigneFacture;
import fr.micromania.entity.referentiel.TypeGarantie;
import fr.micromania.repository.GarantieRepository;
import fr.micromania.repository.LigneFactureRepository;
import fr.micromania.repository.TypeGarantieRepository;
import fr.micromania.service.AdresseService;
import fr.micromania.service.ClientService;
import fr.micromania.service.FideliteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService          clientService;
    private final AdresseService         adresseService;
    private final FideliteService        fideliteService;
    private final GarantieRepository     garantieRepository;
    private final LigneFactureRepository ligneFactureRepository;
    private final TypeGarantieRepository typeGarantieRepository;

    // ── Identification sécurisée par employé ──────────────────

    @GetMapping("/identifier")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<ClientResponse> identifier(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dateNaissance) {

        return ResponseEntity.ok(clientService.identifierParIdentite(nom, prenom, dateNaissance));
    }

    @GetMapping("/{id}/bons-achat")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<java.util.List<BonAchatResponse>> getBonsAchatClient(
            @PathVariable Long id) {

        return ResponseEntity.ok(fideliteService.getBonsAchat(id));
    }

    // ── Back-office (MANAGER / ADMIN) ─────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<Page<ClientSummary>> search(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(clientService.search(q, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN')")
    public ResponseEntity<ClientResponse> creerParEmploye(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal Long idEmploye) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(clientService.creerParEmploye(request, idEmploye));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEUR','MANAGER','ADMIN') or #id == authentication.principal")
    public ResponseEntity<ClientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    // ── Profil (client connecté) ───────────────────────────────

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponse> getMe(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(clientService.getById(idClient));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponse> updateMe(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody UpdateClientRequest request) {

        return ResponseEntity.ok(clientService.update(idClient, request));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> demanderSuppression(
            @AuthenticationPrincipal Long idClient) {

        clientService.demanderSuppression(idClient);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/points")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PointsFideliteResponse> getPoints(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(clientService.getPointsFidelite(idClient));
    }

    @GetMapping("/me/fidelite")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<FideliteDetailResponse> getFidelite(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(fideliteService.getDetail(idClient));
    }

    @GetMapping("/me/bons-achat")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<BonAchatResponse>> getBonsAchat(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(fideliteService.getBonsAchat(idClient));
    }

    @GetMapping("/me/historique-points")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<HistoriquePointsResponse>> getHistoriquePoints(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(fideliteService.getHistorique(idClient));
    }

    @PutMapping("/me/magasin-favori/{idMagasin}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponse> setMagasinFavori(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idMagasin) {
        return ResponseEntity.ok(clientService.setMagasinFavori(idClient, idMagasin));
    }

    @DeleteMapping("/me/magasin-favori")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> removeMagasinFavori(
            @AuthenticationPrincipal Long idClient) {
        clientService.removeMagasinFavori(idClient);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/verifier-email")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> simulerVerificationEmail(
            @AuthenticationPrincipal Long idClient) {
        clientService.simulerVerificationEmail(idClient);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/verifier-telephone")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> simulerVerificationTelephone(
            @AuthenticationPrincipal Long idClient) {
        clientService.simulerVerificationTelephone(idClient);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/ultimate/subscribe")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponse> souscrireUltimate(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(clientService.souscrireUltimate(idClient));
    }

    // ── Adresses ───────────────────────────────────────────────

    @GetMapping("/me/adresses")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<AdresseResponse>> getAdresses(
            @AuthenticationPrincipal Long idClient) {

        return ResponseEntity.ok(adresseService.getByClient(idClient));
    }

    @PostMapping("/me/adresses")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AdresseResponse> addAdresse(
            @AuthenticationPrincipal Long idClient,
            @Valid @RequestBody AdresseRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(adresseService.ajouter(idClient, request));
    }

    @PutMapping("/me/adresses/{idAdresse}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AdresseResponse> updateAdresse(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idAdresse,
            @Valid @RequestBody AdresseRequest request) {

        return ResponseEntity.ok(adresseService.modifier(idAdresse, idClient, request));
    }

    @DeleteMapping("/me/adresses/{idAdresse}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> deleteAdresse(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idAdresse) {

        adresseService.supprimer(idAdresse, idClient);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/adresses/{idAdresse}/defaut")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> setAdresseDefaut(
            @AuthenticationPrincipal Long idClient,
            @PathVariable Long idAdresse) {

        adresseService.setDefaut(idAdresse, idClient);
        return ResponseEntity.noContent().build();
    }

    /**
     * Garanties du client connecté.
     * Combine deux sources :
     *   1. Les entités Garantie classiques (table garantie via venteUnite)
     *   2. Les lignes de facture avec garantieLabel non null (achat avec option garantie)
     */
    @GetMapping("/me/garanties")
    @PreAuthorize("hasRole('CLIENT')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<GarantieResponse>> getMesGaranties(
            @AuthenticationPrincipal Long idClient) {

        // Source 1 : entités Garantie réelles
        List<GarantieResponse> fromGaranties = garantieRepository.findByClientId(idClient).stream()
                .map(g -> new GarantieResponse(
                        g.getId(),
                        g.getVenteUnite() != null ? g.getVenteUnite().getId() : null,
                        g.getTypeGarantie() != null ? g.getTypeGarantie().getCode() : null,
                        g.getTypeGarantie() != null ? g.getTypeGarantie().getDescription() : null,
                        g.getTypeGarantie() != null ? g.getTypeGarantie().getDureeMois() : null,
                        g.getDateDebut(),
                        g.getDateFin(),
                        g.isEstEtendue(),
                        g.getDateExtension()))
                .toList();

        // Source 2 : lignes de facture avec garantieLabel (achat web ou JavaFX)
        List<LigneFacture> lignes = ligneFactureRepository.findByClientIdWithGarantieLabel(idClient);
        List<TypeGarantie> allTypes = typeGarantieRepository.findAllByOrderByCodeAsc();

        List<GarantieResponse> fromLignes = lignes.stream()
                .map(lf -> {
                    String label = lf.getGarantieLabel();
                    // Résoudre le TypeGarantie par description ou code
                    TypeGarantie matched = allTypes.stream()
                            .filter(tg -> label != null &&
                                    (label.equalsIgnoreCase(tg.getDescription()) || label.equalsIgnoreCase(tg.getCode())))
                            .findFirst().orElse(null);

                    LocalDate dateDebut = (lf.getFacture() != null && lf.getFacture().getDateFacture() != null)
                            ? lf.getFacture().getDateFacture().toLocalDate() : LocalDate.now();
                    Integer dureeMois = matched != null ? matched.getDureeMois() : null;
                    LocalDate dateFin = (dureeMois != null) ? dateDebut.plusMonths(dureeMois) : null;

                    return new GarantieResponse(
                            null,
                            null,
                            matched != null ? matched.getCode() : null,
                            label,
                            dureeMois,
                            dateDebut,
                            dateFin,
                            false,
                            null);
                })
                .toList();

        List<GarantieResponse> combined = new ArrayList<>(fromGaranties);
        combined.addAll(fromLignes);
        return ResponseEntity.ok(combined);
    }

    // ── Admin uniquement ───────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        clientService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
