/* ============================================================
   MICROMANIA — main.js
   ============================================================ */

/* ── INJECTION DU LOGIN MODAL + TOASTS ── */
function injectAppShells() {
  if (document.getElementById('loginModal')) return; // déjà injecté
  document.body.insertAdjacentHTML('beforeend', `
    <div class="login-modal-overlay" id="loginModal" role="dialog" aria-modal="true" aria-labelledby="loginModalTitle">
      <div class="login-modal-box">
        <button class="login-modal-close" id="loginModalClose" aria-label="Fermer">&#x2715;</button>
        <div class="login-modal-title" id="loginModalTitle">// CONNEXION</div>

        <div id="loginModalAlertError" class="alert alert-error" style="display:none">Email ou mot de passe incorrect.</div>
        <div id="loginModalAlertLogout" class="alert alert-info" style="display:none">Vous avez été déconnecté.</div>
        <div id="loginModalAlertSuccess" class="alert alert-success" style="display:none"></div>

        <form action="/auth/login" method="post" id="loginModalForm">
          <div class="form-group">
            <label class="form-label" for="loginEmail">Email</label>
            <input class="form-control" type="email" id="loginEmail" name="username"
                   placeholder="votre@email.com" required autocomplete="email"/>
          </div>
          <div class="form-group">
            <label class="form-label" for="loginPassword">Mot de passe</label>
            <input class="form-control" type="password" id="loginPassword" name="password"
                   placeholder="••••••••" required autocomplete="current-password"/>
          </div>
          <div class="form-group" style="display:flex;align-items:center;gap:0.6rem;margin-bottom:1rem">
            <input type="checkbox" id="loginRememberMe" name="remember-me"/>
            <label for="loginRememberMe" style="margin:0;font-size:0.9rem">Se souvenir de moi</label>
          </div>
          <button type="submit" class="btn btn-primary btn-full">Connexion</button>
          <input type="hidden" id="loginModalCsrf" name="_csrf" value=""/>
        </form>

        <div class="login-divider">ou continuer avec</div>

        <div class="social-btn-grid">
          <button type="button" class="btn-social" data-social="Google" onclick="showSocialToast()">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
            </svg>Google
          </button>
          <button type="button" class="btn-social" data-social="Facebook" onclick="showSocialToast()">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" fill="#1877F2"/>
            </svg>Facebook
          </button>
          <button type="button" class="btn-social" data-social="X" onclick="showSocialToast()">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" fill="currentColor"/>
            </svg>X
          </button>
          <button type="button" class="btn-social" data-social="Discord" onclick="showSocialToast()">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <path d="M20.317 4.37a19.791 19.791 0 0 0-4.885-1.515.074.074 0 0 0-.079.037c-.21.375-.444.864-.608 1.25a18.27 18.27 0 0 0-5.487 0 12.64 12.64 0 0 0-.617-1.25.077.077 0 0 0-.079-.037A19.736 19.736 0 0 0 3.677 4.37a.07.07 0 0 0-.032.027C.533 9.046-.32 13.58.099 18.057.1 18.095.12 18.13.15 18.15a19.9 19.9 0 0 0 5.993 3.03.078.078 0 0 0 .084-.028 14.09 14.09 0 0 0 1.226-1.994.076.076 0 0 0-.041-.106 13.107 13.107 0 0 1-1.872-.892.077.077 0 0 1-.008-.128 10.2 10.2 0 0 0 .372-.292.074.074 0 0 1 .077-.01c3.928 1.793 8.18 1.793 12.062 0a.074.074 0 0 1 .078.01c.12.098.246.198.373.292a.077.077 0 0 1-.006.127 12.299 12.299 0 0 1-1.873.892.077.077 0 0 0-.041.107c.36.698.772 1.362 1.225 1.993a.076.076 0 0 0 .084.028 19.839 19.839 0 0 0 6.002-3.03.077.077 0 0 0 .032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 0 0-.031-.03zM8.02 15.33c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z" fill="#5865F2"/>
            </svg>Discord
          </button>
        </div>

        <div class="login-modal-footer">
          Pas encore de compte ? <a href="/auth/inscription">Créer un compte</a>
        </div>
      </div>
    </div>
    <div class="login-social-toast" id="loginSocialToast">Connexion sociale disponible prochainement</div>
    <div class="add-to-cart-toast" id="addToCartToast" role="status" aria-live="polite">
      <span id="addToCartToastMsg"></span>
    </div>
  `);
}

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
  injectAppShells();

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
  const currentUrl = new URL(window.location.href);
  const path = currentUrl.pathname;

  document.querySelectorAll('.nav-link').forEach(link => {
    const href = link.getAttribute('href');
    if (!href) return;

    let targetUrl;
    try {
      targetUrl = new URL(href, window.location.origin);
    } catch (e) {
      return;
    }

    const hrefPath = targetUrl.pathname;
    const targetParams = Array.from(targetUrl.searchParams.entries());

    if (hrefPath === '/' && path === '/' && targetParams.length === 0) {
      link.classList.add('active');
      return;
    }

    if (targetParams.length > 0) {
      if (path !== hrefPath) return;
      const matches = targetParams.every(([key, value]) => currentUrl.searchParams.get(key) === value);
      if (matches) {
        link.classList.add('active');
      }
      return;
    }

    if (hrefPath !== '/' && path.startsWith(hrefPath)) {
      link.classList.add('active');
    }
  });

  initPromoCarousel();
  initRailCarousels();
  initMegaMenus();
  initUserDropdown();
  initProductCards();
  initAddToCartForms();
});

