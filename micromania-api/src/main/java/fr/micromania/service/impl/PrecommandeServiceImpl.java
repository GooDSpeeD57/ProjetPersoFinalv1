package fr.micromania.service.impl;

import fr.micromania.dto.commande.CommandeResponse;
import fr.micromania.dto.precommande.*;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.commande.*;
import fr.micromania.entity.referentiel.*;
import fr.micromania.mapper.CommandeMapper;
import fr.micromania.mapper.PrecommandeMapper;
import fr.micromania.repository.*;
import fr.micromania.service.PrecommandeService;
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
public class PrecommandeServiceImpl implements PrecommandeService {

    private final PrecommandeRepository         precommandeRepository;
    private final ClientRepository              clientRepository;
    private final ProduitVariantRepository      variantRepository;
    private final CommandeRepository            commandeRepository;
    private final CanalVenteRepository          canalVenteRepository;
    private final StatutPrecommandeRepository   statutPrecommandeRepository;
    private final ModePaiementRepository        modePaiementRepository;
    private final StatutCommandeRepository      statutCommandeRepository;
    private final ModeLivraisonRepository       modeLivraisonRepository;
    private final PrecommandeMapper             precommandeMapper;
    private final CommandeMapper                commandeMapper;

    // ── Création ──────────────────────────────────────────────

