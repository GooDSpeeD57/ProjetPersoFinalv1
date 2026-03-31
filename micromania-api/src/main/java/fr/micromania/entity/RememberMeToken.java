package fr.micromania.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "remember_me_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RememberMeToken {

    @Id
    @Column(name = "serie", length = 64)
    private String serie;

    @Column(name = "token_value", nullable = false, length = 64)
    private String tokenValue;

    @Column(name = "date_derniere", nullable = false)
    private LocalDateTime dateDerniere;

    @Column(name = "username", nullable = false, length = 150)
    private String username;

    @Column(name = "user_type", nullable = false, length = 10)
    private String userType;
}
