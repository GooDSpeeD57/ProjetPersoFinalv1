package com.monprojet.boutiquejeux.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monprojet.boutiquejeux.exception.ApiException;
import com.monprojet.boutiquejeux.util.SessionManager;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client HTTP singleton — Spring Boot API.
 *
 * API GENERIQUE (préférée) :
 *   get(path, Class<T>)              → T
 *   get(path, TypeReference<T>)      → T  (pour List<Dto>, Page<Dto>…)
 *   post(path, body, Class<T>)       → T
 *   patch(path, body, Class<T>)      → T
 *
 * API MAP (rétrocompatibilité contrôleurs existants) :
 *   get(path)                        → Map<String,Object>
 *   getList(path)                    → List<Map<String,Object>>
 *   post(path, body)                 → Map<String,Object>
 *   patch(path, body)                → Map<String,Object>
 *   postPublic(path, body)           → Map<String,Object>
 *   delete(path)                     → void
 *
 * Toutes les méthodes lèvent ApiException (status HTTP + message) en cas d'erreur >= 400.
 */
public class ApiClient {

    private static ApiClient instance;
    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private final HttpClient   http;
    private final ObjectMapper mapper;

    private ApiClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public static ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    // ════════════════════════════════════════════════════════════════
    //  API GÉNÉRIQUE — retourne un DTO typé
    // ════════════════════════════════════════════════════════════════

