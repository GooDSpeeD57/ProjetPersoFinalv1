import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../providers/auth_provider.dart';
import '../core/constants.dart';
import '../core/theme.dart';
import 'home_screen.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _check();
  }

  Future<void> _check() async {
    // Vérification silencieuse du token — on va TOUJOURS sur HomeScreen.
    // Les écrans qui nécessitent une auth (Panier, Compte) gèrent eux-mêmes l'état non connecté.
    try {
      await context.read<AuthProvider>().checkAuth();
    } catch (e) {
      debugPrint('checkAuth error: $e');
    }
    if (!mounted) return;
    Navigator.of(context).pushReplacement(
      MaterialPageRoute(builder: (_) => const HomeScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppConstants.bg,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Icône gaming avec glow
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                color: AppConstants.bg2,
                shape: BoxShape.circle,
                border: Border.all(color: AppConstants.accentCyan, width: 2),
                boxShadow: cyanGlow(blur: 28),
              ),
              child: const Icon(
                Icons.videogame_asset,
                size: 40,
                color: AppConstants.accentCyan,
              ),
            ),

            const SizedBox(height: 28),

            Text(
              'MICROMANIA',
              style: GoogleFonts.orbitron(
                color: AppConstants.accentCyan,
                fontSize: 26,
                fontWeight: FontWeight.w900,
                letterSpacing: 4,
                shadows: [
                  Shadow(
                    color: AppConstants.accentCyan.withValues(alpha: 0.6),
                    blurRadius: 18,
                  ),
                ],
              ),
            ),

            const SizedBox(height: 6),

            Text(
              'ZING',
              style: GoogleFonts.rajdhani(
                color: AppConstants.textGrey,
                fontSize: 13,
                letterSpacing: 8,
              ),
            ),

            const SizedBox(height: 48),

            const SizedBox(
              width: 28,
              height: 28,
              child: CircularProgressIndicator(
                color: AppConstants.accentCyan,
                strokeWidth: 2,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
