class CommandeSummary {
  final int id;
  final String reference;
  final String statut;
  final String? modeLivraison;
  final double total;
  final int nbArticles;
  final String dateCommande;

  CommandeSummary({
    required this.id,
    required this.reference,
    required this.statut,
    this.modeLivraison,
    required this.total,
    required this.nbArticles,
    required this.dateCommande,
  });

  factory CommandeSummary.fromJson(Map<String, dynamic> j) => CommandeSummary(
        id: (j['id'] as num?)?.toInt() ?? 0,
        reference: j['referenceCommande'] ?? j['reference'] ?? '',
        statut: j['statut'] ?? '',
        modeLivraison: j['modeLivraison'],
        total: (j['montantTotal'] as num?)?.toDouble() ?? (j['total'] as num?)?.toDouble() ?? 0.0,
        nbArticles: (j['nbArticles'] as num?)?.toInt() ?? 0,
        dateCommande: j['dateCommande']?.toString() ?? '',
      );
}

class CommandeDetail {
  final int id;
  final String reference;
  final String statut;
  final String? modeLivraison;
  final String? adresseLivraison;
  final double sousTotal;
  final double fraisLivraison;
  final double total;
  final String? modePaiement;
  final String dateCommande;
  final List<LigneCommande> lignes;

  CommandeDetail({
    required this.id,
    required this.reference,
    required this.statut,
    this.modeLivraison,
    this.adresseLivraison,
    required this.sousTotal,
    required this.fraisLivraison,
    required this.total,
    this.modePaiement,
    required this.dateCommande,
    required this.lignes,
  });

  factory CommandeDetail.fromJson(Map<String, dynamic> j) => CommandeDetail(
        id: (j['id'] as num?)?.toInt() ?? 0,
        reference: j['referenceCommande'] ?? j['reference'] ?? '',
        statut: j['statut'] ?? '',
        modeLivraison: j['modeLivraison'],
        adresseLivraison: j['adresseLivraison'],
        sousTotal: (j['sousTotal'] as num?)?.toDouble() ?? 0.0,
        fraisLivraison: (j['fraisLivraison'] as num?)?.toDouble() ?? 0.0,
        total: (j['montantTotal'] as num?)?.toDouble() ?? (j['total'] as num?)?.toDouble() ?? 0.0,
        modePaiement: j['modePaiement'],
        dateCommande: j['dateCommande']?.toString() ?? '',
        lignes: (j['lignes'] as List? ?? [])
            .map((l) => LigneCommande.fromJson(l))
            .toList(),
      );
}

class LigneCommande {
  final int id;
  final String nomProduit;
  final String? plateforme;
  final String? imageUrl;
  final int quantite;
  final double prixUnitaire;
  final double montant;

  LigneCommande({
    required this.id,
    required this.nomProduit,
    this.plateforme,
    this.imageUrl,
    required this.quantite,
    required this.prixUnitaire,
    required this.montant,
  });

  factory LigneCommande.fromJson(Map<String, dynamic> j) => LigneCommande(
        id: (j['id'] as num?)?.toInt() ?? 0,
        nomProduit: j['nomCommercial'] ?? j['nomProduit'] ?? j['nom'] ?? '',
        plateforme: j['plateforme'],
        imageUrl: j['imageUrl'],
        quantite: (j['quantite'] as num?)?.toInt() ?? 1,
        prixUnitaire: (j['prixUnitaire'] as num?)?.toDouble() ?? 0.0,
        montant: (j['montantLigne'] as num?)?.toDouble() ?? (j['montant'] as num?)?.toDouble() ?? 0.0,
      );
}
