package fr.micromania.repository;

import fr.micromania.entity.commande.PaiementTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementTransactionRepository extends JpaRepository<PaiementTransaction, Long> {
}
