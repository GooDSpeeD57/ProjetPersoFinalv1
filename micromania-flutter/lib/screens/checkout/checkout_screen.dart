import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import '../../core/constants.dart';
import '../../providers/auth_provider.dart';
import '../../providers/panier_provider.dart';
import '../../services/checkout_service.dart';

class CheckoutScreen extends StatefulWidget {
  const CheckoutScreen({super.key});

  @override
  State<CheckoutScreen> createState() => _CheckoutScreenState();
}

class _CheckoutScreenState extends State<CheckoutScreen> {
  final _svc = CheckoutService();

  // ── Données chargées ────────────────────────────────────────────────────
  List<Map<String, dynamic>> _adresses  = [];
  List<Map<String, dynamic>> _magasins  = [];
  List<Map<String, dynamic>> _bons      = [];
  bool _loading = true;

  // ── Sélections ──────────────────────────────────────────────────────────
  String    _modeLivraison = 'DOMICILE';  // DOMICILE | RETRAIT_MAGASIN | LIVRAISON_MAGASIN
  String    _modePaiement  = 'CB';
  int?      _idAdresse;
  int?      _idMagasin;
  final     _idsBons = <int>{};
  bool      _submitting = false;

  // ── Géolocalisation ─────────────────────────────────────────────────────
  Position? _position;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    // Géolocalisation en parallèle (silencieux si refusée)
    await _tenterGeolocPrecise();

