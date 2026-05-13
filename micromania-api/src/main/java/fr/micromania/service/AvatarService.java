package fr.micromania.service;

import fr.micromania.dto.client.AvatarAdminDto;
import fr.micromania.dto.client.AvatarDto;
import fr.micromania.dto.client.AvatarRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Gestion des avatars utilisateurs.
 */
public interface AvatarService {

    List<AvatarDto> getActifs();

    List<AvatarAdminDto> getAll();

    AvatarAdminDto creer(AvatarRequest request);

    AvatarAdminDto modifier(Long id, AvatarRequest request);

    void supprimer(Long id);

    AvatarAdminDto uploadAvatar(MultipartFile file, String nom, String alt);
}
