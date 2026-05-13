import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../../core/constants.dart';
import '../../models/catalogue_models.dart';
import '../../services/catalogue_service.dart';
import '../catalogue/product_detail_screen.dart';

class _ScanResult {
  final ProduitDetail produit;
  final VariantDetail variant;

  const _ScanResult({required this.produit, required this.variant});
}

class ScannerScreen extends StatefulWidget {
  const ScannerScreen({super.key});

  @override
  State<ScannerScreen> createState() => _ScannerScreenState();
}

class _ScannerScreenState extends State<ScannerScreen>
    with WidgetsBindingObserver {
  final _service = CatalogueService();
  final _controller = MobileScannerController(
    detectionSpeed: DetectionSpeed.noDuplicates,
  );

  bool _scanning = true;
  bool _recherche = false;
  String? _dernierCode;
  _ScanResult? _resultat;
  String? _erreur;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _controller.dispose();
    super.dispose();
  }

  Future<void> _onDetect(BarcodeCapture capture) async {
    if (!_scanning || _recherche) return;
    final code = capture.barcodes.firstOrNull?.rawValue;
    if (code == null || code == _dernierCode) return;

    setState(() {
      _scanning = false;
      _recherche = true;
      _dernierCode = code;
      _resultat = null;
      _erreur = null;
    });

    try {
      // Appel unique : l'API retourne directement le produit complet via l'EAN
      final produit = await _service.rechercherProduitParEan(code);
      if (!mounted) return;

      if (produit == null || produit.variants.isEmpty) {
        setState(() {
          _erreur = 'Produit introuvable pour ce code-barres';
          _recherche = false;
        });
        return;
      }

      // Trouver le variant dont l'EAN correspond (fallback sur le premier)
      final variant = produit.variants.firstWhere(
        (v) => v.ean == code,
        orElse: () => produit.variants.first,
      );

      setState(() {
        _resultat = _ScanResult(produit: produit, variant: variant);
        _recherche = false;
      });
      _autoriserRescan();
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _erreur = 'Erreur : $e';
        _recherche = false;
      });
      _autoriserRescan();
    }
  }

  /// Ré-autorise le scan après 2,5 s — permet de rescanner le même article
  /// sans appuyer sur un bouton.
  void _autoriserRescan() {
    Future.delayed(const Duration(milliseconds: 2500), () {
      if (mounted) {
        setState(() {
          _scanning = true;
          _dernierCode = null;
        });
      }
    });
  }

  void _reset() {
    setState(() {
      _scanning = true;
      _recherche = false;
      _dernierCode = null;
      _resultat = null;
      _erreur = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Scanner'),
        actions: [
          IconButton(
            icon: ValueListenableBuilder<MobileScannerState>(
              valueListenable: _controller,
              builder: (_, state, __) => Icon(
                state.torchState == TorchState.on
                    ? Icons.flash_on
                    : Icons.flash_off,
              ),
            ),
            onPressed: () => _controller.toggleTorch(),
          ),
        ],
      ),
      body: Column(
        children: [
          // Vue caméra
          Expanded(
            flex: 2,
            child: Stack(
              children: [
                MobileScanner(
                  controller: _controller,
                  onDetect: _onDetect,
                ),
                // Cadre de visée
                Center(
                  child: Container(
                    width: 240,
                    height: 160,
                    decoration: BoxDecoration(
                      border:
                          Border.all(color: AppConstants.accentRed, width: 3),
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),
                // Instruction bas
                Positioned(
                  bottom: 16,
                  left: 0,
                  right: 0,
                  child: Center(
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16, vertical: 8),
                      decoration: BoxDecoration(
                        color: Colors.black54,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        _recherche
                            ? 'Recherche en cours…'
                            : 'Pointez vers un code-barres',
                        style: const TextStyle(
                            color: Colors.white, fontSize: 13),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),

          // Zone résultat
          Expanded(
            flex: 3,
            child: _recherche
                ? const Center(child: CircularProgressIndicator())
                : _resultat != null
                    ? _buildResultat(_resultat!)
                    : _erreur != null
                        ? _buildErreur()
                        : _buildInstruction(),
          ),
        ],
      ),
    );
  }

  Widget _buildInstruction() {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.qr_code_scanner, size: 64, color: AppConstants.textGrey),
          SizedBox(height: 16),
          Text(
            'Scannez un code-barres\npour obtenir les prix du jeu',
            textAlign: TextAlign.center,
            style: TextStyle(
                color: AppConstants.textGrey, fontSize: 15, height: 1.5),
          ),
        ],
      ),
    );
  }

  Widget _buildErreur() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.search_off, size: 64, color: Colors.orange),
          const SizedBox(height: 16),
          Text(
            _erreur ?? 'Produit introuvable',
            textAlign: TextAlign.center,
            style: const TextStyle(
                color: AppConstants.textGrey, fontSize: 15),
          ),
          const SizedBox(height: 8),
          Text(
            _dernierCode ?? '',
            style: const TextStyle(
                fontFamily: 'monospace',
                color: AppConstants.textGrey,
                fontSize: 12),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: _reset,
            icon: const Icon(Icons.qr_code_scanner),
            label: const Text('Scanner à nouveau'),
          ),
        ],
      ),
    );
  }

  Widget _buildResultat(_ScanResult r) {
    final v = r.variant;
    final p = r.produit;

    String fmt(double? prix) =>
        prix != null ? '${prix.toStringAsFixed(2)} €' : '—';

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        children: [
          // Nom + plateforme
          Text(
            v.nomCommercial ?? p.nom,
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          if (v.plateforme != null) ...[
            const SizedBox(height: 4),
            Text(v.plateforme!,
                style: const TextStyle(
                    color: AppConstants.textGrey, fontSize: 14)),
          ],
          if (v.ean != null) ...[
            const SizedBox(height: 2),
            Text('EAN : ${v.ean}',
                style: const TextStyle(
                    color: AppConstants.textGrey,
                    fontSize: 11,
                    fontFamily: 'monospace')),
          ],
          const SizedBox(height: 20),

          // 3 prix en colonnes
          IntrinsicHeight(
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _prixBox('NEUF', fmt(v.prixNeuf), AppConstants.primaryBlue),
                const SizedBox(width: 8),
                _prixBox('OCCASION', fmt(v.prixOccasion), Colors.orange.shade700),
                const SizedBox(width: 8),
                _prixBoxHighlight('REPRISE', fmt(v.prixReprise)),
              ],
            ),
          ),

          if (v.prixLocation != null) ...[
            const SizedBox(height: 10),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 10),
              decoration: BoxDecoration(
                color: Colors.purple.shade900.withValues(alpha: 0.3),
                border: Border.all(color: Colors.purple.shade300),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Column(
                children: [
                  Text('LOCATION',
                      style: TextStyle(
                          color: Colors.purple.shade300,
                          fontSize: 11,
                          fontWeight: FontWeight.bold)),
                  const SizedBox(height: 3),
                  Text(fmt(v.prixLocation),
                      style: TextStyle(
                          color: Colors.purple.shade200,
                          fontSize: 16,
                          fontWeight: FontWeight.bold)),
                ],
              ),
            ),
          ],

          const SizedBox(height: 24),

          Row(
            children: [
              Expanded(
                child: OutlinedButton.icon(
                  onPressed: _reset,
                  icon: const Icon(Icons.qr_code_scanner),
                  label: const Text('Scanner autre'),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: AppConstants.primaryBlue,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton.icon(
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => ProductDetailScreen(
                          slug: p.slug, variantId: v.id),
                    ),
                  ).then((_) => _reset()),
                  icon: const Icon(Icons.info_outline),
                  label: const Text('Voir détail'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _prixBox(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 14),
        decoration: BoxDecoration(
          border: Border.all(color: color),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(label,
                style: TextStyle(
                    color: color,
                    fontSize: 10,
                    fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(value,
                style: TextStyle(
                    color: color,
                    fontSize: 16,
                    fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }

  Widget _prixBoxHighlight(String label, String value) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 14),
        decoration: BoxDecoration(
          color: Colors.green.shade700,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(label,
                style: const TextStyle(
                    color: Colors.white70,
                    fontSize: 10,
                    fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(value,
                style: const TextStyle(
                    color: Colors.white,
                    fontSize: 16,
                    fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }
}
