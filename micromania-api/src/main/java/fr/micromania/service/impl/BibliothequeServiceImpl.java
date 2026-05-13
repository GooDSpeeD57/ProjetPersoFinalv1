package fr.micromania.service.impl;

import fr.micromania.dto.bibliotheque.BibliothequeResponse;
import fr.micromania.repository.BibliothequeClientRepository;
import fr.micromania.service.BibliothequeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BibliothequeServiceImpl implements BibliothequeService {

    private final BibliothequeClientRepository bibliothequeClientRepository;

    @Override
    public List<BibliothequeResponse> getByClientId(Long idClient) {
        return bibliothequeClientRepository.findByClientId(idClient).stream()
                .map(b -> new BibliothequeResponse(
                        b.getId(),
                        b.getVariant()    != null ? b.getVariant().getId()              : null,
                        b.getVariant()    != null ? b.getVariant().getNomCommercial()   : null,
                        b.getVariant()    != null ? b.getVariant().getSku()             : null,
                        b.getFacture()    != null ? b.getFacture().getId()              : null,
                        b.getCleProduit() != null ? b.getCleProduit().getCleActivation(): null,
                        b.getDateAttribution()
                ))
                .toList();
    }
}
