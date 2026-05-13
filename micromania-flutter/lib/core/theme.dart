import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'constants.dart';

// ── Glow helpers ─────────────────────────────────────────────────────
List<BoxShadow> cyanGlow({double blur = 18, double spread = 0}) => [
  BoxShadow(
    color: AppConstants.accentCyan.withValues(alpha: 0.35),
    blurRadius: blur,
    spreadRadius: spread,
  ),
];

List<BoxShadow> redGlow({double blur = 18, double spread = 0}) => [
  BoxShadow(
    color: AppConstants.accentRed.withValues(alpha: 0.35),
    blurRadius: blur,
    spreadRadius: spread,
  ),
];

// ── Gradient de fond gaming ───────────────────────────────────────────
const gamingGradient = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [AppConstants.bg, AppConstants.bg2, AppConstants.bg3],
);

// ── Thème principal ───────────────────────────────────────────────────
final ThemeData appTheme = ThemeData(
  useMaterial3: true,
  brightness: Brightness.dark,

  colorScheme: const ColorScheme.dark(
    primary: AppConstants.accentCyan,
    onPrimary: AppConstants.bg,
    secondary: AppConstants.accentRed,
    onSecondary: Colors.white,
    tertiary: AppConstants.accentPurple,
    surface: AppConstants.cardColor,
    onSurface: AppConstants.textLight,
    outline: AppConstants.borderColor,
    surfaceContainerHighest: AppConstants.bg3,
    error: AppConstants.accentRed,
  ),

  scaffoldBackgroundColor: AppConstants.bg,

  // ── Typographie : Rajdhani (corps) + Orbitron (titres) ───────────
  textTheme: GoogleFonts.rajdhaniTextTheme(
    const TextTheme(
      displayLarge:   TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w700),
      displayMedium:  TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w700),
      displaySmall:   TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w600),
      headlineLarge:  TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w700),
      headlineMedium: TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w600),
      headlineSmall:  TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w600),
      titleLarge:     TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w600),
      titleMedium:    TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w500),
      titleSmall:     TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w500),
      bodyLarge:      TextStyle(color: AppConstants.textLight),
      bodyMedium:     TextStyle(color: AppConstants.textLight),
      bodySmall:      TextStyle(color: AppConstants.textGrey),
      labelLarge:     TextStyle(color: AppConstants.textLight, fontWeight: FontWeight.w600),
      labelMedium:    TextStyle(color: AppConstants.textLight),
      labelSmall:     TextStyle(color: AppConstants.textGrey),
    ),
  ),

  // ── AppBar ───────────────────────────────────────────────────────
  appBarTheme: AppBarTheme(
    backgroundColor: AppConstants.bg2,
    foregroundColor: AppConstants.textLight,
    elevation: 0,
    centerTitle: true,
    surfaceTintColor: Colors.transparent,
    shadowColor: Colors.transparent,
    titleTextStyle: GoogleFonts.orbitron(
      color: AppConstants.accentCyan,
      fontSize: 17,
      fontWeight: FontWeight.bold,
      letterSpacing: 2,
    ),
    iconTheme: const IconThemeData(color: AppConstants.accentCyan),
    actionsIconTheme: const IconThemeData(color: AppConstants.accentCyan),
    shape: const Border(
      bottom: BorderSide(color: AppConstants.borderColor),
    ),
  ),

  // ── Bottom Nav ───────────────────────────────────────────────────
  bottomNavigationBarTheme: const BottomNavigationBarThemeData(
    backgroundColor: AppConstants.bg2,
    selectedItemColor: AppConstants.accentCyan,
    unselectedItemColor: AppConstants.textGrey,
    selectedLabelStyle: TextStyle(fontWeight: FontWeight.bold, fontSize: 11),
    unselectedLabelStyle: TextStyle(fontSize: 11),
    type: BottomNavigationBarType.fixed,
    elevation: 16,
  ),

  // ── Bouton principal ─────────────────────────────────────────────
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: AppConstants.accentCyan,
      foregroundColor: AppConstants.bg,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
      textStyle: GoogleFonts.rajdhani(
        fontWeight: FontWeight.bold,
        fontSize: 16,
        letterSpacing: 1.5,
      ),
      elevation: 0,
    ),
  ),

  // ── OutlinedButton ───────────────────────────────────────────────
  outlinedButtonTheme: OutlinedButtonThemeData(
    style: OutlinedButton.styleFrom(
      foregroundColor: AppConstants.accentCyan,
      side: const BorderSide(color: AppConstants.accentCyan),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
    ),
  ),

  // ── TextButton ───────────────────────────────────────────────────
  textButtonTheme: TextButtonThemeData(
    style: TextButton.styleFrom(foregroundColor: AppConstants.accentCyan),
  ),

  // ── Cartes ───────────────────────────────────────────────────────
  cardTheme: CardThemeData(
    elevation: 0,
    color: AppConstants.cardColor,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(12),
      side: const BorderSide(color: AppConstants.borderColor),
    ),
    margin: const EdgeInsets.symmetric(horizontal: 0, vertical: 4),
  ),

  // ── Champs de saisie ────────────────────────────────────────────
  inputDecorationTheme: InputDecorationTheme(
    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: const BorderSide(color: AppConstants.borderColor),
    ),
    enabledBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: const BorderSide(color: AppConstants.borderColor),
    ),
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: const BorderSide(color: AppConstants.accentCyan, width: 2),
    ),
    errorBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: const BorderSide(color: AppConstants.accentRed),
    ),
    focusedErrorBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: const BorderSide(color: AppConstants.accentRed, width: 2),
    ),
    filled: true,
    fillColor: AppConstants.bg3,
    labelStyle: const TextStyle(color: AppConstants.textGrey),
    hintStyle: const TextStyle(color: AppConstants.textGrey),
    prefixIconColor: AppConstants.textGrey,
    suffixIconColor: AppConstants.textGrey,
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
  ),

  // ── Chips ────────────────────────────────────────────────────────
  chipTheme: ChipThemeData(
    backgroundColor: AppConstants.bg3,
    selectedColor: AppConstants.accentCyan.withValues(alpha: 0.15),
    disabledColor: AppConstants.bg3,
    padding: const EdgeInsets.symmetric(horizontal: 4),
    side: const BorderSide(color: AppConstants.borderColor),
    labelStyle: const TextStyle(color: AppConstants.textLight, fontSize: 12),
    secondaryLabelStyle: const TextStyle(color: AppConstants.accentCyan, fontSize: 12),
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
    checkmarkColor: AppConstants.accentCyan,
  ),

  // ── Icônes par défaut ────────────────────────────────────────────
  iconTheme: const IconThemeData(color: AppConstants.textGrey, size: 22),

  // ── ListTile ─────────────────────────────────────────────────────
  listTileTheme: const ListTileThemeData(
    iconColor: AppConstants.accentCyan,
    textColor: AppConstants.textLight,
    tileColor: AppConstants.cardColor,
  ),

  // ── Divider ──────────────────────────────────────────────────────
  dividerTheme: const DividerThemeData(
    color: AppConstants.borderColor,
    thickness: 1,
  ),

  // ── SnackBar ─────────────────────────────────────────────────────
  snackBarTheme: SnackBarThemeData(
    backgroundColor: AppConstants.cardColor,
    contentTextStyle: const TextStyle(color: AppConstants.textLight),
    actionTextColor: AppConstants.accentCyan,
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
    behavior: SnackBarBehavior.floating,
  ),

  // ── RefreshIndicator ─────────────────────────────────────────────
  progressIndicatorTheme: const ProgressIndicatorThemeData(
    color: AppConstants.accentCyan,
  ),

  // ── Badge ────────────────────────────────────────────────────────
  badgeTheme: const BadgeThemeData(
    backgroundColor: AppConstants.accentRed,
    textColor: Colors.white,
  ),

  // ── Switch / CheckBox ────────────────────────────────────────────
  switchTheme: SwitchThemeData(
    thumbColor: WidgetStateProperty.resolveWith(
      (s) => s.contains(WidgetState.selected) ? AppConstants.accentCyan : AppConstants.textGrey,
    ),
    trackColor: WidgetStateProperty.resolveWith(
      (s) => s.contains(WidgetState.selected)
          ? AppConstants.accentCyan.withValues(alpha: 0.3)
          : AppConstants.borderColor,
    ),
  ),
);