    /** GET → objet typé (ex: EmployeDto.class) */
    public <T> T get(String path, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(builder(path).GET()).build());
        return deserialize(resp.body(), type);
    }

    /** GET → type complexe (ex: new TypeReference<List<EmployeDto>>(){}) */
    public <T> T get(String path, TypeReference<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(builder(path).GET()).build());
        return deserialize(resp.body(), type);
    }

    /** GET public (sans JWT) → objet typé */
    public <T> T getPublic(String path, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(builder(path).GET().build());
        return deserialize(resp.body(), type);
    }

    /** POST → objet typé */
    public <T> T post(String path, Object body, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(jsonPost(path, body)).build());
        return deserialize(resp.body(), type);
    }

    /** POST public (sans JWT) → objet typé */
    public <T> T postPublic(String path, Object body, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(jsonPost(path, body).build());
        return deserialize(resp.body(), type);
    }

    /** PATCH → objet typé */
    public <T> T patch(String path, Object body, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(jsonPatch(path, body)).build());
        return deserialize(resp.body(), type);
    }

    /** PUT → objet typé */
    public <T> T put(String path, Object body, Class<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(jsonPut(path, body)).build());
        return deserialize(resp.body(), type);
    }

    // ════════════════════════════════════════════════════════════════
    //  API MAP — rétrocompatibilité (contrôleurs existants)
    // ════════════════════════════════════════════════════════════════

    /** GET → Map (objet unique ou Page) */
    public Map<String, Object> get(String path) throws ApiException {
        return get(path, new TypeReference<>() {});
    }

    /** GET → List de Maps (tableau JSON) */
    public List<Map<String, Object>> getList(String path) throws ApiException {
        return get(path, new TypeReference<>() {});
    }

    /** GET public → Map */
    public Map<String, Object> getPublic(String path) throws ApiException {
        HttpResponse<String> resp = execute(builder(path).GET().build());
        return deserialize(resp.body(), new TypeReference<>() {});
    }

    /** POST avec JWT → Map */
    public Map<String, Object> post(String path, Object body) throws ApiException {
        return post(path, body, new TypeReference<Map<String, Object>>() {});
    }

    /** POST sans JWT → Map */
    public Map<String, Object> postPublic(String path, Object body) throws ApiException {
        HttpResponse<String> resp = execute(jsonPost(path, body).build());
        return deserialize(resp.body(), new TypeReference<>() {});
    }

    /** PATCH avec JWT → Map */
    public Map<String, Object> patch(String path, Object body) throws ApiException {
        return patch(path, body, new TypeReference<Map<String, Object>>() {});
    }

    /** DELETE avec JWT → void */
    public void delete(String path) throws ApiException {
        execute(auth(builder(path).DELETE()).build());
    }

    // ════════════════════════════════════════════════════════════════
    //  PRIVÉ — builders & exécution
    // ════════════════════════════════════════════════════════════════

    private HttpRequest.Builder builder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json");
    }

    private HttpRequest.Builder auth(HttpRequest.Builder b) {
        return b.header("Authorization", SessionManager.getInstance().getBearerToken());
    }

    private HttpRequest.Builder jsonPost(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            return builder(path)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
        } catch (Exception e) {
            throw new RuntimeException("Sérialisation JSON impossible", e);
        }
    }

    private HttpRequest.Builder jsonPatch(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            return builder(path)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json));
        } catch (Exception e) {
            throw new RuntimeException("Sérialisation JSON impossible", e);
        }
    }

    private HttpRequest.Builder jsonPut(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            return builder(path)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json));
        } catch (Exception e) {
            throw new RuntimeException("Sérialisation JSON impossible", e);
        }
    }

    private HttpResponse<String> execute(HttpRequest req) throws ApiException {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                throw new ApiException(resp.statusCode(), extractError(resp.body(), resp.statusCode()));
            }
            return resp;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Connexion impossible : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  PRIVÉ — désérialisation
    // ════════════════════════════════════════════════════════════════

    private <T> T deserialize(String body, Class<T> type) throws ApiException {
        if (body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, type);
        } catch (Exception e) {
            throw new ApiException(0, "Réponse API illisible : " + e.getMessage());
        }
    }

    private <T> T deserialize(String body, TypeReference<T> type) throws ApiException {
        if (body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, type);
        } catch (Exception e) {
            throw new ApiException(0, "Réponse API illisible : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  PRIVÉ — extraction du message d'erreur
    //  Supporte : ErrorResponse {status,message,path}
    //             ProblemDetail {status,title,detail,instance}
    //             Spring Security {status,message}
    // ════════════════════════════════════════════════════════════════

    private String extractError(String body, int status) {
        if (body == null || body.isBlank()) return "Erreur HTTP " + status;
        try {
            Map<String, Object> err = mapper.readValue(body, new TypeReference<>() {});
            if (err.get("message") instanceof String m && !m.isBlank()) return m;
            if (err.get("detail")  instanceof String d && !d.isBlank()) return d;
            if (err.get("title")   instanceof String t && !t.isBlank()) return t;
            if (err.get("error")   instanceof String e && !e.isBlank()) return e;
        } catch (Exception ignored) {}
        return body.length() > 200 ? body.substring(0, 200) : body;
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS — surcharges TypeReference pour Map (internes)
    // ════════════════════════════════════════════════════════════════

    private <T> T post(String path, Object body, TypeReference<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(jsonPost(path, body)).build());
        return deserialize(resp.body(), type);
    }

    private <T> T patch(String path, Object body, TypeReference<T> type) throws ApiException {
        HttpResponse<String> resp = execute(auth(jsonPatch(path, body)).build());
        return deserialize(resp.body(), type);
    }

    public ObjectMapper getMapper() { return mapper; }

    // ════════════════════════════════════════════════════════════════
    //  UPLOAD — multipart/form-data (pour les images)
    // ════════════════════════════════════════════════════════════════

    /**
     * Upload un fichier image en multipart/form-data.
     *
     * @param path      endpoint API (ex: "/variants/12/images/upload")
     * @param file      fichier local à envoyer
     * @param alt       texte alternatif (peut être vide)
     * @param principale true si image principale du variant
     * @param type      classe de retour attendue (ex: ProduitImageDto.class)
     */
    public <T> T uploadImage(String path, File file, String alt, boolean principale, Class<T> type)
            throws ApiException {
        try {
            String boundary = "----JavaFXBoundary" + System.currentTimeMillis();
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String filename  = file.getName();
            String mimeType  = guessMimeType(filename);

            // ── Construire le corps multipart manuellement ────────────────────
            List<byte[]> chunks = new ArrayList<>();

            // Partie "file"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
                    + "Content-Type: " + mimeType + "\r\n\r\n").getBytes());
            chunks.add(fileBytes);
            chunks.add("\r\n".getBytes());

            // Partie "alt"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"alt\"\r\n\r\n"
                    + (alt != null ? alt : "") + "\r\n").getBytes());

            // Partie "principale"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"principale\"\r\n\r\n"
                    + principale + "\r\n").getBytes());

            // Fermeture
            chunks.add(("--" + boundary + "--\r\n").getBytes());

            // Concaténer
            int total = chunks.stream().mapToInt(b -> b.length).sum();
            byte[] body = new byte[total];
            int offset = 0;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, body, offset, chunk.length);
                offset += chunk.length;
            }

            HttpRequest req = auth(
                    builder(path)
                            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            ).build();

            HttpResponse<String> resp = execute(req);
            return deserialize(resp.body(), type);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Upload impossible : " + e.getMessage());
        }
    }

    /**
     * Upload multipart d'un screenshot produit vers POST /produits/{id}/screenshots/upload
     * Paramètres form : file, alt, ordre
     */
    public <T> T uploadScreenshot(String path, File file, String alt, int ordre, Class<T> type)
            throws ApiException {
        try {
            String boundary = "----JavaFXBoundary" + System.currentTimeMillis();
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String filename  = file.getName();
            String mimeType  = guessMimeType(filename);

            List<byte[]> chunks = new ArrayList<>();

            // Partie "file"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
                    + "Content-Type: " + mimeType + "\r\n\r\n").getBytes());
            chunks.add(fileBytes);
            chunks.add("\r\n".getBytes());

            // Partie "alt"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"alt\"\r\n\r\n"
                    + (alt != null ? alt : "") + "\r\n").getBytes());

            // Partie "ordre"
            chunks.add(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"ordre\"\r\n\r\n"
                    + ordre + "\r\n").getBytes());

            // Fermeture
            chunks.add(("--" + boundary + "--\r\n").getBytes());

            int total = chunks.stream().mapToInt(b -> b.length).sum();
            byte[] body = new byte[total];
            int offset = 0;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, body, offset, chunk.length);
                offset += chunk.length;
            }

            HttpRequest req = auth(
                    builder(path)
                            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            ).build();

            HttpResponse<String> resp = execute(req);
            return deserialize(resp.body(), type);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Upload screenshot impossible : " + e.getMessage());
        }
    }

    private static String guessMimeType(String filename) {
        if (filename == null) return "image/jpeg";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif"))  return "image/gif";
        return "image/jpeg";
    }
}