/* ── USER DROPDOWN ── */
function initUserDropdown() {
  const wrapper = document.getElementById('navUserDropdown');
  const btn     = document.getElementById('navUserBtn');
  const menu    = wrapper?.querySelector('.nav-user-menu');
  if (!wrapper || !btn || !menu) return;

  const open  = () => { wrapper.classList.add('is-open');    btn.setAttribute('aria-expanded', 'true');  };
  const close = () => { wrapper.classList.remove('is-open'); btn.setAttribute('aria-expanded', 'false'); };
  const toggle = () => wrapper.classList.contains('is-open') ? close() : open();

  btn.addEventListener('click', (e) => { e.stopPropagation(); toggle(); });

  // Fermeture sur clic extérieur
  document.addEventListener('click', (e) => {
    if (!wrapper.contains(e.target)) close();
  });

  // Fermeture sur Escape
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') close();
  });

  // Fermeture quand on clique sur un lien du menu
  menu.querySelectorAll('.nav-user-menu-item').forEach(item => {
    item.addEventListener('click', () => close());
  });
}

function initMegaMenus() {
  const items = Array.from(document.querySelectorAll('.nav-item-mega'));
  if (!items.length) return;

  const desktopQuery = window.matchMedia('(hover: hover) and (pointer: fine) and (min-width: 769px)');

  const closeAll = () => {
    items.forEach(item => {
      item.classList.remove('is-open');
      item.querySelector('.nav-mega-toggle')?.setAttribute('aria-expanded', 'false');
    });
  };

  const openItem = (item) => {
    closeAll();
    item.classList.add('is-open');
    item.querySelector('.nav-mega-toggle')?.setAttribute('aria-expanded', 'true');
  };

  const syncMode = () => {
    closeAll();
    items.forEach(item => {
      const toggle = item.querySelector('.nav-mega-toggle');
      if (!toggle) return;
      toggle.setAttribute('aria-expanded', 'false');
      toggle.setAttribute('aria-haspopup', 'true');
    });
  };

  items.forEach(item => {
    const toggle = item.querySelector('.nav-mega-toggle');
    const menu = item.querySelector('.mega-menu');
    if (!toggle || !menu) return;

    toggle.addEventListener('click', (event) => {
      if (desktopQuery.matches) return;
      event.preventDefault();
      event.stopPropagation();
      const isOpen = item.classList.contains('is-open');
      if (isOpen) {
        closeAll();
      } else {
        openItem(item);
      }
    });
  });

  document.addEventListener('click', (event) => {
    if (desktopQuery.matches) return;
    if (!event.target.closest('.nav-item-mega')) {
      closeAll();
    }
  });

  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') closeAll();
  });

  const handleBreakpointChange = () => syncMode();
  if (typeof desktopQuery.addEventListener === 'function') {
    desktopQuery.addEventListener('change', handleBreakpointChange);
  } else if (typeof desktopQuery.addListener === 'function') {
    desktopQuery.addListener(handleBreakpointChange);
  }
  window.addEventListener('resize', syncMode);

  syncMode();
}



