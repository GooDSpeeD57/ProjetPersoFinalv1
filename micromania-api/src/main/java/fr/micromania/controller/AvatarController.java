package fr.micromania.controller;

import fr.micromania.dto.client.AvatarDto;
import fr.micromania.mapper.ClientMapper;
import fr.micromania.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarRepository avatarRepository;
    private final ClientMapper clientMapper;

    @GetMapping
    public ResponseEntity<List<AvatarDto>> getActifs() {
        return ResponseEntity.ok(
                avatarRepository.findByActifTrueOrderByIdAsc().stream()
                        .map(clientMapper::toAvatarDto)
                        .toList()
        );
    }
}