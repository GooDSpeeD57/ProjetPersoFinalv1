package com.monprojet.boutiquejeux.dto.api.facture;

public record ApiCheckoutPanierRequest(
    Long idAdresse,
    Long idBonAchat,
    String modePaiementCode,
    String modeLivraisonCode,
    Long idMagasinRetrait
) {
    public ApiCheckoutPanierRequest(Long idAdresse,
                                    Long idBonAchat,
                                    String modePaiementCode) {
        this(idAdresse, idBonAchat, modePaiementCode, "DOMICILE", null);
    }
}
