/**
 * MediSphere Main Application Shell & View Router
 */
const App = {
  views: {
    dashboard: DashboardView,
    patients: PatientsView,
    predictions: PredictionsView,
    alerts: AlertsView,
    careplans: CareplansView,
    population: PopulationView,
    architecture: ArchitectureView,
  },
  current: 'dashboard',
  params: {},
  contentEl: null,

  init() {
    this.contentEl = document.getElementById('content-area');

    document.querySelectorAll('.nav-item').forEach(btn => {
      btn.addEventListener('click', () => {
        this.navigate(btn.dataset.view, {});
      });
    });

    const streamBtn = document.getElementById('topbar-stream-btn');
    streamBtn?.addEventListener('click', async () => {
      streamBtn.disabled = true;
      streamBtn.innerHTML = '⚡ Streaming 12K vitals/sec…';
      try {
        const res = await Api.simulateTick();
        if (this.current === 'alerts' || this.current === 'dashboard') {
          await this.render();
        }
        streamBtn.innerHTML = res.alertCount
          ? `🚨 +${res.alertCount} New Alert${res.alertCount > 1 ? 's' : ''}`
          : '✓ Telemetry Nominal';
      } finally {
        setTimeout(() => {
          if (streamBtn) {
            streamBtn.disabled = false;
            streamBtn.innerHTML = '⚡ Advance Kafka Stream';
          }
        }, 1400);
      }
    });

    this.navigate('dashboard', {});
  },

  navigate(viewName, params = {}) {
    if (!this.views[viewName]) viewName = 'dashboard';
    this.current = viewName;
    this.params = params;

    document.querySelectorAll('.nav-item').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.view === viewName);
    });

    this.render();
  },

  setParam(key, value) {
    this.params[key] = value;
  },

  async render() {
    const view = this.views[this.current];
    if (!view || !this.contentEl) return;

    try {
      await view.render(this.contentEl, this.params);
    } catch (err) {
      this.contentEl.innerHTML = `
        <div class="empty-state">
          <h3 style="color:#EF4444; margin-bottom:8px;">Unable to render view</h3>
          <p>${err.message}</p>
        </div>
      `;
      console.error('Render error in view:', this.current, err);
    }
  }
};

document.addEventListener('DOMContentLoaded', () => App.init());
