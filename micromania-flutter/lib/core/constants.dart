import 'package:flutter/material.dart';

class AppConstants {
  // ── API ───────────────────────────────────────────────────────────
  static const String apiBaseUrl = 'http://goodspeed57.ddns.net:8080/api/v1';
  static const String canalVente = 'WEB';

  // ── Palette dark gaming (identique au thème web) ──────────────────
  static const Color bg          = Color(0xFF080C10);   // fond principal
  static const Color bg2         = Color(0xFF0D1117);   // fond secondaire (AppBar, NavBar)
  static const Color bg3         = Color(0xFF111820);   // fond champs / surfaces
  static const Color cardColor   = Color(0xFF121A24);   // fond carte
  static const Color borderColor = Color(0xFF1E2D3D);   // bordure

  static const Color accentCyan   = Color(0xFF00D4FF);  // primary — cyan
  static const Color accentRed    = Color(0xFFFF3366);  // secondary — rouge/rose
  static const Color accentPurple = Color(0xFF7C3AED);  // tertiary — violet

  static const Color textLight   = Color(0xFFE2E8F0);   // texte principal
  static const Color textGrey    = Color(0xFF64748B);   // texte atténué
  static const Color success     = Color(0xFF00FF88);   // succès
  static const Color warn        = Color(0xFFFFBB00);   // avertissement
  static const Color starGold    = Color(0xFFFFBB00);   // étoiles

  // ── Aliases rétro-compatibilité (les écrans existants n'ont pas besoin d'être modifiés)
  static const Color primaryBlue    = accentCyan;
  static const Color backgroundGrey = bg;
  static const Color cardWhite      = cardColor;
  static const Color textDark       = textLight;

  // ── Clé secure storage ────────────────────────────────────────────
  static const String tokenKey = 'jwt_token';
}
