import '../core/api_client.dart';
import '../models/garantie_models.dart';

class GarantieService {
  final _api = ApiClient();

  /// Récupère toutes les garanties du client connecté.
  Future<List<GarantieModel>> getMesGaranties() async {
    final data = await _api.get('/clients/me/garanties');
    final list = (data is List ? data : []) as List;
    return list
        .map((g) => GarantieModel.fromJson(g as Map<String, dynamic>))
        .toList();
  }
}
