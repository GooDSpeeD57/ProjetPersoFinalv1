import 'package:flutter/material.dart';
import '../core/constants.dart';

class PriceTag extends StatelessWidget {
  final double? prixNeuf;
  final double? prixOccasion;
  final double? prixReprise;

  const PriceTag({super.key, this.prixNeuf, this.prixOccasion, this.prixReprise});

  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 12,
      runSpacing: 6,
      children: [
        if (prixNeuf != null)
          _chip('NEUF', '${prixNeuf!.toStringAsFixed(2)} €', AppConstants.primaryBlue),
        if (prixOccasion != null)
          _chip('OCCASION', '${prixOccasion!.toStringAsFixed(2)} €', Colors.orange[700]!),
        if (prixReprise != null)
          _chip('REPRISE', '${prixReprise!.toStringAsFixed(2)} €', Colors.green[700]!),
      ],
    );
  }

  Widget _chip(String label, String value, Color color) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          border: Border.all(color: color),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          children: [
            Text(label, style: TextStyle(fontSize: 10, color: color, fontWeight: FontWeight.bold)),
            Text(value, style: TextStyle(fontSize: 16, color: color, fontWeight: FontWeight.bold)),
          ],
        ),
      );
}
