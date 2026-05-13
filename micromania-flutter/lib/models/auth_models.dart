class AuthResponse {
  final String token;
  final String tokenType;
  final String? prenom;
  final String? nom;
  final String userType;

  AuthResponse({
    required this.token,
    required this.tokenType,
    this.prenom,
    this.nom,
    required this.userType,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> j) => AuthResponse(
        token: j['accessToken'] ?? j['token'] ?? '',
        tokenType: j['tokenType'] ?? 'Bearer',
        prenom: j['pseudo'] ?? j['prenom'],
        nom: j['email'] ?? j['nom'],
        userType: j['typeFidelite'] ?? j['userType'] ?? 'CLIENT',
      );
}
