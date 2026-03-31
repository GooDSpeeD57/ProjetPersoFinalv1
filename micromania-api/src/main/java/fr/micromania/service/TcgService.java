package fr.micromania.service;

import fr.micromania.dto.tcg.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TcgService {

    Page<TcgCarteSummary> search(Long idMagasin, String nomCarte, String codeEtat,
                                  String langue, Boolean foil, Long idJeu, Pageable pageable);

    TcgCarteResponse getById(Long id);

    TcgCarteResponse ajouter(AddTcgCarteInventaireRequest request);

    void marquerVendu(Long idInventaire);
}
