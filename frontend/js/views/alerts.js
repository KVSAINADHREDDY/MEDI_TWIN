/**
 * View 4: Real-Time Health Surveillance & Alerts
 * Live Kafka Telemetry Ingestion, Dynamic ECG Waveform Oscilloscope, and Anomaly Triage
 * (Strictly matches Screenshot 3)
 */
const AlertsView = {
  title: 'Real-time Health Surveillance',

  async render(el) {
    el.innerHTML = `<div class="empty-state">Subscribing to Apache Kafka wearable telemetry streams…</div>`;
    const [summary, alerts, patients] = await Promise.all([
      Api.summary(),
      Api.alerts(),
      Api.patients()
    ]);

    const criticalCount = alerts.filter(a => a.severity === 'critical').length || 12;
    const sarahAlert = alerts.find(a => a.patientName?.includes('Sarah') || a.patientId === 'pat-sarah-m') || alerts[0];

    el.innerHTML = `
      <div class="view-header">
        <div class="view-title-group">
          <h1>Real-time Health Surveillance</h1>
          <p>Continuous Wearable Telemetry &middot; Kafka Event Streaming &middot; Automated Anomaly Triage</p>
        </div>
        <div class="view-actions">
          <button id="alert-stream-btn" class="btn btn-stream">
            ⚡ Advance Stream / Trigger Anomaly
          </button>
        </div>
      </div>

      <!-- 3-Stat Metric Row (Matches Screenshot 3) -->
      <div class="stat-grid-3">
        <div class="stat-card">
          <div class="stat-card-title">Alerts Today</div>
          <div class="stat-card-value">${summary.alertsToday}</div>
          <div class="stat-card-subtext subtext-alert">${criticalCount} Critical &middot; Anomaly Detection Active</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Wearables Online</div>
          <div class="stat-card-value">${summary.wearablesOnline}</div>
          <div class="stat-card-subtext subtext-up">98.3% uptime &middot; Apache Kafka Stream</div>
        </div>

        <div class="stat-card">
          <div class="stat-card-title">Avg Response</div>
          <div class="stat-card-value">${summary.avgResponseTimeMinutes || '3.2'} min</div>
          <div class="stat-card-subtext subtext-up">↓ 67% vs manual physician review</div>
        </div>
      </div>

      <!-- Main Specimen Card (Screenshot 3 Exact Layout) -->
      <div class="clinical-specimen-card">
        <div class="specimen-header">
          <div class="specimen-title">
            <span>Real-time Monitoring &mdash; Kafka Streams + Anomaly Detection</span>
          </div>
          <span class="badge badge-high mono">CRITICAL TELEMETRY SPIKE</span>
        </div>

        <div class="specimen-body">
          <div class="telemetry-row" style="background: rgba(239, 68, 68, 0.08); padding: 12px; border-radius: 6px; border: 1px solid rgba(239, 68, 68, 0.3);">
            <div class="telemetry-label" style="color:#EF4444; font-weight:700; width:100px;">ALERT:</div>
            <div class="telemetry-value highlight-red" style="font-size:15px;">
              Patient Sarah M. &nbsp;|&nbsp; HR spike 145 bpm &nbsp;|&nbsp; Time: 14:23 (Kafka Stream)
            </div>
          </div>

          <div class="telemetry-row">
            <div class="telemetry-label">Context:</div>
            <div class="telemetry-value">
              At rest &nbsp;|&nbsp; No exercise &nbsp;|&nbsp; Previous avg: <strong>68 bpm</strong> (Normal Sinus Baseline)
            </div>
          </div>

          <div class="telemetry-row">
            <div class="telemetry-label">AI Analysis:</div>
            <div class="telemetry-value">
              <strong class="highlight-red">Possible atrial fibrillation</strong> &nbsp;|&nbsp; Confidence: <strong class="highlight-cyan">89%</strong>
            </div>
          </div>

          <div class="telemetry-row">
            <div class="telemetry-label">Auto-actions:</div>
            <div class="telemetry-value">
              <span class="badge badge-cyan">Notified cardiologist</span> &nbsp;
              <span class="badge badge-cyan">Scheduled ECG</span> &nbsp;
              <span class="badge badge-optimal">Encrypted Audit Log Sealed</span>
            </div>
          </div>

          <div class="telemetry-row">
            <div class="telemetry-label">Kafka Stream:</div>
            <div class="telemetry-value">
              <span class="mono highlight-green">12K vitals/sec</span> &nbsp;|&nbsp; Lag: <span class="mono highlight-green">0.8s</span> &nbsp;|&nbsp; Topic: <span class="mono" style="color:var(--text-subtle);">vitals.wearables.stream</span>
            </div>
          </div>

          <div class="telemetry-row">
            <div class="telemetry-label">Twin Update:</div>
            <div class="telemetry-value">
              Arrhythmia risk increased to <strong class="highlight-red">34%</strong> (Living model synchronized)
            </div>
          </div>

          <!-- Live Oscilloscope Cardiac Rhythm Monitor -->
          <div style="margin: 18px 0; background: #060B13; border: 1px solid var(--border-color); border-radius: var(--radius); padding: 14px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
              <div style="display:flex; align-items:center; gap:8px;">
                <span class="pulse-dot" style="background:#EF4444; box-shadow:0 0 8px #EF4444;"></span>
                <strong style="color:#EF4444; font-size:12.5px; text-transform:uppercase; letter-spacing:0.5px;">
                  Live Telemetry Waveform: Lead II ECG &middot; Sarah M. (145 BPM - Irregular Rhythm)
                </strong>
              </div>
              <span class="mono highlight-cyan" style="font-size:11.5px;">Sampling: 500 Hz &middot; Sweep: 25mm/s</span>
            </div>
            
            <canvas id="live-ecg-canvas" style="width:100%; height:130px; display:block; border-radius:4px;"></canvas>
          </div>

          <div class="specimen-actions">
            <button class="btn btn-primary btn-sm" id="btn-ack-sarah">
              ✓ Acknowledge
            </button>
            <button class="btn btn-outline btn-sm" id="btn-escalate">
              🚨 Escalate to CCU
            </button>
            <button class="btn btn-outline btn-sm" id="btn-view-ecg-full">
              📈 View ECG
            </button>
            <button class="btn btn-outline btn-sm" id="btn-contact-patient">
              📞 Contact Patient
            </button>
          </div>

          <div id="alert-action-feedback" style="margin-top:10px;"></div>
        </div>
      </div>

      <!-- Real-Time Anomaly Feed Table -->
      <div class="table-panel">
        <div class="table-head-bar">
          <h2>Continuous Clinical Anomaly Queue (Apache Kafka Event Bus)</h2>
          <span class="badge badge-cyan">${alerts.length} Ingested Events</span>
        </div>
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Patient</th>
              <th>Alert Condition</th>
              <th>Clinical Context</th>
              <th>AI Confidence</th>
              <th>Automated Action</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            ${alerts.map(a => `
              <tr>
                <td class="mono" style="font-size:11.5px;">${new Date(a.ts).toLocaleTimeString()}</td>
                <td><strong>${a.patientName}</strong></td>
                <td>
                  <span class="badge badge-${a.severity === 'critical' ? 'high' : 'moderate'}">${a.title}</span>
                </td>
                <td class="mono" style="font-size:12px; max-width:240px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                  ${a.context}
                </td>
                <td class="mono highlight-cyan">${Math.round(a.confidence * 100)}%</td>
                <td style="font-size:12px; color:var(--text-muted);">${a.autoAction}</td>
                <td>
                  ${a.acknowledged
                    ? '<span class="badge badge-optimal">✓ Acknowledged</span>'
                    : `<button class="btn btn-outline btn-sm" data-ack-id="${a.alertId}">Acknowledge</button>`
                  }
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;

    setTimeout(() => {
      EcgMonitor.init('live-ecg-canvas', 'Atrial Fibrillation', 145);
    }, 50);

    el.querySelector('#alert-stream-btn')?.addEventListener('click', async () => {
      const btn = el.querySelector('#alert-stream-btn');
      btn.disabled = true;
      btn.textContent = 'Kafka Batch Ingesting…';
      try {
        await Api.simulateTick();
        await this.render(el);
      } finally {
        setTimeout(() => {
          if (btn) { btn.disabled = false; btn.textContent = '⚡ Advance Stream / Trigger Anomaly'; }
        }, 1200);
      }
    });

    el.querySelector('#btn-ack-sarah')?.addEventListener('click', async () => {
      if (sarahAlert) {
        await Api.acknowledgeAlert(sarahAlert.alertId);
        const fb = el.querySelector('#alert-action-feedback');
        fb.innerHTML = `<div class="badge badge-optimal" style="padding:8px 12px; font-size:12px;">✓ Alert acknowledged by Dr. Meera Iyer. Care team notified.</div>`;
      }
    });

    el.querySelector('#btn-escalate')?.addEventListener('click', () => {
      const fb = el.querySelector('#alert-action-feedback');
      fb.innerHTML = `<div class="badge badge-high" style="padding:8px 12px; font-size:12px;">🚨 Telemetry packet escalated to Cardiac Care Unit (CCU) Rapid Response Team.</div>`;
    });

    el.querySelector('#btn-view-ecg-full')?.addEventListener('click', () => {
      const modal = document.getElementById('global-modal');
      modal.innerHTML = `
        <div class="modal-box">
          <div class="modal-header">
            <h3>Diagnostic 12-Lead Rhythm Strip &mdash; Sarah M. (BioTelemetry Stream)</h3>
            <button class="modal-close-btn" onclick="document.getElementById('global-modal').style.display='none'">&times;</button>
          </div>
          <div class="modal-content">
            <div style="background:#060B13; border:1px solid #1E293B; border-radius:6px; padding:16px; margin-bottom:14px;">
              <canvas id="modal-ecg-canvas" style="width:100%; height:180px; display:block;"></canvas>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Interpretation:</div>
              <div class="telemetry-value highlight-red">Rapid Atrial Fibrillation with variable AV conduction (Ventricular Rate: 145-152 bpm)</div>
            </div>
            <div class="telemetry-row">
              <div class="telemetry-label">Prior Rhythm:</div>
              <div class="telemetry-value">Normal Sinus Rhythm (68 bpm) recorded 18 min prior</div>
            </div>
          </div>
        </div>
      `;
      modal.style.display = 'flex';
      setTimeout(() => {
        EcgMonitor.init('modal-ecg-canvas', 'Atrial Fibrillation', 145);
      }, 50);
    });

    el.querySelector('#btn-contact-patient')?.addEventListener('click', () => {
      const fb = el.querySelector('#alert-action-feedback');
      fb.innerHTML = `<div class="badge badge-cyan" style="padding:8px 12px; font-size:12px;">📞 Secure telehealth video channel initiated with patient Sarah M.</div>`;
    });

    el.querySelectorAll('[data-ack-id]').forEach(btn => {
      btn.addEventListener('click', async () => {
        btn.disabled = true;
        await Api.acknowledgeAlert(btn.dataset.ackId);
        await this.render(el);
      });
    });
  }
};
