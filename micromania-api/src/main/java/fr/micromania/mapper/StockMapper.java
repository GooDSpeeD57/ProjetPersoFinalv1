package fr.micromania.mapper;

import fr.micromania.dto.stock.*;
import fr.micromania.entity.stock.MouvementStock;
import fr.micromania.entity.stock.StockEntrepot;
import fr.micromania.entity.stock.StockMagasin;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface StockMapper {

    @Mapping(target = "idVariant",    source = "variant.id")
    @Mapping(target = "nomCommercial",source = "variant.nomCommercial")
    @Mapping(target = "sku",          source = "variant.sku")
    @Mapping(target = "idMagasin",    source = "magasin.id")
    @Mapping(target = "nomMagasin",   source = "magasin.nom")
    StockMagasinResponse toStockMagasinResponse(StockMagasin stock);

    List<StockMagasinResponse> toStockMagasinResponseList(List<StockMagasin> stocks);

    @Mapping(target = "idVariant",    source = "variant.id")
    @Mapping(target = "nomCommercial",source = "variant.nomCommercial")
    @Mapping(target = "sku",          source = "variant.sku")
    @Mapping(target = "idEntrepot",   source = "entrepot.id")
    @Mapping(target = "nomEntrepot",  source = "entrepot.nom")
    StockEntrepotResponse toStockEntrepotResponse(StockEntrepot stock);

    List<StockEntrepotResponse> toStockEntrepotResponseList(List<StockEntrepot> stocks);

    @Mapping(target = "idVariant",     source = "variant.id")
    @Mapping(target = "nomCommercial", source = "variant.nomCommercial")
    @Mapping(target = "typeMouvement", source = "typeMouvement.code")
    @Mapping(target = "lieu",          expression = "java(mouvement.getMagasin() != null ? mouvement.getMagasin().getNom() : mouvement.getEntrepot().getNom())")
    MouvementStockResponse toMouvementResponse(MouvementStock mouvement);

    List<MouvementStockResponse> toMouvementResponseList(List<MouvementStock> mouvements);
}
