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
});

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

/* ── QUANTITÉ PANIER ── */
function changeQty(input, delta) {
  const current = parseInt(input.value) || 1;
  const next = Math.max(1, current + delta);
  input.value = next;
}


