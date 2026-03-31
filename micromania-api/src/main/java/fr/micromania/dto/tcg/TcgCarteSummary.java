package fr.micromania.dto.tcg;

import java.math.BigDecimal;

public record TcgCarteSummary(
    Long idInventaire,
    String nomCarte,
    String extension,
    String rarete,
    String etatCarte,
    String langue,
    boolean foil,
    BigDecimal prixVente,
    String magasin
) {}
