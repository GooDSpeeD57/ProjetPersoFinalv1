import '../core/api_client.dart';
import '../models/client_models.dart';

class ClientService {
  final _api = ApiClient();

  Future<ClientResponse> getMe() async {
    final data = await _api.get('/clients/me');
    return ClientResponse.fromJson(data);
  }

  Future<FideliteDetail> getFidelite() async {
    final data = await _api.get('/clients/me/fidelite');
    return FideliteDetail.fromJson(data);
  }

  Future<List<BonAchat>> getBonsAchat() async {
    final data = await _api.get('/clients/me/bons-achat') as List;
    return data.map((b) => BonAchat.fromJson(b)).toList();
  }

  Future<ClientResponse> updateMe(Map<String, dynamic> body) async {
    final data = await _api.put('/clients/me', body);
    return ClientResponse.fromJson(data);
  }
}
