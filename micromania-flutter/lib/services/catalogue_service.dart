import '../core/api_client.dart';
import '../models/catalogue_models.dart';

class CatalogueService {
  final _api = ApiClient();

  Future<PageResult<VariantSummary>> getCatalogue({
    String? q,
    String? famille,
    String? plateforme,
    String? etat,
    String? tri,
    int page = 0,
    int size = 20,
  }) async {
    final params = <String, dynamic>{'page': page, 'size': size};
    if (q != null && q.isNotEmpty) params['q'] = q;
    if (famille != null) params['famille'] = famille;
    if (plateforme != null) params['plateforme'] = plateforme;
    if (etat != null) params['etat'] = etat;
    if (tri != null) params['tri'] = tri;

    final data = await _api.get('/catalogue', params: params, auth: false);
    final content = (data['content'] as List)
        .map((v) => VariantSummary.fromJson(v))
        .toList();
    return PageResult(
      content: content,
      totalElements: data['totalElements'] ?? 0,
      totalPages: data['totalPages'] ?? 1,
      number: data['number'] ?? 0,
      last: data['last'] ?? true,
    );
  }

  Future<List<VariantSummary>> getMisEnAvant() async {
    final data = await _api.get('/produits/mis-en-avant', auth: false) as List;
    return data.map((v) => VariantSummary.fromJson(v)).toList();
  }

  Future<ProduitDetail> getProduitBySlug(String slug) async {
    final data = await _api.get('/produits/slug/$slug', auth: false);
    return ProduitDetail.fromJson(data);
  }

  Future<ProduitDetail> getProduitById(int id) async {
    final data = await _api.get('/produits/$id', auth: false);
    return ProduitDetail.fromJson(data);
  }

  /// Recherche un variant par EAN (code barre) pour obtenir le prix de reprise
  Future<VariantSummary?> rechercherParEan(String ean) async {
    try {
      final data = await _api.get(
        '/catalogue',
        params: {'q': ean, 'size': 1},
        auth: false,
      );
      final content = data['content'] as List;
      if (content.isEmpty) return null;
      return VariantSummary.fromJson(content.first);
    } catch (_) {
      return null;
    }
  }
}
