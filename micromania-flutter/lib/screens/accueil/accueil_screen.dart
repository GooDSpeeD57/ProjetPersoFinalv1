import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../catalogue/catalogue_screen.dart';
import '../catalogue/product_detail_screen.dart';
import '../magasins/magasins_screen.dart';

class AccueilScreen extends StatefulWidget {
  const AccueilScreen({super.key});

  @override
  State<AccueilScreen> createState() => _AccueilScreenState();
}

class _AccueilScreenState extends State<AccueilScreen> {
  final _service = CatalogueService();

  late Future<List<VariantSummary>> _misEnAvant;
  late Future<List<VariantSummary>> _jeux;
  late Future<List<VariantSummary>> _consoles;
  late Future<List<VariantSummary>> _accessoires;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  void _charger() {
    _misEnAvant  = _service.getMisEnAvant();
    _jeux        = _service.getCatalogue(famille: 'jeux',        size: 12).then((p) => _dedup(p.content));
    _consoles    = _service.getCatalogue(famille: 'consoles',    size: 12).then((p) => _dedup(p.content));
    _accessoires = _service.getCatalogue(famille: 'accessoires', size: 12).then((p) => _dedup(p.content));
  }

  List<VariantSummary> _dedup(List<VariantSummary> items) {
    final seen = <int, VariantSummary>{};
    for (final v in items) {
      final existing = seen[v.produitId];
      if (existing == null) {
        seen[v.produitId] = v;
      } else {
        final isNeuf = (v.statut ?? '').toUpperCase().contains('NEUF');
        final existingIsNeuf = (existing.statut ?? '').toUpperCase().contains('NEUF');
        if (isNeuf && !existingIsNeuf) seen[v.produitId] = v;
      }
    }
    return seen.values.toList();
  }

  void _ouvrirCatalogue({String? famille}) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => CatalogueScreen(familleInitiale: famille),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppConstants.bg,
      appBar: AppBar(
        backgroundColor: AppConstants.bg2,
        elevation: 0,
        centerTitle: true,
        title: Text(
          'MICROMANIA',
          style: GoogleFonts.orbitron(
            color: AppConstants.accentCyan,
            fontSize: 18,
            fontWeight: FontWeight.w900,
            letterSpacing: 3,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.location_on_outlined, color: AppConstants.textLight),
            tooltip: 'Magasins',
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const MagasinsScreen()),
            ),
          ),
          IconButton(
            icon: const Icon(Icons.search, color: AppConstants.textLight),
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const CatalogueScreen()),
            ),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() => _charger());
        },
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // ── Hero banner ─────────────────────────────────────
              _HeroBanner(onShop: () => _ouvrirCatalogue()),

              const SizedBox(height: 8),

              // ── Mis en avant ────────────────────────────────────
              _Carousel(
                titre: 'MIS EN AVANT',
                futur: _misEnAvant,
                onVoirTout: () => _ouvrirCatalogue(),
              ),

              // ── Jeux ────────────────────────────────────────────
              _Carousel(
                titre: 'JEUX VIDÉO',
                futur: _jeux,
                onVoirTout: () => _ouvrirCatalogue(famille: 'jeux'),
              ),

              // ── Consoles ────────────────────────────────────────
              _Carousel(
                titre: 'CONSOLES',
                futur: _consoles,
                onVoirTout: () => _ouvrirCatalogue(famille: 'consoles'),
              ),

              // ── Accessoires ─────────────────────────────────────
              _Carousel(
                titre: 'ACCESSOIRES',
                futur: _accessoires,
                onVoirTout: () => _ouvrirCatalogue(famille: 'accessoires'),
              ),

              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Banner
// ─────────────────────────────────────────────────────────────────────────────

