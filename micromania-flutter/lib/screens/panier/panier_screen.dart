import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/constants.dart';
import '../../models/panier_models.dart';
import '../../providers/panier_provider.dart';
import '../../providers/auth_provider.dart';
import '../login_screen.dart';
import '../checkout/checkout_screen.dart';

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
                if (ok == true && mounted) await panierProv.vider();
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
        color: AppConstants.bg2,
        boxShadow: [BoxShadow(color: Colors.black45, blurRadius: 8, offset: const Offset(0, -2))],
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
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const CheckoutScreen()),
              ),
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
  final LignePanier ligne;
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
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Image
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: ligne.imageFullUrl.isNotEmpty
                      ? CachedNetworkImage(
                          imageUrl: ligne.imageFullUrl,
                          width: 64, height: 64,
                          fit: BoxFit.cover,
                          errorWidget: (_, __, ___) => Container(
                            width: 64, height: 64,
                            color: AppConstants.bg3,
                            child: const Icon(Icons.videogame_asset, size: 36,
                                color: AppConstants.borderColor),
                          ),
                        )
                      : Container(
                          width: 64, height: 64,
                          color: AppConstants.bg3,
                          child: const Icon(Icons.videogame_asset, size: 36,
                              color: AppConstants.borderColor),
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
                        style: const TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 13),
                      ),
                      const SizedBox(height: 2),
                      // Plateforme + statut sur une ligne
                      if (ligne.plateforme != null || ligne.statut != null)
                        Text(
                          [
                            if (ligne.plateforme != null) ligne.plateforme!,
                            if (ligne.statut != null) ligne.statut!,
                          ].join(' · '),
                          style: const TextStyle(
                              color: AppConstants.textGrey, fontSize: 12),
                        ),
                      const SizedBox(height: 6),
                      Text(
                        '${ligne.prixUnitaire.toStringAsFixed(2)} €',
                        style: const TextStyle(
                            color: AppConstants.primaryBlue,
                            fontWeight: FontWeight.bold,
                            fontSize: 14),
                      ),
                    ],
                  ),
                ),
                // Quantité + retirer
                Column(
                  children: [
                    Row(
                      children: [
                        IconButton(
                          icon: const Icon(Icons.remove_circle_outline),
                          onPressed: () => onUpdateQte(ligne.quantite - 1),
                          color: AppConstants.primaryBlue,
                          iconSize: 20,
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 6),
                          child: Text('${ligne.quantite}',
                              style: const TextStyle(
                                  fontWeight: FontWeight.bold, fontSize: 16)),
                        ),
                        IconButton(
                          icon: const Icon(Icons.add_circle_outline),
                          onPressed: () => onUpdateQte(ligne.quantite + 1),
                          color: AppConstants.primaryBlue,
                          iconSize: 20,
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                        ),
                      ],
                    ),
                    TextButton(
                      onPressed: onRemove,
                      style: TextButton.styleFrom(
                          foregroundColor: AppConstants.accentRed,
                          padding: EdgeInsets.zero,
                          minimumSize: Size.zero,
                          tapTargetSize: MaterialTapTargetSize.shrinkWrap),
                      child: const Text('Retirer', style: TextStyle(fontSize: 12)),
                    ),
                  ],
                ),
              ],
            ),
            // Ligne garantie (si présente)
            if (ligne.typeGarantie != null) ...[
              const SizedBox(height: 6),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppConstants.accentCyan.withValues(alpha: 0.08),
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(
                      color: AppConstants.accentCyan.withValues(alpha: 0.3)),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.shield_outlined,
                        size: 13, color: AppConstants.accentCyan),
                    const SizedBox(width: 4),
                    Text(
                      ligne.typeGarantie!,
                      style: const TextStyle(
                          fontSize: 11, color: AppConstants.accentCyan),
                    ),
                    if (ligne.garantiePrix != null) ...[
                      const SizedBox(width: 6),
                      Text(
                        '+${ligne.garantiePrix!.toStringAsFixed(2)} €',
                        style: const TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.bold,
                            color: AppConstants.accentCyan),
                      ),
                    ],
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
