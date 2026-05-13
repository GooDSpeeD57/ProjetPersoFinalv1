package fr.micromania.controller;

import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.FormatProduitDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.StatutProduitDto;
import fr.micromania.entity.referentiel.*;
import fr.micromania.service.ReferentielService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/referentiel")
@RequiredArgsConstructor
public class ReferentielController {

    private final ReferentielService referentielService;

    @GetMapping("/editions")
    public ResponseEntity<List<EditionDto>> getEditions() {
        return ResponseEntity.ok(referentielService.getEditions());
    }

    @GetMapping("/modes-paiement")
    public ResponseEntity<List<ModePaiement>> getModesPaiement() {
        return ResponseEntity.ok(referentielService.getModesPaiement());
    }

    @GetMapping("/contextes-vente")
    public ResponseEntity<List<ContexteVente>> getContextesVente() {
        return ResponseEntity.ok(referentielService.getContextesVente());
    }

    @GetMapping("/plateformes")
    public ResponseEntity<List<PlatformeDto>> getPlateformes() {
        return ResponseEntity.ok(referentielService.getPlateformes());
    }

    @GetMapping("/formats-produit")
    public ResponseEntity<List<FormatProduitDto>> getFormatsProduit() {
        return ResponseEntity.ok(referentielService.getFormatsProduit());
    }

    @GetMapping("/statuts-produit")
    public ResponseEntity<List<StatutProduitDto>> getStatutsProduit() {
        return ResponseEntity.ok(referentielService.getStatutsProduit());
    }

    @GetMapping("/taux-tva")
    public ResponseEntity<List<TauxTva>> getTauxTva() {
        return ResponseEntity.ok(referentielService.getTauxTva());
    }

    @GetMapping("/type-categories")
    public ResponseEntity<List<TypeCategorie>> getTypeCategories() {
        return ResponseEntity.ok(referentielService.getTypeCategories());
    }

    @GetMapping("/modes-livraison")
    public ResponseEntity<List<ModeLivraison>> getModesLivraison() {
        return ResponseEntity.ok(referentielService.getModesLivraison());
    }

    @GetMapping("/canaux-vente")
    public ResponseEntity<List<CanalVente>> getCanauxVente() {
        return ResponseEntity.ok(referentielService.getCanauxVente());
    }

    @GetMapping("/types-garantie")
    public ResponseEntity<List<Map<String, Object>>> getTypesGarantie(
            @RequestParam(required = false) Long categorieId) {
        return ResponseEntity.ok(referentielService.getTypesGarantie(categorieId));
    }

    @GetMapping("/etats-carte-tcg")
    public ResponseEntity<List<EtatCarteTcg>> getEtatsCarteTcg() {
        return ResponseEntity.ok(referentielService.getEtatsCarteTcg());
    }

    @GetMapping("/modes-compensation-reprise")
    public ResponseEntity<List<ModeCompensationReprise>> getModesCompensationReprise() {
        return ResponseEntity.ok(referentielService.getModesCompensationReprise());
    }

    @GetMapping("/types-retour")
    public ResponseEntity<List<TypeRetour>> getTypesRetour() {
        return ResponseEntity.ok(referentielService.getTypesRetour());
    }

    @GetMapping("/types-reduction")
    public ResponseEntity<List<TypeReduction>> getTypesReduction() {
        return ResponseEntity.ok(referentielService.getTypesReduction());
    }

    @GetMapping("/types-mouvement")
    public ResponseEntity<List<TypeMouvement>> getTypesMouvement() {
        return ResponseEntity.ok(referentielService.getTypesMouvement());
    }

    @GetMapping("/types-fidelite")
    public ResponseEntity<List<TypeFidelite>> getTypesFidelite() {
        return ResponseEntity.ok(referentielService.getTypesFidelite());
    }

    @GetMapping("/statuts-commande")
    public ResponseEntity<List<StatutCommande>> getStatutsCommande() {
        return ResponseEntity.ok(referentielService.getStatutsCommande());
    }

    @GetMapping("/statuts-panier")
    public ResponseEntity<List<StatutPanier>> getStatutsPanier() {
        return ResponseEntity.ok(referentielService.getStatutsPanier());
    }

    @GetMapping("/statuts-facture")
    public ResponseEntity<List<StatutFacture>> getStatutsFacture() {
        return ResponseEntity.ok(referentielService.getStatutsFacture());
    }

    @GetMapping("/statuts-reprise")
    public ResponseEntity<List<StatutReprise>> getStatutsReprise() {
        return ResponseEntity.ok(referentielService.getStatutsReprise());
    }

    @GetMapping("/statuts-retour")
    public ResponseEntity<List<StatutRetour>> getStatutsRetour() {
        return ResponseEntity.ok(referentielService.getStatutsRetour());
    }

    @GetMapping("/statuts-sav")
    public ResponseEntity<List<StatutSav>> getStatutsSav() {
        return ResponseEntity.ok(referentielService.getStatutsSav());
    }

    @GetMapping("/statuts-avis")
    public ResponseEntity<List<StatutAvis>> getStatutsAvis() {
        return ResponseEntity.ok(referentielService.getStatutsAvis());
    }

    @GetMapping("/statuts-precommande")
    public ResponseEntity<List<StatutPrecommande>> getStatutsPrecommande() {
        return ResponseEntity.ok(referentielService.getStatutsPrecommande());
    }

    @GetMapping("/statuts-paiement")
    public ResponseEntity<List<StatutPaiement>> getStatutsPaiement() {
        return ResponseEntity.ok(referentielService.getStatutsPaiement());
    }

    @GetMapping("/statuts-planning")
    public ResponseEntity<List<StatutPlanning>> getStatutsPlanning() {
        return ResponseEntity.ok(referentielService.getStatutsPlanning());
    }

    @GetMapping("/statuts-abonnement")
    public ResponseEntity<List<StatutAbonnement>> getStatutsAbonnement() {
        return ResponseEntity.ok(referentielService.getStatutsAbonnement());
    }
}
