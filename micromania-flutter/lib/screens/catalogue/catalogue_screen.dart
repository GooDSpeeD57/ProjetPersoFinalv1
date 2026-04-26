import 'package:flutter/material.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../../widgets/product_card.dart';
import 'product_detail_screen.dart';

class CatalogueScreen extends StatefulWidget {
  const CatalogueScreen({super.key});

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

  final _familles = ['Tous', 'jeux', 'consoles', 'accessoires'];
  final _plateformes = ['Toutes', 'ps5', 'ps4', 'xbox-series', 'xbox', 'switch', 'switch2', 'pc'];

  @override
  void initState() {
    super.initState();
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
        page: _page,
        size: 20,
      );
      setState(() {
        _items.addAll(result.content);
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

  void _search(String q) {
    _query = q.isEmpty ? null : q;
    _charger(reset: true);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MICROMANIA'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () => _showSearchBar(context),
          ),
        ],
      ),
      body: Column(
        children: [
          // Filtres famille
          _buildFamilleFilter(),
          // Filtres plateforme
          _buildPlateformeFilter(),
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

  Widget _buildFamilleFilter() {
    return SizedBox(
      height: 40,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        itemCount: _familles.length,
        itemBuilder: (_, i) {
          final f = _familles[i];
          final selected = (f == 'Tous' && _famille == null) ||
              f == _famille;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: FilterChip(
              label: Text(f == 'Tous' ? 'Tous' : _capitalize(f)),
              selected: selected,
              onSelected: (_) {
                setState(() => _famille = f == 'Tous' ? null : f);
                _charger(reset: true);
              },
              selectedColor: AppConstants.primaryBlue.withValues(alpha:0.15),
              checkmarkColor: AppConstants.primaryBlue,
              labelStyle: TextStyle(
                color: selected ? AppConstants.primaryBlue : AppConstants.textGrey,
                fontWeight: selected ? FontWeight.bold : FontWeight.normal,
                fontSize: 12,
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildPlateformeFilter() {
    return SizedBox(
      height: 36,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12),
        itemCount: _plateformes.length,
        itemBuilder: (_, i) {
          final p = _plateformes[i];
          final selected = (p == 'Toutes' && _plateforme == null) ||
              p == _plateforme;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(p == 'Toutes' ? 'Toutes' : p.toUpperCase()),
              selected: selected,
              onSelected: (_) {
                setState(() => _plateforme = p == 'Toutes' ? null : p);
                _charger(reset: true);
              },
              selectedColor: AppConstants.accentRed.withValues(alpha:0.15),
              labelStyle: TextStyle(
                color: selected ? AppConstants.accentRed : AppConstants.textGrey,
                fontSize: 11,
                fontWeight: selected ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          );
        },
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
        final items = snap.data!.content;
        return ListView.builder(
          itemCount: items.length,
          itemBuilder: (_, i) => ListTile(
            leading: const Icon(Icons.videogame_asset_outlined),
            title: Text(items[i].nom, maxLines: 1, overflow: TextOverflow.ellipsis),
            subtitle: Text(items[i].plateforme ?? ''),
            onTap: () {
              _onSearch(items[i].nom);
              close(context, items[i].nom);
            },
          ),
        );
      },
    );
  }
}
