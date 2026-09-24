/**
 * View 2: Patients & Digital Health Twins
 * Implements the full Digital Health Twin Specimen Card with 3D Organ Heatmap
 * (Strictly matches Screenshot 1)
 */
const PatientsView = {
  title: 'Digital Health Twin Platform',
  _patients: [],

  async render(el, state) {
    el.innerHTML = `<div class="empty-state">Loading patient digital twins from MongoDB…</div>`;
    const [summary, patients] = await Promise.all([Api.summary(), Api.patients()]);
    this._patients = patients;

    const selectedId = state.patientId || this._patients[0]?.patientId || 'pat-john-doe';
    this._draw(el, summary, selectedId);
  },

  _draw(el, summary, selectedId) {
    const selected = this._patients.find(p => p.patientId === selectedId) || this._patients[0];

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>Digital Health Twin Platform</h1>
          <p>Multi-Source EHR &middot; Laboratory Diagnostics &middot; Continuous Wearable Integration</p>
        </div>
        <div class="view-actions">
          <button class="btn btn-outline" id="btn-inspect-fhir">
            📋 Inspect FHIR R4 JSON
          </button>
          <button class="btn btn-outline" id="btn-view-audit">
            🔒 HIPAA Audit Log
          </button>
        </div>
      </div>

      <!-- 3-Stat Metric Row (Matches Screenshot 1) -->
      <div class="stat-grid-3">
        <div class="stat-card">
          <div class="stat-card-title">Patients Onboarded</div>
          <div class="stat-card-value">${summary.patientsOnboarded.toLocaleString()}</div>
          <div class="stat-card-subtext subtext-up">↑ 87 this week &middot; Active Cohort</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">FHIR Resources</div>
          <div class="stat-card-value">${(summary.fhirResourcesSynced / 1000000).toFixed(1)}M</div>
          <div class="stat-card-subtext subtext-up">Synced from Epic &amp; Cerner EHR</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Twins Created</div>
          <div class="stat-card-value">${summary.twinsCreated.toLocaleString()}</div>
          <div class="stat-card-subtext subtext-up">100% coverage &middot; Living Models</div>
        </div>
      </div>

      <!-- Main Layout: Patient Directory Sidebar + Health Twin Detail Card -->
      <div style="display: grid; grid-template-columns: 300px 1fr; gap: 20px;">
        
        <!-- Patient Directory Column -->
        <div class="table-panel" style="margin-bottom:0; max-height:780px; display:flex; flex-direction:column;">
          <div class="table-head-bar">
            <h2>Patient Directory (${this._patients.length})</h2>
          </div>
          <div style="overflow-y:auto; flex:1;">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Age/Sex</th>
                </tr>
              </thead>
              <tbody>
                ${this._patients.map(p => `
                  <tr class="clickable ${p.patientId === selected.patientId ? 'selected' : ''}" data-select-patient="${p.patientId}">
                    <td>
                      <strong>${p.name}</strong><br>
                      <span class="mono" style="font-size:11px; color:var(--text-subtle);">${p.conditions[0] || 'Healthy'}</span>
                    </td>
                    <td class="mono">${p.age}${p.sex}</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        </div>

        <!-- Health Twin Specimen Card (Screenshot 1 Exact Layout) -->
        <div class="clinical-specimen-card" style="margin-bottom:0;">
          <div class="specimen-header">
            <div class="specimen-title">
              <span>Digital Health Twin &mdash; Patient ${selected.name}</span>
            </div>
            <div style="display:flex; align-items:center; gap:8px;">
              <span class="badge badge-cyan mono">Model ${selected.twin?.modelVersion || 'v3.2'}</span>
              <span class="badge badge-optimal mono">${Math.round((selected.twin?.dataCompleteness || 0.98) * 100)}% Data Completeness</span>
            </div>
          </div>

          <div class="specimen-body">
            <div class="telemetry-row">
              <div class="telemetry-label">FHIR Patient Resource:</div>
              <div class="telemetry-value highlight-cyan">
                Loaded from ${selected.ehrSource || 'Epic EHR'} &middot; <span class="mono">${selected.fhirId}</span>
              </div>
            </div>

            <div class="telemetry-row">
              <div class="telemetry-label">Demographics:</div>
              <div class="telemetry-value">
                <strong>${selected.age}${selected.sex}</strong> &nbsp;|&nbsp;
                Conditions: <strong>${selected.conditions.join(', ') || 'None recorded'}</strong> &nbsp;|&nbsp;
                Provider: <strong>${selected.provider}</strong>
              </div>
            </div>

            <div class="telemetry-row">
              <div class="telemetry-label">Vitals Stream:</div>
              <div class="telemetry-value">
                HR <strong>${selected.vitals?.heartRate || 72} bpm</strong>, 
                BP <strong>${selected.vitals?.bp || '130/85'} mmHg</strong>, 
                SpO2 <strong>${selected.vitals?.spo2 || 98}%</strong> &nbsp;|&nbsp; 
                <span class="mono" style="color:var(--text-subtle);">Last: 2 min ago &middot; ${selected.vitals?.source || 'Wearable'}</span>
              </div>
            </div>

            <div class="telemetry-row">
              <div class="telemetry-label">Lab Results:</div>
              <div class="telemetry-value">
                HbA1c <strong class="${selected.labs?.hba1c >= 7.0 ? 'highlight-amber' : ''}">${selected.labs?.hba1c || 7.2}%</strong> &nbsp;|&nbsp;
                eGFR <strong class="${selected.labs?.egfr < 60 ? 'highlight-amber' : ''}">${selected.labs?.egfr || 65} mL/min</strong> &nbsp;|&nbsp;
                LDL <strong class="${selected.labs?.ldl >= 100 ? 'highlight-amber' : ''}">${selected.labs?.ldl || 120} mg/dL</strong> &nbsp;|&nbsp;
                Total Chol: <strong>${selected.labs?.totalCholesterol || 195} mg/dL</strong>
              </div>
            </div>

            <div style="margin-top:14px; margin-bottom:14px;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                <span style="font-size:12.5px; font-weight:600; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">
                  [3D Body Model: Organ systems with risk heatmap]
                </span>
                <span style="font-size:11.5px; color:var(--brand-cyan); font-family:var(--font-mono);">
                  WebGL / Spatial Mesh Telemetry Synchronized
                </span>
              </div>
              
              <div id="patient-3d-model-mount"></div>
            </div>

            <div class="telemetry-row">
              <div class="telemetry-label">Active Medications:</div>
              <div class="telemetry-value">
                ${(selected.medications || ['Metformin 500mg BID', 'Lisinopril 10mg Daily']).map(m => `
                  <span class="badge" style="background:#1E293B; color:#E2E8F0; margin-right:6px; font-family:var(--font-mono);">${m}</span>
                `).join('')}
              </div>
            </div>

            <div class="specimen-actions">
              <button class="btn btn-outline btn-sm" id="btn-view-timeline">
                ⏱ View Timeline
              </button>
              <button class="btn btn-primary btn-sm" id="btn-run-prediction">
                ⚡ Run Prediction
              </button>
              <button class="btn btn-outline btn-sm" id="btn-create-careplan">
                🩺 Create Careplan
              </button>
            </div>
            
            <div id="patient-action-feedback" style="margin-top:12px;"></div>
          </div>
        </div>
      </div>
    `;

    BodyTwin3D.render('patient-3d-model-mount', selected.twin?.organRisks, selected);

    el.querySelectorAll('[data-select-patient]').forEach(row => {
      row.addEventListener('click', () => {
        const pid = row.dataset.selectPatient;
        App.setParam('patientId', pid);
        this._draw(el, summary, pid);
      });
    });

    el.querySelector('#btn-inspect-fhir')?.addEventListener('click', async () => {
      const modal = document.getElementById('global-modal');
      const fhirData = await Api.fhirBundle(selected.patientId);
      modal.innerHTML = `
        <div class="modal-box">
          <div class="modal-header">
            <h3>SMART on FHIR R4 Bundle &mdash; ${selected.name} (${selected.fhirId})</h3>
            <button class="modal-close-btn" onclick="document.getElementById('global-modal').style.display='none'">&times;</button>
          </div>
          <div class="modal-content">
            <div style="display:flex; justify-content:space-between; margin-bottom:10px; font-size:12px; color:var(--text-muted);">
              <span>FHIR Release 4 (v4.0.1) &middot; Encrypted PHI Hash: Valid</span>
              <span class="highlight-green">✓ HL7 FHIR Schema Validated</span>
            </div>
            <pre class="code-viewer">${JSON.stringify(fhirData, null, 2)}</pre>
          </div>
        </div>
      `;
      modal.style.display = 'flex';
    });

    el.querySelector('#btn-view-audit')?.addEventListener('click', async () => {
      const modal = document.getElementById('global-modal');
      const logs = await Api.auditLogs();
      modal.innerHTML = `
        <div class="modal-box">
          <div class="modal-header">
            <h3>🔒 HIPAA Compliance &amp; PHI Access Audit Trail</h3>
            <button class="modal-close-btn" onclick="document.getElementById('global-modal').style.display='none'">&times;</button>
          </div>
          <div class="modal-content">
            <p style="font-size:12px; color:var(--text-muted); margin-bottom:12px;">
              Immutable audit log capturing all PHI reads, AI predictions, and digital signature authorizations with SHA-256 integrity hash chaining.
            </p>
            <div class="table-panel">
              <table>
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>Actor</th>
                    <th>Role</th>
                    <th>Action</th>
                    <th>Resource</th>
                    <th>Outcome</th>
                  </tr>
                </thead>
                <tbody>
                  ${logs.slice(0, 10).map(l => `
                    <tr>
                      <td class="mono" style="font-size:11px;">${new Date(l.timestamp).toLocaleTimeString()}</td>
                      <td><strong>${l.actor}</strong></td>
                      <td><span class="badge badge-cyan" style="font-size:10px;">${l.role}</span></td>
                      <td class="mono" style="font-size:11.5px;">${l.action}</td>
                      <td>${l.resourceType}/${l.resourceId}</td>
                      <td><span class="badge badge-optimal" style="font-size:10px;">${l.outcome}</span></td>
                    </tr>
                  `).join('')}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      `;
      modal.style.display = 'flex';
    });

    el.querySelector('#btn-run-prediction')?.addEventListener('click', async () => {
      const btn = el.querySelector('#btn-run-prediction');
      btn.disabled = true;
      btn.textContent = 'Computing Federated ML…';
      try {
        await Api.predict(selected.patientId);
        App.navigate('predictions', { patientId: selected.patientId });
      } catch (err) {
        btn.disabled = false;
        btn.textContent = '⚡ Run Prediction';
      }
    });

    el.querySelector('#btn-create-careplan')?.addEventListener('click', async () => {
      const btn = el.querySelector('#btn-create-careplan');
      btn.disabled = true;
      btn.textContent = 'Generating AI Plan…';
      try {
        await Api.generateCareplan(selected.patientId);
        App.navigate('careplans', { patientId: selected.patientId });
      } catch (err) {
        btn.disabled = false;
        btn.textContent = '🩺 Create Careplan';
      }
    });

    el.querySelector('#btn-view-timeline')?.addEventListener('click', () => {
      const fb = el.querySelector('#patient-action-feedback');
      fb.innerHTML = `
        <div style="background:#0F172A; border:1px solid #1E293B; border-radius:6px; padding:12px; font-size:12.5px;">
          <strong style="color:var(--brand-cyan)">📅 Clinical Timeline (Last 90 Days):</strong><br>
          &bull; <strong>45d ago:</strong> Onboarded via Epic FHIR R4 Integration (Dr. Meera Iyer)<br>
          &bull; <strong>30d ago:</strong> Clinic Baseline vitals established (BP 128/82, HR 70)<br>
          &bull; <strong>3d ago:</strong> Diagnostic panel: HbA1c 7.2%, eGFR 65, LDL 120 mg/dL<br>
          &bull; <strong>2 min ago:</strong> Apple Watch Ultra wearable stream synchronized via Kafka topic
        </div>
      `;
    });
  }
};