function initPromoCarousel() {
  const root = document.querySelector('[data-promo-carousel]');
  if (!root) return;

  const slides = Array.from(root.querySelectorAll('.promo-slide'));
  const dots = Array.from(root.querySelectorAll('.promo-dot'));
  const prev = root.querySelector('[data-promo-prev]');
  const next = root.querySelector('[data-promo-next]');
  if (!slides.length) return;

  let index = 0;
  let timer = null;

  const render = (nextIndex) => {
    index = (nextIndex + slides.length) % slides.length;
    slides.forEach((slide, i) => slide.classList.toggle('is-active', i === index));
    dots.forEach((dot, i) => dot.classList.toggle('is-active', i === index));
  };

  const schedule = () => {
    if (slides.length < 2) return;
    clearInterval(timer);
    timer = setInterval(() => render(index + 1), 5500);
  };

  prev?.addEventListener('click', () => {
    render(index - 1);
    schedule();
  });
  next?.addEventListener('click', () => {
    render(index + 1);
    schedule();
  });
  dots.forEach((dot) => {
    dot.addEventListener('click', () => {
      render(Number(dot.dataset.index || 0));
      schedule();
    });
  });

  root.addEventListener('mouseenter', () => clearInterval(timer));
  root.addEventListener('mouseleave', schedule);

  render(0);
  schedule();
}

function initRailCarousels() {
  document.querySelectorAll('[data-rail-wrapper]').forEach(wrapper => {
    const rail = wrapper.querySelector('[data-rail]');
    const prev = wrapper.querySelector('[data-rail-prev]');
    const next = wrapper.querySelector('[data-rail-next]');
    if (!rail) return;

    const step = () => Math.max(rail.clientWidth * 0.85, 260);
    const refresh = () => {
      const maxLeft = rail.scrollWidth - rail.clientWidth - 8;
      if (prev) prev.disabled = rail.scrollLeft <= 8;
      if (next) next.disabled = rail.scrollLeft >= maxLeft;
    };

    prev?.addEventListener('click', () => rail.scrollBy({ left: -step(), behavior: 'smooth' }));
    next?.addEventListener('click', () => rail.scrollBy({ left: step(), behavior: 'smooth' }));
    rail.addEventListener('scroll', refresh, { passive: true });
    window.addEventListener('resize', refresh);
    refresh();
  });
}

/* ── FILTRE CATALOGUE : submit auto sur changement select ── */
function resetRadioGroup(form, groupName) {
  const checked = form.querySelector(`input[name="${groupName}"]:checked`);
  if (!checked) return;
  const emptyChoice = form.querySelector(`input[name="${groupName}"][value=""]`);
  if (emptyChoice) {
    emptyChoice.checked = true;
    return;
  }
  checked.checked = false;
}

function autoSubmitFilter(changedGroup) {
  const form = document.getElementById('filter-form');
  if (!form) return;

  if (changedGroup === 'univers') {
    const univers = form.querySelector('input[name="univers"]:checked')?.value || '';
    if (univers === 'tcg') {
      resetRadioGroup(form, 'plateforme');
      resetRadioGroup(form, 'famille');
    } else {
      resetRadioGroup(form, 'licence');
      resetRadioGroup(form, 'format');
    }
  }

  const pageInput = form.querySelector('input[name="page"]');
  if (pageInput) pageInput.value = '0';
  form.submit();
}

/* ── LOGIN MODAL ── */
function openLoginModal(opts) {
  const overlay = document.getElementById('loginModal');
  if (!overlay) return;
  if (opts?.error) document.getElementById('loginModalAlertError').style.display = '';
  if (opts?.logout) document.getElementById('loginModalAlertLogout').style.display = '';
  if (opts?.success) {
    const el = document.getElementById('loginModalAlertSuccess');
    el.textContent = opts.success; el.style.display = '';
  }
  overlay.classList.add('is-open');
  overlay.querySelector('input[type="email"]')?.focus();
  document.body.style.overflow = 'hidden';
}

function closeLoginModal() {
  const overlay = document.getElementById('loginModal');
  if (!overlay) return;
  overlay.classList.remove('is-open');
  document.body.style.overflow = '';
}

