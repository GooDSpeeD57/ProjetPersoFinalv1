package fr.micromania.service.impl;

import fr.micromania.dto.garantie.ExtensionGarantieRequest;
import fr.micromania.dto.garantie.ExtensionGarantieResponse;
import fr.micromania.dto.garantie.GarantieResponse;
import fr.micromania.entity.ExtensionGarantie;
import fr.micromania.entity.Garantie;
import fr.micromania.entity.referentiel.TypeGarantie;
import fr.micromania.repository.ExtensionGarantieRepository;
import fr.micromania.repository.GarantieRepository;
import fr.micromania.repository.TypeGarantieRepository;
import fr.micromania.service.GarantieService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GarantieServiceImpl implements GarantieService {

    private final GarantieRepository          garantieRepository;
    private final ExtensionGarantieRepository extensionGarantieRepository;
    private final TypeGarantieRepository      typeGarantieRepository;

    @Override
    public List<GarantieResponse> getByClientId(Long clientId) {
        return garantieRepository.findByClientId(clientId).stream()
                .map(this::toGarantieResponse)
                .toList();
    }

    @Override
    public GarantieResponse getByVenteUniteId(Long idVenteUnite) {
        Garantie garantie = garantieRepository.findByVenteUniteId(idVenteUnite)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Garantie introuvable pour la vente unité : " + idVenteUnite));
        return toGarantieResponse(garantie);
    }

    @Override
    public GarantieResponse getById(Long id) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable : " + id));
        return toGarantieResponse(garantie);
    }

    @Override
    @Transactional
    public ExtensionGarantieResponse ajouterExtension(Long idGarantie, ExtensionGarantieRequest request) {
        Garantie garantie = garantieRepository.findById(idGarantie)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable : " + idGarantie));
        TypeGarantie typeGarantie = typeGarantieRepository.findById(request.idTypeGarantie())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Type de garantie introuvable : " + request.idTypeGarantie()));

        // Audit : enregistrement conservé pour traçabilité
        ExtensionGarantie extension = ExtensionGarantie.builder()
                .garantie(garantie)
                .typeGarantie(typeGarantie)
                .dateFinEtendue(request.dateFinEtendue())
                .build();
        extension = extensionGarantieRepository.save(extension);

        // Mise à jour de la Garantie principale (ONE ROW philosophy)
        garantie.setTypeGarantie(typeGarantie);
        garantie.setDateFin(request.dateFinEtendue());
        garantie.setEstEtendue(true);
        garantie.setDateExtension(LocalDate.now());
        garantieRepository.save(garantie);

        return toExtensionResponse(extension);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private GarantieResponse toGarantieResponse(Garantie g) {
        String nomProduit = (g.getVenteUnite() != null
                && g.getVenteUnite().getLigneFacture() != null
                && g.getVenteUnite().getLigneFacture().getVariant() != null)
                ? g.getVenteUnite().getLigneFacture().getVariant().getNomCommercial() : null;
        String numeroSerie = g.getVenteUnite() != null ? g.getVenteUnite().getNumeroSerie() : null;
        String code        = g.getTypeGarantie() != null ? g.getTypeGarantie().getCode()        : null;
        String description = g.getTypeGarantie() != null ? g.getTypeGarantie().getDescription() : null;
        long totalMois = (g.getDateDebut() != null && g.getDateFin() != null)
                ? g.getDateDebut().until(g.getDateFin(), ChronoUnit.MONTHS) : 0;
        String typeItem = (g.getTypeGarantie() != null
                && g.getTypeGarantie().getPrixExtension() != null
                && g.getTypeGarantie().getPrixExtension().signum() > 0)
                ? "EXTENSION" : "LEGALE";
        return new GarantieResponse(
                g.getId(),
                g.getVenteUnite() != null ? g.getVenteUnite().getId() : null,
                code, description, (int) totalMois,
                g.getDateDebut(), g.getDateFin(),
                numeroSerie, nomProduit, typeItem
        );
    }

    private ExtensionGarantieResponse toExtensionResponse(ExtensionGarantie e) {
        return new ExtensionGarantieResponse(
                e.getId(),
                e.getGarantie()     != null ? e.getGarantie().getId()            : null,
                e.getTypeGarantie() != null ? e.getTypeGarantie().getCode()      : null,
                e.getDateAchat(),
                e.getDateFinEtendue()
        );
    }
}
