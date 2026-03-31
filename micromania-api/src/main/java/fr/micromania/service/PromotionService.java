package fr.micromania.service;

import fr.micromania.dto.promotion.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionService {

    PromotionResponse creer(CreatePromotionRequest request);

    PromotionResponse getById(Long id);

    Page<PromotionResponse> getActives(Pageable pageable);

    ValidatePromoResponse valider(ValidatePromoRequest request);

    void desactiver(Long id);
}
