package fr.micromania.service.impl;

import fr.micromania.entity.Adresse;
import fr.micromania.entity.Magasin;
import fr.micromania.repository.AdresseRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.service.MagasinCheckoutService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MagasinCheckoutServiceImpl implements MagasinCheckoutService {

    private final MagasinRepository  magasinRepository;
    private final AdresseRepository  adresseRepository;

    // ─────────────────────────────────────────────────────────────────────────
    //  API publique
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public Magasin resoudrePourCheckout(Adresse adresse) {
        if (adresse != null && adresse.getMagasin() != null) {
            return adresse.getMagasin();
        }

        List<Adresse> adressesMagasins = adresseRepository.findAllMagasinAddressesActives();
        if (!adressesMagasins.isEmpty()) {
            return adressesMagasins.stream()
                    .sorted(Comparator
                            .comparingDouble((Adresse a) -> scoreMagasin(adresse, a))
                            .thenComparing(a -> a.getMagasin().getNom(), String.CASE_INSENSITIVE_ORDER))
                    .map(Adresse::getMagasin)
                    .findFirst()
                    .orElseGet(() -> magasinRepository.findByActifTrue().stream().findFirst().orElse(null));
        }

        return magasinRepository.findByActifTrue().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun magasin actif disponible pour facturer la commande web"));
    }

    @Override
    public Magasin chargerRetraitOuProche(Long idMagasinRetrait, Adresse adresse) {
        if (adresse == null) {
            throw new IllegalStateException("Choisissez une adresse de référence pour le retrait magasin");
        }
        if (idMagasinRetrait != null) {
            return magasinRepository.findByIdAndActifTrue(idMagasinRetrait)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Magasin introuvable : " + idMagasinRetrait));
        }
        return resoudrePourCheckout(adresse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers privés
    // ─────────────────────────────────────────────────────────────────────────

    private double scoreMagasin(Adresse adresseClient, Adresse adresseMagasin) {
        if (adresseClient == null) return 999.0;

        Double distance = calculerDistanceKm(adresseClient, adresseMagasin);
        if (distance != null) return distance;

        String cpClient   = adresseClient.getCodePostal()  != null ? adresseClient.getCodePostal().trim()  : "";
        String cpMagasin  = adresseMagasin.getCodePostal() != null ? adresseMagasin.getCodePostal().trim() : "";
        String villeClient  = adresseClient.getVille()  != null ? adresseClient.getVille().trim()  : "";
        String villeMagasin = adresseMagasin.getVille() != null ? adresseMagasin.getVille().trim() : "";

        if (!cpClient.isBlank() && cpClient.equalsIgnoreCase(cpMagasin))       return 0.05;
        if (!villeClient.isBlank() && villeClient.equalsIgnoreCase(villeMagasin)) return 0.10;
        if (cpClient.length() >= 2 && cpMagasin.length() >= 2
                && cpClient.substring(0, 2).equals(cpMagasin.substring(0, 2))) return 0.20;
        return 999.0;
    }

    private Double calculerDistanceKm(Adresse a, Adresse b) {
        if (a == null || a.getLatitude() == null || a.getLongitude() == null
                || b == null || b.getLatitude() == null || b.getLongitude() == null) {
            return null;
        }
        double lat1 = a.getLatitude().doubleValue();
        double lon1 = a.getLongitude().doubleValue();
        double lat2 = b.getLatitude().doubleValue();
        double lon2 = b.getLongitude().doubleValue();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 6371.0 * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
    }
}