function showSocialToast() {
  const toast = document.getElementById('loginSocialToast');
  if (!toast) return;
  toast.classList.add('is-visible');
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('is-visible'), 3000);
}

document.addEventListener('DOMContentLoaded', () => {
  // Injecter le token CSRF depuis la meta dans le formulaire du modal
  const csrfToken = document.querySelector('meta[name="_csrf_token"]')?.content;
  const csrfParam = document.querySelector('meta[name="_csrf_param"]')?.content;
  const csrfInput = document.getElementById('loginModalCsrf');
  if (csrfInput && csrfToken) {
    csrfInput.value = csrfToken;
    csrfInput.name = csrfParam || '_csrf';
  }

  // Fermer en cliquant le bouton ×
  document.getElementById('loginModalClose')?.addEventListener('click', closeLoginModal);

  // Fermer en cliquant le fond
  document.getElementById('loginModal')?.addEventListener('click', e => {
    if (e.target === e.currentTarget) closeLoginModal();
  });

  // Fermer avec Escape
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeLoginModal();
  });

  // Icône compte dans la navbar → ouvre le modal
  document.querySelector('[data-open-login]')?.addEventListener('click', e => {
    e.preventDefault();
    openLoginModal();
  });
});

/* ── CARTES PRODUIT CLIQUABLES ── */
function initProductCards() {
  document.querySelectorAll('.product-card[data-href]').forEach(card => {
    card.addEventListener('click', e => {
      if (!e.target.closest('button, a, form, input')) {
        window.location.href = card.dataset.href;
      }
    });
  });
}

/* ── QUANTITÉ PANIER ── */
function changeQty(input, delta) {
  const current = parseInt(input.value) || 1;
  const next = Math.max(1, current + delta);
  input.value = next;
}

/* ── Ajout au panier AJAX ───────────────────────────────────── */
function initAddToCartForms() {
  document.addEventListener('submit', e => {
    const form = e.target;
    if (!form || !form.action) return;
    try {
      if (new URL(form.action).pathname !== '/panier/ajouter') return;
    } catch (_) { return; }

    e.preventDefault();

    const btn = form.querySelector('button[type="submit"]');
    if (btn) btn.disabled = true;

    fetch('/panier/ajouter-ajax', {
      method: 'POST',
      body: new FormData(form)
    })
      .then(r => r.json())
      .then(result => {
        showCartToast(result.success, result.message);
        if (result.cartCount !== undefined) updateCartBadge(result.cartCount);
      })
      .catch(() => showCartToast(false, 'Erreur réseau'))
      .finally(() => { if (btn) btn.disabled = false; });
  });
}

function showCartToast(success, message) {
  const toast = document.getElementById('addToCartToast');
  const msg   = document.getElementById('addToCartToastMsg');
  if (!toast || !msg) return;

  clearTimeout(toast._cartTimer);
  msg.textContent = message || (success ? 'Ajouté au panier' : 'Erreur');
  toast.classList.remove('is-visible', 'is-success', 'is-error');
  void toast.offsetWidth;
  toast.classList.add('is-visible', success ? 'is-success' : 'is-error');
  toast._cartTimer = setTimeout(() => toast.classList.remove('is-visible'), 3000);
}

function updateCartBadge(count) {
  const badge = document.getElementById('navCartBadge');
  if (!badge) return;
  badge.textContent = count;
  badge.style.display = count > 0 ? '' : 'none';
}


function loadAnalytics() {
  console.log("Analytics activé");
}

function acceptCookies() {
  localStorage.setItem("cookieConsent", "accepted");
  const banner = document.getElementById("cookie-banner");
  if (banner) banner.style.display = "none";
  loadAnalytics();
}

function refuseCookies() {
  localStorage.setItem("cookieConsent", "refused");
  const banner = document.getElementById("cookie-banner");
  if (banner) banner.style.display = "none";
}

document.addEventListener("DOMContentLoaded", () => {
  const consent = localStorage.getItem("cookieConsent");
  const banner = document.getElementById("cookie-banner");

  if (!banner) return;

  if (!consent) {
    banner.style.display = "block";
    return;
  }

  if (consent === "accepted") {
    loadAnalytics();
  }
});