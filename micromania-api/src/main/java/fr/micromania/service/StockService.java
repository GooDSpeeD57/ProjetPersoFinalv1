package fr.micromania.service;

import fr.micromania.dto.stock.*;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StockService {

    List<StockMagasinResponse> getStockVariantTousMagasins(Long idVariant);

    StockMagasinResponse getStockVariantMagasin(Long idVariant, Long idMagasin);

    List<StockEntrepotResponse> getStockVariantTousEntrepots(Long idVariant);

    StockMagasinResponse ajusterStockMagasin(AjustementStockRequest request);

    StockEntrepotResponse ajusterStockEntrepot(AjustementStockRequest request);

    List<StockMagasinResponse> getRuptureMagasin(Long idMagasin);

    List<MouvementStockResponse> getMouvements(Long idVariant, Long idMagasin, Pageable pageable);
}
