document.addEventListener('DOMContentLoaded', () => {
  // Confirm forms with data-confirm attribute
  document.querySelectorAll('form[data-confirm]')
    .forEach(f => f.addEventListener('submit', (e) => {
      const msg = f.getAttribute('data-confirm') || 'よろしいですか？';
      if (!window.confirm(msg)) { e.preventDefault(); }
    }));

  // Smooth anchors
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', (e) => {
      const id = a.getAttribute('href').slice(1);
      const el = document.getElementById(id);
      if (el) {
        e.preventDefault();
        el.scrollIntoView({behavior:'smooth', block:'start'});
      }
    });
  });
});

