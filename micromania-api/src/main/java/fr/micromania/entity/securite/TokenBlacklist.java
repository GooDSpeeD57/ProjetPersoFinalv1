package fr.micromania.entity.securite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenBlacklist {

    @Id
    @Column(name = "jti", length = 36, nullable = false)
    private String jti;

    @Column(name = "expire_le", nullable = false)
    private LocalDateTime expireLe;
}
