/* ============================================================
   MICROMANIA — main.js
   ============================================================ */

/* ── INDICATEUR FORCE MOT DE PASSE ── */
function initPasswordStrength(inputId) {
  const input = document.getElementById(inputId);
  if (!input) return;
  input.addEventListener('input', () => checkStrength(input.value));
}

function checkStrength(pwd) {
  const checks = [
    pwd.length >= 8,
    /[A-Z]/.test(pwd) && /[a-z]/.test(pwd),
    /\d/.test(pwd),
    /[@$!%*?&\-_#]/.test(pwd)
  ];
  const score = checks.filter(Boolean).length;
  const cls = score <= 1 ? 'strength-weak' : score <= 3 ? 'strength-medium' : 'strength-strong';
  for (let i = 1; i <= 4; i++) {
    const bar = document.getElementById('strength-' + i);
    if (!bar) continue;
    bar.className = 'strength-bar';
    if (i <= score) bar.classList.add(cls);
  }
}

/* ── VALIDATION CHAMP EN TEMPS RÉEL ── */
const PATTERNS = {
  pseudo:   { re: /^[a-zA-Z0-9_\-]{3,50}$/,         msg: 'Lettres, chiffres, _ ou - (3-50 caractères)' },
  email:    { re: /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/,   msg: 'Format email invalide' },
  telephone:{ re: /^(\+33|0)[1-9](\d{2}){4}$/,       msg: 'Format invalide (ex: 0612345678)' },
  nom:      { re: /^[a-zA-ZÀ-ÿ\s\-']{2,100}$/,       msg: 'Lettres uniquement (2-100 caractères)' },
  prenom:   { re: /^[a-zA-ZÀ-ÿ\s\-']{2,100}$/,       msg: 'Lettres uniquement (2-100 caractères)' },
};

function validateLive(fieldName, value) {
  const v = PATTERNS[fieldName];
  if (!v) return;
  const input   = document.querySelector(`[name="${fieldName}"]`);
  const feedback = input?.parentElement?.querySelector('.invalid-feedback');
  if (!input) return;
  if (value && !v.re.test(value)) {
    input.classList.add('is-invalid');
    if (feedback) { feedback.textContent = v.msg; feedback.style.display = 'block'; }
  } else {
    input.classList.remove('is-invalid');
    if (feedback) feedback.style.display = 'none';
  }
}

/* ── CONFIRMATION SUPPRESSION ── */
function confirmDelete(msg, formId) {
  if (confirm(msg || 'Confirmer la suppression ?')) {
    document.getElementById(formId)?.submit();
  }
}

/* ── TOAST (affichage auto des flash Thymeleaf) ── */
document.addEventListener('DOMContentLoaded', () => {
  // Masquer les alertes après 5s
  document.querySelectorAll('.alert-auto-hide').forEach(el => {
    setTimeout(() => el.style.opacity = '0', 4500);
    setTimeout(() => el.remove(), 5000);
  });

  // Init champs avec validation live
  Object.keys(PATTERNS).forEach(field => {
    const input = document.querySelector(`[name="${field}"]`);
    if (input) input.addEventListener('input', e => validateLive(field, e.target.value));
  });

  // Init force mot de passe
  initPasswordStrength('motDePasse');
  initPasswordStrength('password');

  // Nav : marquer le lien actif
  const path = window.location.pathname;
  document.querySelectorAll('.nav-link').forEach(link => {
    if (link.getAttribute('href') && path.startsWith(link.getAttribute('href')) && link.getAttribute('href') !== '/') {
      link.classList.add('active');
    }
  });
});

/* ── FILTRE CATALOGUE : submit auto sur changement select ── */
function autoSubmitFilter() {
  document.getElementById('filter-form')?.submit();
}

/* ── QUANTITÉ PANIER ── */
function changeQty(input, delta) {
  const current = parseInt(input.value) || 1;
  const next = Math.max(1, current + delta);
  input.value = next;
}
