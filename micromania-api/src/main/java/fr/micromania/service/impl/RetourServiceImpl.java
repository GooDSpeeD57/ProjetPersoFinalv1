package fr.micromania.service.impl;

import fr.micromania.dto.retour.*;
import fr.micromania.entity.RetourLigne;
import fr.micromania.entity.RetourProduit;
import fr.micromania.entity.commande.Facture;
import fr.micromania.entity.commande.LigneFacture;
import fr.micromania.entity.referentiel.StatutRetour;
import fr.micromania.entity.referentiel.TypeRetour;
import fr.micromania.repository.*;
import fr.micromania.service.RetourService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetourServiceImpl implements RetourService {

    private static final Set<String> STATUTS_TERMINAUX = Set.of("TRAITE", "REFUSE", "ACCEPTE");

    private final RetourProduitRepository retourProduitRepository;
    private final RetourLigneRepository retourLigneRepository;
    private final StatutRetourRepository statutRetourRepository;
    private final TypeRetourRepository typeRetourRepository;
    private final FactureRepository factureRepository;
    private final LigneFactureRepository ligneFactureRepository;

    @Override
    @Transactional
    public RetourResponse creer(CreateRetourRequest request) {
        Facture facture = factureRepository.findById(request.idFacture())
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable : " + request.idFacture()));

        TypeRetour typeRetour = typeRetourRepository.findById(request.idTypeRetour())
                .orElseThrow(() -> new EntityNotFoundException("TypeRetour introuvable : " + request.idTypeRetour()));

        StatutRetour statutDemande = chargerStatutRetour("DEMANDE");

        RetourProduit retour = RetourProduit.builder()
                .referenceRetour("RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .facture(facture)
                .client(facture.getClient())
                .statutRetour(statutDemande)
                .typeRetour(typeRetour)
                .motifRetour(request.motifRetour())
                .build();

        for (RetourLigneRequest ligneReq : request.lignes()) {
            LigneFacture ligneFacture = ligneFactureRepository.findById(ligneReq.idLigneFacture())
                    .orElseThrow(() -> new EntityNotFoundException("LigneFacture introuvable : " + ligneReq.idLigneFacture()));

            RetourLigne ligne = RetourLigne.builder()
                    .retourProduit(retour)
                    .ligneFacture(ligneFacture)
                    .quantite(ligneReq.quantite())
                    .motif(ligneReq.motif())
                    .build();

            retour.getLignes().add(ligne);
        }

        return toResponse(retourProduitRepository.save(retour));
    }

    @Override
    public RetourResponse getById(Long id) {
        return toResponse(retourProduitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Retour introuvable : " + id)));
    }

    @Override
    public List<RetourResponse> getByFacture(Long idFacture) {
        return retourProduitRepository.findByFactureId(idFacture)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RetourResponse updateStatut(Long id, UpdateStatutRetourRequest request) {
        RetourProduit retour = retourProduitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Retour introuvable : " + id));

        retour.setStatutRetour(chargerStatutRetour(request.codeStatut()));

        if (request.montantRembourse() != null) {
            retour.setMontantRembourse(request.montantRembourse());
        }

        if (STATUTS_TERMINAUX.contains(request.codeStatut()) && retour.getDateTraitement() == null) {
            retour.setDateTraitement(LocalDateTime.now());
        }

        return toResponse(retourProduitRepository.save(retour));
    }

    private StatutRetour chargerStatutRetour(String code) {
        return statutRetourRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("StatutRetour introuvable : " + code));
    }

    private RetourResponse toResponse(RetourProduit retour) {
        List<RetourLigneResponse> ligneResponses = retour.getLignes().stream()
                .map(l -> new RetourLigneResponse(
                        l.getId(),
                        l.getLigneFacture().getId(),
                        l.getQuantite(),
                        l.getMotif()))
                .toList();

        return new RetourResponse(
                retour.getId(),
                retour.getReferenceRetour(),
                retour.getFacture().getId(),
                retour.getClient() != null ? retour.getClient().getId() : null,
                retour.getStatutRetour().getCode(),
                retour.getTypeRetour().getCode(),
                retour.getMotifRetour(),
                retour.getDateDemande(),
                retour.getDateTraitement(),
                retour.getMontantRembourse(),
                ligneResponses);
    }
}
