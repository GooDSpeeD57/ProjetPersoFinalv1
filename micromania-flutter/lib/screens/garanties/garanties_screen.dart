import 'package:flutter/material.dart';
import '../../core/constants.dart';
import '../../models/garantie_models.dart';
import '../../services/garantie_service.dart';

class GarantiesScreen extends StatefulWidget {
  const GarantiesScreen({super.key});

  @override
  State<GarantiesScreen> createState() => _GarantiesScreenState();
}

class _GarantiesScreenState extends State<GarantiesScreen> {
  final _service = GarantieService();
  List<GarantieModel>? _garanties;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    setState(() => _loading = true);
    try {
      final list = await _service.getMesGaranties();
      setState(() {
        _garanties = list;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Center(child: CircularProgressIndicator());

    if (_garanties == null || _garanties!.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.shield_outlined, size: 72, color: Colors.grey),
            SizedBox(height: 16),
            Text('Aucune garantie enregistrée',
                style: TextStyle(color: AppConstants.textGrey, fontSize: 16)),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _charger,
      child: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: _garanties!.length,
        itemBuilder: (_, i) {
          final g = _garanties![i];
          return _GarantieCard(
            garantie: g,
            onTap: () => _afficherDetail(context, g),
          );
        },
      ),
    );
  }

  void _afficherDetail(BuildContext context, GarantieModel g) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (_) => _DetailBottomSheet(garantie: g),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Carte garantie
// ─────────────────────────────────────────────────────────────────────────────

class _GarantieCard extends StatelessWidget {
  final GarantieModel garantie;
  final VoidCallback onTap;

  const _GarantieCard({required this.garantie, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final g = garantie;
    final color = _statutColor(g);

    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          child: Row(
            children: [
              // Icône statut
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: color.withValues(alpha: 0.12),
                  shape: BoxShape.circle,
                ),
                child: Icon(
                  g.isExtension ? Icons.shield : Icons.verified_user_outlined,
                  color: color,
                  size: 22,
                ),
              ),
              const SizedBox(width: 14),

              // Infos principales
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Nom produit
                    Text(
                      g.nomProduit ?? '—',
                      style: const TextStyle(
                          fontWeight: FontWeight.bold, fontSize: 13),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),

                    // Badge type + durée
                    Row(children: [
                      _TypeBadge(isExtension: g.isExtension),
                      const SizedBox(width: 8),
                      if (g.dureeMois != null)
                        Text('${g.dureeMois} mois',
                            style: const TextStyle(
                                fontSize: 11, color: AppConstants.textGrey)),
                    ]),
                    const SizedBox(height: 2),

                    // Date de fin
                    if (g.dateFin != null)
                      Text(
                        'Fin : ${_formatDate(g.dateFin!)}',
                        style: TextStyle(fontSize: 11, color: color),
                      ),
                  ],
                ),
              ),

              // Badge statut
              _StatutBadge(garantie: g),
            ],
          ),
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Bottom sheet de détail
// ─────────────────────────────────────────────────────────────────────────────

class _DetailBottomSheet extends StatelessWidget {
  final GarantieModel garantie;
  const _DetailBottomSheet({required this.garantie});

