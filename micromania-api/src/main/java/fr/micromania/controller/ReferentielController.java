package fr.micromania.controller;

import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.FormatProduitDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.StatutProduitDto;
import fr.micromania.entity.referentiel.*;
import fr.micromania.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/referentiel")
@RequiredArgsConstructor
public class ReferentielController {

    // ── existing ────────────────────────────────────────────────────────────
    private final fr.micromania.repository.EditionProduitRepository editionProduitRepository;
    private final ModePaiementRepository    modePaiementRepository;
    private final ContexteVenteRepository   contexteVenteRepository;
    private final PlateformeRepository      plateformeRepository;
    private final FormatProduitRepository   formatProduitRepository;
    private final StatutProduitRepository   statutProduitRepository;

    // ── new ─────────────────────────────────────────────────────────────────
    private final TauxTvaRepository               tauxTvaRepository;
    private final TypeCategorieRepository          typeCategorieRepository;
    private final ModeLivraisonRepository          modeLivraisonRepository;
    private final CanalVenteRepository             canalVenteRepository;
    private final TypeGarantieRepository           typeGarantieRepository;
    private final EtatCarteTcgRepository           etatCarteTcgRepository;
    private final ModeCompensationRepriseRepository modeCompensationRepriseRepository;
    private final TypeRetourRepository             typeRetourRepository;
    private final TypeReductionRepository          typeReductionRepository;
    private final TypeMouvementRepository          typeMouvementRepository;
    private final TypeFideliteRepository           typeFideliteRepository;
    private final StatutCommandeRepository         statutCommandeRepository;
    private final StatutPanierRepository           statutPanierRepository;
    private final StatutFactureRepository          statutFactureRepository;
    private final StatutRepriseRepository          statutRepriseRepository;
    private final StatutRetourRepository           statutRetourRepository;
    private final StatutSavRepository              statutSavRepository;
    private final StatutAvisRepository             statutAvisRepository;
    private final StatutPrecommandeRepository      statutPrecommandeRepository;
    private final StatutPaiementRepository         statutPaiementRepository;
    private final StatutPlanningRepository         statutPlanningRepository;
    private final StatutAbonnementRepository       statutAbonnementRepository;

    // ── existing endpoints ──────────────────────────────────────────────────

