/**
 * View 3: AI Risk Prediction Engine
 * Implements Federated Learning CVD & Diabetes models with SHAP Explainability Waterfall
 * (Strictly matches Screenshot 2)
 */
const PredictionsView = {
  title: 'AI Risk Prediction Engine',

  async render(el, state) {
    el.innerHTML = `<div class="empty-state">Loading TensorFlow Federated risk models…</div>`;
    const [summary, predictions, patients, flModel] = await Promise.all([
      Api.summary(),
      Api.predictions(),
      Api.patients(),
      Api.flModel()
    ]);

    const byId = Object.fromEntries(patients.map(p => [p.patientId, p]));
    const focusId = state.patientId || 'pat-john-doe';
    let focusPred = predictions.find(p => p.patientId === focusId);

    if (!focusPred) {
      focusPred = predictions[0] || {
        predId: 'pred-default',
        patientId: 'pat-john-doe',
        condition: 'Cardiovascular Risk (10-year)',
        model: 'CVD-Risk-v3.2',
        federatedRound: 47,
        probability: 0.243,
        category: 'High Risk',
        populationAvg: 0.121,
        relativeRisk: 2.01,
        contributions: [
          { factor: 'HbA1c (7.2%)', contribution: 0.08, detail: 'Glycemic dysregulation (+8.0%)' },
          { factor: 'Blood Pressure (130/85 mmHg)', contribution: 0.06, detail: 'Elevated systolic pressure (+6.0%)' },
          { factor: 'Age (58 yrs)', contribution: 0.05, detail: 'Vascular aging risk (+5.0%)' },
          { factor: 'LDL (120 mg/dL)', contribution: 0.03, detail: 'Atherogenic lipid fraction (+3.0%)' },
          { factor: 'Smoking Exposure', contribution: 0.023, detail: 'Current smoker (+2.3%)' }
        ],
        recommendation: ['Intensify statin therapy', 'Target BP <130/80 mmHg', 'Tighten glycemic control']
      };
    }

    const patient = byId[focusPred.patientId] || byId['pat-john-doe'] || patients[0];

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>AI Risk Prediction Engine</h1>
          <p>Privacy-Preserving Federated Learning &middot; SHAP Explainability &middot; Clinical Decision Support</p>
        </div>
        <div class="view-actions">
          <button class="btn btn-outline" id="btn-trigger-fl-round">
            🔄 Train Federated Round (TFF)
          </button>
        </div>
      </div>

      <!-- 3-Stat Metric Row (Matches Screenshot 2) -->
      <div class="stat-grid-3">
        <div class="stat-card">
          <div class="stat-card-title">Risk Predictions</div>
          <div class="stat-card-value">${summary.predictionsToday}</div>
          <div class="stat-card-subtext subtext-up">Today &middot; Active Cohort Surveillance</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Model Accuracy</div>
          <div class="stat-card-value">${summary.modelAccuracy}%</div>
          <div class="stat-card-subtext subtext-up">↑ 2.1% FL round ${summary.federatedRound}</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">High Risk Patients</div>
          <div class="stat-card-value">${summary.highRiskPatients}</div>
          <div class="stat-card-subtext subtext-alert">Require intervention</div>
        </div>
      </div>

      <!-- Layout: Prediction List + Main Specimen Card (Screenshot 2) -->
      <div style="display: grid; grid-template-columns: 280px 1fr; gap: 20px;">
        
        <!-- Predictions Sidebar Directory -->
        <div class="table-panel" style="margin-bottom:0; max-height:760px; overflow-y:auto;">
          <div class="table-head-bar">
            <h2>Evaluated Cohort</h2>
          </div>
          <table>
            <thead>
              <tr>
                <th>Patient</th>
                <th>10-Yr Risk</th>
              </tr>
            </thead>
            <tbody>
              ${patients.map(p => {
                const pred = predictions.find(x => x.patientId === p.patientId);
                const prob = pred ? Math.round(pred.probability * 100) : (p.patientId === 'pat-john-doe' ? 24 : 14);
                const isHigh = prob >= 20;
                return `
                  <tr class="clickable ${p.patientId === patient.patientId ? 'selected' : ''}" data-pred-patient="${p.patientId}">
                    <td>
                      <strong>${p.name}</strong><br>
                      <span class="mono" style="font-size:11px; color:var(--text-subtle);">${p.age}${p.sex} &middot; ${p.conditions[0] || 'Healthy'}</span>
                    </td>
                    <td>
                      <span class="badge ${isHigh ? 'badge-high' : 'badge-optimal'} mono">${prob}%</span>
                    </td>
                  </tr>
                `;
              }).join('')}
            </tbody>
          </table>
        </div>

        <!-- Main Risk Engine Specimen Card (Screenshot 2 Exact Layout) -->
        <div>
          <div class="clinical-specimen-card" style="margin-bottom:20px;">
            <div class="specimen-header">
              <div class="specimen-title">
                <span>TensorFlow Federated &mdash; Cardiovascular Risk Prediction</span>
              </div>
              <span class="badge badge-high mono">Federated Round ${focusPred.federatedRound || 47}</span>
            </div>

            <div class="specimen-body">
              <div class="telemetry-row">
                <div class="telemetry-label">Patient:</div>
                <div class="telemetry-value">
                  <strong>${patient.name}</strong> &nbsp;|&nbsp; 
                  Model: <span class="mono highlight-cyan">${focusPred.model || 'CVD-Risk-v3.2'}</span> &nbsp;|&nbsp; 
                  Federated Round: <span class="mono highlight-cyan">${focusPred.federatedRound || 47}</span>
                </div>
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Input Features:</div>
                <div class="telemetry-value">
                  Age (${patient.age}), BP (${patient.vitals?.bp || '130/85'}), HbA1c (${patient.labs?.hba1c || 7.2}%), 
                  LDL (${patient.labs?.ldl || 120}), eGFR (${patient.labs?.egfr || 65}), 
                  Smoking (${patient.smoking ? 'Yes' : 'No'}), FH (${patient.familyHistoryCVD ? 'Yes' : 'No'})
                </div>
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Prediction:</div>
                <div class="telemetry-value">
                  <span class="highlight-red" style="font-size:16px;">10-year CVD Risk: ${Math.round(focusPred.probability * 1000) / 10}%</span> &nbsp;|&nbsp; 
                  Category: <span class="badge badge-high">${focusPred.category || 'High Risk'}</span>
                </div>
              </div>

              <!-- SHAP Explanation Waterfall -->
              <div class="shap-container">
                <div class="shap-header">
                  SHAP Explanation (Feature Attribution Vector): 
                  <span style="color:#FFFFFF; font-weight:normal;">HbA1c (+8%), BP (+6%), Age (+5%)</span>
                </div>
                ${(focusPred.contributions || []).map(c => {
                  const pct = Math.min(100, Math.max(10, (c.contribution / 0.08) * 100));
                  return `
                    <div class="shap-row">
                      <div class="shap-row-header">
                        <span class="shap-factor-name">${c.factor}</span>
                        <span class="shap-factor-impact">${c.detail}</span>
                      </div>
                      <div class="shap-bar-track">
                        <div class="shap-bar-fill" style="width: ${pct}%;"></div>
                      </div>
                    </div>
                  `;
                }).join('')}
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Comparison:</div>
                <div class="telemetry-value">
                  Population avg <span class="mono highlight-cyan">12.1%</span> &nbsp;|&nbsp; 
                  Patient <strong class="highlight-red">${focusPred.relativeRisk || 2.0}x higher risk</strong> than age-matched cohort
                </div>
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Recommendation:</div>
                <div class="telemetry-value">
                  <strong>${(focusPred.recommendations || focusPred.recommendation || ['Intensify statin therapy', 'BP target <130/80 mmHg']).join(' &middot; ')}</strong>
                </div>
              </div>

              <div class="specimen-actions">
                <button class="btn btn-primary btn-sm" id="btn-pred-careplan">
                  🩺 Generate Careplan
                </button>
                <button class="btn btn-outline btn-sm" id="btn-alert-provider">
                  🔔 Alert Provider
                </button>
                <button class="btn btn-outline btn-sm" id="btn-schedule-followup">
                  📅 Schedule Follow-up
                </button>
              </div>

              <div id="pred-feedback" style="margin-top:10px;"></div>
            </div>
          </div>

          <!-- Interactive What-If Simulation Panel -->
          <div class="what-if-panel">
            <div class="what-if-title">
              <span>🔬 Interactive Clinician "What-If" Simulation Studio</span>
              <span class="badge badge-cyan" style="font-size:10.5px;">Live Local Inference</span>
            </div>
            <p style="font-size:12px; color:var(--text-muted); margin-bottom:14px;">
              Adjust biometric parameters in real time to simulate treatment responses and project risk reduction prior to modifying prescriptions.
            </p>

            <div class="slider-group">
              <div class="slider-item">
                <label>Systolic Blood Pressure: <span id="val-sys">${patient.vitals?.systolic || 130} mmHg</span></label>
                <input type="range" id="slider-sys" min="100" max="190" value="${patient.vitals?.systolic || 130}" step="2">
              </div>
              <div class="slider-item">
                <label>Glycated Hemoglobin (HbA1c): <span id="val-hba1c">${patient.labs?.hba1c || 7.2}%</span></label>
                <input type="range" id="slider-hba1c" min="5.0" max="11.0" value="${patient.labs?.hba1c || 7.2}" step="0.1">
              </div>
              <div class="slider-item">
                <label>LDL Cholesterol: <span id="val-ldl">${patient.labs?.ldl || 120} mg/dL</span></label>
                <input type="range" id="slider-ldl" min="50" max="220" value="${patient.labs?.ldl || 120}" step="5">
              </div>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; background:#090E17; border:1px solid #1E293B; border-radius:6px; padding:12px 18px;">
              <div>
                <span style="font-size:12px; color:var(--text-muted);">Simulated 10-Yr CVD Risk:</span>
                <div id="sim-risk-output" class="mono" style="font-size:22px; font-weight:700; color:#EF4444;">
                  ${Math.round(focusPred.probability * 1000) / 10}%
                </div>
              </div>
              <button class="btn btn-outline btn-sm" id="btn-reset-sliders">Reset to Measured Baseline</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const sysSlider = el.querySelector('#slider-sys');
    const hba1cSlider = el.querySelector('#slider-hba1c');
    const ldlSlider = el.querySelector('#slider-ldl');

    const updateWhatIf = async () => {
      const sys = parseInt(sysSlider.value);
      const hba1c = parseFloat(hba1cSlider.value);
      const ldl = parseInt(ldlSlider.value);

      el.querySelector('#val-sys').textContent = `${sys} mmHg`;
      el.querySelector('#val-hba1c').textContent = `${hba1c.toFixed(1)}%`;
      el.querySelector('#val-ldl').textContent = `${ldl} mg/dL`;

      const simRes = await Api.whatIf(patient.patientId, {
        age: patient.age,
        systolic: sys,
        hba1c: hba1c,
        ldl: ldl,
        smoking: patient.smoking
      });

      const outEl = el.querySelector('#sim-risk-output');
      const prob = Math.round(simRes.probability * 1000) / 10;
      outEl.textContent = `${prob}%`;
      outEl.style.color = prob >= 20 ? '#EF4444' : (prob >= 10 ? '#F59E0B' : '#10B981');
    };

    sysSlider?.addEventListener('input', updateWhatIf);
    hba1cSlider?.addEventListener('input', updateWhatIf);
    ldlSlider?.addEventListener('input', updateWhatIf);

    el.querySelector('#btn-reset-sliders')?.addEventListener('click', () => {
      sysSlider.value = patient.vitals?.systolic || 130;
      hba1cSlider.value = patient.labs?.hba1c || 7.2;
      ldlSlider.value = patient.labs?.ldl || 120;
      updateWhatIf();
    });

    el.querySelectorAll('[data-pred-patient]').forEach(row => {
      row.addEventListener('click', () => {
        App.navigate('predictions', { patientId: row.dataset.predPatient });
      });
    });

    el.querySelector('#btn-pred-careplan')?.addEventListener('click', async () => {
      await Api.generateCareplan(patient.patientId);
      App.navigate('careplans', { patientId: patient.patientId });
    });

    el.querySelector('#btn-alert-provider')?.addEventListener('click', () => {
      const fb = el.querySelector('#pred-feedback');
      fb.innerHTML = `<div class="badge badge-high" style="padding:8px 12px; font-size:12px;">🔔 High-risk clinical notification dispatched to ${patient.provider} via FHIR Communication Hook</div>`;
    });

    el.querySelector('#btn-schedule-followup')?.addEventListener('click', () => {
      const fb = el.querySelector('#pred-feedback');
      fb.innerHTML = `<div class="badge badge-optimal" style="padding:8px 12px; font-size:12px;">📅 Follow-up telehealth consultation scheduled for ${patient.name} with ${patient.provider} in 14 days</div>`;
    });

    el.querySelector('#btn-trigger-fl-round')?.addEventListener('click', async () => {
      const btn = el.querySelector('#btn-trigger-fl-round');
      btn.disabled = true;
      btn.textContent = 'Aggregating Model Gradients…';
      try {
        await Api.trainFLRound();
        await this.render(el, { patientId: patient.patientId });
      } finally {
        setTimeout(() => {
          if (btn) { btn.disabled = false; btn.textContent = '🔄 Train Federated Round (TFF)'; }
        }, 1000);
      }
    });
  }
};