    @Override
    @Transactional
    public PrecommandeResponse creer(CreatePrecommandeRequest request) {
        var client = clientRepository.findByIdAndDeletedFalse(request.idClient())
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + request.idClient()));

        Precommande precommande = Precommande.builder()
            .referencePrecommande("PRE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .client(client)
            .acomptePaye(request.acompteAVerser() != null ? request.acompteAVerser() : BigDecimal.ZERO)
            .montantTotalEstime(BigDecimal.ZERO)
            .dateDisponibiliteEstimee(request.dateDisponibiliteEstimee())
            .commentaireClient(request.commentaireClient())
            .build();

        precommande.setCanalVente(
                canalVenteRepository.findById(request.idCanalVente())
                        .orElseThrow(() -> new EntityNotFoundException("Canal de vente introuvable : " + request.idCanalVente()))
        );

        precommande.setStatutPrecommande(
                chargerStatutPrecommande(
                        request.acompteAVerser() != null && request.acompteAVerser().compareTo(BigDecimal.ZERO) > 0
                                ? "ACOMPTE_PAYE"
                                : "ENREGISTREE"
                )
        );

        if (request.idModePaiement() != null) {
            precommande.setModePaiement(
                    modePaiementRepository.findById(request.idModePaiement())
                            .orElseThrow(() -> new EntityNotFoundException("Mode de paiement introuvable : " + request.idModePaiement()))
            );
        }

        // Lignes + calcul du total estimé
        BigDecimal totalEstime = BigDecimal.ZERO;
        for (PrecommandeLigneRequest ligneReq : request.lignes()) {
            ProduitVariant variant = variantRepository.findById(ligneReq.idVariant())
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + ligneReq.idVariant()));

            BigDecimal montantLigne = ligneReq.prixUnitaireEstime()
                .multiply(BigDecimal.valueOf(ligneReq.quantite()));

            PrecommandeLigne ligne = PrecommandeLigne.builder()
                .precommande(precommande)
                .variant(variant)
                .quantite(ligneReq.quantite())
                .prixUnitaireEstime(ligneReq.prixUnitaireEstime())
                .montantLigneEstime(montantLigne)
                .build();

            precommande.getLignes().add(ligne);
            totalEstime = totalEstime.add(montantLigne);
        }
        precommande.setMontantTotalEstime(totalEstime);

        precommande = precommandeRepository.save(precommande);
        log.info("Précommande créée : ref={} client={} total={}",
            precommande.getReferencePrecommande(), request.idClient(), totalEstime);

        return precommandeMapper.toResponse(precommande);
    }

    // ── Lecture ────────────────────────────────────────────────

    @Override
    public PrecommandeResponse getById(Long id) {
        return precommandeMapper.toResponse(precommandeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Précommande introuvable : " + id)));
    }

    @Override
    public PrecommandeResponse getByReference(String reference) {
        return precommandeMapper.toResponse(precommandeRepository.findByReferencePrecommande(reference)
            .orElseThrow(() -> new EntityNotFoundException("Précommande introuvable : " + reference)));
    }

    @Override
    public Page<PrecommandeResponse> getByClient(Long idClient, Pageable pageable) {
        return precommandeRepository.findByClientIdOrderByDatePrecommandeDesc(idClient, pageable)
            .map(precommandeMapper::toResponse);
    }

    @Override
    public Page<PrecommandeResponse> getByStatut(String statut, Pageable pageable) {
        return precommandeRepository.findByStatut(statut, pageable)
            .map(precommandeMapper::toResponse);
    }

    // ── Conversion en commande ────────────────────────────────

    @Override
    @Transactional
    public CommandeResponse convertirEnCommande(Long idPrecommande) {
        Precommande pre = precommandeRepository.findById(idPrecommande)
            .orElseThrow(() -> new EntityNotFoundException("Précommande introuvable : " + idPrecommande));

        String statut = pre.getStatutPrecommande().getCode();
        if (statut.equals("ANNULEE") || statut.equals("CONVERTIE_EN_COMMANDE")) {
            throw new IllegalStateException("Précommande ne peut pas être convertie — statut : " + statut);
        }

        // Construire la commande depuis la précommande
        Commande commande = Commande.builder()
            .referenceCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .client(pre.getClient())
            .canalVente(pre.getCanalVente())
            .modePaiement(pre.getModePaiement())
            .sousTotal(BigDecimal.ZERO)
            .montantRemise(BigDecimal.ZERO)
            .fraisLivraison(BigDecimal.ZERO)
            .montantTotal(BigDecimal.ZERO)
            .commentaireClient(pre.getCommentaireClient())
            .build();

        commande.setStatutCommande(chargerStatutCommande("CREEE"));

        // Mode de livraison par défaut DOMICILE
        commande.setModeLivraison(chargerModeLivraison("DOMICILE"));

        // Convertir lignes précommande → lignes commande
        BigDecimal sousTotal = BigDecimal.ZERO;
        for (PrecommandeLigne pl : pre.getLignes()) {
            LigneCommande lc = LigneCommande.builder()
                .commande(commande)
                .variant(pl.getVariant())
                .quantite(pl.getQuantite())
                .prixUnitaire(pl.getPrixUnitaireEstime())
                .montantLigne(pl.getMontantLigneEstime())
                .build();
            commande.getLignes().add(lc);
            sousTotal = sousTotal.add(lc.getMontantLigne());
        }

        // Déduire l'acompte déjà versé du total
        BigDecimal total = sousTotal.subtract(pre.getAcomptePaye()).max(BigDecimal.ZERO);
        commande.setSousTotal(sousTotal);
        commande.setMontantTotal(total);

        commande = commandeRepository.save(commande);

        // Mettre à jour le statut de la précommande
        pre.setStatutPrecommande(chargerStatutPrecommande("CONVERTIE_EN_COMMANDE"));
        pre.setDateConversionCommande(LocalDateTime.now());
        precommandeRepository.save(pre);

        log.info("Précommande {} convertie en commande {}",
            pre.getReferencePrecommande(), commande.getReferenceCommande());

        return commandeMapper.toResponse(commande);
    }

    // ── Annulation ─────────────────────────────────────────────

    @Override
    @Transactional
    public void annuler(Long id, String motif) {
        Precommande pre = precommandeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Précommande introuvable : " + id));

        if ("CONVERTIE_EN_COMMANDE".equals(pre.getStatutPrecommande().getCode())) {
            throw new IllegalStateException("Impossible d'annuler une précommande déjà convertie");
        }

        pre.setStatutPrecommande(chargerStatutPrecommande("ANNULEE"));
        pre.setCommentaireClient(motif);
        precommandeRepository.save(pre);

        // Si acompte versé → déclencher un remboursement (TODO: intégration paiement)
        if (pre.getAcomptePaye().compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Remboursement acompte à déclencher : précommande={} montant={}",
                pre.getReferencePrecommande(), pre.getAcomptePaye());
        }

        log.info("Précommande {} annulée : {}", pre.getReferencePrecommande(), motif);
    }

    private StatutPrecommande chargerStatutPrecommande(String code) {
        return statutPrecommandeRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Statut précommande introuvable : " + code));
    }

    private StatutCommande chargerStatutCommande(String code) {
        return statutCommandeRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Statut commande introuvable : " + code));
    }

    private ModeLivraison chargerModeLivraison(String code) {
        return modeLivraisonRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Mode de livraison introuvable : " + code));
    }
}
