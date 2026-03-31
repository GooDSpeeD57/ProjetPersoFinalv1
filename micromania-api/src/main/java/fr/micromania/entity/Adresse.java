package fr.micromania.entity;

import fr.micromania.entity.referentiel.TypeAdresse;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "adresse")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adresse")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin")
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrepot")
    private Entrepot entrepot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_adresse", nullable = false)
    private TypeAdresse typeAdresse;

    @Column(name = "rue", nullable = false, length = 255)
    private String rue;

    @Column(name = "complement", length = 255)
    private String complement;

    @Column(name = "ville", nullable = false, length = 100)
    private String ville;

    @Column(name = "code_postal", nullable = false, length = 15)
    private String codePostal;

    @Column(name = "pays", nullable = false, length = 100)
    private String pays = "France";

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "est_defaut", nullable = false)
    private boolean estDefaut = false;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