    try {
      final results = await Future.wait([
        _svc.getAdresses(),
        _svc.getMagasins(),
        _svc.getBonsAchat(),
      ]);

      final magasins = results[1];
      _calculerEtTrierDistances(magasins);

      setState(() {
        _adresses = results[0];
        _magasins = magasins;
        _bons     = results[2]
            .where((b) => !(b['utilise'] as bool? ?? false))
            .toList();
        // Pré-sélectionner adresse défaut
        final defaut = _adresses.firstWhere(
          (a) => a['estDefaut'] == true,
          orElse: () => _adresses.isNotEmpty ? _adresses.first : {},
        );
        if (defaut.isNotEmpty) _idAdresse = (defaut['id'] as num?)?.toInt();
        // Pré-sélectionner le magasin favori du compte
        _idMagasin = _idMagasinFavoriOuPlusProche();
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  /// Tente d'obtenir la position GPS (timeout 5 s, silencieux si refus).
  Future<void> _tenterGeolocPrecise() async {
    try {
      var perm = await Geolocator.checkPermission();
      if (perm == LocationPermission.denied) {
        perm = await Geolocator.requestPermission();
      }
      if (perm == LocationPermission.whileInUse ||
          perm == LocationPermission.always) {
        _position = await Geolocator.getCurrentPosition(
          locationSettings: const LocationSettings(
            accuracy: LocationAccuracy.low,
            timeLimit: Duration(seconds: 5),
          ),
        );
      }
    } catch (_) {}
  }

  /// Calcule la distance de chaque magasin et trie du plus proche au plus loin.
  void _calculerEtTrierDistances(List<Map<String, dynamic>> magasins) {
    if (_position == null) return;
    for (final m in magasins) {
      final lat = (m['latitude']  as num?)?.toDouble();
      final lng = (m['longitude'] as num?)?.toDouble();
      if (lat != null && lng != null) {
        m['_distanceKm'] = Geolocator.distanceBetween(
              _position!.latitude, _position!.longitude, lat, lng)
            / 1000.0;
      }
    }
    magasins.sort((a, b) {
      final da = (a['_distanceKm'] as double?) ?? double.infinity;
      final db = (b['_distanceKm'] as double?) ?? double.infinity;
      return da.compareTo(db);
    });
  }

  /// Retourne l'id du magasin favori du compte (s'il est dans la liste),
  /// ou l'id du magasin le plus proche si la géoloc est disponible,
  /// ou null sinon.
  int? _idMagasinFavoriOuPlusProche() {
    final client = context.read<AuthProvider>().client;
    final favoriId = client?.magasinFavoriId;
    if (favoriId != null) {
      final existe = _magasins.any(
          (m) => (m['id'] as num?)?.toInt() == favoriId);
      if (existe) return favoriId;
    }
    // Pas de favori → prendre le plus proche (déjà trié)
    if (_position != null && _magasins.isNotEmpty) {
      return (_magasins.first['id'] as num?)?.toInt();
    }
    return null;
  }

  Future<void> _valider() async {
    // Validations
    if (_modeLivraison == 'DOMICILE' && _idAdresse == null) {
      _snack('Sélectionnez une adresse de livraison', error: true);
      return;
    }
    if ((_modeLivraison == 'RETRAIT_MAGASIN' || _modeLivraison == 'LIVRAISON_MAGASIN') &&
        _idMagasin == null) {
      _snack('Sélectionnez un magasin', error: true);
      return;
    }

    setState(() => _submitting = true);
    try {
      final facture = await _svc.checkout(
        idAdresse:         _modeLivraison == 'DOMICILE' ? _idAdresse : null,
        idMagasinRetrait:  _modeLivraison != 'DOMICILE' ? _idMagasin : null,
        modePaiementCode:  _modePaiement,
        modeLivraisonCode: _modeLivraison,
        idsBonAchat:       _idsBons.toList(),
      );
      if (!mounted) return;
      // Vider le panier + rafraîchir les points fidélité
      context.read<PanierProvider>().reset();
      context.read<AuthProvider>().refreshClient();
      // Aller à la confirmation
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => _ConfirmationScreen(facture: facture)),
      );
    } catch (e) {
      setState(() => _submitting = false);
      _snack(e.toString(), error: true);
    }
  }

  void _snack(String msg, {bool error = false}) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(msg),
      backgroundColor: error ? AppConstants.accentRed : Colors.green,
    ));
  }

  // ── Build ──────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    final total = context.watch<PanierProvider>().total;

    return Scaffold(
      appBar: AppBar(title: const Text('Finaliser la commande')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _section('Mode de livraison', Icons.local_shipping_outlined),
                  _livraisonSelector(),

                  const SizedBox(height: 8),

                  // Adresse (si DOMICILE)
                  if (_modeLivraison == 'DOMICILE') ...[
                    _section('Adresse de livraison', Icons.home_outlined),
                    _adresseSelector(),
                  ],

                  // Magasin (si RETRAIT ou LIVRAISON_MAGASIN)
                  if (_modeLivraison != 'DOMICILE') ...[
                    _section('Magasin', Icons.store_outlined),
                    _magasinSelector(),
                  ],

                  const SizedBox(height: 8),
                  _section('Mode de paiement', Icons.payment_outlined),
                  _paiementSelector(),

                  if (_bons.isNotEmpty) ...[
                    const SizedBox(height: 8),
                    _section('Bons d\'achat', Icons.local_offer_outlined),
                    _bonsSelector(),
                  ],

                  const SizedBox(height: 24),
                  _recap(total),
                ],
              ),
            ),
    );
  }

  // ── Livraison ─────────────────────────────────────────────────────────

  Widget _livraisonSelector() {
    return Column(
      children: [
        _livraisonTile('DOMICILE',         Icons.home,         'À domicile',
            'Livraison à votre adresse'),
        _livraisonTile('RETRAIT_MAGASIN',  Icons.store,        'Retrait en magasin',
            'Récupérez votre commande en magasin'),
        _livraisonTile('LIVRAISON_MAGASIN',Icons.shopping_bag, 'En boutique',
            'Commandez et récupérez sur place'),
      ],
    );
  }

  Widget _livraisonTile(String code, IconData icon, String titre, String sous) {
    final sel = _modeLivraison == code;
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
        side: BorderSide(
            color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
            width: sel ? 2 : 1),
      ),
      child: ListTile(
        leading: Icon(icon,
            color: sel ? AppConstants.accentCyan : AppConstants.textGrey),
        title: Text(titre,
            style: TextStyle(
                fontWeight: FontWeight.bold,
                color: sel ? AppConstants.accentCyan : AppConstants.textLight)),
        subtitle: Text(sous,
            style: const TextStyle(fontSize: 12, color: AppConstants.textGrey)),
        trailing: sel
            ? const Icon(Icons.check_circle, color: AppConstants.accentCyan)
            : null,
        onTap: () => setState(() {
          _modeLivraison = code;
          // Pré-sélectionner le favori ou le plus proche quand on choisit un mode magasin
          if (code != 'DOMICILE') {
            _idMagasin ??= _idMagasinFavoriOuPlusProche();
          }
        }),
      ),
    );
  }

  // ── Adresse ───────────────────────────────────────────────────────────

  Widget _adresseSelector() {
    if (_adresses.isEmpty) {
      return const Card(
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Text('Aucune adresse enregistrée.\nAjoutez-en une dans votre compte.',
              style: TextStyle(color: AppConstants.textGrey)),
        ),
      );
    }
    return Column(
      children: _adresses.map((a) {
        final id  = (a['id'] as num).toInt();
        final sel = _idAdresse == id;
        final rue = a['rue'] ?? '';
        final cp  = a['codePostal'] ?? '';
        final vil = a['ville'] ?? '';
        return Card(
          margin: const EdgeInsets.only(bottom: 6),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
            side: BorderSide(
                color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
                width: sel ? 2 : 1),
          ),
          child: RadioListTile<int>(
            value: id,
            groupValue: _idAdresse,
            onChanged: (v) => setState(() => _idAdresse = v),
            activeColor: AppConstants.accentCyan,
            title: Text('$rue, $cp $vil',
                style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
            subtitle: a['complement'] != null
                ? Text(a['complement'], style: const TextStyle(fontSize: 12))
                : null,
          ),
        );
      }).toList(),
    );
  }

  // ── Magasin ───────────────────────────────────────────────────────────

  Widget _magasinSelector() {
    if (_magasins.isEmpty) {
      return const Card(
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Text('Aucun magasin disponible.',
              style: TextStyle(color: AppConstants.textGrey)),
        ),
      );
    }

    final client    = context.read<AuthProvider>().client;
    final favoriId  = client?.magasinFavoriId;

    return Column(
      children: _magasins.take(5).map((m) {
        final id      = (m['id'] as num).toInt();
        final sel     = _idMagasin == id;
        final isFav   = favoriId != null && id == favoriId;
        final distKm  = m['_distanceKm'] as double?;
        final distTxt = distKm != null
            ? distKm < 1
                ? '${(distKm * 1000).round()} m'
                : '${distKm.toStringAsFixed(1)} km'
            : null;
        final adresse = m['adresseComplete'] ?? m['ville'] ?? '';

        return Card(
          margin: const EdgeInsets.only(bottom: 6),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
            side: BorderSide(
              color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
              width: sel ? 2 : 1,
            ),
          ),
          child: RadioListTile<int>(
            value: id,
            groupValue: _idMagasin,
            onChanged: (v) => setState(() => _idMagasin = v),
            activeColor: AppConstants.accentCyan,
            title: Row(
              children: [
                Expanded(
                  child: Text(m['nom'] ?? '',
                      style: const TextStyle(
                          fontSize: 13, fontWeight: FontWeight.w500)),
                ),
                if (isFav)
                  Container(
                    margin: const EdgeInsets.only(left: 6),
                    padding: const EdgeInsets.symmetric(
                        horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(
                      color: AppConstants.accentCyan.withValues(alpha: 0.15),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                          color: AppConstants.accentCyan.withValues(alpha: 0.4)),
                    ),
                    child: const Text('⭐ Favori',
                        style: TextStyle(
                            fontSize: 10,
                            color: AppConstants.accentCyan,
                            fontWeight: FontWeight.bold)),
                  ),
                if (distTxt != null) ...[
                  const SizedBox(width: 6),
                  Text(distTxt,
                      style: TextStyle(
                          fontSize: 11,
                          color: sel
                              ? AppConstants.accentCyan
                              : AppConstants.textGrey)),
                ],
              ],
            ),
            subtitle: adresse.isNotEmpty
                ? Text(adresse,
                    style: const TextStyle(
                        fontSize: 12, color: AppConstants.textGrey))
                : null,
          ),
        );
      }).toList(),
    );
  }

  // ── Paiement ──────────────────────────────────────────────────────────

  Widget _paiementSelector() {
    final codes  = ['CB', 'PAYPAL', 'APPLE_PAY', 'GOOGLE_PAY'];
    final icons  = [Icons.credit_card, Icons.account_balance_wallet, Icons.apple, Icons.g_mobiledata];
    final labels = ['Carte bancaire', 'PayPal', 'Apple Pay', 'Google Pay'];
    return Wrap(
      spacing: 10,
      runSpacing: 10,
      children: List.generate(codes.length, (i) {
        final code  = codes[i];
        final icon  = icons[i];
        final label = labels[i];
        final sel   = _modePaiement == code;
        return GestureDetector(
          onTap: () => setState(() => _modePaiement = code),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 150),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            decoration: BoxDecoration(
              color: sel ? AppConstants.accentCyan.withValues(alpha: 0.12) : AppConstants.bg2,
              border: Border.all(
                  color: sel ? AppConstants.accentCyan : AppConstants.borderColor,
                  width: sel ? 2 : 1),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(icon,
                    size: 20,
                    color: sel ? AppConstants.accentCyan : AppConstants.textGrey),
                const SizedBox(width: 6),
                Text(label,
                    style: TextStyle(
                      fontWeight: sel ? FontWeight.bold : FontWeight.normal,
                      color: sel ? AppConstants.accentCyan : AppConstants.textLight,
                      fontSize: 13,
                    )),
              ],
            ),
          ),
        );
      }),
    );
  }

  // ── Bons d'achat ──────────────────────────────────────────────────────

  Widget _bonsSelector() {
    return Column(
      children: _bons.map((b) {
        final id     = (b['id'] as num).toInt();
        final valeur = (b['valeur'] as num?)?.toDouble() ?? 0.0;
        final code   = b['codeBon'] ?? '#$id';
        final sel    = _idsBons.contains(id);
        return CheckboxListTile(
          value: sel,
          onChanged: (v) => setState(() {
            if (v == true) _idsBons.add(id);
            else _idsBons.remove(id);
          }),
          activeColor: AppConstants.accentCyan,
          title: Text('${valeur.toStringAsFixed(2)} €  —  $code',
              style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
          subtitle: b['dateExpiration'] != null
              ? Text('Expire le ${(b['dateExpiration'] as String).substring(0, 10)}',
                  style: const TextStyle(fontSize: 11, color: AppConstants.textGrey))
              : null,
          controlAffinity: ListTileControlAffinity.leading,
        );
      }).toList(),
    );
  }

  // ── Récap + bouton ────────────────────────────────────────────────────

  Widget _recap(double total) {
    final remiseBons = _bons
        .where((b) => _idsBons.contains((b['id'] as num).toInt()))
        .fold(0.0, (s, b) => s + ((b['valeur'] as num?)?.toDouble() ?? 0.0));
    final aRegler = (total - remiseBons).clamp(0.0, double.infinity);

    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppConstants.bg2,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: AppConstants.borderColor),
          ),
          child: Column(
            children: [
              _recapRow('Sous-total', '${total.toStringAsFixed(2)} €'),
              if (remiseBons > 0)
                _recapRow('Bons d\'achat', '−${remiseBons.toStringAsFixed(2)} €',
                    color: Colors.green),
              const Divider(height: 20),
              _recapRow('Total à régler', '${aRegler.toStringAsFixed(2)} €',
                  bold: true, color: AppConstants.accentCyan),
            ],
          ),
        ),
        const SizedBox(height: 16),
        SizedBox(
          width: double.infinity,
          child: ElevatedButton.icon(
            onPressed: _submitting ? null : _valider,
            icon: _submitting
                ? const SizedBox(
                    width: 18, height: 18,
                    child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                : const Icon(Icons.check_circle_outline),
            label: const Text('Confirmer la commande'),
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 14),
            ),
          ),
        ),
      ],
    );
  }

  Widget _recapRow(String label, String value,
      {bool bold = false, Color? color}) {
    final style = TextStyle(
      fontWeight: bold ? FontWeight.bold : FontWeight.normal,
      fontSize: bold ? 16 : 14,
      color: color ?? AppConstants.textLight,
    );
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [Text(label, style: style), Text(value, style: style)],
      ),
    );
  }

  Widget _section(String title, IconData icon) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Row(children: [
        Icon(icon, size: 18, color: AppConstants.accentCyan),
        const SizedBox(width: 8),
        Text(title,
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
      ]),
    );
  }
}

