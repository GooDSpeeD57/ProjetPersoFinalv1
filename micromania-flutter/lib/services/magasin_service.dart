import '../core/api_client.dart';
import '../models/magasin_models.dart';

class MagasinService {
  final _api = ApiClient();

  Future<List<Magasin>> getMagasins() async {
    final data = await _api.get('/magasins', auth: false) as List;
    return data.map((m) => Magasin.fromJson(m)).toList();
  }

  Future<List<HoraireMagasin>> getHoraires(int idMagasin) async {
    try {
      final data = await _api.get('/magasins/$idMagasin/horaires', auth: false) as List;
      return data.map((h) => HoraireMagasin.fromJson(h)).toList();
    } catch (_) {
      return [];
    }
  }
}
