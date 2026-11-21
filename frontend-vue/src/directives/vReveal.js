// Lightweight GPU-friendly scroll-reveal directive using IntersectionObserver
export const vReveal = {
  mounted(el, binding) {
    const prefersReduced =
      typeof window !== 'undefined' &&
      window.matchMedia &&
      window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (prefersReduced || typeof IntersectionObserver === 'undefined') {
      el.classList.add('v-reveal-visible');
      return;
    }

    const delay =
      typeof binding.value === 'number'
        ? binding.value
        : binding.value?.delay || 0;

    el.classList.add('v-reveal-init');
    if (delay > 0) {
      el.style.transitionDelay = `${Math.min(delay, 600)}ms`;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            el.classList.add('v-reveal-visible');
            observer.unobserve(el);
          }
        });
      },
      { threshold: 0.08, rootMargin: '0px 0px -24px 0px' }
    );

    el._revealObserver = observer;
    observer.observe(el);
  },
  unmounted(el) {
    if (el._revealObserver) {
      el._revealObserver.disconnect();
      delete el._revealObserver;
    }
  }
};
