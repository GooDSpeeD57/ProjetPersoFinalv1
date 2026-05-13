import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../../widgets/product_card.dart';
import 'product_detail_screen.dart';

class CatalogueScreen extends StatefulWidget {
  final String? familleInitiale;
  const CatalogueScreen({super.key, this.familleInitiale});

  @override
  State<CatalogueScreen> createState() => _CatalogueScreenState();
}

class _CatalogueScreenState extends State<CatalogueScreen> {
  final _service = CatalogueService();
  final _searchCtrl = TextEditingController();
  final _scrollCtrl = ScrollController();

  List<VariantSummary> _items = [];
  bool _loading = false;
  bool _hasMore = true;
  int _page = 0;
  String? _query;
  String? _famille;
  String? _plateforme;
  String? _etat;

  final _familles    = ['Tous', 'jeux', 'consoles', 'accessoires'];
  final _plateformes = ['Toutes', 'ps5', 'ps4', 'xbox-series', 'xbox', 'switch', 'switch2', 'pc'];
  final _etats       = ['Tout', 'NEUF', 'OCCASION'];

  @override
  void initState() {
    super.initState();
    _famille = widget.familleInitiale;
    _charger(reset: true);
    _scrollCtrl.addListener(_onScroll);
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    _scrollCtrl.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollCtrl.position.pixels >= _scrollCtrl.position.maxScrollExtent - 300 &&
        !_loading && _hasMore) {
      _charger();
    }
  }

  Future<void> _charger({bool reset = false}) async {
    if (_loading) return;
    if (reset) {
      _items = [];
      _page = 0;
      _hasMore = true;
    }
    setState(() => _loading = true);
    try {
      final result = await _service.getCatalogue(
        q: _query,
        famille: _famille,
        plateforme: _plateforme,
        etat: _etat,
        page: _page,
        size: 20,
      );
      final deduped = _dedup(result.content);
      setState(() {
        _items.addAll(deduped);
        _hasMore = !result.last;
        _page++;
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur : $e'), backgroundColor: AppConstants.accentRed),
        );
      }
    }
    if (mounted) setState(() => _loading = false);
  }

  /// 1 carte par produit : NEUF prioritaire, OCCASION si pas de NEUF.
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

  Widget _buildDrawer(BuildContext context) {
    // Copies temporaires pour l'édition dans le drawer
    String? tmpFamille   = _famille;
    String? tmpPlateforme = _plateforme;
    String? tmpEtat      = _etat;

    return StatefulBuilder(
      builder: (context, setDrawerState) {
        return Drawer(
          backgroundColor: AppConstants.bg2,
          child: SafeArea(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Header
                Padding(
                  padding: const EdgeInsets.fromLTRB(20, 20, 20, 8),
                  child: Row(
                    children: [
                      const Icon(Icons.tune, color: AppConstants.accentCyan),
                      const SizedBox(width: 10),
                      Text(
                        'FILTRES',
                        style: GoogleFonts.orbitron(
                          color: AppConstants.accentCyan,
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          letterSpacing: 2,
                        ),
                      ),
                    ],
                  ),
                ),
                const Divider(color: AppConstants.borderColor),

                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // ── Famille ───────────────────────────────
                        _drawerSection('FAMILLE'),
                        Wrap(
                          spacing: 8, runSpacing: 6,
                          children: _familles.map((f) {
                            final val = f == 'Tous' ? null : f;
                            final sel = tmpFamille == val;
                            return ChoiceChip(
                              label: Text(f == 'Tous' ? 'Tous' : _capitalize(f)),
                              selected: sel,
                              onSelected: (_) => setDrawerState(() => tmpFamille = val),
                              selectedColor: AppConstants.accentCyan.withValues(alpha: 0.2),
                              labelStyle: TextStyle(
                                color: sel ? AppConstants.accentCyan : AppConstants.textGrey,
                                fontSize: 12,
                                fontWeight: sel ? FontWeight.bold : FontWeight.normal,
                              ),
                              side: BorderSide(
                                color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
                              ),
                              backgroundColor: AppConstants.bg3,
                            );
                          }).toList(),
                        ),

                        const SizedBox(height: 20),

                        // ── Plateforme ────────────────────────────
                        _drawerSection('PLATEFORME'),
                        Wrap(
                          spacing: 8, runSpacing: 6,
                          children: _plateformes.map((p) {
                            final val = p == 'Toutes' ? null : p;
                            final sel = tmpPlateforme == val;
                            return ChoiceChip(
                              label: Text(p == 'Toutes' ? 'Toutes' : p.toUpperCase()),
                              selected: sel,
                              onSelected: (_) => setDrawerState(() => tmpPlateforme = val),
                              selectedColor: AppConstants.accentRed.withValues(alpha: 0.2),
                              labelStyle: TextStyle(
                                color: sel ? AppConstants.accentRed : AppConstants.textGrey,
                                fontSize: 11,
                                fontWeight: sel ? FontWeight.bold : FontWeight.normal,
                              ),
                              side: BorderSide(
                                color: sel ? AppConstants.accentRed : AppConstants.borderColor,
                              ),
                              backgroundColor: AppConstants.bg3,
                            );
                          }).toList(),
                        ),

                        const SizedBox(height: 20),

                        // ── État ──────────────────────────────────
                        _drawerSection('ÉTAT'),
                        Wrap(
                          spacing: 8, runSpacing: 6,
                          children: _etats.map((e) {
                            final val = e == 'Tout' ? null : e;
                            final sel = tmpEtat == val;
                            return ChoiceChip(
                              label: Text(e),
                              selected: sel,
                              onSelected: (_) => setDrawerState(() => tmpEtat = val),
                              selectedColor: AppConstants.accentCyan.withValues(alpha: 0.2),
                              labelStyle: TextStyle(
                                color: sel ? AppConstants.accentCyan : AppConstants.textGrey,
                                fontSize: 12,
                                fontWeight: sel ? FontWeight.bold : FontWeight.normal,
                              ),
                              side: BorderSide(
                                color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
                              ),
                              backgroundColor: AppConstants.bg3,
                            );
                          }).toList(),
                        ),

                        const SizedBox(height: 24),
                      ],
                    ),
                  ),
                ),

                // Boutons bas
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      // Réinitialiser
                      SizedBox(
                        width: double.infinity,
                        child: OutlinedButton(
                          onPressed: () {
                            setDrawerState(() {
                              tmpFamille = null;
                              tmpPlateforme = null;
                              tmpEtat = null;
                            });
                          },
                          style: OutlinedButton.styleFrom(
                            foregroundColor: AppConstants.textGrey,
                            side: const BorderSide(color: AppConstants.borderColor),
                          ),
                          child: const Text('Réinitialiser'),
                        ),
                      ),
                      const SizedBox(height: 8),
                      // Appliquer
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: () {
                            setState(() {
                              _famille    = tmpFamille;
                              _plateforme = tmpPlateforme;
                              _etat       = tmpEtat;
                            });
                            Navigator.pop(context);
                            _charger(reset: true);
                          },
                          child: const Text('Appliquer'),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _drawerSection(String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Text(
        label,
        style: GoogleFonts.orbitron(
          color: AppConstants.textGrey,
          fontSize: 10,
          fontWeight: FontWeight.bold,
          letterSpacing: 2,
        ),
      ),
    );
  }

  void _search(String q) {
    _query = q.isEmpty ? null : q;
    _charger(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: AppConstants.bg2,
        elevation: 0,
        leading: Builder(
          builder: (ctx) => IconButton(
            icon: const Icon(Icons.menu, color: AppConstants.textLight),
            onPressed: () => Scaffold.of(ctx).openDrawer(),
          ),
        ),
        centerTitle: true,
        title: Text(
          'CATALOGUE',
          style: GoogleFonts.orbitron(
            color: AppConstants.textLight,
            fontSize: 14,
            fontWeight: FontWeight.bold,
            letterSpacing: 2,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.search, color: AppConstants.textLight),
            onPressed: () => _showSearchBar(context),
          ),
        ],
      ),
      drawer: _buildDrawer(context),
      body: Column(
        children: [
          // Filtres actifs (indicateur visuel)
          if (_famille != null || _plateforme != null || _etat != null)
            _buildFiltresActifs(),
          // Grille produits
          Expanded(
            child: RefreshIndicator(
              onRefresh: () => _charger(reset: true),
              child: _items.isEmpty && !_loading
                  ? const Center(child: Text('Aucun produit trouvé'))
                  : GridView.builder(
                      controller: _scrollCtrl,
                      padding: const EdgeInsets.all(12),
                      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        childAspectRatio: 0.62,
                        crossAxisSpacing: 10,
                        mainAxisSpacing: 10,
                      ),
                      itemCount: _items.length + (_hasMore ? 1 : 0),
                      itemBuilder: (context, i) {
                        if (i >= _items.length) {
                          return const Center(child: CircularProgressIndicator());
                        }
                        return ProductCard(
                          variant: _items[i],
                          onTap: () => Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) => ProductDetailScreen(
                                slug: _items[i].slug,
                                variantId: _items[i].id,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFiltresActifs() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      color: AppConstants.bg3,
      child: Row(
        children: [
          const Icon(Icons.filter_list, size: 14, color: AppConstants.accentCyan),
          const SizedBox(width: 6),
          Expanded(
            child: Wrap(
              spacing: 6,
              children: [
                if (_famille != null)
                  _activeChip(_capitalize(_famille!), () {
                    setState(() => _famille = null);
                    _charger(reset: true);
                  }),
                if (_plateforme != null)
                  _activeChip(_plateforme!.toUpperCase(), () {
                    setState(() => _plateforme = null);
                    _charger(reset: true);
                  }),
                if (_etat != null)
                  _activeChip(_etat!, () {
                    setState(() => _etat = null);
                    _charger(reset: true);
                  }),
              ],
            ),
          ),
          GestureDetector(
            onTap: () {
              setState(() { _famille = null; _plateforme = null; _etat = null; });
              _charger(reset: true);
            },
            child: const Text('Tout effacer',
                style: TextStyle(color: AppConstants.accentRed, fontSize: 11)),
          ),
        ],
      ),
    );
  }

  Widget _activeChip(String label, VoidCallback onRemove) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: AppConstants.accentCyan.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppConstants.accentCyan.withValues(alpha: 0.4)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(label, style: const TextStyle(color: AppConstants.accentCyan, fontSize: 11)),
          const SizedBox(width: 4),
          GestureDetector(
            onTap: onRemove,
            child: const Icon(Icons.close, size: 12, color: AppConstants.accentCyan),
          ),
        ],
      ),
    );
  }

  void _showSearchBar(BuildContext context) {
    showSearch(
      context: context,
      delegate: _ProductSearchDelegate(_service, _search),
    );
  }

  String _capitalize(String s) =>
      s.isEmpty ? s : s[0].toUpperCase() + s.substring(1);
}

