package fr.micromania.service;

import fr.micromania.dto.magasin.CreateMagasinRequest;
import fr.micromania.dto.magasin.HoraireMagasinDto;
import fr.micromania.dto.magasin.HoraireMagasinRequest;
import fr.micromania.dto.magasin.MagasinAdminResponse;
import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;
import fr.micromania.dto.magasin.UpdateMagasinRequest;

import java.util.List;

/**
 * Opérations publiques et d'administration sur les magasins.
 */
public interface MagasinService {

    // ── Lecture publique ─────────────────────────────────────────────────────
    List<MagasinPublicResponse> getMagasinsActifs(String q);
    MagasinPublicResponse       getMagasinActifById(Long idMagasin);
    List<MagasinProximiteResponse> getMagasinsProches(Long idClient, Long idAdresse, Integer limit);

    // ── Administration ───────────────────────────────────────────────────────
    List<MagasinAdminResponse> listerTous();
    MagasinAdminResponse       creerMagasin(CreateMagasinRequest request);
    MagasinAdminResponse       modifierMagasin(Long id, UpdateMagasinRequest request);
    void                       supprimerMagasin(Long id);

    // ── Horaires ─────────────────────────────────────────────────────────────
    List<HoraireMagasinDto> getHoraires(Long idMagasin);
    List<HoraireMagasinDto> setHoraires(Long idMagasin, List<HoraireMagasinRequest> horaires);
    HoraireMagasinDto       updateHoraire(Long idMagasin, int jour, HoraireMagasinRequest request);
}
