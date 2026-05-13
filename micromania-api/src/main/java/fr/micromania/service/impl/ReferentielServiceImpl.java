package fr.micromania.service.impl;

import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.FormatProduitDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.StatutProduitDto;
import fr.micromania.entity.referentiel.*;
import fr.micromania.repository.*;
import fr.micromania.service.ReferentielService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferentielServiceImpl implements ReferentielService {

    private final EditionProduitRepository            editionProduitRepository;
    private final ModePaiementRepository              modePaiementRepository;
    private final ContexteVenteRepository             contexteVenteRepository;
    private final PlateformeRepository                plateformeRepository;
    private final FormatProduitRepository             formatProduitRepository;
    private final StatutProduitRepository             statutProduitRepository;
    private final TauxTvaRepository                   tauxTvaRepository;
    private final TypeCategorieRepository             typeCategorieRepository;
    private final ModeLivraisonRepository             modeLivraisonRepository;
    private final CanalVenteRepository                canalVenteRepository;
    private final TypeGarantieRepository              typeGarantieRepository;
    private final EtatCarteTcgRepository              etatCarteTcgRepository;
    private final ModeCompensationRepriseRepository   modeCompensationRepriseRepository;
    private final TypeRetourRepository                typeRetourRepository;
    private final TypeReductionRepository             typeReductionRepository;
    private final TypeMouvementRepository             typeMouvementRepository;
    private final TypeFideliteRepository              typeFideliteRepository;
    private final StatutCommandeRepository            statutCommandeRepository;
    private final StatutPanierRepository              statutPanierRepository;
    private final StatutFactureRepository             statutFactureRepository;
    private final StatutRepriseRepository             statutRepriseRepository;
    private final StatutRetourRepository              statutRetourRepository;
    private final StatutSavRepository                 statutSavRepository;
    private final StatutAvisRepository                statutAvisRepository;
    private final StatutPrecommandeRepository         statutPrecommandeRepository;
    private final StatutPaiementRepository            statutPaiementRepository;
    private final StatutPlanningRepository            statutPlanningRepository;
    private final StatutAbonnementRepository          statutAbonnementRepository;

    @Override
    public List<EditionDto> getEditions() {
        return editionProduitRepository.findByActifTrueOrderByOrdreAffichageAsc().stream()
                .map(e -> new EditionDto(e.getId(), e.getCode(), e.getLibelle()))
                .toList();
    }

    @Override public List<ModePaiement>  getModesPaiement()  { return modePaiementRepository.findAll(); }
    @Override public List<ContexteVente> getContextesVente() { return contexteVenteRepository.findAll(); }

    @Override
    public List<PlatformeDto> getPlateformes() {
        return plateformeRepository.findAllByOrderByLibelleAsc().stream()
                .map(p -> new PlatformeDto(p.getId(), p.getCode(), p.getLibelle()))
                .toList();
    }

    @Override
    public List<FormatProduitDto> getFormatsProduit() {
        return formatProduitRepository.findAllByOrderByCodeAsc().stream()
                .map(f -> new FormatProduitDto(f.getId(), f.getCode(), f.getDescription()))
                .toList();
    }

    @Override
    public List<StatutProduitDto> getStatutsProduit() {
        return statutProduitRepository.findAllByOrderByCodeAsc().stream()
                .map(s -> new StatutProduitDto(s.getId(), s.getCode(), s.getDescription()))
                .toList();
    }

    @Override public List<TauxTva>                 getTauxTva()                 { return tauxTvaRepository.findAllByActifTrueOrderByTauxAsc(); }
    @Override public List<TypeCategorie>           getTypeCategories()          { return typeCategorieRepository.findAllByOrderByCodeAsc(); }
    @Override public List<ModeLivraison>           getModesLivraison()          { return modeLivraisonRepository.findAll(); }
    @Override public List<CanalVente>              getCanauxVente()             { return canalVenteRepository.findAll(); }
    @Override public List<EtatCarteTcg>            getEtatsCarteTcg()           { return etatCarteTcgRepository.findAllByOrderByCodeAsc(); }
    @Override public List<ModeCompensationReprise> getModesCompensationReprise(){ return modeCompensationRepriseRepository.findAllByOrderByCodeAsc(); }
    @Override public List<TypeRetour>              getTypesRetour()             { return typeRetourRepository.findAllByOrderByCodeAsc(); }
    @Override public List<TypeReduction>           getTypesReduction()          { return typeReductionRepository.findAll(); }
    @Override public List<TypeMouvement>           getTypesMouvement()          { return typeMouvementRepository.findAll(); }
    @Override public List<TypeFidelite>            getTypesFidelite()           { return typeFideliteRepository.findAll(); }
    @Override public List<StatutCommande>          getStatutsCommande()         { return statutCommandeRepository.findAll(); }
    @Override public List<StatutPanier>            getStatutsPanier()           { return statutPanierRepository.findAll(); }
    @Override public List<StatutFacture>           getStatutsFacture()          { return statutFactureRepository.findAll(); }
    @Override public List<StatutReprise>           getStatutsReprise()          { return statutRepriseRepository.findAll(); }
    @Override public List<StatutRetour>            getStatutsRetour()           { return statutRetourRepository.findAll(); }
    @Override public List<StatutSav>               getStatutsSav()              { return statutSavRepository.findAll(); }
    @Override public List<StatutAvis>              getStatutsAvis()             { return statutAvisRepository.findAll(); }
    @Override public List<StatutPrecommande>       getStatutsPrecommande()      { return statutPrecommandeRepository.findAll(); }
    @Override public List<StatutPaiement>          getStatutsPaiement()         { return statutPaiementRepository.findAll(); }
    @Override public List<StatutPlanning>          getStatutsPlanning()         { return statutPlanningRepository.findAll(); }
    @Override public List<StatutAbonnement>        getStatutsAbonnement()       { return statutAbonnementRepository.findAll(); }

    @Override
    public List<Map<String, Object>> getTypesGarantie(Long categorieId) {
        List<TypeGarantie> types = (categorieId != null)
                ? typeGarantieRepository.findByCategorieIdOrUniversel(categorieId)
                : typeGarantieRepository.findAllByOrderByCodeAsc();
        return types.stream().map(tg -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",            tg.getId());
            m.put("code",          tg.getCode());
            m.put("description",   tg.getDescription());
            m.put("dureeMois",     tg.getDureeMois());
            m.put("prixExtension", tg.getPrixExtension());
            m.put("categorieId",   tg.getCategorie() != null ? tg.getCategorie().getId() : null);
            return m;
        }).toList();
    }
}
