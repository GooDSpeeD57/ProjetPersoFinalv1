import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/constants.dart';
import '../../models/commande_models.dart';
import '../../services/commande_service.dart';

class CommandeDetailScreen extends StatefulWidget {
  final int id;
  const CommandeDetailScreen({super.key, required this.id});

  @override
  State<CommandeDetailScreen> createState() => _CommandeDetailScreenState();
}

class _CommandeDetailScreenState extends State<CommandeDetailScreen> {
  final _service = CommandeService();
  CommandeDetail? _commande;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    try {
      final c = await _service.getDetail(widget.id);
      setState(() { _commande = c; _loading = false; });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_commande != null ? 'Commande ${_commande!.reference}' : 'Commande'),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _commande == null
              ? const Center(child: Text('Commande introuvable'))
              : _buildDetail(_commande!),
    );
  }

  Widget _buildDetail(CommandeDetail c) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Statut
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _infoRow('Référence', c.reference),
                  _infoRow('Statut', c.statut),
                  _infoRow('Livraison', c.modeLivraison),
                  _infoRow('Paiement', c.modePaiement),
                  _infoRow('Date', c.dateCommande.length >= 10 ? c.dateCommande.substring(0, 10) : c.dateCommande),
                  if (c.adresseLivraison != null) _infoRow('Adresse', c.adresseLivraison),
                ],
              ),
            ),
          ),

          const SizedBox(height: 16),
          const Text('Articles', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
          const SizedBox(height: 8),

          // Lignes
          ...c.lignes.map((l) => Card(
            margin: const EdgeInsets.only(bottom: 8),
            child: Padding(
              padding: const EdgeInsets.all(10),
              child: Row(
                children: [
                  // Image
                  ClipRRect(
                    borderRadius: BorderRadius.circular(6),
                    child: l.imageUrl != null
                        ? CachedNetworkImage(
                            imageUrl: l.imageUrl!.startsWith('http')
                                ? l.imageUrl!
                                : 'http://goodspeed57.ddns.net:8080${l.imageUrl}',
                            width: 52, height: 52,
                            fit: BoxFit.cover,
                            errorWidget: (_, __, ___) =>
                                const Icon(Icons.videogame_asset, size: 40),
                          )
                        : Container(
                            width: 52, height: 52,
                            color: Colors.grey[200],
                            child: const Icon(Icons.videogame_asset)),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(l.nomProduit,
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                        Text('x${l.quantite}  —  ${l.prixUnitaire.toStringAsFixed(2)} €/u',
                            style: const TextStyle(color: AppConstants.textGrey, fontSize: 12)),
                      ],
                    ),
                  ),
                  Text('${l.montant.toStringAsFixed(2)} €',
                      style: const TextStyle(fontWeight: FontWeight.bold, color: AppConstants.primaryBlue)),
                ],
              ),
            ),
          )),

          // Total
          const SizedBox(height: 8),
          Card(
            color: AppConstants.primaryBlue.withValues(alpha: 0.05),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _totalRow('Sous-total', c.sousTotal),
                  _totalRow('Frais de livraison', c.fraisLivraison),
                  const Divider(),
                  _totalRow('Total', c.total, bold: true),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _infoRow(String label, String? value) {
    if (value == null) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 90,
            child: Text(label,
                style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
          ),
          Expanded(
            child: Text(value,
                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
          ),
        ],
      ),
    );
  }

  Widget _totalRow(String label, double value, {bool bold = false}) {
    final style = TextStyle(
      fontWeight: bold ? FontWeight.bold : FontWeight.normal,
      fontSize: bold ? 16 : 14,
      color: bold ? AppConstants.primaryBlue : AppConstants.textDark,
    );
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: style),
          Text('${value.toStringAsFixed(2)} €', style: style),
        ],
      ),
    );
  }
}
