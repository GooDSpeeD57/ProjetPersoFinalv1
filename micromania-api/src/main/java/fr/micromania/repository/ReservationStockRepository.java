package fr.micromania.repository;

import fr.micromania.entity.stock.ReservationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationStockRepository extends JpaRepository<ReservationStock, Long> {

    List<ReservationStock> findByCommandeId(Long idCommande);
}
