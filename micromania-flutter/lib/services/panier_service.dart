import '../core/api_client.dart';
import '../core/constants.dart';
import '../models/panier_models.dart';

class PanierService {
  final _api = ApiClient();

  Future<PanierResponse> getPanier() async {
    final data = await _api.get(
      '/panier',
      params: {'canalVente': AppConstants.canalVente},
    );
    return PanierResponse.fromJson(data);
  }

  Future<PanierResponse> addLigne({
    required int idVariant,
    required int quantite,
    int? idCanalVente,
  }) async {
    final data = await _api.post('/panier/lignes', {
      'idVariant': idVariant,
      'quantite': quantite,
      'idCanalVente': idCanalVente ?? 1,
    });
    return PanierResponse.fromJson(data);
  }

  Future<PanierResponse> updateLigne(int idLigne, int quantite) async {
    final data = await _api.put('/panier/lignes/$idLigne', {'quantite': quantite});
    return PanierResponse.fromJson(data);
  }

  Future<PanierResponse> removeLigne(int idLigne) async {
    final data = await _api.delete('/panier/lignes/$idLigne');
    // Après suppression, recharger le panier
    return getPanier();
  }

  Future<PanierResponse> vider() async {
    await _api.delete('/panier?canalVente=${AppConstants.canalVente}');
    return getPanier();
  }
}
