package com.monprojet.boutiquejeux.dto.api.facture;

import java.util.List;

public record ApiCheckoutPanierRequest(
    Long idAdresse,
    List<Long> idsBonAchat,
    String modePaiementCode,
    String modeLivraisonCode,
    Long idMagasinRetrait
) {
    public ApiCheckoutPanierRequest(Long idAdresse,
                                    List<Long> idsBonAchat,
                                    String modePaiementCode) {
        this(idAdresse, idsBonAchat, modePaiementCode, "DOMICILE", null);
    }
}
