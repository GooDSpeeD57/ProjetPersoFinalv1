package fr.micromania;

import fr.micromania.entity.Avatar;
import fr.micromania.entity.Client;
import fr.micromania.entity.referentiel.TypeFidelite;
import fr.micromania.repository.ClientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Client — tests intégration JPA")
class ClientIntegrationTest {

    @Autowired ClientRepository clientRepository;
    @Autowired EntityManager entityManager;

    Client client;

    @BeforeEach
    void setUp() {
        TypeFidelite tf = entityManager.createQuery(
                        "select tf from TypeFidelite tf where tf.code = :code", TypeFidelite.class)
                .setParameter("code", "NORMAL")
                .getSingleResult();

        Avatar avatar = entityManager.createQuery(
                        "select a from Avatar a where a.actif = true order by a.id", Avatar.class)
                .setMaxResults(1)
                .getSingleResult();

        client = new Client();
        client.setPseudo("integtest");
        client.setNom("Test");
        client.setPrenom("Integ");
        client.setEmail("integ@test.fr");
        client.setTelephone("0699887766");
        client.setMotDePasse("$2a$12$hashed");
        client.setDateNaissance(LocalDate.of(1990, 1, 1));
        client.setAvatar(avatar);
        client.setTypeFidelite(tf);
        client.setActif(true);
        client.setDeleted(false);
        client.setEmailVerifie(true);
        client.setCompteActive(true);
        client.setRgpdConsent(true);
        client.setDoitDefinirMotDePasse(false);
    }

    @Test
    @DisplayName("findByEmailAndDeletedFalse — retrouve le client par email")
    void findByEmail_ok() {
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByEmailAndDeletedFalse("integ@test.fr");

        assertThat(found).isPresent();
        assertThat(found.get().getPseudo()).isEqualTo("integtest");
    }

    @Test
    @DisplayName("findByEmailAndDeletedFalse — ne retrouve pas un client supprimé")
    void findByEmail_clientSupprime_vide() {
        client.setDeleted(true);
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByEmailAndDeletedFalse("integ@test.fr");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail — retourne true si email pris")
    void existsByEmail_pris() {
        clientRepository.save(client);

        assertThat(clientRepository.existsByEmailAndDeletedFalse("integ@test.fr")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail — retourne false si email libre")
    void existsByEmail_libre() {
        assertThat(clientRepository.existsByEmailAndDeletedFalse("libre@test.fr")).isFalse();
    }

    @Test
    @DisplayName("softDelete — marque deleted=true et actif=false")
    void softDelete_ok() {
        Client saved = clientRepository.save(client);

        clientRepository.softDelete(saved.getId());
        clientRepository.flush();
        entityManager.clear();

        Client updated = clientRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
        assertThat(updated.isActif()).isFalse();
    }

    @Test
    @DisplayName("search — trouve le client par nom partiel")
    void search_parNom() {
        clientRepository.save(client);

        var results = clientRepository.search("integ", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getPseudo()).isEqualTo("integtest");
    }

    @Test
    @DisplayName("search — recherche insensible à la casse")
    void search_insensibleCasse() {
        clientRepository.save(client);

        var results = clientRepository.search("INTEG", PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("search — retourne vide si aucun résultat")
    void search_aucunResultat() {
        clientRepository.save(client);

        var results = clientRepository.search("zzzzinexistant", PageRequest.of(0, 10));

        assertThat(results.getContent()).isEmpty();
    }
}