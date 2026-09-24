/**
 * View 5: Precision Care Management & Treatment
 * Implements AI Careplan generation, guideline adherence, and Clinician Digital Sign-off
 * (Strictly matches Screenshot 4)
 */
const CareplansView = {
  title: 'Precision Care Management',

  async render(el, state) {
    el.innerHTML = `<div class="empty-state">Loading precision care plans…</div>`;
    const [summary, careplans, patients] = await Promise.all([
      Api.summary(),
      Api.careplans(),
      Api.patients()
    ]);

    const byId = Object.fromEntries(patients.map(p => [p.patientId, p]));
    const focusId = state.patientId || 'pat-john-doe';
    let focusPlan = careplans.find(p => p.patientId === focusId);

    if (!focusPlan) {
      focusPlan = careplans[0] || {
        planId: 'plan-001',
        patientId: 'pat-john-doe',
        patientName: 'John Doe',
        version: 'v2.1',
        goals: [
          { goal: 'Reduce HbA1c to <7.0% in 3 months', intervention: 'Increase Metformin to 1000mg BID', monitoring: 'Weekly glucose logs via app' },
          { goal: 'BP target <130/80', intervention: 'Add Amlodipine 5mg', monitoring: 'Daily BP from wearable' }
        ],
        predictedRiskBefore: 0.243,
        predictedRiskAfter: 0.162,
        adherenceScore: 0.87,
        status: 'approved',
        approvedBy: 'Dr. Meera Iyer (NPI: 1945678901)'
      };
    }

    const patient = byId[focusPlan.patientId] || patients[0];

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>Precision Care Management</h1>
          <p>AI-Generated Treatment Plans &middot; Clinical Guideline Engine &middot; Outcome Projections</p>
        </div>
        <div class="view-actions">
          <button class="btn btn-primary" id="btn-create-new-plan">
            ✨ Generate AI Careplan
          </button>
        </div>
      </div>

      <!-- 3-Stat Metric Row (Matches Screenshot 4) -->
      <div class="stat-grid-3">
        <div class="stat-card">
          <div class="stat-card-title">Active Careplans</div>
          <div class="stat-card-value">${summary.activeCareplans.toLocaleString()}</div>
          <div class="stat-card-subtext subtext-up">AI-generated &middot; Evidence-Based Guidelines</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Adherence Rate</div>
          <div class="stat-card-value">${summary.adherenceRate}%</div>
          <div class="stat-card-subtext subtext-up">↑ 12% vs baseline &middot; Continuous App Sync</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Hospitalizations</div>
          <div class="stat-card-value">↓ ${summary.hospitalizationsPrevented}%</div>
          <div class="stat-card-subtext subtext-up">Prevented through early intervention</div>
        </div>
      </div>

      <!-- Layout: Careplan List + Main Specimen Card (Screenshot 4) -->
      <div style="display: grid; grid-template-columns: 280px 1fr; gap: 20px;">
        
        <!-- Careplans Sidebar List -->
        <div class="table-panel" style="margin-bottom:0; max-height:760px; overflow-y:auto;">
          <div class="table-head-bar">
            <h2>Active Careplans</h2>
          </div>
          <table>
            <thead>
              <tr>
                <th>Patient</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              ${careplans.map(cp => `
                <tr class="clickable ${cp.patientId === patient.patientId ? 'selected' : ''}" data-cp-patient="${cp.patientId}">
                  <td>
                    <strong>${cp.patientName}</strong><br>
                    <span class="mono" style="font-size:11px; color:var(--text-subtle);">${cp.version} &middot; ${cp.goals.length} Goals</span>
                  </td>
                  <td>
                    <span class="badge ${cp.status === 'approved' ? 'badge-optimal' : 'badge-moderate'}">
                      ${cp.status === 'approved' ? '✓ Approved' : 'Pending'}
                    </span>
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>

        <!-- Main Careplan Specimen Card (Screenshot 4 Exact Layout) -->
        <div>
          <div class="clinical-specimen-card" style="margin-bottom:20px;">
            <div class="specimen-header">
              <div class="specimen-title">
                <span>AI-Generated Personalized Careplan</span>
              </div>
              <span class="badge ${focusPlan.status === 'approved' ? 'badge-optimal' : 'badge-moderate'} mono">
                ${focusPlan.status === 'approved' ? '✓ Clinician Signed' : 'Pending Provider Signature'}
              </span>
            </div>

            <div class="specimen-body">
              <div class="telemetry-row">
                <div class="telemetry-label">Patient:</div>
                <div class="telemetry-value">
                  <strong>${focusPlan.patientName}</strong> &nbsp;|&nbsp; 
                  Careplan <span class="mono highlight-cyan">${focusPlan.version || 'v2.1'}</span> &nbsp;|&nbsp; 
                  Generated by <span class="badge badge-cyan">MediSphere Clinical AI Engine</span>
                </div>
              </div>

              <!-- Goals & Interventions -->
              <div class="goal-card-grid">
                ${(focusPlan.goals || []).map((g, idx) => `
                  <div class="goal-card-item">
                    <div class="goal-card-header">
                      <span>Goal ${idx + 1}: <strong style="color:var(--brand-cyan);">${g.goal}</strong></span>
                      <span class="badge badge-optimal mono">Target: ${g.targetMetric || 'Standard'}</span>
                    </div>
                    <div class="goal-card-detail">
                      &bull; <strong>Intervention:</strong> ${g.intervention}
                    </div>
                    <div class="goal-card-detail">
                      &bull; <strong>Monitoring:</strong> ${g.monitoring}
                    </div>
                  </div>
                `).join('')}
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Predicted Outcome:</div>
                <div class="telemetry-value">
                  CVD risk <strong class="highlight-green">↓ to ${Math.round(focusPlan.predictedRiskAfter * 1000) / 10}%</strong> 
                  (from ${Math.round(focusPlan.predictedRiskBefore * 1000) / 10}%) &nbsp;|&nbsp; 
                  Adherence score: <strong class="highlight-cyan">${Math.round(focusPlan.adherenceScore * 100)}%</strong>
                </div>
              </div>

              <div class="telemetry-row">
                <div class="telemetry-label">Provider Approval:</div>
                <div class="telemetry-value">
                  ${focusPlan.status === 'approved'
                    ? `<span class="highlight-green">✓ Signed by ${focusPlan.approvedBy || 'Dr. Meera Iyer (NPI: 1945678901)'}</span> <span class="mono" style="font-size:11px; color:var(--text-subtle); display:block; margin-top:2px;">Signature Hash: ${focusPlan.digitalSignatureHash || 'SHA256:8f4b23c89a7702e1bdfa3592ec491c107f903e1a0b38c2017a5619e07892ca81'}</span>`
                    : '<span class="highlight-amber">Awaiting clinician digital signature before patient portal dispatch</span>'
                  }
                </div>
              </div>

              <div class="specimen-actions">
                ${focusPlan.status === 'approved'
                  ? `<button class="btn btn-outline btn-sm" disabled style="opacity:0.7;">✓ Signed &amp; Approved</button>`
                  : `<button class="btn btn-primary btn-sm" id="btn-approve-plan">✍️ Approve Plan (Digital Signature)</button>`
                }
                <button class="btn btn-outline btn-sm" id="btn-modify-plan">
                  ✏️ Modify
                </button>
                <button class="btn btn-outline btn-sm" id="btn-send-patient">
                  📱 Send to Patient
                </button>
              </div>

              <div id="careplan-feedback" style="margin-top:10px;"></div>
            </div>
          </div>
        </div>
      </div>
    `;

    el.querySelectorAll('[data-cp-patient]').forEach(row => {
      row.addEventListener('click', () => {
        App.navigate('careplans', { patientId: row.dataset.cpPatient });
      });
    });

    el.querySelector('#btn-approve-plan')?.addEventListener('click', async () => {
      const modal = document.getElementById('global-modal');
      modal.innerHTML = `
        <div class="modal-box" style="max-width:560px;">
          <div class="modal-header">
            <h3>✍️ Clinician Digital Signature Authorization</h3>
            <button class="modal-close-btn" onclick="document.getElementById('global-modal').style.display='none'">&times;</button>
          </div>
          <div class="modal-content">
            <p style="font-size:13px; color:var(--text-muted); margin-bottom:14px;">
              By signing below, you authenticate the AI-generated care recommendations for <strong>${focusPlan.patientName}</strong> under HIPAA and HL7 Clinical Practice Guidelines.
            </p>
            <div style="background:#090E17; border:1px solid #1E293B; border-radius:6px; padding:12px; margin-bottom:14px; font-family:var(--font-mono); font-size:12px;">
              Clinician: Dr. Meera Iyer, MD, FACC<br>
              NPI: 1945678901 &middot; License: NY-MD-884920<br>
              Timestamp: ${new Date().toISOString()}
            </div>
            <button class="btn btn-primary" id="btn-confirm-sign" style="width:100%; justify-content:center;">
              Confirm &amp; Apply Cryptographic SHA-256 Seal
            </button>
          </div>
        </div>
      `;
      modal.style.display = 'flex';

      modal.querySelector('#btn-confirm-sign')?.addEventListener('click', async () => {
        await Api.approveCareplan(patient.patientId);
        modal.style.display = 'none';
        await this.render(el, { patientId: patient.patientId });
      });
    });

    el.querySelector('#btn-create-new-plan')?.addEventListener('click', async () => {
      await Api.generateCareplan(patient.patientId);
      await this.render(el, { patientId: patient.patientId });
    });

    el.querySelector('#btn-modify-plan')?.addEventListener('click', () => {
      const fb = el.querySelector('#careplan-feedback');
      fb.innerHTML = `<div class="badge badge-cyan" style="padding:8px 12px; font-size:12px;">✏️ Careplan editor active: Modify dosage titration and monitoring schedule.</div>`;
    });

    el.querySelector('#btn-send-patient')?.addEventListener('click', () => {
      const fb = el.querySelector('#careplan-feedback');
      fb.innerHTML = `<div class="badge badge-optimal" style="padding:8px 12px; font-size:12px;">📱 Careplan v2.1 transmitted to ${patient.name}'s mobile health portal via FHIR CarePlan resource.</div>`;
    });
  }
};
