import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../models/catalogue_models.dart';
import '../core/constants.dart';

class ProductCard extends StatelessWidget {
  final VariantSummary variant;
  final VoidCallback onTap;

  const ProductCard({super.key, required this.variant, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Card(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Image
            Expanded(
              child: Stack(
                children: [
                  ClipRRect(
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                    child: variant.imageFullUrl.isNotEmpty
                        ? CachedNetworkImage(
                            imageUrl: variant.imageFullUrl,
                            width: double.infinity,
                            fit: BoxFit.cover,
                            placeholder: (_, __) => _placeholder(),
                            errorWidget: (_, __, ___) => _placeholder(),
                          )
                        : _placeholder(),
                  ),
                  // Badge NOUVEAUTÉ / PRÉ-COMMANDE
                  if (variant.estPreCommande)
                    Positioned(
                      top: 8, left: 8,
                      child: _badge('PRÉ-COMMANDE', AppConstants.accentRed),
                    )
                  else if (variant.misEnAvant)
                    Positioned(
                      top: 8, left: 8,
                      child: _badge('NOUVEAUTÉ', AppConstants.primaryBlue),
                    ),
                  // PEGI
                  if (variant.pegi != null)
                    Positioned(
                      top: 8, right: 8,
                      child: _pegiBadge(variant.pegi!),
                    ),
                ],
              ),
            ),
            // Infos
            Padding(
              padding: const EdgeInsets.all(8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    variant.nom,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 12,
                      color: AppConstants.textDark,
                    ),
                  ),
                  if (variant.plateforme != null) ...[
                    const SizedBox(height: 2),
                    Text(
                      variant.plateforme!,
                      style: const TextStyle(
                        fontSize: 11,
                        color: AppConstants.textGrey,
                      ),
                    ),
                  ],
                  // Note étoiles
                  if (variant.noteMoyenne != null) ...[
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        ...List.generate(5, (i) => Icon(
                          i < variant.noteMoyenne!.round()
                              ? Icons.star
                              : Icons.star_border,
                          size: 12,
                          color: AppConstants.starGold,
                        )),
                        const SizedBox(width: 4),
                        Text('(${variant.nbAvis})',
                            style: const TextStyle(fontSize: 10, color: AppConstants.textGrey)),
                      ],
                    ),
                  ],
                  const SizedBox(height: 6),
                  // Prix
                  if (variant.prix != null)
                    Text(
                      '${variant.prix!.toStringAsFixed(2)} €',
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 15,
                        color: AppConstants.primaryBlue,
                      ),
                    )
                  else
                    const Text('Prix non disponible',
                        style: TextStyle(fontSize: 11, color: AppConstants.textGrey)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _placeholder() => Container(
        color: AppConstants.bg3,
        child: const Center(
          child: Icon(Icons.videogame_asset, size: 48, color: AppConstants.borderColor),
        ),
      );

  Widget _badge(String label, Color color) => Container(
        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
        decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(4)),
        child: Text(label,
            style: const TextStyle(color: Colors.white, fontSize: 9, fontWeight: FontWeight.bold)),
      );

  Widget _pegiBadge(int pegi) => Container(
        width: 28, height: 28,
        decoration: BoxDecoration(
          color: _pegiColor(pegi),
          borderRadius: BorderRadius.circular(4),
        ),
        child: Center(
          child: Text('$pegi',
              style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
        ),
      );

  Color _pegiColor(int pegi) {
    if (pegi >= 18) return Colors.red[800]!;
    if (pegi >= 16) return Colors.red[400]!;
    if (pegi >= 12) return Colors.orange;
    if (pegi >= 7) return Colors.green;
    return Colors.green[300]!;
  }
}
