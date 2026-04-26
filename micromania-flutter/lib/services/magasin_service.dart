import '../core/api_client.dart';
import '../models/magasin_models.dart';

class MagasinService {
  final _api = ApiClient();

  Future<List<Magasin>> getMagasins() async {
    final data = await _api.get('/magasins', auth: false) as List;
    return data.map((m) => Magasin.fromJson(m)).toList();
  }
}
