document.addEventListener('DOMContentLoaded', () => {
  const tabs = [...document.querySelectorAll('[data-translation-tab]')];
  const panels = [...document.querySelectorAll('[data-translation-panel]')];
  tabs.forEach(tab => tab.addEventListener('click', () => {
    const target = tab.dataset.translationTab;
    tabs.forEach(t => t.classList.toggle('active', t === tab));
    panels.forEach(p => p.classList.toggle('active', p.dataset.translationPanel === target));
  }));

  document.querySelectorAll('[data-confirm-button]').forEach(button => {
    button.addEventListener('click', event => {
      if (!window.confirm(button.dataset.confirmButton || 'Are you sure?')) event.preventDefault();
    });
  });

  document.querySelectorAll('[data-confirm]').forEach(form => {
    form.addEventListener('submit', event => {
      const message = form.dataset.confirm || 'Are you sure?';
      if (!window.confirm(message)) event.preventDefault();
    });
  });
});
