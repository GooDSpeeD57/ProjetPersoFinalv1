import '../core/api_client.dart';
import '../models/auth_models.dart';

class AuthService {
  final _api = ApiClient();

  Future<AuthResponse> login(String email, String password) async {
    final data = await _api.post(
      '/auth/login/client',
      {'email': email, 'motDePasse': password},
      auth: false,
    );
    final response = AuthResponse.fromJson(data);
    await _api.saveToken(response.token);
    return response;
  }

  /// Crée un nouveau compte client et connecte automatiquement
  Future<AuthResponse> register({
    required String pseudo,
    required String nom,
    required String prenom,
    required String email,
    required String motDePasse,
    required bool rgpdConsent,
    String? telephone,
    String? dateNaissance, // format yyyy-MM-dd
  }) async {
    final body = <String, dynamic>{
      'pseudo': pseudo,
      'nom': nom,
      'prenom': prenom,
      'email': email,
      'motDePasse': motDePasse,
      'rgpdConsent': rgpdConsent,
    };
    if (telephone != null && telephone.isNotEmpty) body['telephone'] = telephone;
    if (dateNaissance != null && dateNaissance.isNotEmpty) body['dateNaissance'] = dateNaissance;

    final data = await _api.post('/auth/register', body, auth: false);
    final response = AuthResponse.fromJson(data);
    await _api.saveToken(response.token);
    return response;
  }

  Future<void> logout() => _api.clearToken();

  Future<bool> isLoggedIn() => _api.hasToken();
}
