import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/constants.dart';
import '../../providers/auth_provider.dart';
import '../../services/client_service.dart';
import '../../models/client_models.dart';
import '../login_screen.dart';
import '../commandes/commandes_screen.dart';
import 'fidelite_card_screen.dart';

class CompteScreen extends StatefulWidget {
  const CompteScreen({super.key});

  @override
  State<CompteScreen> createState() => _CompteScreenState();
}

class _CompteScreenState extends State<CompteScreen> {
  final _clientService = ClientService();
  FideliteDetail? _fidelite;
  List<BonAchat>? _bonsAchat;

  @override
  void initState() {
    super.initState();
    _chargerFidelite();
    _chargerBonsAchat();
  }

  Future<void> _chargerFidelite() async {
    final auth = context.read<AuthProvider>();
    if (!auth.isAuthenticated) return;
    try {
      final f = await _clientService.getFidelite();
      if (mounted) setState(() => _fidelite = f);
    } catch (_) {}
  }

  Future<void> _chargerBonsAchat() async {
    final auth = context.read<AuthProvider>();
    if (!auth.isAuthenticated) return;
    try {
      final bons = await _clientService.getBonsAchat();
      if (mounted) setState(() => _bonsAchat = bons);
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();

    if (!auth.isAuthenticated) {
      return Scaffold(
        appBar: AppBar(title: const Text('Mon compte')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.person_outline, size: 72, color: Colors.grey),
              const SizedBox(height: 16),
              const Text('Connectez-vous pour accéder à votre compte'),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: () => Navigator.push(
                  context, MaterialPageRoute(builder: (_) => const LoginScreen())),
                child: const Text('Se connecter'),
              ),
            ],
          ),
        ),
      );
    }

    final client = auth.client!;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mon compte'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Déconnexion',
            onPressed: () async {
              await auth.logout();
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          await auth.refreshClient();
          await _chargerFidelite();
          await _chargerBonsAchat();
        },
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Profil
              _buildProfilCard(client),
              const SizedBox(height: 16),

              // Carte fidélité
              _buildFideliteCard(client),
              const SizedBox(height: 16),

              // Bons d'achat
              if (_bonsAchat != null && _bonsAchat!.isNotEmpty)
                _buildBonsAchat(_bonsAchat!),

              const SizedBox(height: 16),

              // Menu compte
              _buildMenuTile(
                icon: Icons.receipt_long_outlined,
                label: 'Mes commandes',
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => Scaffold(
                      appBar: AppBar(title: const Text('Mes commandes')),
                      body: const CommandesScreen(),
                    ),
                  ),
                ),
              ),
              _buildMenuTile(
                icon: Icons.credit_card,
                label: 'Ma carte fidélité',
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => FideliteCardScreen(client: client),
                  ),
                ),
              ),
              _buildMenuTile(
                icon: Icons.star_outline,
                label: 'Programme fidélité',
                subtitle: '${client.typeFidelite} — ${client.soldePoints} pts',
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => FideliteCardScreen(client: client),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildProfilCard(ClientResponse client) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            CircleAvatar(
              radius: 30,
              backgroundColor: AppConstants.primaryBlue,
              child: Text(
                client.prenom.isNotEmpty ? client.prenom[0].toUpperCase() : '?',
                style: const TextStyle(
                  fontSize: 24,
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    client.nomComplet,
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  Text(client.email,
                      style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
                  if (client.telephone != null)
                    Text(client.telephone!,
                        style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFideliteCard(ClientResponse client) {
    final niveau = client.typeFidelite;
    final couleurNiveau = _niveauColor(niveau);

    return GestureDetector(
      onTap: () => Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => FideliteCardScreen(client: client)),
      ),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [couleurNiveau, couleurNiveau.withValues(alpha: 0.7)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(color: couleurNiveau.withValues(alpha: 0.4), blurRadius: 12, offset: const Offset(0, 4)),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('MICROMANIA',
                    style: TextStyle(
                        color: Colors.white, fontWeight: FontWeight.w900, fontSize: 16, letterSpacing: 2)),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.white24,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(niveau,
                      style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 12)),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Text(
              client.nomComplet,
              style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.stars, color: Colors.white70, size: 18),
                const SizedBox(width: 6),
                Text(
                  '${client.soldePoints} points',
                  style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ],
            ),
            const SizedBox(height: 8),
            const Text(
              'Appuyez pour voir la carte et le QR code',
              style: TextStyle(color: Colors.white70, fontSize: 11),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBonsAchat(List<BonAchat> bons) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Bons d\'achat', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        const SizedBox(height: 8),
        ...bons.where((b) => !b.utilise).map((b) => Card(
              margin: const EdgeInsets.only(bottom: 6),
              child: ListTile(
                leading: const Icon(Icons.local_offer_outlined, color: AppConstants.primaryBlue),
                title: Text('Bon de ${b.valeur.toStringAsFixed(2)} €',
                    style: const TextStyle(fontWeight: FontWeight.bold)),
                subtitle: Text(
                    b.dateExpiration != null
                        ? 'Expire le ${b.dateExpiration!.substring(0, 10)}'
                        : 'Pas de date d\'expiration',
                    style: const TextStyle(fontSize: 12)),
              ),
            )),
      ],
    );
  }

  Widget _buildMenuTile({
    required IconData icon,
    required String label,
    String? subtitle,
    required VoidCallback onTap,
  }) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: Icon(icon, color: AppConstants.primaryBlue),
        title: Text(label, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: subtitle != null ? Text(subtitle, style: const TextStyle(fontSize: 12)) : null,
        trailing: const Icon(Icons.chevron_right, color: AppConstants.textGrey),
        onTap: onTap,
      ),
    );
  }

  Color _niveauColor(String niveau) {
    return switch (niveau.toUpperCase()) {
      'ULTIMATE'  => const Color(0xFF6A0DAD),
      'GOLD'      => const Color(0xFFC8860A),
      'SILVER'    => const Color(0xFF607D8B),
      _           => AppConstants.primaryBlue,
    };
  }
}
