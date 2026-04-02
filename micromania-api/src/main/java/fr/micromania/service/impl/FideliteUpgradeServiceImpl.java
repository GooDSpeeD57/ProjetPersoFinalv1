package fr.micromania.service.impl;

import fr.micromania.entity.Client;
import fr.micromania.entity.PointsFidelite;
import fr.micromania.entity.referentiel.TypeFidelite;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.PointsFideliteRepository;
import fr.micromania.repository.TypeFideliteRepository;
import fr.micromania.service.FideliteUpgradeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FideliteUpgradeServiceImpl implements FideliteUpgradeService {

    private final ClientRepository clientRepository;
    private final PointsFideliteRepository pointsRepository;
    private final TypeFideliteRepository typeFideliteRepository;

    @Override
    public void appliquerUpgradeAutomatique(Long idClient) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        PointsFidelite points = pointsRepository.findByClientId(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Compte points introuvable pour le client : " + idClient));

        BigDecimal totalAchatsAnnuel = points.getTotalAchatsAnnuel() != null
            ? points.getTotalAchatsAnnuel()
            : BigDecimal.ZERO;

        TypeFidelite typeActuel = client.getTypeFidelite();
        TypeFidelite typeCible = determinerTypeCible(totalAchatsAnnuel, typeActuel);

        if (typeCible == null || Objects.equals(typeCible.getId(), typeActuel.getId())) {
            return;
        }

        client.setTypeFidelite(typeCible);
        clientRepository.save(client);

        log.info("Upgrade fidélité automatique : client={} {} -> {} (total achats annuel={})",
            idClient,
            typeActuel.getCode(),
            typeCible.getCode(),
            totalAchatsAnnuel);
    }

    private TypeFidelite determinerTypeCible(BigDecimal totalAchatsAnnuel, TypeFidelite typeActuel) {
        TypeFidelite typeCible = typeActuel;

        TypeFidelite premium = typeFideliteRepository.findByCode("PREMIUM").orElse(null);
        if (estEligible(totalAchatsAnnuel, premium) && rang(premium) > rang(typeCible)) {
            typeCible = premium;
        }

        TypeFidelite ultimate = typeFideliteRepository.findByCode("ULTIMATE").orElse(null);
        if (estEligible(totalAchatsAnnuel, ultimate) && rang(ultimate) > rang(typeCible)) {
            typeCible = ultimate;
        }

        return typeCible;
    }

    private boolean estEligible(BigDecimal totalAchatsAnnuel, TypeFidelite typeFidelite) {
        return typeFidelite != null
            && typeFidelite.getSeuilUpgradeEuro() != null
            && totalAchatsAnnuel.compareTo(typeFidelite.getSeuilUpgradeEuro()) >= 0;
    }

    private int rang(TypeFidelite typeFidelite) {
        if (typeFidelite == null || typeFidelite.getCode() == null) {
            return 0;
        }
        return switch (typeFidelite.getCode()) {
            case "NORMAL" -> 1;
            case "PREMIUM" -> 2;
            case "ULTIMATE" -> 3;
            default -> 0;
        };
    }
}
