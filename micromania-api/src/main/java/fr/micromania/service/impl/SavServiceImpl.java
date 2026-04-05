package fr.micromania.service.impl;

import fr.micromania.dto.sav.*;
import fr.micromania.entity.DossierSav;
import fr.micromania.entity.VenteUnite;
import fr.micromania.entity.referentiel.StatutSav;
import fr.micromania.mapper.SavMapper;
import fr.micromania.repository.*;
import fr.micromania.service.SavService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavServiceImpl implements SavService {

    private final DossierSavRepository dossierSavRepository;
    private final VenteUniteRepository venteUniteRepository;
    private final GarantieRepository garantieRepository;
    private final EmployeRepository employeRepository;
    private final StatutSavRepository statutSavRepository;
    private final SavMapper savMapper;

    @Override
    @Transactional
    public DossierSavResponse ouvrir(CreateDossierSavRequest request) {
        VenteUnite venteUnite = venteUniteRepository.findById(request.idVenteUnite())
                .orElseThrow(() -> new EntityNotFoundException("VenteUnite introuvable : " + request.idVenteUnite()));

        DossierSav dossier = DossierSav.builder()
                .referenceSav("SAV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .venteUnite(venteUnite)
                .statutSav(chargerStatutSav("OUVERT"))
                .panneDeclaree(request.panneDeclaree())
                .build();

        if (request.idGarantie() != null) {
            dossier.setGarantie(garantieRepository.findById(request.idGarantie())
                    .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable : " + request.idGarantie())));
        }

        if (request.idEmploye() != null) {
            dossier.setEmploye(employeRepository.findByIdAndDeletedFalse(request.idEmploye())
                    .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + request.idEmploye())));
        }

        return savMapper.toResponse(dossierSavRepository.save(dossier));
    }

    @Override
    public DossierSavResponse getById(Long id) {
        return savMapper.toResponse(dossierSavRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier SAV introuvable : " + id)));
    }

    @Override
    public DossierSavResponse getByReference(String reference) {
        return savMapper.toResponse(dossierSavRepository.findByReferenceSav(reference)
                .orElseThrow(() -> new EntityNotFoundException("Dossier SAV introuvable : " + reference)));
    }

    @Override
    public Page<DossierSavResponse> filter(String statut, Long idEmploye, Pageable pageable) {
        return dossierSavRepository.filter(statut, idEmploye, pageable)
                .map(savMapper::toResponse);
    }

    @Override
    @Transactional
    public DossierSavResponse update(Long id, UpdateDossierSavRequest request) {
        DossierSav dossier = dossierSavRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier SAV introuvable : " + id));

        if (request.diagnostic() != null) {
            dossier.setDiagnostic(request.diagnostic());
        }
        if (request.solutionApportee() != null) {
            dossier.setSolutionApportee(request.solutionApportee());
        }
        if (request.idEmploye() != null) {
            dossier.setEmploye(employeRepository.findByIdAndDeletedFalse(request.idEmploye())
                    .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + request.idEmploye())));
        }
        if (request.codeStatut() != null && !request.codeStatut().isBlank()) {
            dossier.setStatutSav(chargerStatutSav(request.codeStatut()));
            if ("CLOTURE".equals(request.codeStatut()) && dossier.getDateCloture() == null) {
                dossier.setDateCloture(LocalDateTime.now());
            }
        }

        return savMapper.toResponse(dossierSavRepository.save(dossier));
    }

    @Override
    @Transactional
    public DossierSavResponse cloturer(Long id, String solution) {
        DossierSav dossier = dossierSavRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier SAV introuvable : " + id));

        dossier.setSolutionApportee(solution);
        dossier.setStatutSav(chargerStatutSav("CLOTURE"));
        dossier.setDateCloture(LocalDateTime.now());

        return savMapper.toResponse(dossierSavRepository.save(dossier));
    }

    private StatutSav chargerStatutSav(String code) {
        return statutSavRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Statut SAV introuvable : " + code));
    }
}