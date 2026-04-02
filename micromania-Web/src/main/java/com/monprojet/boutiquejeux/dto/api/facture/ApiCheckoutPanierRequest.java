package com.monprojet.boutiquejeux.dto.api.facture;

public record ApiCheckoutPanierRequest(
    Long idAdresse,
    Long idBonAchat,
    String modePaiementCode
) {}
