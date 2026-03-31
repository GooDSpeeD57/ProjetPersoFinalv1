package fr.micromania.entity.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statut_commande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_commande")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
}
