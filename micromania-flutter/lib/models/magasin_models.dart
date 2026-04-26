class Magasin {
  final int id;
  final String nom;
  final String? adresse;
  final String? codePostal;
  final String? ville;
  final String? telephone;
  final double? latitude;
  final double? longitude;
  final bool actif;

  Magasin({
    required this.id,
    required this.nom,
    this.adresse,
    this.codePostal,
    this.ville,
    this.telephone,
    this.latitude,
    this.longitude,
    required this.actif,
  });

  factory Magasin.fromJson(Map<String, dynamic> j) => Magasin(
        id: j['id'] ?? 0,
        nom: j['nom'] ?? '',
        adresse: j['adresseComplete'] ?? j['rue'] ?? j['adresse'],
        codePostal: j['codePostal'],
        ville: j['ville'],
        telephone: j['telephone'],
        latitude: j['latitude'] != null ? (j['latitude'] as num).toDouble() : null,
        longitude: j['longitude'] != null ? (j['longitude'] as num).toDouble() : null,
        actif: j['actif'] ?? true,
      );

  String get adresseComplete {
    final parts = [adresse, codePostal, ville].whereType<String>().toList();
    return parts.isEmpty ? nom : parts.join(', ');
  }

  // Distance sera calculée en runtime
  double? distanceKm;
}
