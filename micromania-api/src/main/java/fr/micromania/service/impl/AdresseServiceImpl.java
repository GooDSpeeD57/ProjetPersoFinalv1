package fr.micromania.service.impl;

import fr.micromania.dto.client.AdresseRequest;
import fr.micromania.dto.client.AdresseResponse;
import fr.micromania.entity.Adresse;
import fr.micromania.entity.Client;
import fr.micromania.entity.referentiel.TypeAdresse;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.AdresseRepository;
import fr.micromania.repository.ClientRepository;
import fr.micromania.repository.TypeAdresseRepository;
import fr.micromania.service.AdresseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdresseServiceImpl implements AdresseService {

    private final AdresseRepository adresseRepository;
    private final ClientRepository clientRepository;
    private final TypeAdresseRepository typeAdresseRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<AdresseResponse> getByClient(Long idClient) {
        return clientMapper.toAdresseResponseList(adresseRepository.findByClientId(idClient));
    }

    @Override
    @Transactional
    public AdresseResponse ajouter(Long idClient, AdresseRequest request) {
        Client client = clientRepository.findByIdAndDeletedFalse(idClient)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + idClient));

        if (request.estDefaut()) {
            adresseRepository.resetDefautByClient(idClient);
        }

        Adresse adresse = Adresse.builder()
            .client(client)
            .rue(request.rue())
            .complement(request.complement())
            .ville(request.ville())
            .codePostal(request.codePostal())
            .pays(request.pays() != null && !request.pays().isBlank() ? request.pays() : "France")
            .estDefaut(request.estDefaut())
            .typeAdresse(resoudreTypeAdresse(request))
            .build();

        return clientMapper.toAdresseResponse(adresseRepository.save(adresse));
    }

    @Override
    @Transactional
    public AdresseResponse modifier(Long idAdresse, Long idClient, AdresseRequest request) {
        Adresse adresse = adresseRepository.findById(idAdresse)
            .orElseThrow(() -> new EntityNotFoundException("Adresse introuvable : " + idAdresse));

        validerAppartenance(adresse, idClient);

        if (request.rue() != null) adresse.setRue(request.rue());
        if (request.complement() != null) adresse.setComplement(request.complement());
        if (request.ville() != null) adresse.setVille(request.ville());
        if (request.codePostal() != null) adresse.setCodePostal(request.codePostal());
        if (request.pays() != null) adresse.setPays(request.pays());
        if ((request.idTypeAdresse() != null) || (request.codeTypeAdresse() != null && !request.codeTypeAdresse().isBlank())) {
            adresse.setTypeAdresse(resoudreTypeAdresse(request));
        }

        if (request.estDefaut() && !adresse.isEstDefaut()) {
            adresseRepository.resetDefautByClient(idClient);
            adresse.setEstDefaut(true);
        }

        return clientMapper.toAdresseResponse(adresseRepository.save(adresse));
    }

    @Override
    @Transactional
    public void supprimer(Long idAdresse, Long idClient) {
        Adresse adresse = adresseRepository.findById(idAdresse)
            .orElseThrow(() -> new EntityNotFoundException("Adresse introuvable : " + idAdresse));

        validerAppartenance(adresse, idClient);

        if (adresse.isEstDefaut()) {
            throw new IllegalStateException("Impossible de supprimer l'adresse par défaut");
        }
        adresseRepository.delete(adresse);
    }

    @Override
    @Transactional
    public void setDefaut(Long idAdresse, Long idClient) {
        Adresse adresse = adresseRepository.findById(idAdresse)
            .orElseThrow(() -> new EntityNotFoundException("Adresse introuvable : " + idAdresse));

        validerAppartenance(adresse, idClient);

        adresseRepository.resetDefautByClient(idClient);
        adresse.setEstDefaut(true);
        adresseRepository.save(adresse);
    }

    private void validerAppartenance(Adresse adresse, Long idClient) {
        if (adresse.getClient() == null || !adresse.getClient().getId().equals(idClient)) {
            throw new SecurityException("Adresse n'appartient pas au client " + idClient);
        }
    }

    private TypeAdresse resoudreTypeAdresse(AdresseRequest request) {
        if (request.codeTypeAdresse() != null && !request.codeTypeAdresse().isBlank()) {
            return typeAdresseRepository.findByCode(request.codeTypeAdresse().trim().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Type d'adresse introuvable : " + request.codeTypeAdresse()));
        }
        if (request.idTypeAdresse() != null) {
            return typeAdresseRepository.findById(request.idTypeAdresse())
                .orElseThrow(() -> new EntityNotFoundException("Type d'adresse introuvable : " + request.idTypeAdresse()));
        }
        throw new IllegalStateException("Le type d'adresse est obligatoire");
    }
}
