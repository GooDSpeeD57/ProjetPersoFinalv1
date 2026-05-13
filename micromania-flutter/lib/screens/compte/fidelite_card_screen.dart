import 'package:flutter/material.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:barcode_widget/barcode_widget.dart';
import '../../core/constants.dart';
import '../../models/client_models.dart';

class FideliteCardScreen extends StatefulWidget {
  final ClientResponse client;
  const FideliteCardScreen({super.key, required this.client});

  @override
  State<FideliteCardScreen> createState() => _FideliteCardScreenState();
}

class _FideliteCardScreenState extends State<FideliteCardScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabCtrl;

  // Valeur encodée dans le code : ID client en chaîne
  String get _codeValue => 'MICRO-${widget.client.id}';

  @override
  void initState() {
    super.initState();
    _tabCtrl = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final client = widget.client;
    final couleurNiveau = _niveauColor(client.niveauFidelite);

    return Scaffold(
      appBar: AppBar(title: const Text('Carte fidélité')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // Carte visuelle
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [couleurNiveau, couleurNiveau.withValues(alpha: 0.75)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(20),
                boxShadow: [
                  BoxShadow(
                      color: couleurNiveau.withValues(alpha: 0.4),
                      blurRadius: 16,
                      offset: const Offset(0, 6)),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('MICROMANIA',
                      style: TextStyle(
                          color: Colors.white,
                          fontSize: 20,
                          fontWeight: FontWeight.w900,
                          letterSpacing: 3)),
                  const SizedBox(height: 4),
                  Text('Carte ${client.niveauFidelite}',
                      style: const TextStyle(color: Colors.white70, fontSize: 12, letterSpacing: 1)),
                  const SizedBox(height: 24),
                  Text(
                    client.nomComplet,
                    style: const TextStyle(
                        color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '#${client.id}',
                    style: const TextStyle(color: Colors.white60, fontSize: 13),
                  ),
                  const SizedBox(height: 20),
                  Row(
                    children: [
                      const Icon(Icons.stars, color: Colors.white70, size: 20),
                      const SizedBox(width: 8),
                      Text(
                        '${client.pointsFidelite} points',
                        style: const TextStyle(
                            color: Colors.white, fontSize: 22, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            const SizedBox(height: 32),

            // Tabs QR / Code-barres
            Container(
              decoration: BoxDecoration(
                color: Colors.grey[200],
                borderRadius: BorderRadius.circular(12),
              ),
              child: TabBar(
                controller: _tabCtrl,
                indicator: BoxDecoration(
                  color: AppConstants.primaryBlue,
                  borderRadius: BorderRadius.circular(12),
                ),
                labelColor: Colors.white,
                unselectedLabelColor: AppConstants.textGrey,
                tabs: const [
                  Tab(icon: Icon(Icons.qr_code), text: 'QR Code'),
                  Tab(icon: Icon(Icons.barcode_reader), text: 'Code-barres'),
                ],
              ),
            ),

            const SizedBox(height: 24),

            SizedBox(
              height: 200,
              child: TabBarView(
                controller: _tabCtrl,
                children: [
                  // QR Code
                  Center(
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(12),
                        boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 8)],
                      ),
                      child: QrImageView(
                        data: _codeValue,
                        version: QrVersions.auto,
                        size: 160,
                        backgroundColor: Colors.white,
                      ),
                    ),
                  ),

                  // Code-barres EAN-13 / Code128
                  Center(
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(12),
                        boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 8)],
                      ),
                      child: BarcodeWidget(
                        barcode: Barcode.code128(),
                        data: _codeValue,
                        width: 260,
                        height: 100,
                        drawText: true,
                        style: const TextStyle(fontSize: 11, color: AppConstants.textDark),
                      ),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Infos programme
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    _infoRow(Icons.emoji_events, 'Niveau', client.niveauFidelite),
                    const Divider(),
                    _infoRow(Icons.stars, 'Points cumulés', '${client.pointsFidelite} pts'),
                    if (client.abonneUltimate) ...[
                      const Divider(),
                      _infoRow(Icons.workspace_premium, 'Abonnement', 'ULTIMATE ✓'),
                    ],
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, color: AppConstants.primaryBlue, size: 20),
          const SizedBox(width: 12),
          Text(label, style: const TextStyle(color: AppConstants.textGrey)),
          const Spacer(),
          Text(value,
              style: const TextStyle(fontWeight: FontWeight.bold, color: AppConstants.textDark)),
        ],
      ),
    );
  }

  Color _niveauColor(String niveau) {
    return switch (niveau.toUpperCase()) {
      'ULTIMATE' => const Color(0xFF6A0DAD),
      'GOLD'     => const Color(0xFFC8860A),
      'SILVER'   => const Color(0xFF607D8B),
      _          => AppConstants.primaryBlue,
    };
  }
}
