package com.monprojet.boutiquejeux.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.client.ClientDetailDto;
import com.monprojet.boutiquejeux.dto.client.ClientSummaryDto;
import com.monprojet.boutiquejeux.exception.ApiException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service pour les appels API liés aux clients.
 * Centralise la logique de recherche deux-étapes utilisée dans plusieurs contrôleurs.
 */
public class ClientService {

    private static ClientService instance;
    private final ApiClient api = ApiClient.getInstance();

    private ClientService() {}

    public static ClientService getInstance() {
        if (instance == null) instance = new ClientService();
        return instance;
    }

    /**
     * Recherche un client par texte libre, retourne le premier résultat.
     * Lève ApiException si aucun résultat.
     */
    public ClientSummaryDto rechercherPremier(String q) throws ApiException {
        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
        PageDto<ClientSummaryDto> page = api.get(
            "/clients?q=" + encoded + "&size=1",
            new TypeReference<PageDto<ClientSummaryDto>>() {}
        );
        if (page == null || page.content == null || page.content.isEmpty()) {
            throw new ApiException(404, "Aucun client trouvé pour « " + q + " »");
        }
        return page.content.get(0);
    }

    /**
     * Récupère le profil complet d'un client (avec soldePoints).
     */
    public ClientDetailDto getDetail(Long id) throws ApiException {
        return api.get("/clients/" + id, ClientDetailDto.class);
    }

    /**
     * Recherche client deux-étapes : search → profil complet.
     */
    public ClientDetailDto rechercherDetail(String q) throws ApiException {
        ClientSummaryDto summary = rechercherPremier(q);
        return getDetail(summary.id);
    }

    /**
     * Recherche clients paginée (pour l'onglet Recherche).
     */
    public List<ClientSummaryDto> rechercher(String q) throws ApiException {
        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
        PageDto<ClientSummaryDto> page = api.get(
            "/clients?q=" + encoded,
            new TypeReference<PageDto<ClientSummaryDto>>() {}
        );
        return page != null && page.content != null ? page.content : List.of();
    }
}
