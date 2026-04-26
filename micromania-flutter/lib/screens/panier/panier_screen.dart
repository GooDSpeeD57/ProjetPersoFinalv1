import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/constants.dart';
import '../../providers/panier_provider.dart';
import '../../providers/auth_provider.dart';
import '../login_screen.dart';

class PanierScreen extends StatefulWidget {
  const PanierScreen({super.key});

  @override
  State<PanierScreen> createState() => _PanierScreenState();
}

class _PanierScreenState extends State<PanierScreen> {
  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    final auth = context.read<AuthProvider>();
    if (!auth.isAuthenticated) return;
    await context.read<PanierProvider>().charger();
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    if (!auth.isAuthenticated) {
      return Scaffold(
        appBar: AppBar(title: const Text('Mon panier')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.shopping_bag_outlined, size: 72, color: Colors.grey),
              const SizedBox(height: 16),
              const Text('Connectez-vous pour accéder à votre panier'),
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

    final panierProv = context.watch<PanierProvider>();
    final panier = panierProv.panier;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mon panier'),
        actions: [
          if (panier != null && panier.lignes.isNotEmpty)
            TextButton(
              onPressed: () async {
                final ok = await showDialog<bool>(
                  context: context,
                  builder: (_) => AlertDialog(
                    title: const Text('Vider le panier'),
                    content: const Text('Êtes-vous sûr de vouloir vider votre panier ?'),
                    actions: [
                      TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Annuler')),
                      TextButton(onPressed: () => Navigator.pop(context, true),
                          child: const Text('Vider', style: TextStyle(color: Colors.red))),
                    ],
                  ),
                );
                if (ok == true && mounted) await panierProv.panier; // vider
              },
              child: const Text('Vider', style: TextStyle(color: Colors.white70)),
            ),
        ],
      ),
      body: panierProv.loading
          ? const Center(child: CircularProgressIndicator())
          : panier == null || panier.lignes.isEmpty
              ? const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.shopping_bag_outlined, size: 72, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('Votre panier est vide',
                          style: TextStyle(fontSize: 16, color: AppConstants.textGrey)),
                    ],
                  ),
                )
              : Column(
                  children: [
                    Expanded(
                      child: RefreshIndicator(
                        onRefresh: _charger,
                        child: ListView.builder(
                          padding: const EdgeInsets.all(12),
                          itemCount: panier.lignes.length,
                          itemBuilder: (_, i) => _LignePanierCard(
                            ligne: panier.lignes[i],
                            onRemove: () => panierProv.removeLigne(panier.lignes[i].id),
                            onUpdateQte: (q) =>
                                panierProv.updateQuantite(panier.lignes[i].id, q),
                          ),
                        ),
                      ),
                    ),
                    // Récapitulatif + Commander
                    _buildRecap(panier.total),
                  ],
                ),
    );
  }

  Widget _buildRecap(double total) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 8, offset: const Offset(0, -2))],
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('Total', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              Text(
                '${total.toStringAsFixed(2)} €',
                style: const TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                  color: AppConstants.primaryBlue,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Commandez sur micromania.fr ou en magasin'),
                    duration: Duration(seconds: 3),
                  ),
                );
              },
              icon: const Icon(Icons.payment),
              label: const Text('Procéder au paiement'),
            ),
          ),
        ],
      ),
    );
  }
}

class _LignePanierCard extends StatelessWidget {
  final ligne;
  final VoidCallback onRemove;
  final Function(int) onUpdateQte;

  const _LignePanierCard({
    required this.ligne,
    required this.onRemove,
    required this.onUpdateQte,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Row(
          children: [
            // Image
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: ligne.imageFullUrl.isNotEmpty
                  ? CachedNetworkImage(
                      imageUrl: ligne.imageFullUrl,
                      width: 64, height: 64,
                      fit: BoxFit.cover,
                      errorWidget: (_, __, ___) =>
                          const Icon(Icons.videogame_asset, size: 40),
                    )
                  : Container(
                      width: 64, height: 64,
                      color: Colors.grey[200],
                      child: const Icon(Icons.videogame_asset, size: 40),
                    ),
            ),
            const SizedBox(width: 12),
            // Infos
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    ligne.nomProduit,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                  ),
                  if (ligne.plateforme != null)
                    Text(ligne.plateforme,
                        style: const TextStyle(color: AppConstants.textGrey, fontSize: 12)),
                  const SizedBox(height: 6),
                  Text(
                    '${ligne.prixUnitaire.toStringAsFixed(2)} €',
                    style: const TextStyle(
                        color: AppConstants.primaryBlue, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
            // Quantité
            Column(
              children: [
                Row(
                  children: [
                    IconButton(
                      icon: const Icon(Icons.remove_circle_outline),
                      onPressed: () => onUpdateQte(ligne.quantite - 1),
                      color: AppConstants.primaryBlue,
                      iconSize: 20,
                    ),
                    Text('${ligne.quantite}',
                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    IconButton(
                      icon: const Icon(Icons.add_circle_outline),
                      onPressed: () => onUpdateQte(ligne.quantite + 1),
                      color: AppConstants.primaryBlue,
                      iconSize: 20,
                    ),
                  ],
                ),
                TextButton(
                  onPressed: onRemove,
                  style: TextButton.styleFrom(foregroundColor: AppConstants.accentRed),
                  child: const Text('Retirer', style: TextStyle(fontSize: 12)),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
