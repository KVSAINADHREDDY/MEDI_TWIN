/**
 * Thin REST client wrapper for MediSphere Backend API (Java Enterprise Server)
 */
const Api = {
  base: '',

  async _json(path, opts = {}) {
    const res = await fetch(this.base + path, opts);
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.error || `Request failed with status ${res.status}`);
    }
    return res.json();
  },

  summary() { return this._json('/api/summary'); },
  patients() { return this._json('/api/patients'); },
  patient(id) { return this._json(`/api/patients/${id}`); },

  predict(id) { return this._json(`/api/patients/${id}/predict`, { method: 'POST' }); },
  predictions() { return this._json('/api/predictions'); },
  whatIf(id, params) {
    return this._json(`/api/patients/${id}/what-if`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params)
    });
  },

  alerts() { return this._json('/api/alerts'); },
  acknowledgeAlert(id) { return this._json(`/api/alerts/${id}/acknowledge`, { method: 'POST' }); },
  simulateTick() { return this._json('/api/simulate/tick', { method: 'POST' }); },
  ecgStream(id) { return this._json(`/api/ecg/${id}`); },

  generateCareplan(id) { return this._json(`/api/patients/${id}/careplan`, { method: 'POST' }); },
  careplans() { return this._json('/api/careplans'); },
  approveCareplan(id) { return this._json(`/api/careplans/${id}/approve`, { method: 'POST' }); },

  fhirBundle(id) { return this._json(`/api/fhir/${id}`); },
  auditLogs() { return this._json('/api/audit-logs'); },
  flModel() { return this._json('/api/federated-learning'); },
  trainFLRound() { return this._json('/api/federated-learning/train-round', { method: 'POST' }); },
  populationHealth() { return this._json('/api/population-health'); }
};