  @override
  Widget build(BuildContext context) {
    final g = garantie;
    final color = _statutColor(g);

    return DraggableScrollableSheet(
      expand: false,
      initialChildSize: 0.55,
      maxChildSize: 0.85,
      builder: (_, controller) => SingleChildScrollView(
        controller: controller,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Poignée
              Center(
                child: Container(
                  width: 40, height: 4,
                  margin: const EdgeInsets.only(bottom: 20),
                  decoration: BoxDecoration(
                    color: Colors.grey.shade400,
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),

              // En-tête
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: color.withValues(alpha: 0.12),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      g.isExtension ? Icons.shield : Icons.verified_user_outlined,
                      color: color, size: 28,
                    ),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          g.nomProduit ?? '—',
                          style: const TextStyle(
                              fontWeight: FontWeight.bold, fontSize: 16),
                        ),
                        const SizedBox(height: 4),
                        _TypeBadge(isExtension: g.isExtension, large: true),
                      ],
                    ),
                  ),
                  _StatutBadge(garantie: g, large: true),
                ],
              ),
              const SizedBox(height: 20),
              const Divider(),
              const SizedBox(height: 12),

              // Détails
              _DetailRow(
                icon: Icons.calendar_today_outlined,
                label: 'Début',
                value: g.dateDebut != null ? _formatDate(g.dateDebut!) : '—',
              ),
              _DetailRow(
                icon: Icons.event_outlined,
                label: 'Fin de garantie',
                value: g.dateFin != null ? _formatDate(g.dateFin!) : '—',
                valueColor: color,
              ),
              if (g.dureeMois != null)
                _DetailRow(
                  icon: Icons.timelapse_outlined,
                  label: 'Durée totale',
                  value: '${g.dureeMois} mois',
                ),
              if (g.descTypeGarantie != null)
                _DetailRow(
                  icon: Icons.description_outlined,
                  label: 'Type',
                  value: g.descTypeGarantie!,
                ),
              if (g.numeroSerie != null && g.numeroSerie!.isNotEmpty)
                _DetailRow(
                  icon: Icons.qr_code_outlined,
                  label: 'N° de série',
                  value: g.numeroSerie!,
                  monospace: true,
                ),
            ],
          ),
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Widgets utilitaires
// ─────────────────────────────────────────────────────────────────────────────

class _TypeBadge extends StatelessWidget {
  final bool isExtension;
  final bool large;
  const _TypeBadge({required this.isExtension, this.large = false});

  @override
  Widget build(BuildContext context) {
    final label = isExtension ? '🛡 Additionnelle' : '⚖ Légale';
    final color = isExtension ? Colors.orange : AppConstants.primaryBlue;
    return Container(
      padding: EdgeInsets.symmetric(
          horizontal: large ? 10 : 7, vertical: large ? 4 : 2),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: color, width: 0.8),
      ),
      child: Text(label,
          style: TextStyle(
              fontSize: large ? 12 : 10,
              color: color,
              fontWeight: FontWeight.bold)),
    );
  }
}

class _StatutBadge extends StatelessWidget {
  final GarantieModel garantie;
  final bool large;
  const _StatutBadge({required this.garantie, this.large = false});

  @override
  Widget build(BuildContext context) {
    final String label;
    final Color  color;
    if (garantie.expireBientot) {
      label = '⚠ Bientôt';
      color = Colors.orange;
    } else if (garantie.isActive) {
      label = '✓ Active';
      color = Colors.green;
    } else {
      label = '✕ Expirée';
      color = Colors.red;
    }
    return Container(
      padding: EdgeInsets.symmetric(
          horizontal: large ? 12 : 8, vertical: large ? 5 : 3),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: color, width: 0.8),
      ),
      child: Text(label,
          style: TextStyle(
              fontSize: large ? 12 : 10,
              color: color,
              fontWeight: FontWeight.bold)),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String   label;
  final String   value;
  final Color?   valueColor;
  final bool     monospace;

  const _DetailRow({
    required this.icon,
    required this.label,
    required this.value,
    this.valueColor,
    this.monospace = false,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppConstants.textGrey),
          const SizedBox(width: 12),
          SizedBox(
            width: 110,
            child: Text(label,
                style: const TextStyle(
                    color: AppConstants.textGrey, fontSize: 13)),
          ),
          Expanded(
            child: Text(
              value,
              style: TextStyle(
                fontSize: 13,
                fontWeight: FontWeight.w600,
                color: valueColor,
                fontFamily: monospace ? 'monospace' : null,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers partagés
// ─────────────────────────────────────────────────────────────────────────────

Color _statutColor(GarantieModel g) {
  if (g.expireBientot) return Colors.orange;
  if (g.isActive)      return Colors.green;
  return Colors.red;
}

String _formatDate(String iso) {
  try {
    final d = DateTime.parse(iso);
    return '${d.day.toString().padLeft(2, '0')}/'
        '${d.month.toString().padLeft(2, '0')}/'
        '${d.year}';
  } catch (_) {
    return iso;
  }
}
