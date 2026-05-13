package fr.micromania.dto.client;

public record AvatarAdminDto(
        Long    id,
        String  nom,
        String  url,
        String  alt,
        boolean decorative,
        boolean actif
) {}
