package fr.micromania.service.impl;

import fr.micromania.dto.promotion.*;
import fr.micromania.entity.commande.Promotion;
import fr.micromania.mapper.PromotionMapper;
import fr.micromania.repository.PromotionRepository;
import fr.micromania.service.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional
    public PromotionResponse creer(CreatePromotionRequest request) {
        if (promotionRepository.findByCodePromoAndActifTrue(request.codePromo()).isPresent()) {
            throw new IllegalArgumentException("Code promo déjà actif : " + request.codePromo());
        }
        Promotion promotion = Promotion.builder()
            .codePromo(request.codePromo())
            .description(request.description())
            .valeur(request.valeur())
            .dateDebut(request.dateDebut())
            .dateFin(request.dateFin())
            .montantMinimumCommande(request.montantMinimumCommande())
            .nbUtilisationsMax(request.nbUtilisationsMax())
            .nbUtilisationsMaxClient(request.nbUtilisationsMaxClient())
            .cumulable(request.cumulable())
            .actif(true)
            .nbUtilisationsActuel(0)
            .build();
        return promotionMapper.toResponse(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponse getById(Long id) {
        return promotionMapper.toResponse(promotionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Promotion introuvable : " + id)));
    }

    @Override
    public Page<PromotionResponse> getActives(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(promotionMapper::toResponse);
    }

    @Override
    public ValidatePromoResponse valider(ValidatePromoRequest request) {
        return promotionRepository.findByCodePromoAndActifTrue(request.codePromo())
            .map(promo -> {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(promo.getDateDebut()) || now.isAfter(promo.getDateFin())) {
                    return new ValidatePromoResponse(false, "Code promo hors période", BigDecimal.ZERO);
                }
                if (promo.getMontantMinimumCommande() != null
                        && request.montantCommande().compareTo(promo.getMontantMinimumCommande()) < 0) {
                    return new ValidatePromoResponse(false,
                        "Montant minimum requis : " + promo.getMontantMinimumCommande() + " €",
                        BigDecimal.ZERO);
                }
                if (promo.getNbUtilisationsMax() != null
                        && promo.getNbUtilisationsActuel() >= promo.getNbUtilisationsMax()) {
                    return new ValidatePromoResponse(false, "Quota d'utilisation atteint", BigDecimal.ZERO);
                }
                if (request.idClient() != null && promo.getNbUtilisationsMaxClient() != null) {
                    long deja = promotionRepository.countUtilisationsParClient(promo.getId(), request.idClient());
                    if (deja >= promo.getNbUtilisationsMaxClient()) {
                        return new ValidatePromoResponse(false, "Déjà utilisé le nombre de fois autorisé", BigDecimal.ZERO);
                    }
                }
                BigDecimal remise = calculerRemise(promo, request.montantCommande());
                return new ValidatePromoResponse(true,
                    "Code valide — remise de " + remise + " €", remise);
            })
            .orElse(new ValidatePromoResponse(false, "Code promo invalide ou inactif", BigDecimal.ZERO));
    }

    @Override
    @Transactional
    public void desactiver(Long id) {
        Promotion promo = promotionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Promotion introuvable : " + id));
        promo.setActif(false);
        promotionRepository.save(promo);
    }

    private BigDecimal calculerRemise(Promotion promo, BigDecimal montant) {
        return switch (promo.getTypeReduction().getCode()) {
            case "POURCENTAGE"  -> montant.multiply(promo.getValeur()).divide(BigDecimal.valueOf(100));
            case "MONTANT_FIXE" -> promo.getValeur().min(montant);
            default             -> BigDecimal.ZERO;
        };
    }
}
