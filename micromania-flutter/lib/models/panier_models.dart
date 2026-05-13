class PanierResponse {
  final int id;
  final String statutPanier;
  final List<LignePanier> lignes;
  final double sousTotal;
  final double total;
  final String? codePromo;

  PanierResponse({
    required this.id,
    required this.statutPanier,
    required this.lignes,
    required this.sousTotal,
    required this.total,
    this.codePromo,
  });

  factory PanierResponse.fromJson(Map<String, dynamic> j) => PanierResponse(
        id: j['id'] ?? 0,
        statutPanier: j['statutPanier'] ?? '',
        lignes: (j['lignes'] as List? ?? [])
            .map((l) => LignePanier.fromJson(l))
            .toList(),
        sousTotal: (j['sousTotal'] ?? 0).toDouble(),
        total: (j['total'] ?? 0).toDouble(),
        codePromo: j['codePromo'],
      );

  int get nbArticles => lignes.fold(0, (sum, l) => sum + l.quantite);
}

class LignePanier {
  final int id;
  final int variantId;
  final String nomProduit;
  final String? plateforme;
  final String? statut;
  final String? imageUrl;
  final int quantite;
  final double prixUnitaire;
  final double montantLigne;
  final String? typeGarantie;
  final double? garantiePrix;

  LignePanier({
    required this.id,
    required this.variantId,
    required this.nomProduit,
    this.plateforme,
    this.statut,
    this.imageUrl,
    required this.quantite,
    required this.prixUnitaire,
    required this.montantLigne,
    this.typeGarantie,
    this.garantiePrix,
  });

  factory LignePanier.fromJson(Map<String, dynamic> j) => LignePanier(
        id: (j['id'] as num?)?.toInt() ?? 0,
        variantId: (j['idVariant'] as num?)?.toInt() ?? 0,  // backend: idVariant
        nomProduit: j['nomCommercial'] ?? j['nomProduit'] ?? '',  // backend: nomCommercial
        plateforme: j['plateforme'],
        statut: j['statut'],
        imageUrl: j['imageUrl'],
        quantite: (j['quantite'] as num?)?.toInt() ?? 1,
        prixUnitaire: (j['prixUnitaire'] as num?)?.toDouble() ?? 0.0,
        montantLigne: (j['montantLigne'] as num?)?.toDouble() ?? 0.0,
        typeGarantie: j['garantieLabel'] ?? j['typeGarantie'],  // backend: garantieLabel
        garantiePrix: (j['garantiePrix'] as num?)?.toDouble(),
      );

  String get imageFullUrl {
    if (imageUrl == null) return '';
    if (imageUrl!.startsWith('http')) return imageUrl!;
    return 'http://goodspeed57.ddns.net:8080$imageUrl';
  }
}
