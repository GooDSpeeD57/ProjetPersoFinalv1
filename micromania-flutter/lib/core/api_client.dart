import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'constants.dart';

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  ApiClient._internal();

  final _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
  );

  Future<String?> get _token => _storage.read(key: AppConstants.tokenKey);

  Future<Map<String, String>> _headers({bool auth = true}) async {
    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (auth) {
      final token = await _token;
      if (token != null) headers['Authorization'] = 'Bearer $token';
    }
    return headers;
  }

  Uri _uri(String path, [Map<String, dynamic>? params]) {
    final base = Uri.parse('${AppConstants.apiBaseUrl}$path');
    if (params == null || params.isEmpty) return base;
    return base.replace(queryParameters: params.map((k, v) => MapEntry(k, v.toString())));
  }

  // ── GET ──────────────────────────────────────────────────────────

  Future<dynamic> get(String path, {Map<String, dynamic>? params, bool auth = true}) async {
    final response = await http.get(_uri(path, params), headers: await _headers(auth: auth));
    return _handle(response);
  }

  // ── POST ─────────────────────────────────────────────────────────

  Future<dynamic> post(String path, dynamic body, {bool auth = true}) async {
    final uri = _uri(path);
    final encodedBody = jsonEncode(body);
    print('▶ POST $uri');
    print('  body: $encodedBody');
    final response = await http.post(
      uri,
      headers: await _headers(auth: auth),
      body: encodedBody,
    );
    return _handle(response);
  }

  // ── PUT ──────────────────────────────────────────────────────────

  Future<dynamic> put(String path, dynamic body) async {
    final response = await http.put(
      _uri(path),
      headers: await _headers(),
      body: jsonEncode(body),
    );
    return _handle(response);
  }

  // ── PATCH ────────────────────────────────────────────────────────

  Future<dynamic> patch(String path, dynamic body) async {
    final response = await http.patch(
      _uri(path),
      headers: await _headers(),
      body: jsonEncode(body),
    );
    return _handle(response);
  }

  // ── DELETE ───────────────────────────────────────────────────────

  Future<void> delete(String path) async {
    final response = await http.delete(_uri(path), headers: await _headers());
    _handle(response, expectBody: false);
  }

  // ── Token management ─────────────────────────────────────────────

  Future<void> saveToken(String token) =>
      _storage.write(key: AppConstants.tokenKey, value: token);

  Future<void> clearToken() =>
      _storage.delete(key: AppConstants.tokenKey);

  Future<bool> hasToken() async => (await _token) != null;

  // ── Private ───────────────────────────────────────────────────────

  dynamic _handle(http.Response response, {bool expectBody = true}) {
    // ── Log systématique pour debug ───────────────────────────────
    print('◀ HTTP ${response.statusCode} ${response.request?.url}');
    if (response.statusCode >= 400) {
      print('  body brut: ${response.body}');
    }

    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (!expectBody || response.body.isEmpty) return null;
      return jsonDecode(utf8.decode(response.bodyBytes));
    }
    String message = 'Erreur ${response.statusCode}';
    try {
      final body = jsonDecode(utf8.decode(response.bodyBytes));
      message = body['message'] ?? body['error'] ?? message;
    } catch (_) {
      // corps non-JSON (proxy, nginx…)
      print('  corps non-JSON reçu');
    }
    throw ApiException(response.statusCode, message);
  }
}

class ApiException implements Exception {
  final int statusCode;
  final String message;
  ApiException(this.statusCode, this.message);

  bool get isUnauthorized => statusCode == 401;
  bool get isNotFound => statusCode == 404;

  @override
  String toString() => message;
}
