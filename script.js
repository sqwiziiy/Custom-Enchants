/* ══════════════════════════════════════
   Language system
   ══════════════════════════════════════ */
let currentLang = 'en';

function applyLang(lang) {
  currentLang = lang;
  document.documentElement.setAttribute('data-lang', lang);
  document.documentElement.lang = lang;

  // Show/hide [data-en] and [data-ru] elements
  document.querySelectorAll('[data-en]').forEach(el => {
    el.hidden = (lang !== 'en');
  });
  document.querySelectorAll('[data-ru]').forEach(el => {
    el.hidden = (lang !== 'ru');
  });

  // Update section titles with data-en / data-ru attributes
  document.querySelectorAll('[data-en][data-ru]').forEach(el => {
    el.textContent = lang === 'ru' ? el.getAttribute('data-ru') : el.getAttribute('data-en');
    el.hidden = false;
  });

  // Update nav filter button labels
  document.querySelectorAll('.filter-btn[data-en][data-ru]').forEach(btn => {
    btn.textContent = lang === 'ru' ? btn.getAttribute('data-ru') : btn.getAttribute('data-en');
  });

  // Update lang switcher active state
  document.querySelectorAll('.lang-btn').forEach(b => {
    b.classList.toggle('active', b.dataset.lang === lang);
  });

  localStorage.setItem('wiki-lang', lang);
}

// Init
const savedLang = localStorage.getItem('wiki-lang') || 'en';
applyLang(savedLang);

document.querySelectorAll('.lang-btn').forEach(btn => {
  btn.addEventListener('click', () => applyLang(btn.dataset.lang));
});

/* ══════════════════════════════════════
   Tab / filter system
   ══════════════════════════════════════ */
const viewEnchants   = document.getElementById('view-enchants');
const viewTrades     = document.getElementById('view-trades');
const viewStructure  = document.getElementById('view-structure');
const filterBtns     = document.querySelectorAll('.filter-btn');
const enchantCards   = document.querySelectorAll('.enchant-card');

filterBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    filterBtns.forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    const filter = btn.dataset.filter;

    if (filter === 'trades') {
      viewEnchants.hidden  = true;
      viewTrades.hidden    = false;
      viewStructure.hidden = true;
    } else if (filter === 'structure') {
      viewEnchants.hidden  = true;
      viewTrades.hidden    = true;
      viewStructure.hidden = false;
    } else {
      viewEnchants.hidden  = false;
      viewTrades.hidden    = true;
      viewStructure.hidden = true;
      enchantCards.forEach(card => {
        card.style.display = (filter === 'all' || card.dataset.category === filter) ? '' : 'none';
      });
    }
  });
});

/* ══════════════════════════════════════
   Sticky nav shadow
   ══════════════════════════════════════ */
const nav = document.getElementById('sticky-nav');
window.addEventListener('scroll', () => {
  nav.classList.toggle('scrolled', window.scrollY > 80);
});
