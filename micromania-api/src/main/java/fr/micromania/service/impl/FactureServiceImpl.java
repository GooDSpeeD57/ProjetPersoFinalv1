package fr.micromania.service.impl;

import fr.micromania.dto.facture.*;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.referentiel.*;
import fr.micromania.entity.stock.StockMagasin;
import fr.micromania.mapper.FactureMapper;
import fr.micromania.repository.*;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.service.FactureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FactureServiceImpl implements FactureService {

    private final FactureRepository         factureRepository;
    private final CommandeRepository        commandeRepository;
    private final ProduitVariantRepository  variantRepository;
    private final StockMagasinRepository    stockMagasinRepository;
    private final PointsFideliteRepository  pointsRepository;
    private final FactureMapper             factureMapper;

    // ── Génération depuis commande ────────────────────────────

    @Override
    @Transactional
    public FactureResponse genererDepuisCommande(Long idCommande) {
        Commande commande = commandeRepository.findById(idCommande)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + idCommande));

        if (factureRepository.existsByCommandeId(idCommande)) {
            throw new IllegalStateException("Une facture existe déjà pour la commande " + idCommande);
        }

        Facture facture = Facture.builder()
            .referenceFacture("FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .commande(commande)
            .client(commande.getClient())
            .modePaiement(commande.getModePaiement())
            .montantRemise(commande.getMontantRemise())
            .montantTotal(BigDecimal.ZERO)
            .montantHtTotal(BigDecimal.ZERO)
            .montantTvaTotal(BigDecimal.ZERO)
            .montantFinal(BigDecimal.ZERO)
            .build();

        // Résolution statut et contexte
        StatutFacture statut = new StatutFacture();
        statut.setCode("EMISE");
        facture.setStatutFacture(statut);

        ContexteVente contexte = new ContexteVente();
        contexte.setCode("EN_LIGNE");
        facture.setContexteVente(contexte);

        // Résolution magasin
        if (commande.getMagasinRetrait() != null) {
            facture.setMagasin(commande.getMagasinRetrait());
        } else if (commande.getEntrepotExpedition() != null) {
            fr.micromania.entity.Magasin mg = new fr.micromania.entity.Magasin();
            mg.setId(1L); // magasin principal par défaut — à résoudre selon ta config
            facture.setMagasin(mg);
        }

        // Conversion lignes commande → lignes facture
        for (LigneCommande lc : commande.getLignes()) {
            LigneFacture lf = construireLigneFacture(facture, lc.getVariant(),
                lc.getQuantite(), lc.getPrixUnitaire(), lc);
            facture.getLignes().add(lf);
        }

        recalculerTotauxFacture(facture);
        facture = factureRepository.save(facture);

        // Attribuer les points de fidélité
        attribuerPoints(commande.getClient().getId(), facture.getMontantHtTotal(), idCommande);

        // Décrémenter le stock
        decrementerStock(facture);

        log.info("Facture générée : ref={} commande={}", facture.getReferenceFacture(), idCommande);
        return factureMapper.toResponse(facture);
    }

    // ── Vente directe magasin ─────────────────────────────────

    @Override
    @Transactional
    public FactureResponse creerVenteMagasin(CreateFactureVenteRequest request) {
        Facture facture = Facture.builder()
            .referenceFacture("FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .nomClient(request.nomClient())
            .emailClient(request.emailClient())
            .telephoneClient(request.telephoneClient())
            .montantRemise(BigDecimal.ZERO)
            .montantTotal(BigDecimal.ZERO)
            .montantHtTotal(BigDecimal.ZERO)
            .montantTvaTotal(BigDecimal.ZERO)
            .montantFinal(BigDecimal.ZERO)
            .build();

        // Résolutions par ID
        fr.micromania.entity.Magasin magasin = new fr.micromania.entity.Magasin();
        magasin.setId(request.idMagasin());
        facture.setMagasin(magasin);

        ModePaiement mp = new ModePaiement();
        mp.setId(request.idModePaiement());
        facture.setModePaiement(mp);

        StatutFacture statut = new StatutFacture();
        statut.setCode("EMISE");
        facture.setStatutFacture(statut);

        ContexteVente contexte = new ContexteVente();
        contexte.setId(request.idContexteVente());
        facture.setContexteVente(contexte);

        if (request.idClient() != null) {
            fr.micromania.entity.Client client = new fr.micromania.entity.Client();
            client.setId(request.idClient());
            facture.setClient(client);
        }
        if (request.idEmploye() != null) {
            fr.micromania.entity.Employe emp = new fr.micromania.entity.Employe();
            emp.setId(request.idEmploye());
            facture.setEmploye(emp);
        }

        // Lignes facture
        for (LigneFactureRequest lReq : request.lignes()) {
            ProduitVariant variant = variantRepository.findById(lReq.idVariant())
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + lReq.idVariant()));
            LigneFacture lf = construireLigneFacture(facture, variant,
                lReq.quantite(), lReq.prixUnitaire(), null);
            facture.getLignes().add(lf);
        }

        recalculerTotauxFacture(facture);

        // Code promo
        if (request.codePromo() != null) {
            BigDecimal remise = calculerRemisePromo(request.codePromo(), facture.getMontantTotal());
            facture.setMontantRemise(remise);
            facture.setMontantFinal(facture.getMontantTotal().subtract(remise).max(BigDecimal.ZERO));
        }

        facture = factureRepository.save(facture);

        // Points fidélité si client connu
        if (request.idClient() != null) {
            attribuerPoints(request.idClient(), facture.getMontantHtTotal(), null);
        }

        decrementerStock(facture);

        log.info("Vente magasin facturée : ref={} magasin={}", facture.getReferenceFacture(), request.idMagasin());
        return factureMapper.toResponse(facture);
    }

    // ── Lecture ────────────────────────────────────────────────

    @Override
    public FactureResponse getById(Long id) {
        return factureMapper.toResponse(factureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + id)));
    }

    @Override
    public FactureResponse getByReference(String reference) {
        return factureMapper.toResponse(factureRepository.findByReferenceFacture(reference)
            .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + reference)));
    }

    @Override
    public Page<FactureSummary> getByClient(Long idClient, Pageable pageable) {
        return factureRepository.findByClientIdOrderByDateFactureDesc(idClient, pageable)
            .map(factureMapper::toSummary);
    }

    @Override
    public Page<FactureSummary> getByMagasin(Long idMagasin, Pageable pageable) {
        return factureRepository.findByMagasinIdOrderByDateFactureDesc(idMagasin, pageable)
            .map(factureMapper::toSummary);
    }

    @Override
    @Transactional
    public void annuler(Long id) {
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + id));

        if ("ANNULEE".equals(facture.getStatutFacture().getCode())) {
            throw new IllegalStateException("Facture déjà annulée");
        }

        StatutFacture annule = new StatutFacture();
        annule.setCode("ANNULEE");
        facture.setStatutFacture(annule);
        factureRepository.save(facture);
        log.info("Facture {} annulée", facture.getReferenceFacture());
    }

    // ── Helpers privés ─────────────────────────────────────────

    /**
     * Construit une LigneFacture avec calcul automatique HT/TVA.
     * Le taux TVA est résolu dans l'ordre : variant → type_categorie → défaut 20%
     */
    private LigneFacture construireLigneFacture(Facture facture, ProduitVariant variant,
                                                 int quantite, BigDecimal prixUnitaire,
                                                 LigneCommande ligneCommande) {
        BigDecimal taux = resoudreTauxTva(variant);
        BigDecimal montantLigne  = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        BigDecimal montantHt     = montantLigne.divide(
            BigDecimal.ONE.add(taux.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)),
            2, RoundingMode.HALF_UP);
        BigDecimal montantTva    = montantLigne.subtract(montantHt);

        LigneFacture ligne = LigneFacture.builder()
            .facture(facture)
            .variant(variant)
            .ligneCommande(ligneCommande)
            .quantite(quantite)
            .prixUnitaire(prixUnitaire)
            .tauxTvaApplique(taux)
            .montantLigne(montantLigne)
            .montantHtLigne(montantHt)
            .montantTvaLigne(montantTva)
            .build();

        if (variant.getTauxTva() != null) {
            ligne.setTauxTva(variant.getTauxTva());
        }
        return ligne;
    }

    private BigDecimal resoudreTauxTva(ProduitVariant variant) {
        // 1. Taux spécifique au variant
        if (variant.getTauxTva() != null) {
            return variant.getTauxTva().getTaux();
        }
        // 2. Taux par défaut de la catégorie
        var tvaDefaut = variant.getProduit().getCategorie().getTypeCategorie().getTauxTvaDefaut();
        if (tvaDefaut != null) {
            return tvaDefaut.getTaux();
        }
        // 3. Fallback 20%
        return new BigDecimal("20.00");
    }

    private void recalculerTotauxFacture(Facture facture) {
        BigDecimal total    = facture.getLignes().stream()
            .map(LigneFacture::getMontantLigne).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHt  = facture.getLignes().stream()
            .map(LigneFacture::getMontantHtLigne).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTva = facture.getLignes().stream()
            .map(LigneFacture::getMontantTvaLigne).reduce(BigDecimal.ZERO, BigDecimal::add);

        facture.setMontantTotal(total);
        facture.setMontantHtTotal(totalHt);
        facture.setMontantTvaTotal(totalTva);
        facture.setMontantFinal(total.subtract(facture.getMontantRemise()).max(BigDecimal.ZERO));
    }

    private void attribuerPoints(Long idClient, BigDecimal montantHt, Long idCommande) {
        try {
            // Ratio points : 1 point / euro HT (à affiner par type fidélité)
            int points = montantHt.intValue();
            if (points > 0) {
                pointsRepository.addPoints(idClient, points);
                pointsRepository.addAchatAnnuel(idClient, montantHt);
                log.debug("Points attribués : client={} +{} pts", idClient, points);
            }
        } catch (Exception e) {
            log.warn("Échec attribution points client={} : {}", idClient, e.getMessage());
        }
    }

    private void decrementerStock(Facture facture) {
        for (LigneFacture ligne : facture.getLignes()) {
            ProduitVariant variant = ligne.getVariant();
            if (variant.isEstDemat()) continue; // pas de stock physique

            stockMagasinRepository.findByVariantIdAndMagasinId(
                    variant.getId(), facture.getMagasin().getId())
                .ifPresent(stock -> {
                    int nouv = Math.max(0, stock.getQuantiteNeuf() - ligne.getQuantite());
                    stock.setQuantiteNeuf(nouv);
                    stockMagasinRepository.save(stock);
                });
        }
    }

    private BigDecimal calculerRemisePromo(String codePromo, BigDecimal montant) {
        // Délégation simplifiée — en production injecter PromotionService
        return BigDecimal.ZERO;
    }
}
