(() => {
  const header = document.querySelector('.header');
  const drawer = document.querySelector('.mobile-drawer');
  const hamburger = document.querySelector('.hamburger');
  if (hamburger && drawer) {
    hamburger.addEventListener('click', () => {
      const open = drawer.classList.toggle('open');
      hamburger.setAttribute('aria-expanded', String(open));
    });
  }
  window.addEventListener('scroll', () => header?.classList.toggle('scrolled', window.scrollY > 8), {passive:true});

  const hero = document.querySelector('[data-server-hero]');
  if (hero) {
    const slides = [...hero.querySelectorAll('.hero-slide')];
    const dots = [...hero.querySelectorAll('.hero-dot')];
    const progress = hero.querySelector('#heroProgress');
    let index = 0;
    let timer;
    const duration = 6500;
    const show = (next) => {
      if (!slides.length) return;
      index = (next + slides.length) % slides.length;
      slides.forEach((slide, i) => slide.classList.toggle('active', i === index));
      dots.forEach((dot, i) => {
        dot.classList.toggle('active', i === index);
        dot.setAttribute('aria-current', i === index ? 'true' : 'false');
      });
      if (progress) {
        progress.style.transition = 'none';
        progress.style.width = '0%';
        requestAnimationFrame(() => requestAnimationFrame(() => {
          progress.style.transition = `width ${duration}ms linear`;
          progress.style.width = '100%';
        }));
      }
    };
    const restart = () => {
      clearInterval(timer);
      if (slides.length > 1 && !window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        timer = setInterval(() => show(index + 1), duration);
      }
    };
    hero.querySelector('#heroPrev')?.addEventListener('click', () => { show(index - 1); restart(); });
    hero.querySelector('#heroNext')?.addEventListener('click', () => { show(index + 1); restart(); });
    dots.forEach((dot, i) => dot.addEventListener('click', () => { show(i); restart(); }));
    let startX = 0;
    hero.addEventListener('touchstart', e => { startX = e.changedTouches[0].clientX; }, {passive:true});
    hero.addEventListener('touchend', e => {
      const delta = e.changedTouches[0].clientX - startX;
      if (Math.abs(delta) > 45) { show(index + (delta < 0 ? 1 : -1)); restart(); }
    }, {passive:true});
    show(0); restart();
  }

  document.querySelectorAll('[data-rail-wrap]').forEach((wrap) => {
    const rail = wrap.querySelector('.movie-rail');
    if (!rail) return;
    wrap.querySelector('[data-rail-prev]')?.addEventListener('click', () => rail.scrollBy({left: -Math.max(rail.clientWidth * .75, 320), behavior:'smooth'}));
    wrap.querySelector('[data-rail-next]')?.addEventListener('click', () => rail.scrollBy({left: Math.max(rail.clientWidth * .75, 320), behavior:'smooth'}));
  });

  document.querySelectorAll('.locale-menu').forEach((menu) => {
    menu.addEventListener('toggle', () => {
      if (!menu.open) return;
      document.querySelectorAll('.locale-menu[open]').forEach(other => { if (other !== menu) other.removeAttribute('open'); });
    });
  });

  document.querySelectorAll('[data-confirm-button]').forEach((button) => {
    button.addEventListener('click', (event) => {
      const message = button.dataset.confirmButton || 'Continue?';
      if (!window.confirm(message)) event.preventDefault();
    });
  });
})();
