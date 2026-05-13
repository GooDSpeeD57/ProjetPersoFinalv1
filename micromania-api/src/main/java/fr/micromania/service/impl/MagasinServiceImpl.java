package fr.micromania.service.impl;

import fr.micromania.dto.magasin.CreateMagasinRequest;
import fr.micromania.dto.magasin.HoraireMagasinDto;
import fr.micromania.dto.magasin.HoraireMagasinRequest;
import fr.micromania.dto.magasin.MagasinAdminResponse;
import fr.micromania.dto.magasin.MagasinProximiteResponse;
import fr.micromania.dto.magasin.MagasinPublicResponse;
import fr.micromania.dto.magasin.UpdateMagasinRequest;
import fr.micromania.entity.Adresse;
import fr.micromania.entity.HoraireMagasin;
import fr.micromania.entity.Magasin;
import fr.micromania.repository.AdresseRepository;
import fr.micromania.repository.HoraireMagasinRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.service.MagasinService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MagasinServiceImpl implements MagasinService {

    private final MagasinRepository        magasinRepository;
    private final AdresseRepository        adresseRepository;
    private final HoraireMagasinRepository horaireRepository;

    private static final String[] JOURS_SEMAINE =
            {"", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

    @Override
    public List<MagasinPublicResponse> getMagasinsActifs(String q) {
        String filtre = normaliser(q);

        Map<Long, Adresse> adresseParMagasin = adresseRepository.findAllMagasinAddressesActives().stream()
                .collect(Collectors.toMap(
                        a -> a.getMagasin().getId(),
                        a -> a,
                        (a1, a2) -> a1.isEstDefaut() ? a1 : a2
                ));

        return magasinRepository.findByActifTrueOrderByNomAsc().stream()
                .map(magasin -> toPublicResponse(magasin, adresseParMagasin.get(magasin.getId())))
                .filter(magasin -> filtre.isBlank() || match(magasin, filtre))
                .sorted(Comparator.comparing(MagasinPublicResponse::nom, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public MagasinPublicResponse getMagasinActifById(Long idMagasin) {
        Magasin magasin = magasinRepository.findByIdAndActifTrue(idMagasin)
                .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + idMagasin));

        return toPublicResponse(magasin);
    }

    @Override
    public List<MagasinProximiteResponse> getMagasinsProches(Long idClient, Long idAdresse, Integer limit) {
        Adresse adresseClient = adresseRepository.findById(idAdresse)
                .orElseThrow(() -> new EntityNotFoundException("Adresse introuvable : " + idAdresse));

        if (adresseClient.getClient() == null || !adresseClient.getClient().getId().equals(idClient)) {
            throw new EntityNotFoundException("Adresse introuvable : " + idAdresse);
        }

        List<Adresse> adressesMagasins = adresseRepository.findAllMagasinAddressesActives();
        int max = limit != null && limit > 0 ? Math.min(limit, 12) : 5;

        return adressesMagasins.stream()
                .map(adresseMagasin -> toProjection(adresseClient, adresseMagasin))
                .sorted(Comparator
                        .comparingDouble(MagasinProjection::score)
                        .thenComparing(proj -> proj.magasin().getNom(), String.CASE_INSENSITIVE_ORDER))
                .limit(max)
                .map(this::toProximiteResponse)
                .toList();
    }

    // ── Administration ───────────────────────────────────────────────────────

    @Override
    public List<MagasinAdminResponse> listerTous() {
        return magasinRepository.findAll().stream().map(this::toAdmin).toList();
    }

    @Override
    @Transactional
    public MagasinAdminResponse creerMagasin(CreateMagasinRequest req) {
        Magasin m = Magasin.builder()
                .nom(req.nom())
                .telephone(req.telephone())
                .email(req.email())
                .actif(true)
                .build();
        return toAdmin(magasinRepository.save(m));
    }

    @Override
    @Transactional
    public MagasinAdminResponse modifierMagasin(Long id, UpdateMagasinRequest req) {
        Magasin m = magasinRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Magasin introuvable : " + id));
        if (req.nom()       != null) m.setNom(req.nom());
        if (req.telephone() != null) m.setTelephone(req.telephone());
        if (req.email()     != null) m.setEmail(req.email());
        if (req.actif()     != null) m.setActif(req.actif());
        return toAdmin(magasinRepository.save(m));
    }

    @Override
    @Transactional
    public void supprimerMagasin(Long id) {
        Magasin m = magasinRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Magasin introuvable : " + id));
        m.setActif(false);
        magasinRepository.save(m);
    }

    // ── Horaires ─────────────────────────────────────────────────────────────

    @Override
    public List<HoraireMagasinDto> getHoraires(Long idMagasin) {
        magasinRepository.findByIdAndActifTrue(idMagasin)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Magasin introuvable : " + idMagasin));
        return toHoraireDtos(horaireRepository.findByMagasinIdOrderByJourSemaine(idMagasin));
    }

    @Override
    @Transactional
    public List<HoraireMagasinDto> setHoraires(Long idMagasin, List<HoraireMagasinRequest> horaires) {
        Magasin magasin = magasinRepository.findById(idMagasin)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Magasin introuvable : " + idMagasin));
        horaireRepository.deleteByMagasinId(idMagasin);
        horaireRepository.flush();
        List<HoraireMagasin> saved = horaireRepository.saveAll(
                horaires.stream().map(h -> HoraireMagasin.builder()
                        .magasin(magasin)
                        .jourSemaine(h.jourSemaine())
                        .heureOuverture(h.ferme() ? null : h.heureOuverture())
                        .heureFermeture(h.ferme() ? null : h.heureFermeture())
                        .ferme(h.ferme())
                        .build()
                ).toList()
        );
        saved.sort(java.util.Comparator.comparingInt(HoraireMagasin::getJourSemaine));
        return toHoraireDtos(saved);
    }

    @Override
    @Transactional
    public HoraireMagasinDto updateHoraire(Long idMagasin, int jour, HoraireMagasinRequest req) {
        Magasin magasin = magasinRepository.findById(idMagasin)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Magasin introuvable : " + idMagasin));
        HoraireMagasin h = horaireRepository.findByMagasinIdAndJourSemaine(idMagasin, jour)
                .orElseGet(() -> HoraireMagasin.builder().magasin(magasin).jourSemaine(jour).build());
        h.setFerme(req.ferme());
        h.setHeureOuverture(req.ferme() ? null : req.heureOuverture());
        h.setHeureFermeture(req.ferme() ? null : req.heureFermeture());
        return toHoraireDto(horaireRepository.save(h));
    }

    private MagasinAdminResponse toAdmin(Magasin m) {
        return new MagasinAdminResponse(
                m.getId(), m.getNom(), m.getTelephone(), m.getEmail(),
                m.isActif(), m.getDateCreation(), m.getDateModification());
    }

    private List<HoraireMagasinDto> toHoraireDtos(List<HoraireMagasin> list) {
        return list.stream().map(this::toHoraireDto).toList();
    }

    private HoraireMagasinDto toHoraireDto(HoraireMagasin h) {
        String ouv  = h.getHeureOuverture()  != null ? h.getHeureOuverture().toString()  : null;
        String ferm = h.getHeureFermeture()  != null ? h.getHeureFermeture().toString() : null;
        return new HoraireMagasinDto(
                h.getJourSemaine(),
                JOURS_SEMAINE[h.getJourSemaine()],
                ouv, ferm, h.isFerme()
        );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private MagasinPublicResponse toPublicResponse(Magasin magasin) {
        Adresse adresse = adresseRepository.findFirstByMagasinIdAndEstDefautTrue(magasin.getId())
                .orElseGet(() -> adresseRepository.findByMagasinId(magasin.getId()).stream().findFirst().orElse(null));
        return toPublicResponse(magasin, adresse);
    }

    private MagasinPublicResponse toPublicResponse(Magasin magasin, Adresse adresse) {

        String rue = adresse != null ? adresse.getRue() : null;
        String complement = adresse != null ? adresse.getComplement() : null;
        String codePostal = adresse != null ? adresse.getCodePostal() : null;
        String ville = adresse != null ? adresse.getVille() : null;
        String pays = adresse != null ? adresse.getPays() : null;
        Double latitude = adresse != null && adresse.getLatitude() != null ? adresse.getLatitude().doubleValue() : null;
        Double longitude = adresse != null && adresse.getLongitude() != null ? adresse.getLongitude().doubleValue() : null;

        return new MagasinPublicResponse(
                magasin.getId(),
                magasin.getNom(),
                magasin.getTelephone(),
                magasin.getEmail(),
                rue,
                complement,
                codePostal,
                ville,
                pays,
                construireAdresseComplete(rue, complement, codePostal, ville, pays),
                latitude,
                longitude
        );
    }

    private MagasinProjection toProjection(Adresse adresseClient, Adresse adresseMagasin) {
        Double distanceKm = calculerDistanceKm(adresseClient, adresseMagasin);
        double score = distanceKm != null ? distanceKm : scoreSansGps(adresseClient, adresseMagasin);
        return new MagasinProjection(adresseMagasin.getMagasin(), adresseMagasin, distanceKm, score);
    }

    private MagasinProximiteResponse toProximiteResponse(MagasinProjection projection) {
        Adresse adresse = projection.adresse();
        Magasin magasin = projection.magasin();
        String libelleDistance = projection.distanceKm() != null
                ? String.format(java.util.Locale.US, "%.1f km", projection.distanceKm())
                : libelleApproximation(adresse);

        return new MagasinProximiteResponse(
                magasin.getId(),
                magasin.getNom(),
                magasin.getTelephone(),
                magasin.getEmail(),
                adresse.getRue(),
                adresse.getComplement(),
                adresse.getVille(),
                adresse.getCodePostal(),
                adresse.getPays(),
                adresse.getLatitude(),
                adresse.getLongitude(),
                projection.distanceKm(),
                libelleDistance
        );
    }

    private String libelleApproximation(Adresse adresse) {
        return adresse.getCodePostal() + " • " + adresse.getVille();
    }

    private Double calculerDistanceKm(Adresse a, Adresse b) {
        if (a.getLatitude() == null || a.getLongitude() == null || b.getLatitude() == null || b.getLongitude() == null) {
            return null;
        }

        double lat1 = a.getLatitude().doubleValue();
        double lon1 = a.getLongitude().doubleValue();
        double lat2 = b.getLatitude().doubleValue();
        double lon2 = b.getLongitude().doubleValue();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double originLat = Math.toRadians(lat1);
        double targetLat = Math.toRadians(lat2);

        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(originLat) * Math.cos(targetLat) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
        return 6371.0 * c;
    }

    private double scoreSansGps(Adresse adresseClient, Adresse adresseMagasin) {
        String cpClient = safe(adresseClient.getCodePostal());
        String cpMagasin = safe(adresseMagasin.getCodePostal());
        String villeClient = safe(adresseClient.getVille());
        String villeMagasin = safe(adresseMagasin.getVille());

        if (!cpClient.isBlank() && cpClient.equalsIgnoreCase(cpMagasin)) return 0.05;
        if (!villeClient.isBlank() && villeClient.equalsIgnoreCase(villeMagasin)) return 0.10;
        if (cpClient.length() >= 2 && cpMagasin.length() >= 2 && cpClient.substring(0, 2).equals(cpMagasin.substring(0, 2))) {
            return 0.20;
        }
        return 999.0;
    }

    private boolean match(MagasinPublicResponse magasin, String filtre) {
        return contient(magasin.nom(), filtre)
                || contient(magasin.ville(), filtre)
                || contient(magasin.codePostal(), filtre)
                || contient(magasin.adresseComplete(), filtre);
    }

    private boolean contient(String valeur, String filtre) {
        return normaliser(valeur).contains(filtre);
    }

    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.toLowerCase(Locale.ROOT).trim();
    }

    private String construireAdresseComplete(String rue, String complement, String codePostal, String ville, String pays) {
        String ligne1 = joinNonBlank(", ", rue, complement);
        String ligne2 = joinNonBlank(" ", codePostal, ville);
        return joinNonBlank(" • ", ligne1, ligne2, pays);
    }

    private String joinNonBlank(String separator, String... values) {
        return Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + separator + right)
                .orElse("");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private record MagasinProjection(Magasin magasin, Adresse adresse, Double distanceKm, double score) {}
}
