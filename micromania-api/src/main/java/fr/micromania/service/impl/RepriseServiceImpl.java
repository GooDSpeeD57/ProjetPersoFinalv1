package fr.micromania.service.impl;

import fr.micromania.dto.reprise.*;
import fr.micromania.entity.Employe;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.Reprise;
import fr.micromania.entity.RepriseeLigne;
import fr.micromania.entity.catalog.ProduitVariant;
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
    private final RepriseMapper repriseMapper;

    @Override
    @Transactional
    public RepriseResponse creer(CreateRepriseRequest request) {
        Employe employe = employeRepository.findByMagasinIdAndDeletedFalse(request.idMagasin())
            .stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Employé introuvable pour magasin " + request.idMagasin()));
        Magasin magasin = magasinRepository.findById(request.idMagasin())
            .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + request.idMagasin()));

        Reprise reprise = Reprise.builder()
            .referenceReprise("REP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .employe(employe)
            .magasin(magasin)
            .commentaire(request.commentaire())
            .montantTotalEstime(BigDecimal.ZERO)
            .montantTotalValide(BigDecimal.ZERO)
            .build();

        if (request.idClient() != null) {
            reprise.setClient(clientRepository.findByIdAndDeletedFalse(request.idClient())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + request.idClient())));
        }

        BigDecimal totalEstime = BigDecimal.ZERO;
        for (CreateRepriseeLigneRequest ligneReq : request.lignes()) {
            RepriseeLigne ligne = construireLigne(reprise, ligneReq);
            reprise.getLignes().add(ligne);
            totalEstime = totalEstime.add(
                ligneReq.prixEstimeUnitaire().multiply(BigDecimal.valueOf(ligneReq.quantite())));
        }
        reprise.setMontantTotalEstime(totalEstime);

        reprise = repriseRepository.save(reprise);
        log.info("Reprise créée : ref={}", reprise.getReferenceReprise());
        return repriseMapper.toResponse(reprise);
    }

    @Override
    public RepriseResponse getById(Long id) {
        return repriseMapper.toResponse(repriseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + id)));
    }

    @Override
    public RepriseResponse getByReference(String reference) {
        return repriseMapper.toResponse(repriseRepository.findByReferenceReprise(reference)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + reference)));
    }

    @Override
    public Page<RepriseResponse> filter(Long idMagasin, String statut, Long idClient, Pageable pageable) {
        return repriseRepository.filter(idMagasin, statut, idClient, pageable)
            .map(repriseMapper::toResponse);
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

        BigDecimal totalValide = reprise.getLignes().stream()
            .filter(l -> l.getPrixValideUnitaire() != null)
            .map(l -> l.getPrixValideUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        reprise.setMontantTotalValide(totalValide);

        return repriseMapper.toResponse(repriseRepository.save(reprise));
    }

    @Override
    @Transactional
    public RepriseResponse valider(Long idReprise) {
        Reprise reprise = repriseRepository.findById(idReprise)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + idReprise));
        reprise.setDateValidation(LocalDateTime.now());
        return repriseMapper.toResponse(repriseRepository.save(reprise));
    }

    @Override
    @Transactional
    public RepriseResponse refuser(Long idReprise, String motif) {
        Reprise reprise = repriseRepository.findById(idReprise)
            .orElseThrow(() -> new EntityNotFoundException("Reprise introuvable : " + idReprise));
        reprise.setCommentaire(motif);
        return repriseMapper.toResponse(repriseRepository.save(reprise));
    }

    // ── Helper ────────────────────────────────────────────────
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
}
