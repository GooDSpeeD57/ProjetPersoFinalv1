package fr.micromania.dto.facture;

import jakarta.validation.constraints.Pattern;
import java.util.List;

public record CheckoutPanierRequest(
    Long idAdresse,
    List<Long> idsBonAchat,
    @Pattern(regexp = "CB|PAYPAL|APPLE_PAY|GOOGLE_PAY", message = "Mode de paiement non supporté")
    String modePaiementCode,
    @Pattern(regexp = "DOMICILE|RETRAIT_MAGASIN|LIVRAISON_MAGASIN", message = "Mode de livraison non supporté")
    String modeLivraisonCode,
    Long idMagasinRetrait
) {}
