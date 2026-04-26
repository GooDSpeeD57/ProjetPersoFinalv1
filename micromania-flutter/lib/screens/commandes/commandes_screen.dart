import 'package:flutter/material.dart';
import '../../core/constants.dart';
import '../../models/commande_models.dart';
import '../../services/commande_service.dart';
import 'commande_detail_screen.dart';

class CommandesScreen extends StatefulWidget {
  const CommandesScreen({super.key});

  @override
  State<CommandesScreen> createState() => _CommandesScreenState();
}

class _CommandesScreenState extends State<CommandesScreen> {
  final _service = CommandeService();
  List<CommandeSummary>? _commandes;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    setState(() => _loading = true);
    try {
      final list = await _service.getMesCommandes();
      setState(() {
        _commandes = list;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Center(child: CircularProgressIndicator());
    if (_commandes == null || _commandes!.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.receipt_long_outlined, size: 72, color: Colors.grey),
            SizedBox(height: 16),
            Text('Aucune commande', style: TextStyle(color: AppConstants.textGrey, fontSize: 16)),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _charger,
      child: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: _commandes!.length,
        itemBuilder: (_, i) {
          final c = _commandes![i];
          return Card(
            margin: const EdgeInsets.only(bottom: 10),
            child: ListTile(
              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              leading: Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: _statutColor(c.statut).withValues(alpha:0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(Icons.receipt_outlined, color: _statutColor(c.statut)),
              ),
              title: Text(
                c.reference,
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 4),
                  Row(children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: _statutColor(c.statut).withValues(alpha:0.1),
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: _statutColor(c.statut), width: 0.8),
                      ),
                      child: Text(
                        _statutLabel(c.statut),
                        style: TextStyle(
                          fontSize: 11,
                          color: _statutColor(c.statut),
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text(c.dateCommande.substring(0, 10),
                        style: const TextStyle(fontSize: 11, color: AppConstants.textGrey)),
                  ]),
                  const SizedBox(height: 2),
                  Text('${c.nbArticles} article(s)',
                      style: const TextStyle(fontSize: 12, color: AppConstants.textGrey)),
                ],
              ),
              trailing: Text(
                '${c.total.toStringAsFixed(2)} €',
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 15,
                  color: AppConstants.primaryBlue,
                ),
              ),
              onTap: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => CommandeDetailScreen(id: c.id)),
              ),
            ),
          );
        },
      ),
    );
  }

  Color _statutColor(String statut) {
    return switch (statut.toUpperCase()) {
      'LIVREE' || 'LIVRÉ' => Colors.green,
      'EN_COURS' || 'PREPAREE' || 'EXPEDIEE' => Colors.blue,
      'ANNULEE' || 'ANNULÉ' => Colors.red,
      _ => Colors.orange,
    };
  }

  String _statutLabel(String statut) {
    return switch (statut.toUpperCase()) {
      'EN_ATTENTE' => 'En attente',
      'PREPAREE'   => 'En préparation',
      'EXPEDIEE'   => 'Expédiée',
      'LIVREE'     => 'Livrée',
      'ANNULEE'    => 'Annulée',
      _            => statut,
    };
  }
}
