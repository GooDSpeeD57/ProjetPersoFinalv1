package fr.micromania.service.impl;

import fr.micromania.dto.tcg.*;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.tcg.TcgCarteInventaire;
import fr.micromania.entity.tcg.TcgCarteReference;
import fr.micromania.mapper.TcgMapper;
import fr.micromania.repository.*;
import fr.micromania.service.TcgService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TcgServiceImpl implements TcgService {

    private final TcgCarteInventaireRepository inventaireRepository;
    private final MagasinRepository magasinRepository;
    private final TcgMapper tcgMapper;

    @Override
    public Page<TcgCarteSummary> search(Long idMagasin, String nomCarte, String codeEtat,
                                         String langue, Boolean foil, Long idJeu, Pageable pageable) {
        return inventaireRepository.search(idMagasin, nomCarte, codeEtat, langue, foil, idJeu, pageable)
            .map(tcgMapper::toSummary);
    }

    @Override
    public TcgCarteResponse getById(Long id) {
        return tcgMapper.toResponse(inventaireRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Carte TCG introuvable : " + id)));
    }

    @Override
    @Transactional
    public TcgCarteResponse ajouter(AddTcgCarteInventaireRequest request) {
        Magasin magasin = magasinRepository.findById(request.idMagasin())
            .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + request.idMagasin()));

        TcgCarteInventaire carte = TcgCarteInventaire.builder()
            .magasin(magasin)
            .langue(request.langue())
            .foil(request.foil())
            .reverseFoil(request.reverseFoil())
            .alternateArt(request.alternateArt())
            .gradation(request.gradation())
            .prixVente(request.prixVente())
            .prixAchat(request.prixAchat())
            .provenance(request.provenance())
            .disponible(true)
            .build();

        return tcgMapper.toResponse(inventaireRepository.save(carte));
    }

    @Override
    @Transactional
    public void marquerVendu(Long idInventaire) {
        TcgCarteInventaire carte = inventaireRepository.findById(idInventaire)
            .orElseThrow(() -> new EntityNotFoundException("Carte TCG introuvable : " + idInventaire));
        carte.setDisponible(false);
        inventaireRepository.save(carte);
    }
}
