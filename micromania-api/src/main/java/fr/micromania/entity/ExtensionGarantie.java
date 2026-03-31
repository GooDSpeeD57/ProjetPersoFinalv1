package fr.micromania.entity;

import fr.micromania.entity.referentiel.TypeGarantie;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "extension_garantie")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExtensionGarantie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_extension")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_garantie", nullable = false)
    private Garantie garantie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_garantie", nullable = false)
    private TypeGarantie typeGarantie;

    @Column(name = "date_achat", nullable = false, updatable = false)
    private LocalDateTime dateAchat;

    @Column(name = "date_fin_etendue", nullable = false)
    private LocalDate dateFinEtendue;

    @PrePersist
    protected void onCreate() {
        dateAchat = LocalDateTime.now();
    }
}
