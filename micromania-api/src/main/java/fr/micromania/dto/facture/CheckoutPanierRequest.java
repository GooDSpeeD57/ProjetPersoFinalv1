package fr.micromania.dto.facture;

import jakarta.validation.constraints.Pattern;

public record CheckoutPanierRequest(
    Long idAdresse,
    Long idBonAchat,
    @Pattern(regexp = "CB|PAYPAL|APPLE_PAY|GOOGLE_PAY", message = "Mode de paiement non supporté")
    String modePaiementCode
) {}
