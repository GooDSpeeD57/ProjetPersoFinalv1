package fr.micromania.service.impl;

import fr.micromania.dto.reprise.*;
import fr.micromania.entity.Employe;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.Reprise;
import fr.micromania.entity.RepriseeLigne;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.ModeCompensationReprise;
import fr.micromania.entity.referentiel.StatutReprise;
import fr.micromania.mapper.RepriseMapper;
import fr.micromania.repository.*;
import fr.micromania.service.RepriseService;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RepriseServiceImpl implements RepriseService {

    private final RepriseRepository repriseRepository;
    private final ClientRepository clientRepository;
    private final EmployeRepository employeRepository;
    private final MagasinRepository magasinRepository;
    private final ProduitVariantRepository variantRepository;
    private final StatutRepriseRepository statutRepriseRepository;
    private final ModeCompensationRepriseRepository modeCompensationRepriseRepository;
    private final RepriseMapper repriseMapper;

    /** Décote espèces : le client reçoit 90 % de la valeur en avoir. */
    private static final BigDecimal RATIO_ESPECES = new BigDecimal("0.90");

    @Override
    @Transactional
    public RepriseResponse creer(CreateRepriseRequest request) {
        Employe employe = employeRepository.findByMagasinIdAndDeletedFalse(request.idMagasin())
            .stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Employé introuvable pour magasin " + request.idMagasin()));
        Magasin magasin = magasinRepository.findById(request.idMagasin())
            .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + request.idMagasin()));

        ModeCompensationReprise modeComp = modeCompensationRepriseRepository.findById(request.idModeCompensation())
            .orElseThrow(() -> new EntityNotFoundException("Mode de compensation introuvable : " + request.idModeCompensation()));

        StatutReprise statutInitial = statutRepriseRepository.findByCode("EN_ATTENTE")
            .or(() -> statutRepriseRepository.findAll().stream().findFirst())
            .orElseThrow(() -> new IllegalStateException("Aucun statut reprise configuré"));

        Reprise reprise = Reprise.builder()
            .referenceReprise("REP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .employe(employe)
            .magasin(magasin)
            .modeCompensationReprise(modeComp)
            .statutReprise(statutInitial)
            .commentaire(request.commentaire())
            .montantTotalEstime(BigDecimal.ZERO)
            .montantTotalValide(BigDecimal.ZERO)
            .build();

        if (request.idClient() != null) {
            reprise.setClient(clientRepository.findByIdAndDeletedFalse(request.idClient())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + request.idClient())));
        }

        // totalAvoir = somme brute des prix estimés (valeur en avoir, avant décote éventuelle)
        BigDecimal totalAvoir = BigDecimal.ZERO;
        for (CreateRepriseeLigneRequest ligneReq : request.lignes()) {
            RepriseeLigne ligne = construireLigne(reprise, ligneReq);
            reprise.getLignes().add(ligne);
            totalAvoir = totalAvoir.add(
                ligneReq.prixEstimeUnitaire().multiply(BigDecimal.valueOf(ligneReq.quantite())));
        }

        // montantTotalEstime = ce que reçoit réellement le client
        boolean estEspeces = "ESPECES".equalsIgnoreCase(modeComp.getCode());
        BigDecimal totalEstime = estEspeces
            ? totalAvoir.multiply(RATIO_ESPECES).setScale(2, RoundingMode.HALF_UP)
            : totalAvoir;
        reprise.setMontantTotalEstime(totalEstime);

        reprise = repriseRepository.save(reprise);
        log.info("Reprise créée : ref={}, avoir={}, estimé={}", reprise.getReferenceReprise(), totalAvoir, totalEstime);
        return toResponseAvecAvoir(reprise, totalAvoir);
    }

    @Override
    public RepriseResponse getById(Long id) {
        Reprise reprise = repriseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + id));
        return toResponseAvecAvoir(reprise, computeTotalAvoir(reprise));
    }

    @Override
    public RepriseResponse getByReference(String reference) {
        Reprise reprise = repriseRepository.findByReferenceReprise(reference)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + reference));
        return toResponseAvecAvoir(reprise, computeTotalAvoir(reprise));
    }

    @Override
    public Page<RepriseResponse> filter(Long idMagasin, String statut, Long idClient, Pageable pageable) {
        return repriseRepository.filter(idMagasin, statut, idClient, pageable)
            .map(r -> toResponseAvecAvoir(r, computeTotalAvoir(r)));
    }

    @Override
    @Transactional
    public RepriseResponse validerLigne(Long idReprise, ValiderRepriseLigneRequest request) {
        Reprise reprise = repriseRepository.findById(idReprise)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + idReprise));

        reprise.getLignes().stream()
            .filter(l -> l.getId().equals(request.idRepriseLigne()))
            .findFirst()
            .ifPresentOrElse(
                ligne -> {
                    ligne.setPrixValideUnitaire(request.prixValideUnitaire());
                    ligne.setCreeStockOccasion(request.creeStockOccasion());
                },
                () -> { throw new EntityNotFoundException("Ligne reprise introuvable : " + request.idRepriseLigne()); }
            );

        // totalAvoir validé = somme brute des prix validés (avant décote éventuelle)
        BigDecimal totalAvoirValide = reprise.getLignes().stream()
            .filter(l -> l.getPrixValideUnitaire() != null)
            .map(l -> l.getPrixValideUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean estEspeces = "ESPECES".equalsIgnoreCase(reprise.getModeCompensationReprise().getCode());
        BigDecimal totalValide = estEspeces
            ? totalAvoirValide.multiply(RATIO_ESPECES).setScale(2, RoundingMode.HALF_UP)
            : totalAvoirValide;
        reprise.setMontantTotalValide(totalValide);

        return toResponseAvecAvoir(repriseRepository.save(reprise), computeTotalAvoir(reprise));
    }

    @Override
    @Transactional
    public RepriseResponse valider(Long idReprise) {
        Reprise reprise = repriseRepository.findById(idReprise)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + idReprise));
        reprise.setStatutReprise(chargerStatutReprise("VALIDEE"));
        reprise.setDateValidation(LocalDateTime.now());
        Reprise saved = repriseRepository.save(reprise);
        return toResponseAvecAvoir(saved, computeTotalAvoir(saved));
    }

    @Override
    @Transactional
    public RepriseResponse refuser(Long idReprise, String motif) {
        Reprise reprise = repriseRepository.findById(idReprise)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + idReprise));
        reprise.setStatutReprise(chargerStatutReprise("REFUSEE"));
        reprise.setCommentaire(motif);
        Reprise saved = repriseRepository.save(reprise);
        return toResponseAvecAvoir(saved, computeTotalAvoir(saved));
    }

    // ── Helpers ───────────────────────────────────────────────

    /**
     * Calcule la valeur brute en avoir (somme prixEstimé × quantité) à partir des lignes.
     * Ce montant est indépendant du mode de compensation.
     */
    private BigDecimal computeTotalAvoir(Reprise reprise) {
        return reprise.getLignes().stream()
            .map(l -> l.getPrixEstimeUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Construit le RepriseResponse en injectant montantTotalAvoir
     * (le mapper l'ignore car ce champ est calculé ici, pas stocké en base).
     */
    private RepriseResponse toResponseAvecAvoir(Reprise reprise, BigDecimal totalAvoir) {
        RepriseResponse base = repriseMapper.toResponse(reprise);
        return new RepriseResponse(
            base.id(), base.referenceReprise(), base.statut(), base.modeCompensation(),
            base.client(), base.employe(), base.magasin(),
            base.montantTotalEstime(), base.montantTotalValide(), totalAvoir,
            base.commentaire(), base.dateCreation(), base.dateValidation(), base.lignes()
        );
    }

    private RepriseeLigne construireLigne(Reprise reprise, CreateRepriseeLigneRequest req) {
        RepriseeLigne ligne = RepriseeLigne.builder()
            .reprise(reprise)
            .quantite(req.quantite())
            .etatGeneral(req.etatGeneral())
            .prixEstimeUnitaire(req.prixEstimeUnitaire())
            .numeroSerie(req.numeroSerie())
            .commentaires(req.commentaires())
            .descriptionLibre(req.descriptionLibre())
            .build();

        if (req.idVariant() != null) {
            ligne.setVariant(variantRepository.findById(req.idVariant())
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + req.idVariant())));
        }
        return ligne;
    }

    private fr.micromania.entity.referentiel.StatutReprise chargerStatutReprise(String code) {
        return statutRepriseRepository.findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Statut reprise introuvable : " + code));
    }
}
