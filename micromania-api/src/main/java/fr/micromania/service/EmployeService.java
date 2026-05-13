package fr.micromania.service;

import fr.micromania.dto.employe.CreateEmployeRequest;
import fr.micromania.dto.employe.EmployeResponse;
import fr.micromania.dto.employe.UpdateEmployeRequest;

import java.util.List;

/**
 * Gestion des employés.
 */
public interface EmployeService {

    List<EmployeResponse> lister(Long magasinId, String q);

    EmployeResponse me(Long id);

    EmployeResponse getById(Long id);

    EmployeResponse creer(CreateEmployeRequest request);

    EmployeResponse modifier(Long id, UpdateEmployeRequest request);

    void supprimer(Long id);
}