    @GetMapping("/editions")
    public ResponseEntity<List<EditionDto>> getEditions() {
        List<EditionDto> dtos = editionProduitRepository.findByActifTrueOrderByOrdreAffichageAsc()
                .stream()
                .map(e -> new EditionDto(e.getId(), e.getCode(), e.getLibelle()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/modes-paiement")
    public ResponseEntity<List<ModePaiement>> getModesPaiement() {
        return ResponseEntity.ok(modePaiementRepository.findAll());
    }

    @GetMapping("/contextes-vente")
    public ResponseEntity<List<ContexteVente>> getContextesVente() {
        return ResponseEntity.ok(contexteVenteRepository.findAll());
    }

    @GetMapping("/plateformes")
    public ResponseEntity<List<PlatformeDto>> getPlateformes() {
        List<PlatformeDto> dtos = plateformeRepository.findAllByOrderByLibelleAsc()
                .stream()
                .map(p -> new PlatformeDto(p.getId(), p.getCode(), p.getLibelle()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/formats-produit")
    public ResponseEntity<List<FormatProduitDto>> getFormatsProduit() {
        List<FormatProduitDto> dtos = formatProduitRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(f -> new FormatProduitDto(f.getId(), f.getCode(), f.getDescription()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/statuts-produit")
    public ResponseEntity<List<StatutProduitDto>> getStatutsProduit() {
        List<StatutProduitDto> dtos = statutProduitRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(s -> new StatutProduitDto(s.getId(), s.getCode(), s.getDescription()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // ── new endpoints ───────────────────────────────────────────────────────

    @GetMapping("/taux-tva")
    public ResponseEntity<List<TauxTva>> getTauxTva() {
        return ResponseEntity.ok(tauxTvaRepository.findAllByActifTrueOrderByTauxAsc());
    }

    @GetMapping("/type-categories")
    public ResponseEntity<List<TypeCategorie>> getTypeCategories() {
        return ResponseEntity.ok(typeCategorieRepository.findAllByOrderByCodeAsc());
    }

    @GetMapping("/modes-livraison")
    public ResponseEntity<List<ModeLivraison>> getModesLivraison() {
        return ResponseEntity.ok(modeLivraisonRepository.findAll());
    }

    @GetMapping("/canaux-vente")
    public ResponseEntity<List<CanalVente>> getCanauxVente() {
        return ResponseEntity.ok(canalVenteRepository.findAll());
    }

    @GetMapping("/types-garantie")
    public ResponseEntity<List<java.util.Map<String, Object>>> getTypesGarantie(
            @RequestParam(required = false) Long categorieId) {
        List<TypeGarantie> types = (categorieId != null)
                ? typeGarantieRepository.findByCategorieIdOrUniversel(categorieId)
                : typeGarantieRepository.findAllByOrderByCodeAsc();
        List<java.util.Map<String, Object>> result = types.stream().map(tg -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id",            tg.getId());
            m.put("code",          tg.getCode());
            m.put("description",   tg.getDescription());
            m.put("dureeMois",     tg.getDureeMois());
            m.put("prixExtension", tg.getPrixExtension());
            m.put("categorieId",   tg.getCategorie() != null ? tg.getCategorie().getId() : null);
            return m;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/etats-carte-tcg")
    public ResponseEntity<List<EtatCarteTcg>> getEtatsCarteTcg() {
        return ResponseEntity.ok(etatCarteTcgRepository.findAllByOrderByCodeAsc());
    }

    @GetMapping("/modes-compensation-reprise")
    public ResponseEntity<List<ModeCompensationReprise>> getModesCompensationReprise() {
        return ResponseEntity.ok(modeCompensationRepriseRepository.findAllByOrderByCodeAsc());
    }

    @GetMapping("/types-retour")
    public ResponseEntity<List<TypeRetour>> getTypesRetour() {
        return ResponseEntity.ok(typeRetourRepository.findAllByOrderByCodeAsc());
    }

    @GetMapping("/types-reduction")
    public ResponseEntity<List<TypeReduction>> getTypesReduction() {
        return ResponseEntity.ok(typeReductionRepository.findAll());
    }

    @GetMapping("/types-mouvement")
    public ResponseEntity<List<TypeMouvement>> getTypesMouvement() {
        return ResponseEntity.ok(typeMouvementRepository.findAll());
    }

    @GetMapping("/types-fidelite")
    public ResponseEntity<List<TypeFidelite>> getTypesFidelite() {
        return ResponseEntity.ok(typeFideliteRepository.findAll());
    }

    @GetMapping("/statuts-commande")
    public ResponseEntity<List<StatutCommande>> getStatutsCommande() {
        return ResponseEntity.ok(statutCommandeRepository.findAll());
    }

    @GetMapping("/statuts-panier")
    public ResponseEntity<List<StatutPanier>> getStatutsPanier() {
        return ResponseEntity.ok(statutPanierRepository.findAll());
    }

    @GetMapping("/statuts-facture")
    public ResponseEntity<List<StatutFacture>> getStatutsFacture() {
        return ResponseEntity.ok(statutFactureRepository.findAll());
    }

    @GetMapping("/statuts-reprise")
    public ResponseEntity<List<StatutReprise>> getStatutsReprise() {
        return ResponseEntity.ok(statutRepriseRepository.findAll());
    }

    @GetMapping("/statuts-retour")
    public ResponseEntity<List<StatutRetour>> getStatutsRetour() {
        return ResponseEntity.ok(statutRetourRepository.findAll());
    }

    @GetMapping("/statuts-sav")
    public ResponseEntity<List<StatutSav>> getStatutsSav() {
        return ResponseEntity.ok(statutSavRepository.findAll());
    }

    @GetMapping("/statuts-avis")
    public ResponseEntity<List<StatutAvis>> getStatutsAvis() {
        return ResponseEntity.ok(statutAvisRepository.findAll());
    }

    @GetMapping("/statuts-precommande")
    public ResponseEntity<List<StatutPrecommande>> getStatutsPrecommande() {
        return ResponseEntity.ok(statutPrecommandeRepository.findAll());
    }

    @GetMapping("/statuts-paiement")
    public ResponseEntity<List<StatutPaiement>> getStatutsPaiement() {
        return ResponseEntity.ok(statutPaiementRepository.findAll());
    }

    @GetMapping("/statuts-planning")
    public ResponseEntity<List<StatutPlanning>> getStatutsPlanning() {
        return ResponseEntity.ok(statutPlanningRepository.findAll());
    }

    @GetMapping("/statuts-abonnement")
    public ResponseEntity<List<StatutAbonnement>> getStatutsAbonnement() {
        return ResponseEntity.ok(statutAbonnementRepository.findAll());
    }
}
