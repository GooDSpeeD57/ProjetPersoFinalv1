import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:provider/provider.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../../services/checkout_service.dart';
import '../../providers/panier_provider.dart';
import '../../providers/auth_provider.dart';
import '../login_screen.dart';

class ProductDetailScreen extends StatefulWidget {
  final String slug;
  final int? variantId;

  const ProductDetailScreen({super.key, required this.slug, this.variantId});

  @override
  State<ProductDetailScreen> createState() => _ProductDetailScreenState();
}

class _ProductDetailScreenState extends State<ProductDetailScreen> {
  final _service     = CatalogueService();
  final _checkoutSvc = CheckoutService();

  ProduitDetail? _produit;
  VariantDetail? _variantSelected;
  bool _loading       = true;
  bool _ajoutEnCours  = false;
  int  _imageIndex    = 0;
  String _typeAchat   = 'NEUF';

  // Plateforme et édition actuellement sélectionnées
  String? _selectedPlateforme;
  String? _selectedEdition;

  // Garanties disponibles pour ce produit + sélection
  List<Map<String, dynamic>> _garanties = [];
  int? _selectedGarantieId; // null = sans garantie

  /// Variants actifs groupés par plateforme (ordre de première apparition)
  Map<String?, List<VariantDetail>> get _parPlateforme {
    final map = <String?, List<VariantDetail>>{};
    for (final v in (_produit?.variants ?? []).where((v) => v.actif)) {
      map.putIfAbsent(v.plateforme, () => []).add(v);
    }
    return map;
  }

  /// Variants pour la plateforme sélectionnée, groupés par édition
  Map<String?, List<VariantDetail>> get _parEdition {
    final map = <String?, List<VariantDetail>>{};
    for (final v in (_parPlateforme[_selectedPlateforme] ?? [])) {
      map.putIfAbsent(v.edition, () => []).add(v);
    }
    return map;
  }

  /// Garanties filtrées selon le code (même logique que le site web) :
  /// code contient NEUF/NEW → neuf seulement ; OCC/OCCASION → occasion seulement ; sinon → les deux
  List<Map<String, dynamic>> get _garantiesFiltrees {
    final etat = _typeAchat.toUpperCase();
    return _garanties.where((g) {
      final code = (g['code'] as String? ?? '').toUpperCase();
      final isNeufOnly = code.contains('NEUF') || code.contains('NEW');
      final isOccOnly  = code.contains('OCC')  || code.contains('OCCASION');
      if (etat == 'NEUF')     return !isOccOnly;
      if (etat == 'OCCASION') return !isNeufOnly;
      return !isNeufOnly && !isOccOnly;
    }).toList();
  }

  /// Variants de la plateforme + édition sélectionnées
  List<VariantDetail> get _variantsFiltres {
    final variants = _parPlateforme[_selectedPlateforme] ?? [];
    if (_selectedEdition == null) return variants;
    final filtered = variants.where((v) => v.edition == _selectedEdition).toList();
    return filtered.isNotEmpty ? filtered : variants;
  }

  /// Variantes NEUF et OCCASION pour la plateforme+édition sélectionnées
  VariantDetail? get _variantNeuf {
    final vs = _variantsFiltres;
    if (vs.isEmpty) return null;
    return vs.firstWhere(
        (v) => (v.statut ?? '').toUpperCase().contains('NEUF'),
        orElse: () => vs.first);
  }

  VariantDetail? get _variantOccasion {
    final vs = _variantsFiltres;
    if (vs.isEmpty) return null;
    return vs.firstWhere(
        (v) => (v.statut ?? '').toUpperCase().contains('OCCASION'),
        orElse: () => vs.first);
  }

  bool get _plateformeHasNeuf =>
      _variantsFiltres.any(
          (v) => (v.statut ?? '').toUpperCase().contains('NEUF') && v.prixNeuf != null);

