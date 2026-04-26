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

  Future<void> logout() => _api.clearToken();

  Future<bool> isLoggedIn() => _api.hasToken();
}
