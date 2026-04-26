import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:url_launcher/url_launcher.dart';
import 'dart:math';
import '../../core/constants.dart';
import '../../models/magasin_models.dart';
import '../../services/magasin_service.dart';

class MagasinsScreen extends StatefulWidget {
  const MagasinsScreen({super.key});

  @override
  State<MagasinsScreen> createState() => _MagasinsScreenState();
}

class _MagasinsScreenState extends State<MagasinsScreen> {
  final _service = MagasinService();
  List<Magasin> _magasins = [];
  bool _loading = true;
  Position? _position;
  String? _erreurGeo;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    setState(() => _loading = true);
    await _demanderGeoloc();
    try {
      final list = await _service.getMagasins();
      // Calculer distances si position connue
      if (_position != null) {
        for (final m in list) {
          if (m.latitude != null && m.longitude != null) {
            m.distanceKm = _distanceKm(
              _position!.latitude, _position!.longitude,
              m.latitude!, m.longitude!,
            );
          }
        }
        list.sort((a, b) => (a.distanceKm ?? 99999).compareTo(b.distanceKm ?? 99999));
      }
      setState(() {
        _magasins = list;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  Future<void> _demanderGeoloc() async {
    try {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        setState(() => _erreurGeo = 'GPS désactivé');
        return;
      }
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          setState(() => _erreurGeo = 'Permission refusée');
          return;
        }
      }
      if (permission == LocationPermission.deniedForever) {
        setState(() => _erreurGeo = 'Permission refusée définitivement');
        return;
      }
      _position = await Geolocator.getCurrentPosition(
        locationSettings: LocationSettings(accuracy: LocationAccuracy.medium),
      );
    } catch (e) {
      setState(() => _erreurGeo = 'Géolocalisation indisponible');
    }
  }

  double _distanceKm(double lat1, double lon1, double lat2, double lon2) {
    const r = 6371.0;
    final dLat = _rad(lat2 - lat1);
    final dLon = _rad(lon2 - lon1);
    final a = sin(dLat / 2) * sin(dLat / 2) +
        cos(_rad(lat1)) * cos(_rad(lat2)) * sin(dLon / 2) * sin(dLon / 2);
    return r * 2 * atan2(sqrt(a), sqrt(1 - a));
  }

  double _rad(double deg) => deg * pi / 180;

  Future<void> _ouvrir(Magasin m) async {
    final query = m.latitude != null && m.longitude != null
        ? '${m.latitude},${m.longitude}'
        : Uri.encodeComponent('${m.nom} ${m.adresseComplete}');

    final uri = Uri.parse(
      m.latitude != null
          ? 'https://www.google.com/maps/search/?api=1&query=$query'
          : 'https://www.google.com/maps/search/?api=1&query=${Uri.encodeComponent(m.adresseComplete)}',
    );
    if (await canLaunchUrl(uri)) await launchUrl(uri, mode: LaunchMode.externalApplication);
  }

  Future<void> _appeler(String tel) async {
    final uri = Uri.parse('tel:$tel');
    if (await canLaunchUrl(uri)) await launchUrl(uri);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Magasins'),
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _charger),
        ],
      ),
      body: Column(
        children: [
          // Bandeau géoloc
          if (_erreurGeo != null)
            Container(
              color: Colors.orange[50],
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Row(
                children: [
                  const Icon(Icons.location_off, color: Colors.orange, size: 16),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      '$_erreurGeo — distances non calculées',
                      style: const TextStyle(fontSize: 12, color: Colors.orange),
                    ),
                  ),
                ],
              ),
            )
          else if (_position != null)
            Container(
              color: Colors.green[50],
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Row(
                children: [
                  const Icon(Icons.location_on, color: Colors.green, size: 16),
                  const SizedBox(width: 8),
                  const Text('Position trouvée — magasins triés par proximité',
                      style: TextStyle(fontSize: 12, color: Colors.green)),
                ],
              ),
            ),

          Expanded(
            child: _loading
                ? const Center(child: CircularProgressIndicator())
                : _magasins.isEmpty
                    ? const Center(child: Text('Aucun magasin trouvé'))
                    : RefreshIndicator(
                        onRefresh: _charger,
                        child: ListView.builder(
                          padding: const EdgeInsets.all(12),
                          itemCount: _magasins.length,
                          itemBuilder: (_, i) => _MagasinCard(
                            magasin: _magasins[i],
                            onOuvrir: () => _ouvrir(_magasins[i]),
                            onAppeler: _magasins[i].telephone != null
                                ? () => _appeler(_magasins[i].telephone!)
                                : null,
                          ),
                        ),
                      ),
          ),
        ],
      ),
    );
  }
}

class _MagasinCard extends StatelessWidget {
  final Magasin magasin;
  final VoidCallback onOuvrir;
  final VoidCallback? onAppeler;

  const _MagasinCard({
    required this.magasin,
    required this.onOuvrir,
    this.onAppeler,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.store, color: AppConstants.primaryBlue, size: 20),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    magasin.nom,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                ),
                if (magasin.distanceKm != null)
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: AppConstants.primaryBlue.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      magasin.distanceKm! < 1
                          ? '${(magasin.distanceKm! * 1000).round()} m'
                          : '${magasin.distanceKm!.toStringAsFixed(1)} km',
                      style: const TextStyle(
                        color: AppConstants.primaryBlue,
                        fontWeight: FontWeight.bold,
                        fontSize: 12,
                      ),
                    ),
                  ),
              ],
            ),
            if (magasin.adresse != null || magasin.ville != null) ...[
              const SizedBox(height: 6),
              Row(
                children: [
                  const Icon(Icons.location_on_outlined, size: 14, color: AppConstants.textGrey),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      magasin.adresseComplete,
                      style: const TextStyle(color: AppConstants.textGrey, fontSize: 13),
                    ),
                  ),
                ],
              ),
            ],
            if (magasin.telephone != null) ...[
              const SizedBox(height: 4),
              Row(
                children: [
                  const Icon(Icons.phone_outlined, size: 14, color: AppConstants.textGrey),
                  const SizedBox(width: 4),
                  Text(magasin.telephone!,
                      style: const TextStyle(color: AppConstants.textGrey, fontSize: 13)),
                ],
              ),
            ],
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: onOuvrir,
                    icon: const Icon(Icons.map_outlined, size: 16),
                    label: const Text('Itinéraire', style: TextStyle(fontSize: 13)),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: AppConstants.primaryBlue,
                      padding: const EdgeInsets.symmetric(vertical: 8),
                    ),
                  ),
                ),
                if (onAppeler != null) ...[
                  const SizedBox(width: 8),
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: onAppeler,
                      icon: const Icon(Icons.phone, size: 16),
                      label: const Text('Appeler', style: TextStyle(fontSize: 13)),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 8),
                      ),
                    ),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }
}
