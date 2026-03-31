package fr.micromania.entity;

import fr.micromania.entity.commande.LigneFacture;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "retour_ligne")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RetourLigne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour_ligne")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_retour", nullable = false)
    private RetourProduit retourProduit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ligne_facture", nullable = false)
    private LigneFacture ligneFacture;

    @Column(name = "quantite", nullable = false)
    private int quantite;

    @Column(name = "motif", length = 255)
    private String motif;
}
