package fr.micromania.service;

import fr.micromania.dto.panier.*;

public interface PanierService {

    PanierResponse getPanierActif(Long idClient, String canalVente);

    PanierResponse addLigne(Long idClient, AddLignePanierRequest request);

    PanierResponse updateLigne(Long idClient, Long idLigne, UpdateLignePanierRequest request);

    PanierResponse removeLigne(Long idClient, Long idLigne);

    void vider(Long idClient, String canalVente);

    PanierResponse appliquerCodePromo(Long idClient, String codePromo, String canalVente);

    PanierResponse retirerCodePromo(Long idClient, String canalVente);
}
