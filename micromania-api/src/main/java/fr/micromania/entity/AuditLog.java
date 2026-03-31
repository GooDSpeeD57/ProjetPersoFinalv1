package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private Long id;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "donnees_avant", columnDefinition = "JSON")
    private String donneesAvant;

    @Column(name = "donnees_apres", columnDefinition = "JSON")
    private String donneesApres;

    @Column(name = "user_identifier", length = 100)
    private String userIdentifier;

    @Column(name = "date_operation", nullable = false, updatable = false)
    private LocalDateTime dateOperation;

    @PrePersist
    protected void onCreate() {
        dateOperation = LocalDateTime.now();
    }
}
