package fr.micromania.service.impl;

import fr.micromania.dto.employe.CreateEmployeRequest;
import fr.micromania.dto.employe.EmployeResponse;
import fr.micromania.dto.employe.UpdateEmployeRequest;
import fr.micromania.entity.Employe;
import fr.micromania.entity.Magasin;
import fr.micromania.entity.referentiel.Role;
import fr.micromania.repository.EmployeRepository;
import fr.micromania.repository.MagasinRepository;
import fr.micromania.repository.RoleRepository;
import fr.micromania.service.EmployeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeServiceImpl implements EmployeService {

    private final EmployeRepository employeRepository;
    private final RoleRepository    roleRepository;
    private final MagasinRepository magasinRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    public List<EmployeResponse> lister(Long magasinId, String q) {
        List<Employe> employes;
        if (q != null && !q.isBlank()) {
            employes = employeRepository.search(q, Pageable.unpaged()).getContent();
            if (magasinId != null) {
                Long id = magasinId;
                employes = employes.stream()
                        .filter(e -> id.equals(e.getMagasin().getId()))
                        .toList();
            }
        } else if (magasinId != null) {
            employes = employeRepository.findByMagasinIdAndDeletedFalse(magasinId);
        } else {
            employes = employeRepository.findAll().stream()
                    .filter(e -> !e.isDeleted())
                    .toList();
        }
        return employes.stream().map(this::toResponse).toList();
    }

    @Override
    public EmployeResponse me(Long id) {
        return employeRepository.findByIdAndDeletedFalse(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + id));
    }

    @Override
    public EmployeResponse getById(Long id) {
        return employeRepository.findByIdAndDeletedFalse(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + id));
    }

    @Override
    @Transactional
    public EmployeResponse creer(CreateEmployeRequest req) {
        if (employeRepository.existsByEmailAndDeletedFalse(req.email())) {
            throw new DuplicateKeyException("Email déjà utilisé : " + req.email());
        }
        Role role = roleRepository.findById(req.idRole())
                .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable : " + req.idRole()));
        Magasin magasin = magasinRepository.findById(req.idMagasin())
                .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + req.idMagasin()));

        Employe employe = Employe.builder()
                .nom(req.nom())
                .prenom(req.prenom())
                .email(req.email())
                .telephone(req.telephone())
                .motDePasse(passwordEncoder.encode(req.motDePasse()))
                .role(role)
                .magasin(magasin)
                .dateEmbauche(req.dateEmbauche())
                .actif(true)
                .deleted(false)
                .build();
        return toResponse(employeRepository.save(employe));
    }

    @Override
    @Transactional
    public EmployeResponse modifier(Long id, UpdateEmployeRequest req) {
        Employe employe = employeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable : " + id));

        if (req.nom()          != null) employe.setNom(req.nom());
        if (req.prenom()       != null) employe.setPrenom(req.prenom());
        if (req.telephone()    != null) employe.setTelephone(req.telephone());
        if (req.dateEmbauche() != null) employe.setDateEmbauche(req.dateEmbauche());
        if (req.actif()        != null) employe.setActif(req.actif());

        if (req.email() != null && !req.email().equals(employe.getEmail())) {
            if (employeRepository.existsByEmailAndDeletedFalse(req.email())) {
                throw new DuplicateKeyException("Email déjà utilisé : " + req.email());
            }
            employe.setEmail(req.email());
        }
        if (req.motDePasse() != null && !req.motDePasse().isBlank()) {
            employe.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        }
        if (req.idRole() != null) {
            Role role = roleRepository.findById(req.idRole())
                    .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable : " + req.idRole()));
            employe.setRole(role);
        }
        if (req.idMagasin() != null) {
            Magasin magasin = magasinRepository.findById(req.idMagasin())
                    .orElseThrow(() -> new EntityNotFoundException("Magasin introuvable : " + req.idMagasin()));
            employe.setMagasin(magasin);
        }
        return toResponse(employeRepository.save(employe));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employé introuvable : " + id);
        }
        employeRepository.softDelete(id);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private EmployeResponse toResponse(Employe e) {
        return new EmployeResponse(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getTelephone(),
                e.getRole().getCode(),
                e.getRole().getLibelle(),
                e.getMagasin().getId(),
                e.getMagasin().getNom(),
                e.getDateEmbauche(),
                e.isActif(),
                e.getDateCreation()
        );
    }
}
