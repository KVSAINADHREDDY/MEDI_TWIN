/**
 * View 1: Patient 360 Dashboard
 * Master overview of digital health twins, streaming metrics, and clinical alerts
 */
const DashboardView = {
  title: 'Patient 360 Dashboard',

  async render(el) {
    el.innerHTML = `<div class="empty-state">Loading clinical intelligence dashboard…</div>`;
    const [summary, patients, alerts] = await Promise.all([
      Api.summary(),
      Api.patients(),
      Api.alerts()
    ]);

    const recent = patients.slice(0, 6);

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>Patient 360 Dashboard</h1>
          <p>Real-Time Cognitive Health Twin Platform &middot; Enterprise Clinical Operations</p>
        </div>
        <div class="view-actions">
          <button id="dash-stream-btn" class="btn btn-stream">
            ⚡ Advance Kafka Stream
          </button>
        </div>
      </div>

      <!-- 4-Stat Metric Row (Matches Screenshot M1 Foundation) -->
      <div class="stat-grid-4">
        <div class="stat-card">
          <div class="stat-card-title">Patients Onboarded</div>
          <div class="stat-card-value">${summary.patientsOnboarded.toLocaleString()}</div>
          <div class="stat-card-subtext subtext-up">↑ 87 this week &middot; 100% coverage</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">FHIR Resources Synced</div>
          <div class="stat-card-value">${(summary.fhirResourcesSynced / 1000000).toFixed(1)}M</div>
          <div class="stat-card-subtext subtext-up">Synced from Epic &amp; Cerner EHR</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Digital Twins Created</div>
          <div class="stat-card-value">${summary.twinsCreated.toLocaleString()}</div>
          <div class="stat-card-subtext subtext-up">100% active living models</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">High-Risk Patients</div>
          <div class="stat-card-value">${summary.highRiskPatients}</div>
          <div class="stat-card-subtext subtext-alert">Require immediate clinical intervention</div>
        </div>
      </div>

      <!-- Live Clinical Highlights Grid -->
      <div style="display: grid; grid-template-columns: 1.2fr 1fr; gap: 20px; margin-bottom: 24px;">
        <!-- Featured Patient Digital Twin Spotlight (John Doe) -->
        <div class="clinical-specimen-card" style="margin-bottom:0;">
          <div class="specimen-header">
            <div class="specimen-title">
              <span>🫀 Active Twin Spotlight &mdash; John Doe (58M)</span>
            </div>
            <span class="badge badge-high">High CVD Risk 24.3%</span>
          </div>
          <div class="specimen-body">
            <div class="telemetry-row">
              <div class="telemetry-label">FHIR Patient Resource:</div>
              <div class="telemetry-value highlight-cyan">Loaded from Epic EHR &middot; Patient/Epic-EHR-789210</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Clinical Conditions:</div>
              <div class="telemetry-value">Hypertension, Type 2 Diabetes, Hyperlipidemia</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Live Vitals Telemetry:</div>
              <div class="telemetry-value">HR 72 bpm &middot; BP 130/85 mmHg &middot; SpO2 98% (2 min ago)</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Diagnostic Labs:</div>
              <div class="telemetry-value">HbA1c <span class="highlight-amber">7.2%</span> &middot; eGFR <span class="highlight-amber">65 mL/min</span> &middot; LDL <span class="highlight-amber">120 mg/dL</span></div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Active Prescriptions:</div>
              <div class="telemetry-value">Metformin 500mg BID, Lisinopril 10mg Daily</div>
            </div>

            <div class="specimen-actions">
              <button class="btn btn-primary btn-sm" id="btn-open-john-twin">Open Full Digital Twin &rarr;</button>
              <button class="btn btn-outline btn-sm" id="btn-open-john-pred">Run AI Risk Prediction</button>
            </div>
          </div>
        </div>

        <!-- Live Surveillance Stream Box (Sarah M. Anomaly) -->
        <div class="clinical-specimen-card" style="margin-bottom:0;">
          <div class="specimen-header">
            <div class="specimen-title">
              <span>⚡ Real-Time Kafka Stream &amp; Anomaly Feed</span>
            </div>
            <span class="badge badge-high">47 Alerts Today</span>
          </div>
          <div class="specimen-body">
            <div style="background:#090E17; border:1px solid #1E293B; border-radius:6px; padding:12px; margin-bottom:12px;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                <span style="font-weight:700; color:#EF4444; font-size:13px;">🚨 CRITICAL: Sarah M. &middot; HR Spike 145 bpm</span>
                <span class="mono" style="font-size:11.5px; color:var(--text-subtle);">14:23 (Kafka Stream)</span>
              </div>
              <p style="font-size:12.5px; color:var(--text-muted); margin-bottom:6px;">
                Possible Atrial Fibrillation &middot; 89% AI Confidence &middot; Telemetry rate 12K vitals/sec
              </p>
              <span class="badge badge-cyan" style="font-size:11px;">Auto: Notified Cardiologist &amp; Scheduled ECG</span>
            </div>

            <div class="telemetry-row">
              <div class="telemetry-label">Wearables Online:</div>
              <div class="telemetry-value highlight-green">${summary.wearablesOnline} devices (98.3% uptime)</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Avg Anomaly Response:</div>
              <div class="telemetry-value highlight-green">3.2 min (↓ 67% vs manual triage)</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Hospitalizations Prevented:</div>
              <div class="telemetry-value highlight-green">↓ 23% clinical reduction</div>
            </div>

            <div class="specimen-actions">
              <button class="btn btn-outline btn-sm" id="btn-open-surveillance">Open Surveillance Console &rarr;</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Recently Synchronized Digital Health Twins Table -->
      <div class="table-panel">
        <div class="table-head-bar">
          <h2>Recently Synchronized Digital Health Twins (MongoDB Twin Store)</h2>
          <span class="badge badge-cyan">FHIR R4 &amp; SMART Interoperability</span>
        </div>
        <table>
          <thead>
            <tr>
              <th>Patient</th>
              <th>Demographics</th>
              <th>Conditions</th>
              <th>EHR Source</th>
              <th>Twin Completeness</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            ${recent.map(p => `
              <tr class="clickable" data-goto-patient="${p.patientId}">
                <td>
                  <strong>${p.name}</strong><br>
                  <span class="mono" style="color:var(--text-subtle); font-size:11.5px;">${p.fhirId}</span>
                </td>
                <td class="mono">${p.age} yrs &middot; ${p.sex}</td>
                <td>${p.conditions.map(c => `<span class="badge" style="background:#1E293B; color:#94A3B8; margin-right:4px;">${c}</span>`).join('')}</td>
                <td>${p.ehrSource}</td>
                <td class="mono">
                  <div style="display:flex; align-items:center; gap:8px;">
                    <div style="flex:1; height:6px; background:#1E293B; border-radius:3px; overflow:hidden; width:70px;">
                      <div style="width:${Math.round(p.twin.dataCompleteness * 100)}%; height:100%; background:#10B981;"></div>
                    </div>
                    <span>${Math.round(p.twin.dataCompleteness * 100)}%</span>
                  </div>
                </td>
                <td>
                  <button class="btn btn-outline btn-sm" data-patient="${p.patientId}">View Twin &rarr;</button>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;

    el.querySelector('#dash-stream-btn')?.addEventListener('click', async () => {
      const btn = el.querySelector('#dash-stream-btn');
      btn.disabled = true;
      btn.textContent = 'Streaming 12K vitals/sec…';
      try {
        await Api.simulateTick();
        await this.render(el);
      } finally {
        setTimeout(() => {
          if (btn) { btn.disabled = false; btn.textContent = '⚡ Advance Kafka Stream'; }
        }, 1200);
      }
    });

    el.querySelector('#btn-open-john-twin')?.addEventListener('click', () => {
      App.navigate('patients', { patientId: 'pat-john-doe' });
    });

    el.querySelector('#btn-open-john-pred')?.addEventListener('click', () => {
      App.navigate('predictions', { patientId: 'pat-john-doe' });
    });

    el.querySelector('#btn-open-surveillance')?.addEventListener('click', () => {
      App.navigate('alerts', {});
    });

    el.querySelectorAll('[data-goto-patient]').forEach(row => {
      row.addEventListener('click', (e) => {
        if (!e.target.closest('button')) {
          App.navigate('patients', { patientId: row.dataset.gotoPatient });
        }
      });
    });

    el.querySelectorAll('button[data-patient]').forEach(btn => {
      btn.addEventListener('click', () => {
        App.navigate('patients', { patientId: btn.dataset.patient });
      });
    });
  }
};
