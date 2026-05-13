import '../core/api_client.dart';
import '../models/commande_models.dart';

class CommandeService {
  final _api = ApiClient();

  Future<List<CommandeSummary>> getMesCommandes() async {
    // API returns a Page<CommandeSummary> — extract the 'content' list
    final data = await _api.get('/commandes/me');
    final list = (data is Map ? data['content'] : data) as List? ?? [];
    return list.map((c) => CommandeSummary.fromJson(c)).toList();
  }

  Future<CommandeDetail> getDetail(int id) async {
    final data = await _api.get('/commandes/me/$id');
    return CommandeDetail.fromJson(data);
  }
}
