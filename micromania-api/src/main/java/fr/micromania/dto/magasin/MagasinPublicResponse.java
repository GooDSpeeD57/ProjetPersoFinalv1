package fr.micromania.dto.magasin;

public record MagasinPublicResponse(
        Long id,
        String nom,
        String telephone,
        String email,
        String rue,
        String complement,
        String codePostal,
        String ville,
        String pays,
        String adresseComplete,
        Double latitude,
        Double longitude
) {
}
