package fr.micromania.service.impl;

import fr.micromania.dto.stock.*;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.catalog.ProduitVariant;
import fr.micromania.entity.referentiel.TypeMouvement;
import fr.micromania.entity.stock.MouvementStock;
import fr.micromania.entity.stock.StockMagasin;
import fr.micromania.entity.stock.StockEntrepot;
import fr.micromania.mapper.StockMapper;
import fr.micromania.repository.*;
import fr.micromania.service.StockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockMagasinRepository    stockMagasinRepo;
    private final StockEntrepotRepository   stockEntrepotRepo;
    private final MouvementStockRepository  mouvementRepo;
    private final ProduitVariantRepository  variantRepo;
    private final MagasinRepository         magasinRepo;
    private final EntrepotRepository        entrepotRepo;
    private final StockMapper               stockMapper;
    private final TypeMouvementRepository   typeMouvementRepository;

    @Override
    public List<StockMagasinResponse> getStockVariantTousMagasins(Long idVariant) {
        return stockMapper.toStockMagasinResponseList(stockMagasinRepo.findByVariantId(idVariant));
    }

    @Override
    public StockMagasinResponse getStockVariantMagasin(Long idVariant, Long idMagasin) {
        StockMagasin stock = stockMagasinRepo.findByVariantIdAndMagasinId(idVariant, idMagasin)
            .orElseThrow(() -> new EntityNotFoundException(
                "Stock introuvable pour variant=" + idVariant + " magasin=" + idMagasin));
        return stockMapper.toStockMagasinResponse(stock);
    }

    @Override
    public List<StockEntrepotResponse> getStockVariantTousEntrepots(Long idVariant) {
        return stockMapper.toStockEntrepotResponseList(stockEntrepotRepo.findByVariantId(idVariant));
    }

    @Override
    @Transactional
    public StockMagasinResponse ajusterStockMagasin(AjustementStockRequest request) {
        StockMagasin stock = stockMagasinRepo
            .findByVariantIdAndMagasinIdForUpdate(request.idVariant(), request.idMagasin())
            .orElseGet(() -> creerStockMagasin(request.idVariant(), request.idMagasin()));

        appliquerDeltaMagasin(stock, request.sourceStock(), request.delta());
        stock = stockMagasinRepo.save(stock);

        enregistrerMouvement(request, "MAGASIN");
        log.info("Ajustement stock magasin: variant={} magasin={} delta={}",
            request.idVariant(), request.idMagasin(), request.delta());
        return stockMapper.toStockMagasinResponse(stock);
    }

    @Override
    @Transactional
    public StockEntrepotResponse ajusterStockEntrepot(AjustementStockRequest request) {
        StockEntrepot stock = stockEntrepotRepo
            .findByVariantIdAndEntrepotIdForUpdate(request.idVariant(), request.idEntrepot())
            .orElseGet(() -> creerStockEntrepot(request.idVariant(), request.idEntrepot()));

        int nouveau = stock.getQuantiteNeuf() + request.delta();
        if (nouveau < 0) throw new IllegalStateException("Stock entrepôt insuffisant");
        stock.setQuantiteNeuf(nouveau);
        stock = stockEntrepotRepo.save(stock);

        enregistrerMouvement(request, "ENTREPOT");
        return stockMapper.toStockEntrepotResponse(stock);
    }

    @Override
    public List<StockMagasinResponse> getRuptureMagasin(Long idMagasin) {
        return stockMapper.toStockMagasinResponseList(stockMagasinRepo.findRuptureByMagasin(idMagasin));
    }

    @Override
    public List<MouvementStockResponse> getMouvements(Long idVariant, Long idMagasin, Pageable pageable) {
        return mouvementRepo.filter(idVariant, idMagasin, null, null, pageable)
            .map(stockMapper::toMouvementResponse)
            .getContent();
    }

    // ── Helpers privés ────────────────────────────────────────

    private void appliquerDeltaMagasin(StockMagasin stock, String source, int delta) {
        switch (source) {
            case "NEUF"     -> { int n = stock.getQuantiteNeuf() + delta;     if (n < 0) throw new IllegalStateException("Stock neuf insuffisant");     stock.setQuantiteNeuf(n); }
            case "OCCASION" -> { int o = stock.getQuantiteOccasion() + delta; if (o < 0) throw new IllegalStateException("Stock occasion insuffisant"); stock.setQuantiteOccasion(o); }
            case "REPRISE"  -> { int r = stock.getQuantiteReprise() + delta;  if (r < 0) throw new IllegalStateException("Stock reprise insuffisant");  stock.setQuantiteReprise(r); }
            default         -> throw new IllegalArgumentException("Source stock invalide : " + source);
        }
    }

    private StockMagasin creerStockMagasin(Long idVariant, Long idMagasin) {
        ProduitVariant variant = variantRepo.findById(idVariant)
            .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));
        Magasin magasin = magasinRepo.findById(idMagasin)
            .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + idMagasin));
        return StockMagasin.builder()
            .variant(variant).magasin(magasin)
            .quantiteNeuf(0).quantiteOccasion(0).quantiteReprise(0)
            .quantiteReservee(0).quantiteDisponible(0)
            .build();
    }

    private StockEntrepot creerStockEntrepot(Long idVariant, Long idEntrepot) {
        ProduitVariant variant = variantRepo.findById(idVariant)
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + idVariant));
        fr.micromania.entity.Entrepot entrepot = entrepotRepo.findById(idEntrepot)
                .orElseThrow(() -> new EntityNotFoundException("Entrepôt introuvable : " + idEntrepot));
        return StockEntrepot.builder()
                .variant(variant)
                .entrepot(entrepot)
                .quantiteNeuf(0).quantiteReservee(0).quantiteDisponible(0)
                .build();
    }

    private void enregistrerMouvement(AjustementStockRequest req, String lieu) {
        ProduitVariant variant = variantRepo.findById(req.idVariant())
                .orElseThrow(() -> new EntityNotFoundException("Variant introuvable : " + req.idVariant()));

        TypeMouvement typeMouvement = typeMouvementRepository.findByCode("AJUSTEMENT")
                .orElseThrow(() -> new EntityNotFoundException("Type mouvement introuvable : AJUSTEMENT"));

        MouvementStock.MouvementStockBuilder builder = MouvementStock.builder()
                .variant(variant)
                .typeMouvement(typeMouvement)
                .quantite(req.delta())
                .sourceStock(req.sourceStock())
                .commentaire(req.commentaire());

        if ("MAGASIN".equals(lieu)) {
            if (req.idMagasin() == null) {
                throw new IllegalArgumentException("idMagasin obligatoire pour un mouvement magasin");
            }
            builder.magasin(
                    magasinRepo.findById(req.idMagasin())
                            .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + req.idMagasin()))
            );
        } else if ("ENTREPOT".equals(lieu)) {
            if (req.idEntrepot() == null) {
                throw new IllegalArgumentException("idEntrepot obligatoire pour un mouvement entrepôt");
            }
            builder.entrepot(
                    entrepotRepo.findById(req.idEntrepot())
                            .orElseThrow(() -> new EntityNotFoundException("Entrepôt introuvable : " + req.idEntrepot()))
            );
        } else {
            throw new IllegalArgumentException("Lieu de mouvement invalide : " + lieu);
        }

        mouvementRepo.save(builder.build());
    }
}