// ── Écran de confirmation ──────────────────────────────────────────────────

class _ConfirmationScreen extends StatelessWidget {
  final Map<String, dynamic> facture;
  const _ConfirmationScreen({required this.facture});

  @override
  Widget build(BuildContext context) {
    final ref        = facture['reference'] ?? facture['referenceCommande'] ?? '';
    final total      = (facture['montantTotal'] ?? facture['total'] ?? 0.0) as num;
    final pointsGagnes = (facture['pointsGagnes'] as num?)?.toInt();

    // Lire les points mis à jour depuis le provider (refreshClient() lancé avant navigation)
    final soldePoints = context.watch<AuthProvider>().client?.soldePoints;

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                width: 90, height: 90,
                decoration: BoxDecoration(
                  color: Colors.green.withValues(alpha: 0.15),
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.green, width: 2),
                ),
                child: const Icon(Icons.check, color: Colors.green, size: 50),
              ),
              const SizedBox(height: 24),
              const Text('Commande confirmée !',
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center),
              const SizedBox(height: 12),
              if (ref.isNotEmpty)
                Text('Réf. $ref',
                    style: const TextStyle(color: AppConstants.textGrey, fontSize: 14)),
              const SizedBox(height: 8),
              Text('Total : ${total.toStringAsFixed(2)} €',
                  style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: AppConstants.accentCyan)),

              // Points fidélité
              const SizedBox(height: 20),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                decoration: BoxDecoration(
                  color: AppConstants.accentCyan.withValues(alpha: 0.08),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: AppConstants.accentCyan.withValues(alpha: 0.3)),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.stars, color: AppConstants.accentCyan, size: 20),
                    const SizedBox(width: 8),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (pointsGagnes != null && pointsGagnes > 0)
                          Text('+$pointsGagnes points gagnés',
                              style: const TextStyle(
                                  color: AppConstants.accentCyan,
                                  fontWeight: FontWeight.bold,
                                  fontSize: 14)),
                        if (soldePoints != null)
                          Text('Solde : $soldePoints pts',
                              style: const TextStyle(
                                  color: AppConstants.textGrey, fontSize: 12)),
                      ],
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 28),
              const Text(
                'Vous recevrez un email de confirmation.\nMerci pour votre commande !',
                style: TextStyle(color: AppConstants.textGrey, height: 1.6),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 40),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => Navigator.of(context).popUntil((r) => r.isFirst),
                  child: const Text('Retour à l\'accueil'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
