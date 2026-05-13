import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import '../../core/constants.dart';
import '../../core/theme.dart';
import '../../providers/auth_provider.dart';
import '../../providers/panier_provider.dart';
import '../home_screen.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();

  final _pseudoCtrl = TextEditingController();
  final _prenomCtrl = TextEditingController();
  final _nomCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _telCtrl = TextEditingController();
  final _mdpCtrl = TextEditingController();
  final _confirmCtrl = TextEditingController();

  DateTime? _dateNaissance;
  bool _rgpd = false;
  bool _loading = false;
  bool _obscureMdp = true;
  bool _obscureConfirm = true;

  @override
  void dispose() {
    _pseudoCtrl.dispose();
    _prenomCtrl.dispose();
    _nomCtrl.dispose();
    _emailCtrl.dispose();
    _telCtrl.dispose();
    _mdpCtrl.dispose();
    _confirmCtrl.dispose();
    super.dispose();
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _dateNaissance ?? DateTime(now.year - 18, now.month, now.day),
      firstDate: DateTime(1920),
      lastDate: DateTime(now.year - 13, now.month, now.day),
      builder: (context, child) => Theme(
        data: Theme.of(context).copyWith(
          colorScheme: ColorScheme.dark(
            primary: AppConstants.accentCyan,
            surface: AppConstants.bg2,
          ),
        ),
        child: child!,
      ),
    );
    if (picked != null) setState(() => _dateNaissance = picked);
  }

  Future<void> _register() async {
    if (!_formKey.currentState!.validate()) return;
    if (!_rgpd) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Vous devez accepter la politique de confidentialité'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    setState(() => _loading = true);

    final auth = context.read<AuthProvider>();
    final panier = context.read<PanierProvider>();
    final nav = Navigator.of(context);
    final messenger = ScaffoldMessenger.of(context);

    final dateStr = _dateNaissance != null
        ? '${_dateNaissance!.year.toString().padLeft(4, '0')}'
          '-${_dateNaissance!.month.toString().padLeft(2, '0')}'
          '-${_dateNaissance!.day.toString().padLeft(2, '0')}'
        : null;

    final ok = await auth.register(
      pseudo: _pseudoCtrl.text.trim(),
      nom: _nomCtrl.text.trim(),
      prenom: _prenomCtrl.text.trim(),
      email: _emailCtrl.text.trim(),
      motDePasse: _mdpCtrl.text,
      rgpdConsent: _rgpd,
      telephone: _telCtrl.text.trim().isEmpty ? null : _telCtrl.text.trim(),
      dateNaissance: dateStr,
    );

    if (!mounted) return;
    setState(() => _loading = false);

    if (ok) {
      await panier.charger();
      nav.pushAndRemoveUntil(
        MaterialPageRoute(builder: (_) => const HomeScreen()),
        (route) => false,
      );
    } else {
      messenger.showSnackBar(SnackBar(
        content: Text(auth.error ?? 'Erreur lors de la création du compte'),
        backgroundColor: AppConstants.accentRed,
        duration: const Duration(seconds: 6),
      ));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppConstants.bg,
      appBar: AppBar(
        backgroundColor: AppConstants.bg,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AppConstants.accentCyan),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'CRÉER UN COMPTE',
          style: GoogleFonts.orbitron(
            color: AppConstants.accentCyan,
            fontSize: 14,
            fontWeight: FontWeight.bold,
            letterSpacing: 2,
          ),
        ),
        centerTitle: true,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // ── Section identité ────────────────────────────────
                _sectionHeader('IDENTITÉ', Icons.person_outline),
                const SizedBox(height: 12),

                _field(
                  controller: _pseudoCtrl,
                  label: 'Pseudo *',
                  icon: Icons.alternate_email,
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'Pseudo requis';
                    if (v.trim().length < 3) return 'Minimum 3 caractères';
                    return null;
                  },
                ),
                const SizedBox(height: 12),

                Row(
                  children: [
                    Expanded(
                      child: _field(
                        controller: _prenomCtrl,
                        label: 'Prénom *',
                        icon: Icons.badge_outlined,
                        validator: (v) =>
                            (v == null || v.trim().isEmpty) ? 'Requis' : null,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _field(
                        controller: _nomCtrl,
                        label: 'Nom *',
                        icon: Icons.badge_outlined,
                        validator: (v) =>
                            (v == null || v.trim().isEmpty) ? 'Requis' : null,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),

                // Date de naissance
                GestureDetector(
                  onTap: _pickDate,
                  child: AbsorbPointer(
                    child: TextFormField(
                      style: const TextStyle(color: AppConstants.textLight),
                      decoration: InputDecoration(
                        labelText: 'Date de naissance',
                        prefixIcon: const Icon(Icons.cake_outlined),
                        suffixIcon: const Icon(Icons.calendar_today,
                            color: AppConstants.accentCyan, size: 18),
                        hintText: _dateNaissance != null
                            ? '${_dateNaissance!.day.toString().padLeft(2, '0')}/'
                              '${_dateNaissance!.month.toString().padLeft(2, '0')}/'
                              '${_dateNaissance!.year}'
                            : 'JJ/MM/AAAA',
                        hintStyle: TextStyle(
                          color: _dateNaissance != null
                              ? AppConstants.textLight
                              : AppConstants.textGrey,
                        ),
                      ),
                      controller: TextEditingController(
                        text: _dateNaissance != null
                            ? '${_dateNaissance!.day.toString().padLeft(2, '0')}/'
                              '${_dateNaissance!.month.toString().padLeft(2, '0')}/'
                              '${_dateNaissance!.year}'
                            : '',
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                // ── Section contact ─────────────────────────────────
                _sectionHeader('CONTACT', Icons.contact_mail_outlined),
                const SizedBox(height: 12),

                _field(
                  controller: _emailCtrl,
                  label: 'Email *',
                  icon: Icons.email_outlined,
                  keyboardType: TextInputType.emailAddress,
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'Email requis';
                    if (!RegExp(r'^[^@]+@[^@]+\.[^@]+').hasMatch(v.trim())) {
                      return 'Email invalide';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 12),

                _field(
                  controller: _telCtrl,
                  label: 'Téléphone',
                  icon: Icons.phone_outlined,
                  keyboardType: TextInputType.phone,
                ),

                const SizedBox(height: 24),

                // ── Section sécurité ────────────────────────────────
                _sectionHeader('SÉCURITÉ', Icons.lock_outline),
                const SizedBox(height: 12),

                TextFormField(
                  controller: _mdpCtrl,
                  obscureText: _obscureMdp,
                  style: const TextStyle(color: AppConstants.textLight),
                  decoration: InputDecoration(
                    labelText: 'Mot de passe *',
                    prefixIcon: const Icon(Icons.lock_outline),
                    suffixIcon: IconButton(
                      icon: Icon(_obscureMdp
                          ? Icons.visibility_outlined
                          : Icons.visibility_off_outlined),
                      color: AppConstants.textGrey,
                      onPressed: () =>
                          setState(() => _obscureMdp = !_obscureMdp),
                    ),
                  ),
                  validator: (v) {
                    if (v == null || v.isEmpty) return 'Mot de passe requis';
                    if (v.length < 8) return 'Minimum 8 caractères';
                    return null;
                  },
                ),
                const SizedBox(height: 12),

                TextFormField(
                  controller: _confirmCtrl,
                  obscureText: _obscureConfirm,
                  style: const TextStyle(color: AppConstants.textLight),
                  decoration: InputDecoration(
                    labelText: 'Confirmer le mot de passe *',
                    prefixIcon: const Icon(Icons.lock_outline),
                    suffixIcon: IconButton(
                      icon: Icon(_obscureConfirm
                          ? Icons.visibility_outlined
                          : Icons.visibility_off_outlined),
                      color: AppConstants.textGrey,
                      onPressed: () =>
                          setState(() => _obscureConfirm = !_obscureConfirm),
                    ),
                  ),
                  validator: (v) {
                    if (v == null || v.isEmpty) return 'Confirmation requise';
                    if (v != _mdpCtrl.text) return 'Les mots de passe ne correspondent pas';
                    return null;
                  },
                ),

                const SizedBox(height: 24),

                // ── RGPD ────────────────────────────────────────────
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: AppConstants.bg2,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: _rgpd
                          ? AppConstants.accentCyan
                          : AppConstants.borderColor,
                    ),
                  ),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Checkbox(
                        value: _rgpd,
                        onChanged: (v) => setState(() => _rgpd = v ?? false),
                        activeColor: AppConstants.accentCyan,
                        side: BorderSide(color: AppConstants.textGrey),
                      ),
                      Expanded(
                        child: Padding(
                          padding: const EdgeInsets.only(top: 12),
                          child: Text(
                            'J\'accepte la politique de confidentialité et '
                            'le traitement de mes données personnelles par Micromania-Zing. *',
                            style: TextStyle(
                              color: AppConstants.textGrey,
                              fontSize: 12,
                              height: 1.5,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),

                const SizedBox(height: 28),

                // ── Bouton s'inscrire ────────────────────────────────
                Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(8),
                    boxShadow: _loading ? [] : cyanGlow(blur: 14),
                  ),
                  child: ElevatedButton(
                    onPressed: _loading ? null : _register,
                    child: _loading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(
                              color: AppConstants.bg,
                              strokeWidth: 2,
                            ),
                          )
                        : const Text('CRÉER MON COMPTE'),
                  ),
                ),

                const SizedBox(height: 16),

                // Lien retour connexion
                Center(
                  child: GestureDetector(
                    onTap: () => Navigator.pop(context),
                    child: Text.rich(
                      TextSpan(
                        text: 'Déjà un compte ? ',
                        style: TextStyle(
                          color: AppConstants.textGrey,
                          fontSize: 14,
                        ),
                        children: [
                          TextSpan(
                            text: 'Se connecter',
                            style: TextStyle(
                              color: AppConstants.accentCyan,
                              fontWeight: FontWeight.bold,
                              decoration: TextDecoration.underline,
                              decorationColor: AppConstants.accentCyan,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 32),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _sectionHeader(String label, IconData icon) {
    return Row(
      children: [
        Icon(icon, color: AppConstants.accentCyan, size: 18),
        const SizedBox(width: 8),
        Text(
          label,
          style: GoogleFonts.orbitron(
            color: AppConstants.accentCyan,
            fontSize: 11,
            fontWeight: FontWeight.bold,
            letterSpacing: 2,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Divider(color: AppConstants.accentCyan.withValues(alpha: 0.3)),
        ),
      ],
    );
  }

  Widget _field({
    required TextEditingController controller,
    required String label,
    required IconData icon,
    TextInputType? keyboardType,
    String? Function(String?)? validator,
  }) {
    return TextFormField(
      controller: controller,
      keyboardType: keyboardType,
      style: const TextStyle(color: AppConstants.textLight),
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon),
      ),
      validator: validator,
    );
  }
}
