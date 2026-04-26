package fr.micromania.entity;

import fr.micromania.entity.referentiel.TypeFidelite;
import fr.micromania.entity.Magasin;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long id;

    @Column(name = "pseudo", nullable = false, unique = true, length = 50)
    private String pseudo;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telephone", nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_avatar", nullable = false)
    private Avatar avatar;

    @Column(name = "numero_carte_fidelite", unique = true, length = 50)
    private String numeroCarteFidelite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_fidelite", nullable = false)
    private TypeFidelite typeFidelite;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "rgpd_consent", nullable = false)
    private boolean rgpdConsent = false;

    @Column(name = "rgpd_consent_date")
    private LocalDateTime rgpdConsentDate;

    @Column(name = "rgpd_consent_ip", length = 45)
    private String rgpdConsentIp;

    @Column(name = "email_verifie", nullable = false)
    private boolean emailVerifie = false;

    @Column(name = "date_verification_email")
    private LocalDateTime dateVerificationEmail;

    @Column(name = "token_verification_email", length = 255)
    private String tokenVerificationEmail;

    @Column(name = "token_verification_expire_le")
    private LocalDateTime tokenVerificationExpireLe;

    @Column(name = "telephone_verifie", nullable = false)
    private boolean telephoneVerifie = false;

    @Column(name = "date_verification_telephone")
    private LocalDateTime dateVerificationTelephone;

    @Column(name = "token_verification_telephone", length = 255)
    private String tokenVerificationTelephone;

    @Column(name = "token_verification_telephone_expire_le")
    private LocalDateTime tokenVerificationTelephoneExpireLe;

    @Column(name = "compte_active", nullable = false)
    private boolean compteActive = false;

    @Column(name = "cree_par_employe", nullable = false)
    private boolean creeParEmploye = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe_createur")
    private Employe employeCreateur;

    @Column(name = "doit_definir_mot_de_passe", nullable = false)
    private boolean doitDefinirMotDePasse = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_magasin_favori")
    private Magasin magasinFavori;

    @Column(name = "demande_suppression", nullable = false)
    private boolean demandeSuppression = false;

    @Column(name = "date_suppression")
    private LocalDateTime dateSuppression;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @Column(name = "date_derniere_connexion")
    private LocalDateTime dateDerniereConnexion;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        dateCreation = now;
        dateModification = now;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
