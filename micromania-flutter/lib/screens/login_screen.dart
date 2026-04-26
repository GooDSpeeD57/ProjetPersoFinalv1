import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../core/constants.dart';
import '../core/theme.dart';
import '../providers/auth_provider.dart';
import '../providers/panier_provider.dart';
import 'home_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailCtrl = TextEditingController();
  final _mdpCtrl = TextEditingController();
  bool _loading = false;
  bool _obscure = true;

  @override
  void dispose() {
    _emailCtrl.dispose();
    _mdpCtrl.dispose();
    super.dispose();
  }

  Future<void> _login() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _loading = true);
    // Capture providers before any await to avoid BuildContext async-gap warnings
    final auth   = context.read<AuthProvider>();
    final panier = context.read<PanierProvider>();
    final nav    = Navigator.of(context);
    final messenger = ScaffoldMessenger.of(context);

    final ok = await auth.login(_emailCtrl.text.trim(), _mdpCtrl.text);
    if (!mounted) return;
    setState(() => _loading = false);
    if (ok) {
      await panier.charger();
      nav.pushReplacement(MaterialPageRoute(builder: (_) => const HomeScreen()));
    } else {
      messenger.showSnackBar(SnackBar(
        content: Text(auth.error ?? 'Erreur de connexion'),
        backgroundColor: AppConstants.accentRed,
        duration: const Duration(seconds: 6),
      ));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppConstants.bg,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 28),
          child: ConstrainedBox(
            constraints: BoxConstraints(
              minHeight: MediaQuery.of(context).size.height -
                  MediaQuery.of(context).padding.top -
                  MediaQuery.of(context).padding.bottom,
            ),
            child: IntrinsicHeight(
              child: Column(
                children: [
                  const SizedBox(height: 60),

                  // ── Logo / Header ────────────────────────────────
                  _buildHeader(),

                  const Spacer(),

                  // ── Formulaire ───────────────────────────────────
                  _buildForm(),

                  const SizedBox(height: 48),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Column(
      children: [
        // Icône gaming avec glow
        Container(
          width: 90,
          height: 90,
          decoration: BoxDecoration(
            color: AppConstants.bg2,
            shape: BoxShape.circle,
            border: Border.all(color: AppConstants.accentCyan, width: 2),
            boxShadow: cyanGlow(blur: 24),
          ),
          child: const Icon(
            Icons.videogame_asset,
            size: 44,
            color: AppConstants.accentCyan,
          ),
        ),

        const SizedBox(height: 24),

        // MICROMANIA en Orbitron avec glow
        Text(
          'MICROMANIA',
          style: GoogleFonts.orbitron(
            color: AppConstants.accentCyan,
            fontSize: 28,
            fontWeight: FontWeight.w900,
            letterSpacing: 4,
            shadows: [
              Shadow(
                color: AppConstants.accentCyan.withValues(alpha: 0.6),
                blurRadius: 16,
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
            fontWeight: FontWeight.w500,
          ),
        ),

        const SizedBox(height: 8),

        // Ligne décorative cyan
        Container(
          width: 80,
          height: 2,
          decoration: BoxDecoration(
            gradient: const LinearGradient(
              colors: [Colors.transparent, AppConstants.accentCyan, Colors.transparent],
            ),
            boxShadow: cyanGlow(blur: 8),
          ),
        ),

        const SizedBox(height: 40),
      ],
    );
  }

  Widget _buildForm() {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: AppConstants.cardColor,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppConstants.borderColor),
        boxShadow: [
          BoxShadow(
            color: AppConstants.accentCyan.withValues(alpha: 0.06),
            blurRadius: 30,
            spreadRadius: 0,
          ),
        ],
      ),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              'Connexion',
              style: GoogleFonts.orbitron(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppConstants.textLight,
                letterSpacing: 1.5,
              ),
            ),

            const SizedBox(height: 8),
            const Divider(),
            const SizedBox(height: 20),

            // Email
            TextFormField(
              controller: _emailCtrl,
              keyboardType: TextInputType.emailAddress,
              style: const TextStyle(color: AppConstants.textLight),
              decoration: const InputDecoration(
                labelText: 'Email',
                prefixIcon: Icon(Icons.email_outlined),
              ),
              validator: (v) =>
                  v == null || v.isEmpty ? 'Email requis' : null,
            ),

            const SizedBox(height: 16),

            // Mot de passe
            TextFormField(
              controller: _mdpCtrl,
              obscureText: _obscure,
              style: const TextStyle(color: AppConstants.textLight),
              decoration: InputDecoration(
                labelText: 'Mot de passe',
                prefixIcon: const Icon(Icons.lock_outline),
                suffixIcon: IconButton(
                  icon: Icon(_obscure
                      ? Icons.visibility_outlined
                      : Icons.visibility_off_outlined),
                  color: AppConstants.textGrey,
                  onPressed: () => setState(() => _obscure = !_obscure),
                ),
              ),
              validator: (v) =>
                  v == null || v.isEmpty ? 'Mot de passe requis' : null,
            ),

            const SizedBox(height: 28),

            // Bouton Se connecter avec glow au hover / press
            Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                boxShadow: _loading ? [] : cyanGlow(blur: 14),
              ),
              child: ElevatedButton(
                onPressed: _loading ? null : _login,
                child: _loading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          color: AppConstants.bg,
                          strokeWidth: 2,
                        ),
                      )
                    : const Text('SE CONNECTER'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
