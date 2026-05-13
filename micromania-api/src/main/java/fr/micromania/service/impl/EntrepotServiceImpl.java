package fr.micromania.service.impl;

import fr.micromania.dto.entrepot.EntrepotResponse;
import fr.micromania.entity.Entrepot;
import fr.micromania.repository.EntrepotRepository;
import fr.micromania.service.EntrepotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepotServiceImpl implements EntrepotService {

    private final EntrepotRepository entrepotRepository;

    @Override
    public List<EntrepotResponse> lister() {
        return entrepotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EntrepotResponse getById(Long id) {
        return entrepotRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Entrepôt introuvable : " + id));
    }

    private EntrepotResponse toResponse(Entrepot e) {
        return new EntrepotResponse(
                e.getId(),
                e.getNom()         != null ? e.getNom()         : "",
                e.getCode()        != null ? e.getCode()        : "",
                e.getTelephone()   != null ? e.getTelephone()   : "",
                e.getEmail()       != null ? e.getEmail()       : "",
                e.getResponsable() != null ? e.getResponsable() : "",
                e.isActif()
        );
    }
}
