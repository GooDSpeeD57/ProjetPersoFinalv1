import 'package:flutter/foundation.dart';
import '../models/panier_models.dart';
import '../services/panier_service.dart';

class PanierProvider extends ChangeNotifier {
  final _service = PanierService();

  PanierResponse? _panier;
  bool _loading = false;
  String? _error;

  PanierResponse? get panier => _panier;
  bool get loading => _loading;
  String? get error => _error;
  int get nbArticles => _panier?.nbArticles ?? 0;
  double get total => _panier?.total ?? 0.0;

  Future<void> charger() async {
    _loading = true;
    _error = null;
    notifyListeners();
    try {
      _panier = await _service.getPanier();
    } catch (e) {
      _error = e.toString();
    }
    _loading = false;
    notifyListeners();
  }

  Future<bool> addLigne(int idVariant, int quantite) async {
    try {
      _panier = await _service.addLigne(idVariant: idVariant, quantite: quantite);
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<void> removeLigne(int idLigne) async {
    try {
      _panier = await _service.removeLigne(idLigne);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> updateQuantite(int idLigne, int quantite) async {
    if (quantite <= 0) {
      await removeLigne(idLigne);
      return;
    }
    try {
      _panier = await _service.updateLigne(idLigne, quantite);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  void reset() {
    _panier = null;
    notifyListeners();
  }
}
