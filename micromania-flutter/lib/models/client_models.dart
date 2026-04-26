class ClientResponse {
  final int id;
  final String? pseudo;
  final String nom;
  final String prenom;
  final String email;
  final String? telephone;
  final String? dateNaissance;
  final String? numeroCarteFidelite;
  final int soldePoints;
  final String typeFidelite;
  final bool emailVerifie;
  final bool telephoneVerifie;
  final bool ultimateActif;
  final String? magasinFavoriNom;

  ClientResponse({
    required this.id,
    this.pseudo,
    required this.nom,
    required this.prenom,
    required this.email,
    this.telephone,
    this.dateNaissance,
    this.numeroCarteFidelite,
    required this.soldePoints,
    required this.typeFidelite,
    required this.emailVerifie,
    required this.telephoneVerifie,
    required this.ultimateActif,
    this.magasinFavoriNom,
  });

  factory ClientResponse.fromJson(Map<String, dynamic> j) => ClientResponse(
        id: (j['id'] as num?)?.toInt() ?? 0,
        pseudo: j['pseudo'],
        nom: j['nom'] ?? '',
        prenom: j['prenom'] ?? '',
        email: j['email'] ?? '',
        telephone: j['telephone'],
        dateNaissance: j['dateNaissance']?.toString(),
        numeroCarteFidelite: j['numeroCarteFidelite'],
        soldePoints: (j['soldePoints'] as num?)?.toInt() ?? 0,
        typeFidelite: j['typeFidelite'] ?? 'STANDARD',
        emailVerifie: j['emailVerifie'] ?? false,
        telephoneVerifie: j['telephoneVerifie'] ?? false,
        ultimateActif: j['ultimateActif'] ?? false,
        magasinFavoriNom: (j['magasinFavori'] as Map?)?['nom'] as String?,
      );

  String get nomComplet => '$prenom $nom';

  // Compat aliases pour les écrans existants
  int get pointsFidelite => soldePoints;
  String get niveauFidelite => typeFidelite;
  bool get abonneUltimate => ultimateActif;
}

class FideliteDetail {
  final int soldePoints;
  final String typeFidelite;
  final int seuilBon10;
  final int pointsCycleBon10;
  final int pointsAvantBon10;
  final int progressionBon10Percent;
  final int seuilBon20;
  final int pointsCycleBon20;
  final int pointsAvantBon20;
  final int progressionBon20Percent;
  final int totalPointsGagnes;

  FideliteDetail({
    required this.soldePoints,
    required this.typeFidelite,
    required this.seuilBon10,
    required this.pointsCycleBon10,
    required this.pointsAvantBon10,
    required this.progressionBon10Percent,
    required this.seuilBon20,
    required this.pointsCycleBon20,
    required this.pointsAvantBon20,
    required this.progressionBon20Percent,
    required this.totalPointsGagnes,
  });

  factory FideliteDetail.fromJson(Map<String, dynamic> j) => FideliteDetail(
        soldePoints: (j['soldePoints'] as num?)?.toInt() ?? 0,
        typeFidelite: j['typeFidelite'] ?? 'STANDARD',
        seuilBon10: (j['seuilBon10'] as num?)?.toInt() ?? 2000,
        pointsCycleBon10: (j['pointsCycleBon10'] as num?)?.toInt() ?? 0,
        pointsAvantBon10: (j['pointsAvantBon10'] as num?)?.toInt() ?? 0,
        progressionBon10Percent: (j['progressionBon10Percent'] as num?)?.toInt() ?? 0,
        seuilBon20: (j['seuilBon20'] as num?)?.toInt() ?? 8000,
        pointsCycleBon20: (j['pointsCycleBon20'] as num?)?.toInt() ?? 0,
        pointsAvantBon20: (j['pointsAvantBon20'] as num?)?.toInt() ?? 0,
        progressionBon20Percent: (j['progressionBon20Percent'] as num?)?.toInt() ?? 0,
        totalPointsGagnes: (j['totalPointsGagnes'] as num?)?.toInt() ?? 0,
      );
}

class BonAchat {
  final int id;
  final String? codeBon;
  final double valeur;
  final int pointsUtilises;
  final bool utilise;
  final String? dateExpiration;

  BonAchat({
    required this.id,
    this.codeBon,
    required this.valeur,
    required this.pointsUtilises,
    required this.utilise,
    this.dateExpiration,
  });

  factory BonAchat.fromJson(Map<String, dynamic> j) => BonAchat(
        id: (j['id'] as num?)?.toInt() ?? 0,
        codeBon: j['codeBon'],
        valeur: (j['valeur'] as num?)?.toDouble() ?? 0.0,
        pointsUtilises: (j['pointsUtilises'] as num?)?.toInt() ?? 0,
        utilise: j['utilise'] ?? false,
        dateExpiration: j['dateExpiration']?.toString(),
      );
}
