/// Modèle miroir de GarantieResponse (API Spring Boot).
class GarantieModel {
  final int     id;
  final int?    idVenteUnite;
  final String? codeTypeGarantie;
  final String? descTypeGarantie;
  final int?    dureeMois;
  final String? dateDebut;
  final String? dateFin;
  final String? numeroSerie;
  final String? nomProduit;
  /// "LEGALE" = garantie de conformité / "EXTENSION" = extension payante
  final String  typeItem;

  const GarantieModel({
    required this.id,
    this.idVenteUnite,
    this.codeTypeGarantie,
    this.descTypeGarantie,
    this.dureeMois,
    this.dateDebut,
    this.dateFin,
    this.numeroSerie,
    this.nomProduit,
    required this.typeItem,
  });

  factory GarantieModel.fromJson(Map<String, dynamic> j) => GarantieModel(
        id:                j['id'] as int,
        idVenteUnite:      j['idVenteUnite'] as int?,
        codeTypeGarantie:  j['codeTypeGarantie'] as String?,
        descTypeGarantie:  j['descTypeGarantie'] as String?,
        dureeMois:         j['dureeMois'] as int?,
        dateDebut:         j['dateDebut'] as String?,
        dateFin:           j['dateFin'] as String?,
        numeroSerie:       j['numeroSerie'] as String?,
        nomProduit:        j['nomProduit'] as String?,
        typeItem:          (j['typeItem'] as String?) ?? 'LEGALE',
      );

  bool get isExtension => typeItem == 'EXTENSION';

  /// Retourne true si la garantie est encore active.
  bool get isActive {
    if (dateFin == null) return false;
    try {
      return DateTime.parse(dateFin!).isAfter(DateTime.now());
    } catch (_) {
      return false;
    }
  }

  /// Retourne true si la garantie expire dans moins de 30 jours.
  bool get expireBientot {
    if (dateFin == null) return false;
    try {
      final fin = DateTime.parse(dateFin!);
      final now = DateTime.now();
      return fin.isAfter(now) && fin.isBefore(now.add(const Duration(days: 30)));
    } catch (_) {
      return false;
    }
  }
}
