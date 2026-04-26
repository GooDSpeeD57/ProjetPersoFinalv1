import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/constants.dart';
import '../providers/panier_provider.dart';
import 'catalogue/catalogue_screen.dart';
import 'magasins/magasins_screen.dart';
import 'scanner/scanner_screen.dart';
import 'panier/panier_screen.dart';
import 'compte/compte_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _index = 0;

  // Pages créées à la demande (lazy) — le scanner n'est instancié
  // qu'au premier tap pour éviter le crash natif au démarrage.
  final Map<int, Widget> _cache = {};

  Widget _buildPage(int index) {
    return _cache.putIfAbsent(index, () {
      switch (index) {
        case 0: return const CatalogueScreen();
        case 1: return const MagasinsScreen();
        case 2: return const ScannerScreen();
        case 3: return const PanierScreen();
        case 4: return const CompteScreen();
        default: return const CatalogueScreen();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final nbArticles = context.watch<PanierProvider>().nbArticles;

    return Scaffold(
      body: _buildPage(_index),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _index,
        onTap: (i) => setState(() => _index = i),
        type: BottomNavigationBarType.fixed,
        selectedItemColor: AppConstants.primaryBlue,
        unselectedItemColor: AppConstants.textGrey,
        items: [
          const BottomNavigationBarItem(
            icon: Icon(Icons.home_outlined),
            activeIcon: Icon(Icons.home),
            label: 'Catalogue',
          ),
          const BottomNavigationBarItem(
            icon: Icon(Icons.location_on_outlined),
            activeIcon: Icon(Icons.location_on),
            label: 'Magasins',
          ),
          // Bouton scanner central
          const BottomNavigationBarItem(
            icon: _ScannerIcon(),
            label: 'Scanner',
          ),
          BottomNavigationBarItem(
            icon: Badge(
              isLabelVisible: nbArticles > 0,
              label: Text('$nbArticles'),
              child: const Icon(Icons.shopping_bag_outlined),
            ),
            activeIcon: Badge(
              isLabelVisible: nbArticles > 0,
              label: Text('$nbArticles'),
              child: const Icon(Icons.shopping_bag),
            ),
            label: 'Panier',
          ),
          const BottomNavigationBarItem(
            icon: Icon(Icons.person_outline),
            activeIcon: Icon(Icons.person),
            label: 'Compte',
          ),
        ],
      ),
    );
  }
}

class _ScannerIcon extends StatelessWidget {
  const _ScannerIcon();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 52,
      height: 52,
      decoration: const BoxDecoration(
        color: AppConstants.primaryBlue,
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(color: Colors.black26, blurRadius: 8, offset: Offset(0, 3))
        ],
      ),
      child: const Icon(Icons.qr_code_scanner, color: Colors.white, size: 26),
    );
  }
}
