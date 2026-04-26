import 'package:flutter/foundation.dart';
import '../models/auth_models.dart';
import '../models/client_models.dart';
import '../services/auth_service.dart';
import '../services/client_service.dart';

enum AuthState { checking, authenticated, unauthenticated }

class AuthProvider extends ChangeNotifier {
  final _authService = AuthService();
  final _clientService = ClientService();

  AuthState _state = AuthState.checking;
  ClientResponse? _client;
  String? _error;

  AuthState get state => _state;
  ClientResponse? get client => _client;
  String? get error => _error;
  bool get isAuthenticated => _state == AuthState.authenticated;

  Future<void> checkAuth() async {
    _state = AuthState.checking;
    notifyListeners();
    try {
      final loggedIn = await _authService.isLoggedIn();
      if (loggedIn) {
        _client = await _clientService.getMe();
        _state = AuthState.authenticated;
      } else {
        _state = AuthState.unauthenticated;
      }
    } catch (_) {
      _state = AuthState.unauthenticated;
    }
    notifyListeners();
  }

  Future<bool> login(String email, String password) async {
    _error = null;
    try {
      await _authService.login(email, password);
      _client = await _clientService.getMe();
      _state = AuthState.authenticated;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    await _authService.logout();
    _client = null;
    _state = AuthState.unauthenticated;
    notifyListeners();
  }

  Future<void> refreshClient() async {
    try {
      _client = await _clientService.getMe();
      notifyListeners();
    } catch (_) {}
  }
}
