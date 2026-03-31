package fr.micromania.mapper;

import fr.micromania.dto.promotion.*;
import fr.micromania.entity.commande.Promotion;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PromotionMapper {

    @Mapping(target = "typeReduction",      source = "typeReduction.code")
    @Mapping(target = "nbUtilisationsActuel",source = "nbUtilisationsActuel")
    PromotionResponse toResponse(Promotion promotion);

    List<PromotionResponse> toResponseList(List<Promotion> promotions);
}
