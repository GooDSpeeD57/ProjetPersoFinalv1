class HoraireMagasin {
  final int jourSemaine; // 1 = Lundi … 7 = Dimanche
  final String libelleJour;
  final String? heureOuverture; // "HH:mm:ss" ou null
  final String? heureFermeture;
  final bool ferme;

  HoraireMagasin({
    required this.jourSemaine,
    required this.libelleJour,
    this.heureOuverture,
    this.heureFermeture,
    required this.ferme,
  });

  factory HoraireMagasin.fromJson(Map<String, dynamic> j) => HoraireMagasin(
        jourSemaine: j['jourSemaine'] ?? 0,
        libelleJour: j['libelleJour'] ?? '',
        heureOuverture: j['heureOuverture'],
        heureFermeture: j['heureFermeture'],
        ferme: j['ferme'] ?? false,
      );

  /// Retourne "10:00 – 19:30" ou "Fermé"
  String get affichage {
    if (ferme) return 'Fermé';
    final ouv = _hhmm(heureOuverture);
    final ferm = _hhmm(heureFermeture);
    if (ouv.isEmpty && ferm.isEmpty) return '—';
    return '$ouv – $ferm';
  }

  static String _hhmm(String? s) {
    if (s == null || s.length < 5) return '';
    return s.substring(0, 5);
  }
}

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

  /// Rempli après chargement des horaires (appel séparé)
  List<HoraireMagasin> horaires = [];

  /// Distance calculée en runtime
  double? distanceKm;

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

  /// Horaire du jour courant (null si non configuré)
  HoraireMagasin? get horaireAujourdhui {
    final today = DateTime.now().weekday; // 1=Lundi, 7=Dimanche — ISO
    try {
      return horaires.firstWhere((h) => h.jourSemaine == today);
    } catch (_) {
      return null;
    }
  }
}
