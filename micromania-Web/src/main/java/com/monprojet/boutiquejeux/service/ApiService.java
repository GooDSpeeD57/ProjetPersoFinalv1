package com.monprojet.boutiquejeux.service;

import com.monprojet.boutiquejeux.dto.api.auth.ApiAuthResponse;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiAvisClient;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiCategorie;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitDetail;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiProduitSummary;
import com.monprojet.boutiquejeux.dto.api.catalog.ApiVariantSummary;
import com.monprojet.boutiquejeux.dto.api.referentiel.ApiEdition;
import com.monprojet.boutiquejeux.dto.api.client.ApiAdresse;
import com.monprojet.boutiquejeux.dto.api.client.ApiAdresseRequest;
import com.monprojet.boutiquejeux.dto.api.client.ApiAvatar;
import com.monprojet.boutiquejeux.dto.api.client.ApiBonAchat;
import com.monprojet.boutiquejeux.dto.api.client.ApiClient;
import com.monprojet.boutiquejeux.dto.api.client.ApiFideliteDetail;
import com.monprojet.boutiquejeux.dto.api.client.ApiHistoriquePoints;
import com.monprojet.boutiquejeux.dto.api.client.ApiPoints;
import com.monprojet.boutiquejeux.dto.api.client.ApiUpdateClientRequest;
import com.monprojet.boutiquejeux.dto.api.common.ApiPage;
import com.monprojet.boutiquejeux.dto.api.facture.ApiCheckoutPanierRequest;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureDetail;
import com.monprojet.boutiquejeux.dto.api.facture.ApiFactureSummary;
import com.monprojet.boutiquejeux.dto.api.garantie.ApiGarantie;
import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasin;
import com.monprojet.boutiquejeux.dto.api.magasin.ApiMagasinProche;
import com.monprojet.boutiquejeux.dto.api.stock.ApiStockCheckout;
import com.monprojet.boutiquejeux.dto.api.stock.ApiStockEntrepotCheckout;
import com.monprojet.boutiquejeux.dto.api.panier.ApiPanier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ApiPage<ApiProduitSummary> getProduits(int page, int size, String q, Long categorie, String niveau) {
        return getProduits(page, size, q, categorie, niveau, null, null, null, null);
    }

    public ApiPage<ApiProduitSummary> getProduits(int page, int size, String q, Long categorie, String niveau,
                                                   String plateforme, String famille, String etat) {
        return getProduits(page, size, q, categorie, niveau, plateforme, famille, etat, null);
    }

    public ApiPage<ApiProduitSummary> getProduits(int page, int size, String q, Long categorie, String niveau,
                                                   String plateforme, String famille, String etat, String tri) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/produits")
                                .queryParam("page", page)
                                .queryParam("size", size);
                        if (q != null && !q.isBlank()) uriBuilder.queryParam("q", q);
                        if (categorie != null) uriBuilder.queryParam("categorie", categorie);
                        if (niveau != null && !niveau.isBlank()) uriBuilder.queryParam("niveau", niveau);
                        if (plateforme != null && !plateforme.isBlank()) uriBuilder.queryParam("plateforme", plateforme);
                        if (famille != null && !famille.isBlank()) uriBuilder.queryParam("famille", famille);
                        if (etat != null && !etat.isBlank()) uriBuilder.queryParam("etat", etat);
                        if (tri != null && !tri.isBlank()) uriBuilder.queryParam("tri", tri);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("getProduits error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return emptyPage();
        } catch (Exception e) {
            log.error("getProduits error", e);
            return emptyPage();
        }
    }


    public ApiPage<ApiVariantSummary> getCatalogue(int page, int size, String q, Long categorie,
                                                    String plateforme, String famille, String etat,
                                                    String edition, String tri) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/catalogue")
                                .queryParam("page", page)
                                .queryParam("size", size);
                        if (q          != null && !q.isBlank())          uriBuilder.queryParam("q",          q);
                        if (categorie  != null)                           uriBuilder.queryParam("categorie",  categorie);
                        if (plateforme != null && !plateforme.isBlank())  uriBuilder.queryParam("plateforme", plateforme);
                        if (famille    != null && !famille.isBlank())     uriBuilder.queryParam("famille",    famille);
                        if (etat       != null && !etat.isBlank())        uriBuilder.queryParam("etat",       etat);
                        if (edition    != null && !edition.isBlank())     uriBuilder.queryParam("edition",    edition);
                        if (tri        != null && !tri.isBlank())         uriBuilder.queryParam("tri",        tri);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("getCatalogue error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return emptyPageVariant();
        } catch (Exception e) {
            log.error("getCatalogue error", e);
            return emptyPageVariant();
        }
    }

    public List<ApiEdition> getEditions() {
        try {
            return restClient.get()
                    .uri("/referentiel/editions")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("getEditions error", e);
            return List.of();
        }
    }

    public List<com.monprojet.boutiquejeux.dto.api.referentiel.ApiTypeGarantie> getTypesGarantie(Long categorieId) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/referentiel/types-garantie");
                        if (categorieId != null) uriBuilder.queryParam("categorieId", categorieId);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<com.monprojet.boutiquejeux.dto.api.referentiel.ApiTypeGarantie>>() {});
        } catch (Exception e) {
            log.error("getTypesGarantie error", e);
            return List.of();
        }
    }

    public ApiPage<ApiProduitSummary> getProduitsTries(int page, int size, String q, Long categorie, String niveau, String... tris) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/produits")
                                .queryParam("page", page)
                                .queryParam("size", size);
                        if (q != null && !q.isBlank()) uriBuilder.queryParam("q", q);
                        if (categorie != null) uriBuilder.queryParam("categorie", categorie);
                        if (niveau != null && !niveau.isBlank()) uriBuilder.queryParam("niveau", niveau);
                        if (tris != null) {
                            for (String tri : tris) {
                                if (tri != null && !tri.isBlank()) {
                                    uriBuilder.queryParam("sort", tri);
                                }
                            }
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientResponseException e) {
            log.error("getProduitsTries error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return emptyPage();
        } catch (Exception e) {
            log.error("getProduitsTries error", e);
            return emptyPage();
        }
    }

    public List<ApiProduitSummary> getProduitsMisEnAvant() {
        try {
            List<ApiProduitSummary> produits = restClient.get().uri("/produits/mis-en-avant")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return produits != null ? produits : List.of();
        } catch (RestClientResponseException e) {
            log.error("getProduitsMisEnAvant error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.error("getProduitsMisEnAvant error", e);
            return List.of();
        }
    }

    public List<ApiCategorie> getCategories() {
        try {
            List<ApiCategorie> categories = restClient.get().uri("/categories")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return categories != null ? categories : List.of();
        } catch (RestClientResponseException e) {
            log.error("getCategories error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.error("getCategories error", e);
            return List.of();
        }
    }

    public ApiProduitDetail getProduitDetail(Long id) {
        try {
            return restClient.get().uri("/produits/{id}", id)
                    .retrieve()
                    .body(ApiProduitDetail.class);
        } catch (RestClientResponseException e) {
            log.error("getProduitDetail error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("getProduitDetail error", e);
            return null;
        }
    }

    public boolean peutSoumettreAvis(String jwtToken, Long idProduit) {
        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = restClient.get()
                    .uri("/produits/{idProduit}/avis/eligible", idProduit)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(java.util.Map.class);
            return resp != null && Boolean.TRUE.equals(resp.get("eligible"));
        } catch (Exception e) {
            log.error("peutSoumettreAvis error", e);
            return false;
        }
    }

    public ApiAvisClient getMonAvisProduit(String jwtToken, Long idProduit) {
        try {
            return restClient.get().uri("/produits/{idProduit}/avis/me", idProduit)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiAvisClient.class);
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 404 || status == 204) {
                return null;
            }
            log.error("getMonAvisProduit error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("getMonAvisProduit error", e);
            return null;
        }
    }

    public ApiAvisClient soumettreAvisProduit(String jwtToken, Long idProduit, Integer note, String commentaire) {
        try {
            return restClient.post().uri("/produits/{idProduit}/avis", idProduit)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "note", note,
                            "commentaire", commentaire != null ? commentaire : ""
                    ))
                    .retrieve()
                    .body(ApiAvisClient.class);
        } catch (RestClientResponseException e) {
            log.error("soumettreAvisProduit error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public List<ApiMagasin> getMagasins(String q) {
        try {
            List<ApiMagasin> magasins = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/magasins");
                        if (q != null && !q.isBlank()) {
                            uriBuilder.queryParam("q", q);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return magasins != null ? magasins : List.of();
        } catch (RestClientResponseException e) {
            log.error("getMagasins error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.error("getMagasins error", e);
            return List.of();
        }
    }

    public ApiMagasin getMagasinById(Long idMagasin) {
        try {
            return restClient.get().uri("/magasins/{idMagasin}", idMagasin)
                    .retrieve()
                    .body(ApiMagasin.class);
        } catch (RestClientResponseException e) {
            log.error("getMagasinById error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("getMagasinById error", e);
            return null;
        }
    }

    public ApiAuthResponse inscription(String pseudo, String nom, String prenom, LocalDate dateNaissance,
                                       String email, String telephone, String motDePasse, boolean rgpdConsent) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("pseudo", pseudo);
            body.put("nom", nom);
            body.put("prenom", prenom);
            body.put("dateNaissance", dateNaissance);
            body.put("email", email);
            body.put("telephone", telephone);
            body.put("motDePasse", motDePasse);
            body.put("rgpdConsent", rgpdConsent);
            return restClient.post().uri("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(ApiAuthResponse.class);
        } catch (RestClientResponseException e) {
            log.error("inscription error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiAuthResponse loginClient(String email, String motDePasse) {
        return loginClient(email, motDePasse, false);
    }

    public ApiAuthResponse loginClient(String email, String motDePasse, boolean rememberMe) {
        try {
            return restClient.post().uri("/auth/login/client")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", email, "motDePasse", motDePasse, "rememberMe", rememberMe))
                    .retrieve()
                    .body(ApiAuthResponse.class);
        } catch (RestClientResponseException e) {
            log.warn("loginClient error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiAuthResponse loginClientWithRememberMe(String rememberMeToken) {
        try {
            return restClient.post().uri("/auth/remember-me/client")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("rememberMeToken", rememberMeToken))
                    .retrieve()
                    .body(ApiAuthResponse.class);
        } catch (RestClientResponseException e) {
            log.warn("loginClientWithRememberMe error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void logout(String jwtToken) {
        logout(jwtToken, null);
    }

    public void logout(String jwtToken, String rememberMeToken) {
        try {
            RestClient.RequestBodySpec request = restClient.post().uri("/auth/logout");

            if (jwtToken != null && !jwtToken.isBlank()) {
                request.header(HttpHeaders.AUTHORIZATION, bearer(jwtToken));
            }
            if (rememberMeToken != null && !rememberMeToken.isBlank()) {
                request.header("X-Remember-Me-Token", rememberMeToken);
            }

            request.retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.warn("logout error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    public List<ApiAvatar> getAvatars(String jwtToken) {
        try {
            List<ApiAvatar> avatars = restClient.get().uri("/avatars")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return avatars != null ? avatars : List.of();
        } catch (RestClientResponseException e) {
            log.error("getAvatars error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.error("getAvatars error", e);
            return List.of();
        }
    }

    public ApiClient getClientMe(String jwtToken) {
        try {
            return restClient.get().uri("/clients/me")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiClient.class);
        } catch (RestClientResponseException e) {
            log.error("getClientMe error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        }
    }

    public ApiPoints getClientPoints(String jwtToken) {
        try {
            return restClient.get().uri("/clients/me/points")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiPoints.class);
        } catch (RestClientResponseException e) {
            log.error("getClientPoints error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        }
    }

    public ApiFideliteDetail getClientFidelite(String jwtToken) {
        try {
            return restClient.get().uri("/clients/me/fidelite")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiFideliteDetail.class);
        } catch (RestClientResponseException e) {
            log.error("getClientFidelite error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        }
    }

    public List<ApiBonAchat> getClientBonsAchat(String jwtToken) {
        try {
            List<ApiBonAchat> bons = restClient.get().uri("/clients/me/bons-achat")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ApiBonAchat>>() {});
            return bons != null ? bons : List.of();
        } catch (RestClientResponseException e) {
            log.error("getClientBonsAchat error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        }
    }

    public List<ApiHistoriquePoints> getClientHistoriquePoints(String jwtToken) {
        try {
            List<ApiHistoriquePoints> historique = restClient.get().uri("/clients/me/historique-points")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ApiHistoriquePoints>>() {});
            return historique != null ? historique : List.of();
        } catch (RestClientResponseException e) {
            log.error("getClientHistoriquePoints error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        }
    }

    public ApiPage<ApiFactureSummary> getClientFactures(String jwtToken) {
        try {
            return restClient.get().uri("/factures/me?page=0&size=20")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiPage<ApiFactureSummary>>() {});
        } catch (RestClientResponseException e) {
            log.error("getClientFactures error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return emptyPage();
        }
    }

    public List<ApiGarantie> getClientGaranties(String jwtToken) {
        try {
            List<ApiGarantie> list = restClient.get().uri("/clients/me/garanties")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ApiGarantie>>() {});
            return list != null ? list : List.of();
        } catch (RestClientResponseException e) {
            log.error("getClientGaranties error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        }
    }

    public ApiClient setMagasinFavori(String jwtToken, Long idMagasin) {
        try {
            return restClient.put().uri("/clients/me/magasin-favori/{id}", idMagasin)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiClient.class);
        } catch (RestClientResponseException e) {
            log.error("setMagasinFavori error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void removeMagasinFavori(String jwtToken) {
        try {
            restClient.delete().uri("/clients/me/magasin-favori")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("removeMagasinFavori error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void simulerVerificationEmail(String jwtToken) {
        try {
            restClient.post().uri("/clients/me/verifier-email")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("simulerVerificationEmail error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void simulerVerificationTelephone(String jwtToken) {
        try {
            restClient.post().uri("/clients/me/verifier-telephone")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("simulerVerificationTelephone error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiClient subscribeUltimate(String jwtToken) {
        try {
            return restClient.post().uri("/clients/me/ultimate/subscribe")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiClient.class);
        } catch (RestClientResponseException e) {
            log.error("subscribeUltimate error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public List<ApiAdresse> getClientAdresses(String jwtToken) {
        try {
            List<ApiAdresse> adresses = restClient.get().uri("/clients/me/adresses")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return adresses != null ? adresses : List.of();
        } catch (RestClientResponseException e) {
            log.error("getClientAdresses error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        }
    }

    public ApiClient updateClientMe(String jwtToken, ApiUpdateClientRequest request) {
        try {
            return restClient.put().uri("/clients/me")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiClient.class);
        } catch (RestClientResponseException e) {
            log.error("updateClientMe error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiAdresse addClientAdresse(String jwtToken, ApiAdresseRequest request) {
        try {
            return restClient.post().uri("/clients/me/adresses")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiAdresse.class);
        } catch (RestClientResponseException e) {
            log.error("addClientAdresse error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void setAdresseDefaut(String jwtToken, Long idAdresse) {
        try {
            restClient.patch().uri("/clients/me/adresses/{idAdresse}/defaut", idAdresse)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("setAdresseDefaut error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void deleteClientAdresse(String jwtToken, Long idAdresse) {
        try {
            restClient.delete().uri("/clients/me/adresses/{idAdresse}", idAdresse)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("deleteClientAdresse error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public List<ApiMagasinProche> getMagasinsProches(String jwtToken, Long idAdresse) {
        return getMagasinsProches(jwtToken, idAdresse, 5);
    }

    public List<ApiMagasinProche> getMagasinsProches(String jwtToken, Long idAdresse, int limit) {
        try {
            List<ApiMagasinProche> magasins = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/magasins/proches")
                            .queryParam("idAdresse", idAdresse)
                            .queryParam("limit", limit)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return magasins != null ? magasins : List.of();
        } catch (RestClientResponseException e) {
            log.error("getMagasinsProches error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        }
    }

    public List<ApiStockCheckout> getStockCheckout(String jwtToken, Long idVariant) {
        try {
            List<ApiStockCheckout> stocks = restClient.get()
                    .uri("/stock/checkout/variant/{idVariant}", idVariant)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return stocks != null ? stocks : List.of();
        } catch (RestClientResponseException e) {
            log.error("getStockCheckout error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.error("getStockCheckout error", e);
            return List.of();
        }
    }

    public ApiStockEntrepotCheckout getStockEntrepotCheckout(String jwtToken, Long idVariant) {
        try {
            return restClient.get()
                    .uri("/stock/checkout/entrepot/variant/{idVariant}", idVariant)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiStockEntrepotCheckout.class);
        } catch (RestClientResponseException e) {
            log.error("getStockEntrepotCheckout error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("getStockEntrepotCheckout error", e);
            return null;
        }
    }

    public void deleteClientMe(String jwtToken) {
        try {
            restClient.delete().uri("/clients/me")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("deleteClientMe error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiPanier getPanier(String jwtToken) {
        try {
            return restClient.get().uri("/panier?canal=WEB")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiPanier.class);
        } catch (RestClientResponseException e) {
            log.error("getPanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        }
    }

    public ApiPanier addLignePanier(String jwtToken, Long idVariant, Integer quantite, Long typeGarantieId) {
        try {
            java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("idVariant",      idVariant);
            body.put("quantite",       quantite);
            body.put("idCanalVente",   1L);
            if (typeGarantieId != null) body.put("idTypeGarantie", typeGarantieId);
            return restClient.post().uri("/panier/lignes")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(ApiPanier.class);
        } catch (RestClientResponseException e) {
            log.error("addLignePanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiPanier updateLignePanier(String jwtToken, Long idLigne, Integer quantite) {
        try {
            return restClient.put().uri("/panier/lignes/{id}", idLigne)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("quantite", quantite))
                    .retrieve()
                    .body(ApiPanier.class);
        } catch (RestClientResponseException e) {
            log.error("updateLignePanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiPanier removeLignePanier(String jwtToken, Long idLigne) {
        try {
            return restClient.delete().uri("/panier/lignes/{id}", idLigne)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiPanier.class);
        } catch (RestClientResponseException e) {
            log.error("removeLignePanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public void clearPanier(String jwtToken) {
        try {
            restClient.delete().uri("/panier?canal=WEB")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("clearPanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(extractMessage(e));
        }
    }

    public ApiFactureDetail getClientFacture(String jwtToken, Long idFacture) {
        try {
            return restClient.get()
                    .uri("/factures/me/{idFacture}", idFacture)
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .retrieve()
                    .body(ApiFactureDetail.class);
        } catch (RestClientResponseException e) {
            log.error("getClientFacture error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public ApiFactureDetail checkoutPanier(String jwtToken,
                                           Long idAdresse,
                                           List<Long> idsBonAchat,
                                           String modePaiementCode) {
        try {
            return restClient.post()
                    .uri("/factures/me/checkout")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .body(new ApiCheckoutPanierRequest(idAdresse, idsBonAchat, modePaiementCode))
                    .retrieve()
                    .body(ApiFactureDetail.class);
        } catch (RestClientResponseException e) {
            log.error("checkoutPanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public ApiFactureDetail checkoutPanier(String jwtToken,
                                           Long idAdresse,
                                           List<Long> idsBonAchat,
                                           String modePaiementCode,
                                           String modeLivraisonCode,
                                           Long idMagasinRetrait) {
        try {
            return restClient.post()
                    .uri("/factures/me/checkout")
                    .header(HttpHeaders.AUTHORIZATION, bearer(jwtToken))
                    .body(new ApiCheckoutPanierRequest(idAdresse, idsBonAchat, modePaiementCode, modeLivraisonCode, idMagasinRetrait))
                    .retrieve()
                    .body(ApiFactureDetail.class);
        } catch (RestClientResponseException e) {
            log.error("checkoutPanier error {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private String bearer(String token) {
        return token != null && token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private String extractMessage(RestClientResponseException e) {
        String body = e.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return "Erreur API : " + e.getStatusCode();
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<>() {});
            Object message = payload.get("message");
            if (message instanceof String m && !m.isBlank()) {
                return m;
            }
            Object errors = payload.get("errors");
            if (errors instanceof Map<?, ?> map && !map.isEmpty()) {
                return map.values().stream().findFirst().map(String::valueOf).orElse("Requête invalide");
            }
        } catch (Exception ignored) {
        }
        return body;
    }

    private <T> ApiPage<T> emptyPage() {
        return new ApiPage<>(List.of(), 0, 0, 0, 0L, true, true, true);
    }

    private ApiPage<ApiVariantSummary> emptyPageVariant() {
        return new ApiPage<>(List.of(), 0, 0, 0, 0L, true, true, true);
    }
}
