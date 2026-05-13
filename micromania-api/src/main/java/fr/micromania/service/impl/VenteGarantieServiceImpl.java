package fr.micromania.service.impl;

import fr.micromania.dto.facture.LigneFactureRequest;
import fr.micromania.entity.Garantie;
import fr.micromania.entity.VenteUnite;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.commande.LigneFacture;
import fr.micromania.entity.commande.LignePanier;
import fr.micromania.entity.referentiel.TypeGarantie;
import fr.micromania.repository.GarantieRepository;
import fr.micromania.repository.TypeGarantieRepository;
import fr.micromania.repository.VenteUniteRepository;
import fr.micromania.service.VenteGarantieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VenteGarantieServiceImpl implements VenteGarantieService {

    private final VenteUniteRepository   venteUniteRepository;
    private final GarantieRepository     garantieRepository;
    private final TypeGarantieRepository typeGarantieRepository;

    // ─────────────────────────────────────────────────────────────────────────
    //  Vente magasin (JavaFX) — numéros de série fournis dans la requête
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void creerDepuisVenteMagasin(Facture facture, List<LigneFactureRequest> lignesReq) {
        List<LigneFacture> lignesSaved = facture.getLignes();

        for (int i = 0; i < lignesReq.size(); i++) {
            LigneFactureRequest lReq    = lignesReq.get(i);
            LigneFacture        lf      = lignesSaved.get(i);
            ProduitVariant      variant = lf.getVariant();

            if (variant.isEstDemat()) continue;

            String ns = (lReq.numeroSerie() != null && !lReq.numeroSerie().isBlank())
                    ? lReq.numeroSerie().trim() : null;
            String statut = statut(variant);

            VenteUnite venteUnite = venteUniteRepository.save(VenteUnite.builder()
                    .ligneFacture(lf)
                    .numeroSerie(ns)
                    .etatUnite(statut)
                    .sourceStock(statut)
                    .build());

            log.info("VenteUnite créée : id={} serie={} produit={} facture={}",
                    venteUnite.getId(), ns != null ? ns : "—",
                    variant.getNomCommercial(), facture.getReferenceFacture());

            TypeGarantie typeGarantie = lReq.idTypeGarantie() != null
                    ? typeGarantieRepository.findById(lReq.idTypeGarantie()).orElse(null)
                    : resoudreGarantieLegale(categorieId(variant), statut);

            sauvegarderGarantie(venteUnite, typeGarantie, dateVente(facture), variant.getNomCommercial(), statut);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Checkout web — pas de numéro de série
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void creerDepuisCheckoutWeb(Facture facture, List<LignePanier> lignesPanier) {
        List<LigneFacture> lignesSaved = facture.getLignes();

        for (int i = 0; i < Math.min(lignesPanier.size(), lignesSaved.size()); i++) {
            LignePanier    lp      = lignesPanier.get(i);
            LigneFacture   lf      = lignesSaved.get(i);
            ProduitVariant variant = lf.getVariant();

            if (variant.isEstDemat()) continue;

            String statut = statut(variant);

            VenteUnite venteUnite = venteUniteRepository.save(VenteUnite.builder()
                    .ligneFacture(lf)
                    .numeroSerie(null)   // assigné lors de l'expédition
                    .etatUnite(statut)
                    .sourceStock(statut)
                    .build());

            TypeGarantie tgPanier = lp.getTypeGarantie();
            TypeGarantie typeGarantie = (tgPanier != null
                    && tgPanier.getPrixExtension() != null
                    && tgPanier.getPrixExtension().signum() > 0)
                    ? tgPanier
                    : resoudreGarantieLegale(categorieId(variant), statut);

            sauvegarderGarantie(venteUnite, typeGarantie, dateVente(facture), variant.getNomCommercial(), null);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers privés
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Résout le type de garantie légale gratuite depuis la DB (configurable).
     * prixExtension null ou 0 = légal. Code "OCCASION" réservé aux produits d'occasion.
     */
    private TypeGarantie resoudreGarantieLegale(Long categorieId, String statut) {
        if (categorieId == null) return null;
        List<TypeGarantie> candidats = typeGarantieRepository.findByCategorieIdOrUniversel(categorieId);
        List<TypeGarantie> gratuites = candidats.stream()
                .filter(tg -> tg.getPrixExtension() == null || tg.getPrixExtension().signum() == 0)
                .toList();
        if (gratuites.isEmpty()) return null;
        if ("OCCASION".equals(statut)) {
            return gratuites.stream()
                    .filter(tg -> tg.getCode().toUpperCase().contains("OCCASION"))
                    .findFirst().orElse(gratuites.get(0));
        }
        return gratuites.stream()
                .filter(tg -> !tg.getCode().toUpperCase().contains("OCCASION"))
                .findFirst().orElse(gratuites.get(0));
    }

    private void sauvegarderGarantie(VenteUnite venteUnite, TypeGarantie typeGarantie,
                                     LocalDate dateVente, String nomProduit, String statut) {
        int dureeMois = (typeGarantie != null && typeGarantie.getDureeMois() != null)
                ? typeGarantie.getDureeMois() : 24;
        LocalDate dateFin = dateVente.plusMonths(dureeMois);

        garantieRepository.save(Garantie.builder()
                .venteUnite(venteUnite)
                .typeGarantie(typeGarantie)
                .dateDebut(dateVente)
                .dateFin(dateFin)
                .estEtendue(typeGarantie != null
                        && typeGarantie.getPrixExtension() != null
                        && typeGarantie.getPrixExtension().signum() > 0)
                .build());

        log.info("Garantie créée : type={} produit={} statut={} duree={}m fin={}",
                typeGarantie != null ? typeGarantie.getCode() : "LEGALE_DEFAUT",
                nomProduit, statut != null ? statut : "—", dureeMois, dateFin);
    }

    private static String statut(ProduitVariant variant) {
        return variant.getStatutProduit() != null
                ? variant.getStatutProduit().getCode().toUpperCase() : "NEUF";
    }

    private static Long categorieId(ProduitVariant variant) {
        return (variant.getProduit() != null && variant.getProduit().getCategorie() != null)
                ? variant.getProduit().getCategorie().getId() : null;
    }

    private static LocalDate dateVente(Facture facture) {
        return facture.getDateFacture() != null
                ? facture.getDateFacture().toLocalDate() : LocalDate.now();
    }
}