class _ProductSearchDelegate extends SearchDelegate<String> {
  final CatalogueService _service;
  final Function(String) _onSearch;

  _ProductSearchDelegate(this._service, this._onSearch);

  @override
  String get searchFieldLabel => 'Rechercher un jeu, une console…';

  @override
  List<Widget> buildActions(BuildContext context) => [
        IconButton(icon: const Icon(Icons.clear), onPressed: () => query = ''),
      ];

  @override
  Widget buildLeading(BuildContext context) =>
      IconButton(icon: const Icon(Icons.arrow_back), onPressed: () => close(context, ''));

  @override
  Widget buildResults(BuildContext context) {
    _onSearch(query);
    close(context, query);
    return const SizedBox.shrink();
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    if (query.length < 2) return const SizedBox.shrink();
    return FutureBuilder(
      future: _service.getCatalogue(q: query, size: 5),
      builder: (context, snap) {
        if (!snap.hasData) return const Center(child: CircularProgressIndicator());
        // Un seul résultat par produit (dédupe par produitId)
        final seen = <int>{};
        final items = snap.data!.content.where((v) => seen.add(v.produitId)).toList();
        return ListView.builder(
          itemCount: items.length,
          itemBuilder: (_, i) => ListTile(
            leading: const Icon(Icons.videogame_asset_outlined),
            title: Text(items[i].nom, maxLines: 1, overflow: TextOverflow.ellipsis),
            subtitle: Text(items[i].plateforme ?? ''),
            onTap: () {
              close(context, '');
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => ProductDetailScreen(slug: items[i].slug, variantId: items[i].id),
                ),
              );
            },
          ),
        );
      },
    );
  }
}
