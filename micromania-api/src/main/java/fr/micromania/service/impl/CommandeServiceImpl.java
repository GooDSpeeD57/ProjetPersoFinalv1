package fr.micromania.service.impl;

import fr.micromania.dto.commande.*;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.*;
import fr.micromania.entity.stock.StockMagasin;
import fr.micromania.entity.stock.StockEntrepot;
import fr.micromania.mapper.CommandeMapper;
import fr.micromania.repository.*;
import fr.micromania.repository.StockMagasinRepository;
import fr.micromania.repository.StockEntrepotRepository;
import fr.micromania.service.CommandeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository        commandeRepository;
    private final PanierRepository          panierRepository;
    private final ClientRepository          clientRepository;
    private final ProduitVariantRepository  variantRepository;
    private final ProduitPrixRepository     prixRepository;
    private final StockMagasinRepository    stockMagasinRepository;
    private final StockEntrepotRepository   stockEntrepotRepository;
    private final PromotionRepository       promotionRepository;
    private final CommandeMapper            commandeMapper;

    // ── Création ──────────────────────────────────────────────

    @Override
    @Transactional
    public CommandeResponse creer(Long idClient, CreateCommandeRequest request) {
        Panier panier = panierRepository.findById(request.idPanier())
            .orElseThrow(() -> new EntityNotFoundException("Panier introuvable : " + request.idPanier()));

        if (!panier.getClient().getId().equals(idClient)) {
            throw new SecurityException("Panier n'appartient pas au client " + idClient);
        }
        if (panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Impossible de créer une commande depuis un panier vide");
        }

        // ── Construction de la commande ─────────────────────
        Commande commande = Commande.builder()
            .referenceCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .client(panier.getClient())
            .canalVente(panier.getCanalVente())
            .codePromo(request.codePromo() != null ? request.codePromo() : panier.getCodePromo())
            .commentaireClient(request.commentaireClient())
            .sousTotal(BigDecimal.ZERO)
            .montantRemise(BigDecimal.ZERO)
            .fraisLivraison(BigDecimal.ZERO)
            .montantTotal(BigDecimal.ZERO)
            .build();

        // Résolution mode de livraison et statut
        ModeLivraison modeLivraison = new ModeLivraison();
        modeLivraison.setId(request.idModeLivraison());
        commande.setModeLivraison(modeLivraison);

        StatutCommande statut = new StatutCommande();
        statut.setId(1L); // CREEE — à résoudre via repo en prod
        commande.setStatutCommande(statut);

        if (request.idAdresseLivraison() != null) {
            fr.micromania.entity.Adresse adresse = new fr.micromania.entity.Adresse();
            adresse.setId(request.idAdresseLivraison());
            commande.setAdresseLivraison(adresse);
        }
        if (request.idMagasinRetrait() != null) {
            fr.micromania.entity.Magasin magasin = new fr.micromania.entity.Magasin();
            magasin.setId(request.idMagasinRetrait());
            commande.setMagasinRetrait(magasin);
        }

        // ── Conversion lignes panier → lignes commande ───────
        BigDecimal sousTotal = BigDecimal.ZERO;
        for (LignePanier lp : panier.getLignes()) {
            verifierStockDisponible(lp.getVariant(), lp.getQuantite());

            LigneCommande lc = LigneCommande.builder()
                .commande(commande)
                .variant(lp.getVariant())
                .quantite(lp.getQuantite())
                .prixUnitaire(lp.getPrixUnitaire())
                .montantLigne(lp.getPrixUnitaire().multiply(BigDecimal.valueOf(lp.getQuantite())))
                .build();
            commande.getLignes().add(lc);
            sousTotal = sousTotal.add(lc.getMontantLigne());
        }

        // ── Frais de livraison ────────────────────────────────
        BigDecimal frais = calculerFraisLivraison(request.idModeLivraison(), sousTotal);

        // ── Code promo ─────────────────────────────────────────
        BigDecimal remise = BigDecimal.ZERO;
        String codePromo  = commande.getCodePromo();
        if (codePromo != null) {
            remise = appliquerPromo(codePromo, idClient, sousTotal);
            commande.setCodePromo(codePromo);
        }

        commande.setSousTotal(sousTotal);
        commande.setFraisLivraison(frais);
        commande.setMontantRemise(remise);
        commande.setMontantTotal(sousTotal.add(frais).subtract(remise).max(BigDecimal.ZERO));

        commande = commandeRepository.save(commande);

        // ── Invalider le panier ────────────────────────────────
        panier.getStatutPanier().setCode("VALIDE");
        panierRepository.save(panier);

        log.info("Commande créée : ref={} client={} total={}",
            commande.getReferenceCommande(), idClient, commande.getMontantTotal());

        return commandeMapper.toResponse(commande);
    }

    // ── Lecture ────────────────────────────────────────────────

    @Override
    public CommandeResponse getById(Long id) {
        return commandeMapper.toResponse(commandeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + id)));
    }

    @Override
    public CommandeResponse getByReference(String reference) {
        return commandeMapper.toResponse(commandeRepository.findByReferenceCommande(reference)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + reference)));
    }

    @Override
    public Page<CommandeSummary> getByClient(Long idClient, Pageable pageable) {
        return commandeRepository.findByClientIdOrderByDateCommandeDesc(idClient, pageable)
            .map(commandeMapper::toSummary);
    }

    @Override
    public Page<CommandeSummary> filter(Long idClient, String statut, Pageable pageable) {
        return commandeRepository.filter(idClient, statut, null, null, pageable)
            .map(commandeMapper::toSummary);
    }

    // ── Changement de statut ───────────────────────────────────

    @Override
    @Transactional
    public CommandeResponse updateStatut(Long id, UpdateStatutCommandeRequest request) {
        Commande commande = commandeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + id));

        String ancienStatut = commande.getStatutCommande().getCode();
        validerTransitionStatut(ancienStatut, request.codeStatut());

        StatutCommande nouveauStatut = new StatutCommande();
        nouveauStatut.setCode(request.codeStatut());
        commande.setStatutCommande(nouveauStatut);

        // Horodatages selon le statut cible
        switch (request.codeStatut()) {
            case "PAYEE"       -> commande.setDatePaiement(LocalDateTime.now());
            case "PREPARATION" -> commande.setDatePreparation(LocalDateTime.now());
            case "EXPEDIEE"    -> commande.setDateExpedition(LocalDateTime.now());
            case "LIVREE"      -> commande.setDateLivraisonReelle(LocalDateTime.now());
            case "RETIREE"     -> commande.setDateRetrait(LocalDateTime.now());
        }

        commande = commandeRepository.save(commande);
        log.info("Statut commande {} : {} → {}", commande.getReferenceCommande(),
            ancienStatut, request.codeStatut());
        return commandeMapper.toResponse(commande);
    }

    // ── Annulation ─────────────────────────────────────────────

    @Override
    @Transactional
    public void annuler(Long id, String motif) {
        Commande commande = commandeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Commande introuvable : " + id));

        String statut = commande.getStatutCommande().getCode();
        if (statut.equals("EXPEDIEE") || statut.equals("LIVREE") || statut.equals("RETIREE")) {
            throw new IllegalStateException("Impossible d'annuler une commande déjà expédiée/livrée");
        }

        StatutCommande annulee = new StatutCommande();
        annulee.setCode("ANNULEE");
        commande.setStatutCommande(annulee);
        commande.setCommentaireClient(motif);

        // Libérer les réservations de stock si existantes
        libererReservations(commande);

        commandeRepository.save(commande);
        log.info("Commande {} annulée : {}", commande.getReferenceCommande(), motif);
    }

    // ── Helpers privés ─────────────────────────────────────────

    private void verifierStockDisponible(ProduitVariant variant, int quantite) {
        // Pour les produits dématérialisés, pas de vérification de stock physique
        if (variant.isEstDemat()) return;

        boolean stockOk = stockMagasinRepository.findByVariantId(variant.getId()).stream()
            .anyMatch(s -> s.getQuantiteDisponible() >= quantite);

        if (!stockOk) {
            stockOk = stockEntrepotRepository.findByVariantId(variant.getId()).stream()
                .anyMatch(s -> s.getQuantiteDisponible() >= quantite);
        }

        if (!stockOk) {
            throw new IllegalStateException(
                "Stock insuffisant pour le produit : " + variant.getNomCommercial());
        }
    }

    private BigDecimal calculerFraisLivraison(Long idModeLivraison, BigDecimal sousTotal) {
        // Livraison gratuite au-delà de 60€, retrait magasin toujours gratuit
        // idModeLivraison=2 = RETRAIT_MAGASIN (à adapter selon tes données)
        if (idModeLivraison != null && idModeLivraison == 2L) return BigDecimal.ZERO;
        if (sousTotal.compareTo(new BigDecimal("60.00")) >= 0) return BigDecimal.ZERO;
        return new BigDecimal("4.99");
    }

    private BigDecimal appliquerPromo(String codePromo, Long idClient, BigDecimal montant) {
        return promotionRepository.findByCodePromoAndActifTrue(codePromo)
            .map(promo -> {
                String type = promo.getTypeReduction().getCode();
                return switch (type) {
                    case "POURCENTAGE"  -> montant.multiply(promo.getValeur())
                                              .divide(BigDecimal.valueOf(100));
                    case "MONTANT_FIXE" -> promo.getValeur().min(montant);
                    default             -> BigDecimal.ZERO;
                };
            })
            .orElse(BigDecimal.ZERO);
    }

    private void validerTransitionStatut(String actuel, String cible) {
        boolean valide = switch (actuel) {
            case "CREEE"       -> cible.equals("PAYEE")  || cible.equals("ANNULEE");
            case "PAYEE"       -> cible.equals("PREPARATION") || cible.equals("REMBOURSEE") || cible.equals("ANNULEE");
            case "PREPARATION" -> cible.equals("EXPEDIEE") || cible.equals("RETIRABLE") || cible.equals("ANNULEE");
            case "EXPEDIEE"    -> cible.equals("LIVREE");
            case "RETIRABLE"   -> cible.equals("RETIREE");
            default            -> false;
        };
        if (!valide) {
            throw new IllegalStateException(
                "Transition statut invalide : " + actuel + " → " + cible);
        }
    }

    private void libererReservations(Commande commande) {
        // À compléter avec ReservationStockRepository quand injecté
        log.debug("Libération réservations stock pour commande {}", commande.getReferenceCommande());
    }
}
