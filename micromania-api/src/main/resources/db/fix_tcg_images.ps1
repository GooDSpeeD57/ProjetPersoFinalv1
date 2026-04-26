# ══════════════════════════════════════════════════════════════════════════════
# fix_tcg_images.ps1
# 1. Télécharge les logos Pokémon depuis pokemontcg.io → dossier local pokemon/
# 2. Déplace les fichiers One Piece / Yu-Gi-Oh / Lorcana / accessoires TCG
#    vers leurs sous-dossiers respectifs
# ══════════════════════════════════════════════════════════════════════════════

# ── ADAPTER CE CHEMIN au dossier images de ton serveur Spring Boot ────────────
$base = "C:\Users\GooDSpeeD\Desktop\Projet final v1\micromania-api\media\images\catalogue\tcg"

# ── Créer les sous-dossiers ───────────────────────────────────────────────────
New-Item -ItemType Directory -Force -Path "$base\pokemon"    | Out-Null
New-Item -ItemType Directory -Force -Path "$base\one-piece"  | Out-Null
New-Item -ItemType Directory -Force -Path "$base\yugioh"     | Out-Null
New-Item -ItemType Directory -Force -Path "$base\lorcana"    | Out-Null
New-Item -ItemType Directory -Force -Path "$base\accessoire" | Out-Null

# ── 1. Téléchargement logos Pokémon ──────────────────────────────────────────
$pokemon = @{
    "sv3pt5-logo.png" = "https://images.pokemontcg.io/sv3pt5/logo.png"
    "sv7-logo.png"    = "https://images.pokemontcg.io/sv7/logo.png"
    "sv8-logo.png"    = "https://images.pokemontcg.io/sv8/logo.png"
    "sv8pt5-logo.png" = "https://images.pokemontcg.io/sv8pt5/logo.png"
    "sv9-logo.png"    = "https://images.pokemontcg.io/sv9/logo.png"
}

foreach ($entry in $pokemon.GetEnumerator()) {
    $dest = "$base\pokemon\$($entry.Key)"
    if (Test-Path $dest) {
        Write-Host "  [SKIP] $($entry.Key) déjà présent"
    } else {
        Write-Host "  [DL]   $($entry.Key) ..."
        try {
            Invoke-WebRequest -Uri $entry.Value -OutFile $dest -UseBasicParsing
            Write-Host "         OK"
        } catch {
            Write-Host "         ERREUR : $_"
        }
    }
}

# ── 2. Déplacement One Piece ──────────────────────────────────────────────────
$onePiece = @(
    "one-piece-op07-booster.jpg",
    "one-piece-op08-booster.jpg",
    "one-piece-op09-booster.jpg",
    "one-piece-op08-display-24.jpg",
    "one-piece-op09-display-24.jpg"
)
foreach ($f in $onePiece) {
    $src = "$base\$f"
    $dst = "$base\one-piece\$f"
    if (Test-Path $src) { Move-Item -Force $src $dst; Write-Host "  [MOVE] $f → one-piece/" }
    elseif (Test-Path $dst) { Write-Host "  [SKIP] $f déjà en place" }
    else { Write-Host "  [WARN] $f introuvable" }
}

# ── 3. Déplacement Yu-Gi-Oh ───────────────────────────────────────────────────
$yugioh = @(
    "yugioh-the-infinite-forbidden-booster.jpg",
    "yugioh-rage-of-the-abyss-booster.jpg",
    "yugioh-the-infinite-forbidden-display-24.jpg"
)
foreach ($f in $yugioh) {
    $src = "$base\$f"
    $dst = "$base\yugioh\$f"
    if (Test-Path $src) { Move-Item -Force $src $dst; Write-Host "  [MOVE] $f → yugioh/" }
    elseif (Test-Path $dst) { Write-Host "  [SKIP] $f déjà en place" }
    else { Write-Host "  [WARN] $f introuvable" }
}

# ── 4. Déplacement Lorcana ────────────────────────────────────────────────────
$lorcana = @(
    "lorcana-ursulas-return-booster.jpg",
    "lorcana-shimmering-skies-booster.jpg",
    "lorcana-ursulas-return-display-24.jpg"
)
foreach ($f in $lorcana) {
    $src = "$base\$f"
    $dst = "$base\lorcana\$f"
    if (Test-Path $src) { Move-Item -Force $src $dst; Write-Host "  [MOVE] $f → lorcana/" }
    elseif (Test-Path $dst) { Write-Host "  [SKIP] $f déjà en place" }
    else { Write-Host "  [WARN] $f introuvable" }
}

# ── 5. Déplacement accessoires TCG ────────────────────────────────────────────
$accessoires = @(
    "dragon-shield-sleeves-matte-noir.jpg",
    "dragon-shield-sleeves-matte-bleu.jpg",
    "ultra-pro-album-9-cases.jpg",
    "ultra-pro-classeur-pikachu.jpg",
    "ultra-pro-playmat-dracaufeu.jpg",
    "ultra-pro-deck-box-eclipse-rouge.jpg",
    "bcw-boite-rangement-1600.jpg",
    "playmat-one-piece-chapeau-de-paille.jpg"
)
foreach ($f in $accessoires) {
    $src = "$base\$f"
    $dst = "$base\accessoire\$f"
    if (Test-Path $src) { Move-Item -Force $src $dst; Write-Host "  [MOVE] $f → accessoire/" }
    elseif (Test-Path $dst) { Write-Host "  [SKIP] $f déjà en place" }
    else { Write-Host "  [WARN] $f introuvable" }
}

Write-Host ""
Write-Host "Terminé. Structure finale :"
Get-ChildItem -Recurse $base | Where-Object { -not $_.PSIsContainer } |
    Select-Object -ExpandProperty FullName |
    ForEach-Object { Write-Host "  $_" }
