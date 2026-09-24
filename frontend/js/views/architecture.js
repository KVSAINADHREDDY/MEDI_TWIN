/**
 * View 7: 9-Layer Architecture & FHIR / HIPAA Governance
 * Visualizes the 9-Layer Cognitive Health Twin Architecture, Data Pipeline, and Security Vault
 */
const ArchitectureView = {
  title: 'System Architecture & HIPAA Governance',

  async render(el) {
    el.innerHTML = `<div class="empty-state">Loading system architecture &amp; audit governance logs…</div>`;
    const [summary, auditLogs, flModel, patients] = await Promise.all([
      Api.summary(),
      Api.auditLogs(),
      Api.flModel(),
      Api.patients()
    ]);

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>System Architecture &amp; HIPAA Governance</h1>
          <p>9-Layer Cognitive Health Twin Architecture &middot; SMART on FHIR Interoperability &middot; Immutable Audit Trail</p>
        </div>
        <div class="view-actions">
          <button class="btn btn-outline" id="btn-trigger-fl-test">
            🔄 Test Federated Aggregation
          </button>
        </div>
      </div>

      <!-- 9-Layer System Architecture Diagram (Exact match from Section 4) -->
      <div class="clinical-specimen-card">
        <div class="specimen-header">
          <div class="specimen-title">
            <span>MediSphere &mdash; 9-Layer Cognitive Health Twin Architecture</span>
          </div>
          <span class="badge badge-cyan mono">Production Multi-Tier Topology</span>
        </div>
        <div class="specimen-body">
          <div style="display:flex; flex-direction:column; gap:10px;">
            
            <div style="background:#132238; border:1px solid #1E6FD9; border-left:5px solid #1E6FD9; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">1. Presentation Layer</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">Angular 20 / Vanilla SPA, Patient Portal, Clinician Dashboard, WebGL / SVG 3D Body Models</span>
              </div>
              <span class="badge badge-optimal">Active UI</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #38BDF8; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">2. API Gateway &amp; Security Layer</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">Spring Cloud Gateway / Java REST Core, OAuth2, SMART on FHIR Authentication, HIPAA Audit Interceptors</span>
              </div>
              <span class="badge badge-cyan">Port 5050 &middot; Virtual Threads</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #10B981; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">3. Federated Learning Layer</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">TensorFlow Federated (TFF), Privacy-Preserving Distributed ML, On-Device &amp; Multi-Hospital Local Model Training</span>
              </div>
              <span class="badge badge-optimal mono">Round ${flModel.currentRound || 47} &middot; ${flModel.globalAccuracy || 91.4}% Acc</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #6366F1; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">4. Core Domain Services</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">PatientService, TwinService, PredictionService, AlertService, CareplanService</span>
              </div>
              <span class="badge badge-cyan">Java Enterprise Services</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #EC4899; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">5. AI &amp; Risk Intelligence Services</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">CVD 10-Yr Risk Prediction, Diabetes Complications, Disease Progression, Anomaly Detection, SHAP Explainability</span>
              </div>
              <span class="badge badge-high mono">CVD-Risk-v3.2</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #F59E0B; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">6. Data Storage Layer</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">MongoDB Document Store for Health Twins, Time-Series Vitals Engine, FHIR Resource Repository, HIPAA Vault Encrypted</span>
              </div>
              <span class="badge badge-cyan">Document + Time-Series</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #EF4444; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">7. Messaging &amp; Streaming Pipeline</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">Apache Kafka Event Streaming, High-Throughput Wearable Stream Ingestion, Real-Time Alert Dispatch</span>
              </div>
              <span class="badge badge-optimal mono">12K vitals/sec &middot; 0.8s lag</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #8B5CF6; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">8. Multi-Source Ingestion Layer</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">FHIR R4 APIs, Continuous Wearable Biosensors (Apple Watch, BioTelemetry), EHR Systems (Epic, Cerner), Lab Systems</span>
              </div>
              <span class="badge badge-cyan">2.4M FHIR Resources</span>
            </div>

            <div style="background:#132238; border:1px solid #1E2D4A; border-left:5px solid #14B8A6; border-radius:6px; padding:12px 16px; display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:#FFFFFF; font-size:14px;">9. Observability &amp; Clinical Governance</strong><br>
                <span style="font-size:12px; color:var(--text-muted);">Prometheus, Grafana, Clinical KPIs, Model Drift Detection, Consent Management, Immutable HIPAA Audit Trail</span>
              </div>
              <span class="badge badge-optimal">100% Audit Compliance</span>
            </div>

          </div>
        </div>
      </div>

      <!-- Federated Multi-Hospital Topology -->
      <div class="clinical-specimen-card">
        <div class="specimen-header">
          <div class="specimen-title">
            <span>🌐 Federated Learning Hospital Nodes (TensorFlow Federated)</span>
          </div>
          <span class="badge badge-optimal">Zero Raw PHI Centralization</span>
        </div>
        <div class="specimen-body">
          <div style="display:grid; grid-template-columns: repeat(3, 1fr); gap: 16px;">
            ${(flModel.participatingNodes || []).map(n => `
              <div style="background:#090E17; border:1px solid #1E293B; border-radius:6px; padding:14px;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                  <strong style="font-size:13.5px; color:#FFFFFF;">${n.hospitalName}</strong>
                  <span class="badge badge-optimal" style="font-size:10.5px;">${n.status}</span>
                </div>
                <div class="telemetry-row" style="padding:4px 0;">
                  <div class="telemetry-label" style="width:120px; font-size:11.5px;">Local Records:</div>
                  <div class="telemetry-value mono" style="font-size:12px;">${n.localRecords.toLocaleString()}</div>
                </div>
                <div class="telemetry-row" style="padding:4px 0;">
                  <div class="telemetry-label" style="width:120px; font-size:11.5px;">Local Loss:</div>
                  <div class="telemetry-value mono highlight-green" style="font-size:12px;">${n.localLoss}</div>
                </div>
                <div class="telemetry-row" style="padding:4px 0;">
                  <div class="telemetry-label" style="width:120px; font-size:11.5px;">Privacy Epsilon (&epsilon;):</div>
                  <div class="telemetry-value mono highlight-cyan" style="font-size:12px;">${n.epsilon}</div>
                </div>
              </div>
            `).join('')}
          </div>
        </div>
      </div>

      <!-- Immutable HIPAA Audit Log Console -->
      <div class="table-panel">
        <div class="table-head-bar">
          <h2>🔒 HIPAA Audit Trail &amp; Cryptographic Integrity Log</h2>
          <span class="badge badge-cyan">${auditLogs.length} Logged Events</span>
        </div>
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Actor</th>
              <th>Role</th>
              <th>Action</th>
              <th>Resource Type</th>
              <th>Outcome</th>
              <th>Integrity Hash (SHA-256)</th>
            </tr>
          </thead>
          <tbody>
            ${auditLogs.slice(0, 12).map(l => `
              <tr>
                <td class="mono" style="font-size:11px;">${new Date(l.timestamp).toLocaleTimeString()}</td>
                <td><strong>${l.actor}</strong></td>
                <td><span class="badge badge-cyan" style="font-size:10px;">${l.role}</span></td>
                <td class="mono" style="font-size:11.5px;">${l.action}</td>
                <td>${l.resourceType}/${l.resourceId}</td>
                <td><span class="badge badge-optimal" style="font-size:10px;">${l.outcome}</span></td>
                <td class="mono" style="font-size:10.5px; color:var(--text-subtle); max-width:200px; overflow:hidden; text-overflow:ellipsis;">${l.integrityHash}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;

    el.querySelector('#btn-trigger-fl-test')?.addEventListener('click', async () => {
      const btn = el.querySelector('#btn-trigger-fl-test');
      btn.disabled = true;
      btn.textContent = 'Aggregating Gradients…';
      try {
        await Api.trainFLRound();
        await this.render(el);
      } finally {
        setTimeout(() => {
          if (btn) { btn.disabled = false; btn.textContent = '🔄 Test Federated Aggregation'; }
        }, 1000);
      }
    });
  }
};
