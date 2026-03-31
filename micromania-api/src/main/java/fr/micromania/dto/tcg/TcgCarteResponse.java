package fr.micromania.dto.tcg;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TcgCarteResponse(
    Long idInventaire,
    String jeu,
    String extension,
    String codeExtension,
    String nomCarte,
    String numeroCarte,
    String rarete,
    String etatCarte,
    String langue,
    boolean foil,
    boolean reverseFoil,
    boolean alternateArt,
    String gradation,
    BigDecimal prixVente,
    String magasin,
    boolean disponible,
    LocalDateTime dateCreation
) {}