class _HeroBanner extends StatelessWidget {
  final VoidCallback onShop;
  const _HeroBanner({required this.onShop});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 180,
      width: double.infinity,
      margin: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [Color(0xFF0D1F35), Color(0xFF071020)],
        ),
        border: Border.all(color: AppConstants.accentCyan.withValues(alpha: 0.3)),
        boxShadow: [
          BoxShadow(
            color: AppConstants.accentCyan.withValues(alpha: 0.08),
            blurRadius: 20,
          ),
        ],
      ),
      child: Stack(
        children: [
          // Déco fond
          Positioned(
            right: -20,
            bottom: -20,
            child: Icon(
              Icons.videogame_asset,
              size: 160,
              color: AppConstants.accentCyan.withValues(alpha: 0.05),
            ),
          ),
          // Contenu
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  'BIENVENUE',
                  style: GoogleFonts.orbitron(
                    color: AppConstants.accentCyan,
                    fontSize: 11,
                    letterSpacing: 4,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Les meilleurs jeux\nau meilleur prix',
                  style: GoogleFonts.rajdhani(
                    color: AppConstants.textLight,
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    height: 1.2,
                  ),
                ),
                const SizedBox(height: 10),
                GestureDetector(
                  onTap: onShop,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                    decoration: BoxDecoration(
                      color: AppConstants.accentCyan,
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      'Voir le catalogue →',
                      style: GoogleFonts.rajdhani(
                        color: AppConstants.bg,
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Carousel section
// ─────────────────────────────────────────────────────────────────────────────

class _Carousel extends StatelessWidget {
  final String titre;
  final Future<List<VariantSummary>> futur;
  final VoidCallback onVoirTout;

  const _Carousel({
    required this.titre,
    required this.futur,
    required this.onVoirTout,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // En-tête section
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 20, 16, 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Container(
                    width: 3,
                    height: 16,
                    decoration: BoxDecoration(
                      color: AppConstants.accentCyan,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    titre,
                    style: GoogleFonts.orbitron(
                      color: AppConstants.textLight,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.5,
                    ),
                  ),
                ],
              ),
              GestureDetector(
                onTap: onVoirTout,
                child: Text(
                  'Voir tout →',
                  style: TextStyle(
                    color: AppConstants.accentCyan,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
        ),

        // Liste horizontale
        SizedBox(
          height: 230,
          child: FutureBuilder<List<VariantSummary>>(
            future: futur,
            builder: (context, snap) {
              if (snap.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }
              if (snap.hasError || !snap.hasData || snap.data!.isEmpty) {
                return const Center(
                  child: Text('Aucun produit',
                      style: TextStyle(color: AppConstants.textGrey)),
                );
              }
              final items = snap.data!;
              return ListView.builder(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 12),
                itemCount: items.length,
                itemBuilder: (_, i) => _CarteHorizontale(
                  variant: items[i],
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => ProductDetailScreen(
                        slug: items[i].slug,
                        variantId: items[i].id,
                      ),
                    ),
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Carte carousel (format portrait étroit)
// ─────────────────────────────────────────────────────────────────────────────

class _CarteHorizontale extends StatelessWidget {
  final VariantSummary variant;
  final VoidCallback onTap;

  const _CarteHorizontale({required this.variant, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 130,
        margin: const EdgeInsets.only(right: 10),
        decoration: BoxDecoration(
          color: AppConstants.cardColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: AppConstants.borderColor),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Image
            ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(11)),
              child: SizedBox(
                height: 140,
                width: double.infinity,
                child: variant.imageFullUrl.isNotEmpty
                    ? CachedNetworkImage(
                        imageUrl: variant.imageFullUrl,
                        fit: BoxFit.cover,
                        placeholder: (_, __) => _placeholder(),
                        errorWidget: (_, __, ___) => _placeholder(),
                      )
                    : _placeholder(),
              ),
            ),
            // Infos
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      variant.nom,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                        color: AppConstants.textLight,
                        height: 1.3,
                      ),
                    ),
                    const Spacer(),
                    if (variant.prix != null)
                      Text(
                        '${variant.prix!.toStringAsFixed(2)} €',
                        style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.bold,
                          color: AppConstants.accentCyan,
                        ),
                      ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _placeholder() => Container(
        color: AppConstants.bg3,
        child: const Center(
          child: Icon(Icons.videogame_asset, size: 36, color: AppConstants.borderColor),
        ),
      );
}
