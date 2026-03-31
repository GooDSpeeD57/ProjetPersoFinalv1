package fr.micromania.repository;

import fr.micromania.entity.stock.StockEntrepot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockEntrepotRepository extends JpaRepository<StockEntrepot, Long> {

    Optional<StockEntrepot> findByVariantIdAndEntrepotId(Long idVariant, Long idEntrepot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockEntrepot s WHERE s.variant.id = :idVariant AND s.entrepot.id = :idEntrepot")
    Optional<StockEntrepot> findByVariantIdAndEntrepotIdForUpdate(
        @Param("idVariant") Long idVariant,
        @Param("idEntrepot") Long idEntrepot
    );

    List<StockEntrepot> findByEntrepotId(Long idEntrepot);

    List<StockEntrepot> findByVariantId(Long idVariant);
}
