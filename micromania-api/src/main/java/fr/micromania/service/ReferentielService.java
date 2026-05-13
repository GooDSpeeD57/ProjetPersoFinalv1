package fr.micromania.service;

import fr.micromania.dto.referentiel.EditionDto;
import fr.micromania.dto.referentiel.FormatProduitDto;
import fr.micromania.dto.referentiel.PlatformeDto;
import fr.micromania.dto.referentiel.StatutProduitDto;
import fr.micromania.entity.referentiel.*;

import java.util.List;
import java.util.Map;

/**
 * Accès en lecture à tous les référentiels de l'application.
 * Regroupe les 22 tables de paramétrage sans exposer les repositories au controller.
 */
public interface ReferentielService {

    List<EditionDto>       getEditions();
    List<ModePaiement>     getModesPaiement();
    List<ContexteVente>    getContextesVente();
    List<PlatformeDto>     getPlateformes();
    List<FormatProduitDto> getFormatsProduit();
    List<StatutProduitDto> getStatutsProduit();

    List<TauxTva>                    getTauxTva();
    List<TypeCategorie>              getTypeCategories();
    List<ModeLivraison>              getModesLivraison();
    List<CanalVente>                 getCanauxVente();
    List<Map<String, Object>>        getTypesGarantie(Long categorieId);
    List<EtatCarteTcg>               getEtatsCarteTcg();
    List<ModeCompensationReprise>    getModesCompensationReprise();
    List<TypeRetour>                 getTypesRetour();
    List<TypeReduction>              getTypesReduction();
    List<TypeMouvement>              getTypesMouvement();
    List<TypeFidelite>               getTypesFidelite();
    List<StatutCommande>             getStatutsCommande();
    List<StatutPanier>               getStatutsPanier();
    List<StatutFacture>              getStatutsFacture();
    List<StatutReprise>              getStatutsReprise();
    List<StatutRetour>               getStatutsRetour();
    List<StatutSav>                  getStatutsSav();
    List<StatutAvis>                 getStatutsAvis();
    List<StatutPrecommande>          getStatutsPrecommande();
    List<StatutPaiement>             getStatutsPaiement();
    List<StatutPlanning>             getStatutsPlanning();
    List<StatutAbonnement>           getStatutsAbonnement();
}
