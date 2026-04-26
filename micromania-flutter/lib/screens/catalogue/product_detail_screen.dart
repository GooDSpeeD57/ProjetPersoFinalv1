import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:provider/provider.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../../providers/panier_provider.dart';
import '../../providers/auth_provider.dart';
import '../../widgets/price_tag.dart';
import '../login_screen.dart';

class ProductDetailScreen extends StatefulWidget {
  final String slug;
  final int? variantId;

  const ProductDetailScreen({super.key, required this.slug, this.variantId});

  @override
  State<ProductDetailScreen> createState() => _ProductDetailScreenState();
}

class _ProductDetailScreenState extends State<ProductDetailScreen> {
  final _service = CatalogueService();
  ProduitDetail? _produit;
  VariantDetail? _variantSelected;
  bool _loading = true;
  bool _ajoutEnCours = false;
  int _imageIndex = 0;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    try {
      final p = await _service.getProduitBySlug(widget.slug);
      VariantDetail? sel;
      if (widget.variantId != null) {
        sel = p.variants.where((v) => v.id == widget.variantId).firstOrNull;
      }
      sel ??= p.variants.where((v) => v.actif).firstOrNull ?? p.variants.firstOrNull;
      setState(() {
        _produit = p;
        _variantSelected = sel;
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

  Future<void> _ajouterAuPanier() async {
    final auth = context.read<AuthProvider>();
    if (!auth.isAuthenticated) {
      Navigator.push(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
      return;
    }
    if (_variantSelected == null) return;
    setState(() => _ajoutEnCours = true);
    final ok = await context.read<PanierProvider>().addLigne(_variantSelected!.id, 1);
    setState(() => _ajoutEnCours = false);
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(ok ? '✓ Ajouté au panier !' : 'Erreur lors de l\'ajout'),
        backgroundColor: ok ? Colors.green : AppConstants.accentRed,
        duration: const Duration(seconds: 2),
      ));
    }
  }

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
            // Carrousel images
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

                  // Sélecteur de variant
                  if (p.variants.length > 1) ...[
                    const Text('Édition / État',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      children: p.variants.where((v) => v.actif).map((v) {
                        final sel = v.id == _variantSelected?.id;
                        return ChoiceChip(
                          label: Text('${v.statut ?? ''} ${v.edition ?? ''}'.trim()),
                          selected: sel,
                          onSelected: (_) => setState(() => _variantSelected = v),
                          selectedColor: AppConstants.primaryBlue,
                          labelStyle: TextStyle(
                            color: sel ? Colors.white : AppConstants.textDark,
                            fontWeight: FontWeight.bold,
                          ),
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 16),
                  ],

                  // Prix
                  PriceTag(
                    prixNeuf: _variantSelected?.prixNeuf,
                    prixOccasion: _variantSelected?.prixOccasion,
                    prixReprise: _variantSelected?.prixReprise,
                  ),

                  const SizedBox(height: 20),

                  // Bouton panier
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: _ajoutEnCours ? null : _ajouterAuPanier,
                      icon: _ajoutEnCours
                          ? const SizedBox(
                              width: 18, height: 18,
                              child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                          : const Icon(Icons.shopping_bag_outlined),
                      label: const Text('Ajouter au panier'),
                    ),
                  ),

                  // Infos produit
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
            bottom: 8,
            left: 0, right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(
                images.length,
                (i) => Container(
                  margin: const EdgeInsets.symmetric(horizontal: 3),
                  width: i == _imageIndex ? 16 : 8,
                  height: 8,
                  decoration: BoxDecoration(
                    color: i == _imageIndex
                        ? AppConstants.primaryBlue
                        : Colors.grey[400],
                    borderRadius: BorderRadius.circular(4),
                  ),
                ),
              ),
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
              Text(a.prenomClient,
                  style: const TextStyle(fontWeight: FontWeight.bold)),
              const Spacer(),
              Row(children: List.generate(5, (i) => Icon(
                i < a.note ? Icons.star : Icons.star_border,
                size: 14,
                color: AppConstants.starGold,
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