  bool get _plateformeHasOccasion =>
      _variantsFiltres.any(
          (v) => (v.statut ?? '').toUpperCase().contains('OCCASION') && v.prixOccasion != null);

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    try {
      final p = await _service.getProduitBySlug(widget.slug);

      // Déterminer la plateforme initiale
      VariantDetail? refVariant;
      if (widget.variantId != null) {
        refVariant = p.variants.where((v) => v.id == widget.variantId).firstOrNull;
      }
      refVariant ??= p.variants.where((v) => v.actif && v.prixNeuf != null).firstOrNull
                 ?? p.variants.where((v) => v.actif).firstOrNull
                 ?? p.variants.firstOrNull;

      final plat = refVariant?.plateforme;
      final edition = refVariant?.edition;

      // Parmi les variants de cette plateforme + édition, prendre NEUF en priorité
      final variantsPlat = p.variants.where((v) => v.actif && v.plateforme == plat).toList();
      final variantsPlatEdition = edition != null
          ? variantsPlat.where((v) => v.edition == edition).toList()
          : variantsPlat;
      final pool = variantsPlatEdition.isNotEmpty ? variantsPlatEdition : variantsPlat;
      final sel = pool.firstWhere(
            (v) => (v.statut ?? '').toUpperCase().contains('NEUF') && v.prixNeuf != null,
            orElse: () => refVariant ?? p.variants.first);

      // Charger les garanties selon la catégorie du produit
      List<Map<String, dynamic>> garanties = [];
      try {
        garanties = await _checkoutSvc.getTypesGarantie(categorieId: p.categorieId);
      } catch (_) {}

      setState(() {
        _produit = p;
        _selectedPlateforme = plat;
        _selectedEdition = edition;
        _variantSelected = sel;
        _typeAchat = _initTypeAchat(sel);
        _garanties = garanties;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text('Erreur : $e'),
          backgroundColor: AppConstants.accentRed,
        ));
      }
    }
  }

  String _initTypeAchat(VariantDetail? v) {
    if (v == null) return 'NEUF';
    if (v.prixNeuf != null) return 'NEUF';
    if (v.prixOccasion != null) return 'OCCASION';
    return 'NEUF';
  }

  double? get _prixActuel => _typeAchat == 'NEUF'
      ? _variantSelected?.prixNeuf
      : _variantSelected?.prixOccasion;

  bool get _hasNeuf     => _variantSelected?.prixNeuf     != null;
  bool get _hasOccasion => _variantSelected?.prixOccasion != null;
  bool get _hasBoth     => _hasNeuf && _hasOccasion;

  // ── Ajouter au panier avec sélection garantie ──────────────────────────

  Future<void> _ajouterAuPanier() async {
    final auth = context.read<AuthProvider>();
    if (!auth.isAuthenticated) {
      Navigator.push(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
      return;
    }
    if (_variantSelected == null || _prixActuel == null) return;

    setState(() => _ajoutEnCours = true);
    final ok = await context.read<PanierProvider>().addLigne(
      _variantSelected!.id,
      1,
      idTypeGarantie: _selectedGarantieId,
    );
    setState(() => _ajoutEnCours = false);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(ok ? '✓ Ajouté au panier !' : 'Erreur lors de l\'ajout'),
        backgroundColor: ok ? Colors.green : AppConstants.accentRed,
        duration: const Duration(seconds: 2),
      ));
    }
  }

  // ── Build ──────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    if (_loading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }
    if (_produit == null) {
      return Scaffold(
        appBar: AppBar(),
        body: const Center(child: Text('Produit introuvable')),
      );
    }
    final p = _produit!;
    final images = _variantSelected?.images.isNotEmpty == true
        ? _variantSelected!.images
        : p.images;

    return Scaffold(
      appBar: AppBar(title: Text(p.nom, maxLines: 1, overflow: TextOverflow.ellipsis)),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (images.isNotEmpty) _buildCarrousel(images),

            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Nom + plateforme
                  Text(p.nom,
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  if (_variantSelected?.plateforme != null) ...[
                    const SizedBox(height: 4),
                    Text(_variantSelected!.plateforme!,
                        style: const TextStyle(color: AppConstants.textGrey, fontSize: 14)),
                  ],

                  // Note
                  if (p.noteMoyenne != null) ...[
                    const SizedBox(height: 8),
                    Row(children: [
                      ...List.generate(5, (i) => Icon(
                        i < p.noteMoyenne!.round() ? Icons.star : Icons.star_border,
                        size: 18,
                        color: AppConstants.starGold,
                      )),
                      const SizedBox(width: 6),
                      Text('${p.noteMoyenne!.toStringAsFixed(1)} (${p.nbAvis} avis)',
                          style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
                    ]),
                  ],

                  const SizedBox(height: 16),

                  // ── 1. Sélecteur Plateforme ──────────────────────
                  if (_parPlateforme.length > 1) ...[
                    const Text('Plateforme',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _parPlateforme.keys.map((plat) {
                        final sel = plat == _selectedPlateforme;
                        return _variantChip(
                          label: plat ?? 'Standard',
                          selected: sel,
                          onTap: () {
                            // Sélectionner NEUF en priorité pour cette plateforme
                            final variants = _parPlateforme[plat] ?? [];
                            final v = variants.firstWhere(
                              (v) => (v.statut ?? '').toUpperCase().contains('NEUF') && v.prixNeuf != null,
                              orElse: () => variants.first,
                            );
                            setState(() {
                              _selectedPlateforme = plat;
                              _selectedEdition = v.edition;
                              _variantSelected = v;
                              _typeAchat = _initTypeAchat(v);
                            });
                          },
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 16),
                  ],

                  // ── 2. Sélecteur Édition ─────────────────────────
                  if (_parEdition.length > 1) ...[
                    const Text('Édition',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _parEdition.keys.map((edition) {
                        final selEd = edition == _selectedEdition;
                        return _variantChip(
                          label: edition ?? 'Standard',
                          selected: selEd,
                          onTap: () {
                            final variants = _parEdition[edition] ?? [];
                            final v = variants.firstWhere(
                              (v) => (v.statut ?? '').toUpperCase().contains('NEUF') && v.prixNeuf != null,
                              orElse: () => variants.first,
                            );
                            setState(() {
                              _selectedEdition = edition;
                              _variantSelected = v;
                              _typeAchat = _initTypeAchat(v);
                            });
                          },
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 16),
                  ],

                  // ── 3. Protection / Garantie ─────────────────────
                  if (_garantiesFiltrees.isNotEmpty) ...[
                    const Text('PROTECTION',
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 13,
                            letterSpacing: 1,
                            color: AppConstants.textLight)),
                    const SizedBox(height: 6),
                    ..._garantiesFiltrees.map((g) => _garantieCheckbox(g)),
                    const SizedBox(height: 12),
                  ],

                  // ── 4. Toggle NEUF / OCCASION ────────────────────
                  if (_plateformeHasNeuf && _plateformeHasOccasion) ...[
                    const Text('État',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),
                    Row(children: [
                      _etatBtn('NEUF',
                          '${_variantNeuf!.prixNeuf!.toStringAsFixed(2)} €',
                          AppConstants.primaryBlue,
                          onTap: () => setState(() {
                            _variantSelected   = _variantNeuf;
                            _typeAchat         = 'NEUF';
                            _selectedGarantieId = null;
                          })),
                      const SizedBox(width: 10),
                      _etatBtn('OCCASION',
                          '${_variantOccasion!.prixOccasion!.toStringAsFixed(2)} €',
                          Colors.orange[700]!,
                          onTap: () => setState(() {
                            _variantSelected    = _variantOccasion;
                            _typeAchat          = 'OCCASION';
                            _selectedGarantieId = null;
                          })),
                    ]),
                    const SizedBox(height: 16),
                  ] else if (_prixActuel != null) ...[
                    // Prix unique
                    _prixBadge(),
                    const SizedBox(height: 16),
                  ],

                  // Prix de reprise (info seule)
                  if (_variantSelected?.prixReprise != null) ...[
                    Row(children: [
                      const Icon(Icons.swap_horiz, size: 14, color: Colors.green),
                      const SizedBox(width: 4),
                      Text(
                        'Reprise : ${_variantSelected!.prixReprise!.toStringAsFixed(2)} €',
                        style: const TextStyle(fontSize: 12, color: Colors.green),
                      ),
                    ]),
                    const SizedBox(height: 12),
                  ],

                  // Bouton panier
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: (_ajoutEnCours || _prixActuel == null) ? null : _ajouterAuPanier,
                      icon: _ajoutEnCours
                          ? const SizedBox(
                              width: 18, height: 18,
                              child: CircularProgressIndicator(
                                  color: Colors.white, strokeWidth: 2))
                          : const Icon(Icons.shopping_bag_outlined),
                      label: Text(_prixActuel == null
                          ? 'Indisponible'
                          : 'Ajouter au panier — $_typeAchat'),
                    ),
                  ),

                  // Description
                  if (p.description != null || p.resumeCourt != null) ...[
                    const SizedBox(height: 24),
                    const Divider(),
                    const Text('Description',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const SizedBox(height: 8),
                    Text(p.resumeCourt ?? p.description ?? '',
                        style: const TextStyle(color: AppConstants.textGrey, height: 1.5)),
                  ],

                  // Infos complémentaires
                  const SizedBox(height: 16),
                  _buildInfoRow('Éditeur', p.editeur),
                  _buildInfoRow('Date de sortie', p.dateSortie),
                  _buildInfoRow('PEGI', p.pegi != null ? '${p.pegi}+' : null),
                  _buildInfoRow('EAN', _variantSelected?.ean),

                  // Avis
                  if (p.avis.isNotEmpty) ...[
                    const SizedBox(height: 24),
                    const Divider(),
                    const Text('Avis clients',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const SizedBox(height: 8),
                    ...p.avis.map((a) => _buildAvis(a)),
                  ],

                  const SizedBox(height: 32),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ── Widgets helper ──────────────────────────────────────────────────────

  Widget _garantieCheckbox(Map<String, dynamic> g) {
    final id   = (g['id'] as num).toInt();
    final sel  = _selectedGarantieId == id;
    final desc = (g['description'] ?? g['code'] ?? '') as String;
    final duree = g['dureeMois'] != null ? '${g['dureeMois']} mois' : '';
    final prix  = g['prixExtension'] != null
        ? '${(g['prixExtension'] as num).toStringAsFixed(2)} €'
        : '';

    return InkWell(
      onTap: () => setState(() => _selectedGarantieId = sel ? null : id),
      borderRadius: BorderRadius.circular(6),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 6, horizontal: 2),
        child: Row(
          children: [
            Checkbox(
              value: sel,
              onChanged: (v) =>
                  setState(() => _selectedGarantieId = (v == true) ? id : null),
              activeColor: AppConstants.accentCyan,
              materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
              visualDensity: VisualDensity.compact,
            ),
            const SizedBox(width: 6),
            const Text('🛡 ', style: TextStyle(fontSize: 13)),
            Expanded(
              child: RichText(
                text: TextSpan(children: [
                  TextSpan(
                    text: desc,
                    style: const TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: AppConstants.textLight),
                  ),
                  if (duree.isNotEmpty)
                    TextSpan(
                      text: '  $duree',
                      style: const TextStyle(
                          fontSize: 12, color: AppConstants.textGrey),
                    ),
                ]),
              ),
            ),
            if (prix.isNotEmpty)
              Text(prix,
                  style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.bold,
                      color: AppConstants.accentCyan)),
          ],
        ),
      ),
    );
  }

  Widget _variantChip({
    required String label,
    required bool selected,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? AppConstants.accentCyan.withValues(alpha: 0.15) : AppConstants.bg3,
          border: Border.all(
            color: selected ? AppConstants.accentCyan : AppConstants.borderColor,
            width: selected ? 2 : 1,
          ),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 13,
            fontWeight: selected ? FontWeight.bold : FontWeight.normal,
            color: selected ? AppConstants.accentCyan : AppConstants.textGrey,
          ),
        ),
      ),
    );
  }

  Widget _etatBtn(String type, String prix, Color color, {required VoidCallback onTap}) {
    final sel = _typeAchat == type;
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: sel ? color.withValues(alpha: 0.15) : Colors.transparent,
            border: Border.all(color: sel ? color : AppConstants.borderColor, width: sel ? 2 : 1),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Column(
            children: [
              Text(type,
                  style: TextStyle(
                      fontSize: 11, fontWeight: FontWeight.bold,
                      color: sel ? color : AppConstants.textGrey)),
              const SizedBox(height: 4),
              Text(prix,
                  style: TextStyle(
                      fontSize: 18, fontWeight: FontWeight.bold,
                      color: sel ? color : AppConstants.textGrey)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _prixBadge() {
    final color = _typeAchat == 'NEUF' ? AppConstants.primaryBlue : Colors.orange[700]!;
    return Row(children: [
      Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: color.withValues(alpha: 0.12),
          border: Border.all(color: color),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Column(
          children: [
            Text(_typeAchat,
                style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: color)),
            Text('${_prixActuel!.toStringAsFixed(2)} €',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    ]);
  }

  Widget _buildCarrousel(List<ProduitImage> images) {
    return Stack(
      children: [
        SizedBox(
          height: 260,
          child: PageView.builder(
            itemCount: images.length,
            onPageChanged: (i) => setState(() => _imageIndex = i),
            itemBuilder: (_, i) => CachedNetworkImage(
              imageUrl: images[i].fullUrl,
              fit: BoxFit.contain,
              placeholder: (_, __) => const Center(child: CircularProgressIndicator()),
              errorWidget: (_, __, ___) =>
                  const Icon(Icons.videogame_asset, size: 80, color: Colors.grey),
            ),
          ),
        ),
        if (images.length > 1)
          Positioned(
            bottom: 8, left: 0, right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(images.length, (i) => Container(
                margin: const EdgeInsets.symmetric(horizontal: 3),
                width: i == _imageIndex ? 16 : 8,
                height: 8,
                decoration: BoxDecoration(
                  color: i == _imageIndex ? AppConstants.primaryBlue : Colors.grey[400],
                  borderRadius: BorderRadius.circular(4),
                ),
              )),
            ),
          ),
      ],
    );
  }

  Widget _buildInfoRow(String label, String? value) {
    if (value == null) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Row(
        children: [
          SizedBox(
            width: 110,
            child: Text(label,
                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
          ),
          Expanded(
            child: Text(value,
                style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
          ),
        ],
      ),
    );
  }

  Widget _buildAvis(AvisPublic a) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(children: [
              Text(a.prenomClient, style: const TextStyle(fontWeight: FontWeight.bold)),
              const Spacer(),
              Row(children: List.generate(5, (i) => Icon(
                i < a.note ? Icons.star : Icons.star_border,
                size: 14, color: AppConstants.starGold,
              ))),
            ]),
            if (a.commentaire != null) ...[
              const SizedBox(height: 6),
              Text(a.commentaire!,
                  style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
            ],
          ],
        ),
      ),
    );
  }
}

