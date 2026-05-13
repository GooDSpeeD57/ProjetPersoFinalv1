package fr.micromania.service.impl;

import fr.micromania.dto.client.AvatarAdminDto;
import fr.micromania.dto.client.AvatarDto;
import fr.micromania.dto.client.AvatarRequest;
import fr.micromania.entity.Avatar;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.AvatarRepository;
import fr.micromania.service.AvatarService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final ClientMapper     clientMapper;

    @Value("${app.media.images-path:./media/images}")
    private String imagesPath;

    @Override
    public List<AvatarDto> getActifs() {
        return avatarRepository.findByActifTrueOrderByIdAsc().stream()
                .map(clientMapper::toAvatarDto)
                .toList();
    }

    @Override
    public List<AvatarAdminDto> getAll() {
        return avatarRepository.findAll().stream()
                .sorted(Comparator.comparing(Avatar::getId))
                .map(this::toAdminDto)
                .toList();
    }

    @Override
    @Transactional
    public AvatarAdminDto creer(AvatarRequest request) {
        Avatar avatar = Avatar.builder()
                .nom(request.nom())
                .url(request.url())
                .alt(request.alt() != null ? request.alt() : "Avatar utilisateur")
                .decorative(request.decorative())
                .actif(request.actif())
                .build();
        return toAdminDto(avatarRepository.save(avatar));
    }

    @Override
    @Transactional
    public AvatarAdminDto modifier(Long id, AvatarRequest request) {
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avatar introuvable : " + id));
        avatar.setNom(request.nom());
        avatar.setUrl(request.url());
        avatar.setAlt(request.alt() != null ? request.alt() : "Avatar utilisateur");
        avatar.setDecorative(request.decorative());
        avatar.setActif(request.actif());
        return toAdminDto(avatarRepository.save(avatar));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!avatarRepository.existsById(id)) {
            throw new EntityNotFoundException("Avatar introuvable : " + id);
        }
        avatarRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AvatarAdminDto uploadAvatar(MultipartFile file, String nomParam, String altParam) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier manquant");
        }
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar";
        String filename = System.currentTimeMillis() + "-" + originalName
                .replaceAll("[^a-zA-Z0-9._-]", "_").toLowerCase();

        Path targetDir  = Paths.get(imagesPath, "avatars").toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(filename);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile);
            log.info("Avatar sauvegardé : {}", targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder l'avatar : " + e.getMessage(), e);
        }

        String url = "/images/avatars/" + filename;
        String nom = (nomParam != null && !nomParam.isBlank())
                ? nomParam
                : originalName.contains(".") ? originalName.substring(0, originalName.lastIndexOf('.')) : originalName;
        String alt = (altParam != null && !altParam.isBlank()) ? altParam : "Avatar utilisateur";

        Avatar avatar = Avatar.builder()
                .nom(nom).url(url).alt(alt).decorative(false).actif(true)
                .build();
        return toAdminDto(avatarRepository.save(avatar));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private AvatarAdminDto toAdminDto(Avatar a) {
        return new AvatarAdminDto(a.getId(), a.getNom(), a.getUrl(), a.getAlt(), a.isDecorative(), a.isActif());
    }
}
