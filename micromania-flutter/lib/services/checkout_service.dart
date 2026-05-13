import '../core/api_client.dart';

class CheckoutService {
  final _api = ApiClient();

  /// Valide le panier et crée la facture
  Future<Map<String, dynamic>> checkout({
    int? idAdresse,
    List<int> idsBonAchat = const [],
    required String modePaiementCode,
    required String modeLivraisonCode,
    int? idMagasinRetrait,
  }) async {
    final body = <String, dynamic>{
      'modePaiementCode': modePaiementCode,
      'modeLivraisonCode': modeLivraisonCode,
      'idsBonAchat': idsBonAchat,
    };
    if (idAdresse != null) body['idAdresse'] = idAdresse;
    if (idMagasinRetrait != null) body['idMagasinRetrait'] = idMagasinRetrait;
    final data = await _api.post('/factures/me/checkout', body);
    return data as Map<String, dynamic>;
  }

  Future<List<Map<String, dynamic>>> getAdresses() async {
    final data = await _api.get('/clients/me/adresses') as List;
    return data.cast<Map<String, dynamic>>();
  }

  Future<List<Map<String, dynamic>>> getMagasins() async {
    final data = await _api.get('/magasins', auth: false) as List;
    return data.cast<Map<String, dynamic>>();
  }

  Future<List<Map<String, dynamic>>> getTypesGarantie({int? categorieId}) async {
    final params = <String, dynamic>{};
    if (categorieId != null) params['categorieId'] = categorieId;
    final data = await _api.get(
      '/referentiel/types-garantie',
      params: params,
      auth: false,
    ) as List;
    return data.cast<Map<String, dynamic>>();
  }

  Future<List<Map<String, dynamic>>> getBonsAchat() async {
    final data = await _api.get('/clients/me/bons-achat') as List;
    return data.cast<Map<String, dynamic>>();
  }
}
