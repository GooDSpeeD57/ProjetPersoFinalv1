package fr.micromania.controller;

import fr.micromania.dto.client.AvatarAdminDto;
import fr.micromania.dto.client.AvatarDto;
import fr.micromania.dto.client.AvatarRequest;
import fr.micromania.service.AvatarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    // ── Public : liste des avatars actifs ─────────────────────────────────────

    @GetMapping("/avatars")
    public ResponseEntity<List<AvatarDto>> getActifs() {
        return ResponseEntity.ok(avatarService.getActifs());
    }

    // ── Admin : CRUD complet ───────────────────────────────────────────────────

    @GetMapping("/admin/avatars")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','VENDEUR')")
    public ResponseEntity<List<AvatarAdminDto>> getAll() {
        return ResponseEntity.ok(avatarService.getAll());
    }

    @PostMapping("/admin/avatars")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<AvatarAdminDto> creer(@Valid @RequestBody AvatarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avatarService.creer(request));
    }

    @PutMapping("/admin/avatars/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<AvatarAdminDto> modifier(
            @PathVariable Long id,
            @Valid @RequestBody AvatarRequest request) {

        return ResponseEntity.ok(avatarService.modifier(id, request));
    }

    @DeleteMapping("/admin/avatars/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        avatarService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ── Upload ─────────────────────────────────────────────────────────────────

    @PostMapping(value = "/admin/avatars/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<AvatarAdminDto> uploadSingle(
            @RequestPart("file")                           MultipartFile file,
            @RequestParam(value = "nom",  defaultValue = "") String nom,
            @RequestParam(value = "alt",  defaultValue = "") String alt) {

        return ResponseEntity.status(HttpStatus.CREATED).body(avatarService.uploadAvatar(file, nom, alt));
    }

    @PostMapping(value = "/admin/avatars/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<AvatarAdminDto>> uploadMultiple(
            @RequestPart("files") List<MultipartFile> files) {

        List<AvatarAdminDto> created = files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> avatarService.uploadAvatar(f, "", ""))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
