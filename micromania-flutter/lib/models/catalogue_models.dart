class PageResult<T> {
  final List<T> content;
  final int totalElements;
  final int totalPages;
  final int number;
  final bool last;

  PageResult({
    required this.content,
    required this.totalElements,
    required this.totalPages,
    required this.number,
    required this.last,
  });
}

class VariantSummary {
  final int id;
  final int produitId;
  final String nom;
  final String slug;
  final String? categorie;
  final String? typeCategorie;
  final String? plateforme;
  final String? statut;
  final String? edition;
  final String? imageUrl;
  final String? imageAlt;
  final double? prix;
  final bool misEnAvant;
  final bool estPreCommande;
  final int? pegi;
  final double? noteMoyenne;
  final int nbAvis;

  VariantSummary({
    required this.id,
    required this.produitId,
    required this.nom,
    required this.slug,
    this.categorie,
    this.typeCategorie,
    this.plateforme,
    this.statut,
    this.edition,
    this.imageUrl,
    this.imageAlt,
    this.prix,
    required this.misEnAvant,
    required this.estPreCommande,
    this.pegi,
    this.noteMoyenne,
    required this.nbAvis,
  });

  factory VariantSummary.fromJson(Map<String, dynamic> j) => VariantSummary(
        id: j['id'] ?? 0,
        produitId: j['produitId'] ?? 0,
        nom: j['nom'] ?? '',
        slug: j['slug'] ?? '',
        categorie: j['categorie'],
        typeCategorie: j['typeCategorie'],
        plateforme: j['plateforme'],
        statut: j['statut'],
        edition: j['edition'],
        imageUrl: j['imageUrl'],
        imageAlt: j['imageAlt'],
        prix: j['prix'] != null ? (j['prix'] as num).toDouble() : null,
        misEnAvant: j['misEnAvant'] ?? false,
        estPreCommande: j['estPreCommande'] ?? false,
        pegi: j['pegi'],
        noteMoyenne: j['noteMoyenne'] != null
            ? (j['noteMoyenne'] as num).toDouble()
            : null,
        nbAvis: j['nbAvis'] ?? 0,
      );

  String get imageFullUrl {
    if (imageUrl == null) return '';
    if (imageUrl!.startsWith('http')) return imageUrl!;
    return 'http://goodspeed57.ddns.net:8080$imageUrl';
  }
}

class ProduitDetail {
  final int id;
  final String nom;
  final String slug;
  final String? description;
  final String? resumeCourt;
  final String? dateSortie;
  final String? editeur;
  final String? constructeur;
  final int? pegi;
  final bool misEnAvant;
  final bool estPreCommande;
  final String? categorie;
  final List<VariantDetail> variants;
  final List<ProduitImage> images;
  final double? noteMoyenne;
  final int nbAvis;
  final List<AvisPublic> avis;

  ProduitDetail({
    required this.id,
    required this.nom,
    required this.slug,
    this.description,
    this.resumeCourt,
    this.dateSortie,
    this.editeur,
    this.constructeur,
    this.pegi,
    required this.misEnAvant,
    required this.estPreCommande,
    this.categorie,
    required this.variants,
    required this.images,
    this.noteMoyenne,
    required this.nbAvis,
    required this.avis,
  });

  factory ProduitDetail.fromJson(Map<String, dynamic> j) => ProduitDetail(
        id: j['id'] ?? 0,
        nom: j['nom'] ?? '',
        slug: j['slug'] ?? '',
        description: j['description'],
        resumeCourt: j['resumeCourt'],
        dateSortie: j['dateSortie']?.toString(),
        editeur: j['editeur'],
        constructeur: j['constructeur'],
        pegi: j['pegi'],
        misEnAvant: j['misEnAvant'] ?? false,
        estPreCommande: j['estPreCommande'] ?? false,
        // categorie est un objet {id, code, description} → on extrait description
        categorie: (j['categorie'] as Map?)?['description'] as String?,
        variants: (j['variants'] as List? ?? [])
            .map((v) => VariantDetail.fromJson(v))
            .toList(),
        images: (j['images'] as List? ?? [])
            .map((i) => ProduitImage.fromJson(i))
            .toList(),
        noteMoyenne: j['noteMoyenne'] != null
            ? (j['noteMoyenne'] as num).toDouble()
            : null,
        nbAvis: (j['nbAvis'] as num?)?.toInt() ?? 0,
        avis: (j['avis'] as List? ?? [])
            .map((a) => AvisPublic.fromJson(a))
            .toList(),
      );
}

class VariantDetail {
  final int id;
  final String sku;
  final String? ean;
  final String? nomCommercial;
  final String? plateforme;
  final String? statut;
  final String? edition;
  final bool estDemat;
  final double? prixNeuf;
  final double? prixOccasion;
  final double? prixReprise;
  final double? prixLocation;
  final bool actif;
  final List<ProduitImage> images;

  VariantDetail({
    required this.id,
    required this.sku,
    this.ean,
    this.nomCommercial,
    this.plateforme,
    this.statut,
    this.edition,
    required this.estDemat,
    this.prixNeuf,
    this.prixOccasion,
    this.prixReprise,
    this.prixLocation,
    required this.actif,
    required this.images,
  });

  factory VariantDetail.fromJson(Map<String, dynamic> j) => VariantDetail(
        id: j['id'] ?? 0,
        sku: j['sku'] ?? '',
        ean: j['ean'],
        nomCommercial: j['nomCommercial'],
        plateforme: j['plateforme'] is Map ? j['plateforme']['libelle'] : j['plateforme'],
        // le backend envoie "statutProduit", pas "statut"
        statut: j['statutProduit'] ?? j['statut'],
        edition: j['edition'] is Map ? j['edition']['libelle'] : j['edition'],
        estDemat: j['estDemat'] ?? false,
        prixNeuf: j['prixNeuf'] != null ? (j['prixNeuf'] as num).toDouble() : null,
        prixOccasion: j['prixOccasion'] != null ? (j['prixOccasion'] as num).toDouble() : null,
        prixReprise: j['prixReprise'] != null ? (j['prixReprise'] as num).toDouble() : null,
        prixLocation: j['prixLocation'] != null ? (j['prixLocation'] as num).toDouble() : null,
        actif: j['actif'] ?? false,
        images: (j['images'] as List? ?? [])
            .map((i) => ProduitImage.fromJson(i))
            .toList(),
      );

  double? get prix => prixNeuf ?? prixOccasion;
}

class ProduitImage {
  final int id;
  final String url;
  final String? alt;
  final bool principale;

  ProduitImage({required this.id, required this.url, this.alt, required this.principale});

  factory ProduitImage.fromJson(Map<String, dynamic> j) => ProduitImage(
        id: j['id'] ?? 0,
        url: j['url'] ?? '',
        alt: j['alt'],
        principale: j['principale'] ?? false,
      );

  String get fullUrl {
    if (url.startsWith('http')) return url;
    return 'http://goodspeed57.ddns.net:8080$url';
  }
}

class AvisPublic {
  final String prenomClient;
  final int note;
  final String? commentaire;
  final String? dateCreation;

  AvisPublic({
    required this.prenomClient,
    required this.note,
    this.commentaire,
    this.dateCreation,
  });

  factory AvisPublic.fromJson(Map<String, dynamic> j) => AvisPublic(
        // le backend envoie "auteur", pas "prenomClient"
        prenomClient: j['auteur'] ?? j['prenomClient'] ?? 'Anonyme',
        note: (j['note'] as num?)?.toInt() ?? 0,
        commentaire: j['commentaire'],
        dateCreation: j['dateCreation']?.toString(),
      );
}
