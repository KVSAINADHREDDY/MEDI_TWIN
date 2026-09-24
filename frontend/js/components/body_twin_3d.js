/**
 * Interactive Anatomical Digital Twin Organ Risk Model (WebGL / SVG Hybrid)
 * Renders anatomical organ nodes with real-time biometric risk heatmap
 */
const BodyTwin3D = {
  render(containerId, organRisks, patient) {
    const el = document.getElementById(containerId);
    if (!el) return;

    const risks = organRisks || {
      cardiovascular: 0.243,
      endocrine: 0.32,
      renal: 0.18,
      vascular: 0.28,
      respiratory: 0.05,
      cerebral: 0.12
    };

    const getRiskColor = (val) => {
      if (val >= 0.20) return { fill: '#EF4444', glow: 'rgba(239, 68, 68, 0.6)', label: 'High Risk' };
      if (val >= 0.10) return { fill: '#F59E0B', glow: 'rgba(245, 158, 11, 0.5)', label: 'Elevated' };
      return { fill: '#10B981', glow: 'rgba(16, 185, 129, 0.4)', label: 'Optimal' };
    };

    const heart = getRiskColor(risks.cardiovascular || 0.05);
    const pancreas = getRiskColor(risks.endocrine || 0.05);
    const kidneys = getRiskColor(risks.renal || 0.05);
    const lungs = getRiskColor(risks.respiratory || 0.05);
    const brain = getRiskColor(risks.cerebral || 0.05);
    const vascular = getRiskColor(risks.vascular || 0.05);

    el.innerHTML = `
      <div class="twin-visualizer-container">
        <div class="twin-svg-wrapper">
          <svg viewBox="0 0 320 480" class="twin-body-svg">
            <defs>
              <filter id="glow-heart" x="-50%" y="-50%" width="200%" height="200%">
                <feGaussianBlur stdDeviation="6" result="blur" />
                <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
              </filter>
              <filter id="glow-pancreas" x="-50%" y="-50%" width="200%" height="200%">
                <feGaussianBlur stdDeviation="5" result="blur" />
                <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
              </filter>
              <filter id="glow-kidneys" x="-50%" y="-50%" width="200%" height="200%">
                <feGaussianBlur stdDeviation="5" result="blur" />
                <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
              </filter>
              <filter id="glow-brain" x="-50%" y="-50%" width="200%" height="200%">
                <feGaussianBlur stdDeviation="5" result="blur" />
                <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
              </filter>
              <linearGradient id="body-grad" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#1E293B" stop-opacity="0.8" />
                <stop offset="100%" stop-color="#0F172A" stop-opacity="0.9" />
              </linearGradient>
            </defs>

            <g class="holo-grid" opacity="0.15">
              <line x1="20" y1="80" x2="300" y2="80" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="20" y1="160" x2="300" y2="160" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="20" y1="240" x2="300" y2="240" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="20" y1="320" x2="300" y2="320" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="20" y1="400" x2="300" y2="400" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="80" y1="20" x2="80" y2="460" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="160" y1="20" x2="160" y2="460" stroke="#38BDF8" stroke-dasharray="2,4"/>
              <line x1="240" y1="20" x2="240" y2="460" stroke="#38BDF8" stroke-dasharray="2,4"/>
            </g>

            <g class="silhouette" fill="url(#body-grad)" stroke="#334155" stroke-width="1.5">
              <ellipse cx="160" cy="55" rx="32" ry="38" />
              <path d="M148,90 L148,110 L172,110 L172,90 Z" />
              <path d="M120,115 C100,125 80,150 85,210 C90,260 110,290 125,310 L195,310 C210,290 230,260 235,210 C240,150 220,125 200,115 Z" />
              <path d="M85,130 C70,160 55,220 50,280 C48,295 58,300 65,290 C75,240 90,180 100,150 Z" />
              <path d="M235,130 C250,160 265,220 270,280 C272,295 262,300 255,290 C245,240 230,180 220,150 Z" />
              <path d="M125,310 C120,360 115,410 110,460 L140,460 C145,410 150,360 155,310 Z" />
              <path d="M165,310 C170,360 175,410 180,460 L210,460 C205,410 200,360 195,310 Z" />
            </g>

            <g class="vascular-mesh" stroke="${vascular.fill}" stroke-width="1" opacity="0.5">
              <path d="M160,140 Q145,170 140,210 Q135,270 130,350" fill="none"/>
              <path d="M160,140 Q175,170 180,210 Q185,270 190,350" fill="none"/>
              <path d="M160,140 L160,80" fill="none"/>
            </g>

            <!-- Brain (CNS) -->
            <g class="organ-node pulse-slow" data-organ="brain" style="cursor:pointer;" transform="translate(160, 52)">
              <circle r="16" fill="${brain.glow}" filter="url(#glow-brain)" opacity="0.8"/>
              <circle r="10" fill="${brain.fill}" stroke="#fff" stroke-width="1.5"/>
              <text x="0" y="3" text-anchor="middle" font-size="8" fill="#fff" font-weight="bold">CNS</text>
            </g>

            <!-- Lungs (Respiratory) -->
            <g class="organ-node" data-organ="respiratory" style="cursor:pointer;">
              <ellipse cx="132" cy="165" rx="14" ry="24" fill="${lungs.fill}" opacity="0.75" />
              <ellipse cx="188" cy="165" rx="14" ry="24" fill="${lungs.fill}" opacity="0.75" />
            </g>

            <!-- Heart (Cardiovascular) -->
            <g class="organ-node pulse-cardiac" data-organ="cardiovascular" style="cursor:pointer;" transform="translate(150, 168)">
              <circle r="22" fill="${heart.glow}" filter="url(#glow-heart)" />
              <path d="M0,8 C-12,-4 -14,-14 -4,-18 C2,-20 8,-14 0,-4 C-8,-14 -2,-20 4,-18 C14,-14 12,-4 0,8 Z"
                    fill="${heart.fill}" stroke="#FFFFFF" stroke-width="1.5" transform="scale(1.2) translate(0,-2)"/>
              <circle r="4" fill="#FFFFFF" class="heartbeat-dot"/>
            </g>

            <!-- Pancreas (Endocrine) -->
            <g class="organ-node" data-organ="endocrine" style="cursor:pointer;" transform="translate(165, 218)">
              <rect x="-18" y="-7" width="36" height="14" rx="7" fill="${pancreas.fill}" filter="url(#glow-pancreas)" stroke="#fff" stroke-width="1"/>
              <text x="0" y="3" text-anchor="middle" font-size="8" fill="#fff" font-weight="bold">HbA1c</text>
            </g>

            <!-- Kidneys (Renal) -->
            <g class="organ-node" data-organ="renal" style="cursor:pointer;">
              <ellipse cx="130" cy="245" rx="10" ry="14" fill="${kidneys.fill}" stroke="#fff" stroke-width="1" filter="url(#glow-kidneys)"/>
              <ellipse cx="190" cy="245" rx="10" ry="14" fill="${kidneys.fill}" stroke="#fff" stroke-width="1" filter="url(#glow-kidneys)"/>
            </g>

            <g class="callouts" font-family="monospace" font-size="10" fill="#E2E8F0">
              <line x1="168" y1="168" x2="245" y2="150" stroke="#EF4444" stroke-width="1.2" stroke-dasharray="2,2"/>
              <rect x="245" y="138" width="65" height="24" rx="4" fill="#1E293B" stroke="#EF4444" stroke-width="1"/>
              <text x="250" y="154" fill="#EF4444" font-weight="bold">CVD ${Math.round((risks.cardiovascular || 0.243) * 100)}%</text>

              <line x1="145" y1="218" x2="70" y2="210" stroke="${pancreas.fill}" stroke-width="1.2" stroke-dasharray="2,2"/>
              <rect x="10" y="198" width="60" height="24" rx="4" fill="#1E293B" stroke="${pancreas.fill}" stroke-width="1"/>
              <text x="14" y="214" fill="${pancreas.fill}" font-weight="bold">GLU ${Math.round((risks.endocrine || 0.32) * 100)}%</text>

              <line x1="200" y1="245" x2="245" y2="260" stroke="${kidneys.fill}" stroke-width="1.2" stroke-dasharray="2,2"/>
              <rect x="245" y="248" width="65" height="24" rx="4" fill="#1E293B" stroke="${kidneys.fill}" stroke-width="1"/>
              <text x="250" y="264" fill="${kidneys.fill}" font-weight="bold">eGFR ${patient?.labs?.egfr || 65}</text>
            </g>
          </svg>
        </div>

        <div class="twin-organ-matrix">
          <div class="matrix-title">Organ System Risk Stratification</div>
          <div class="matrix-grid">
            <div class="organ-chip ${heart.label === 'High Risk' ? 'chip-high' : 'chip-normal'}">
              <span class="organ-name">Cardiovascular</span>
              <span class="organ-val">${Math.round((risks.cardiovascular || 0.243) * 100)}% risk</span>
              <span class="organ-badge">${heart.label}</span>
            </div>
            <div class="organ-chip ${pancreas.label === 'High Risk' || pancreas.label === 'Elevated' ? 'chip-elevated' : 'chip-normal'}">
              <span class="organ-name">Endocrine / HbA1c</span>
              <span class="organ-val">${Math.round((risks.endocrine || 0.32) * 100)}% risk</span>
              <span class="organ-badge">${pancreas.label}</span>
            </div>
            <div class="organ-chip ${kidneys.label === 'Elevated' ? 'chip-elevated' : 'chip-normal'}">
              <span class="organ-name">Renal / eGFR</span>
              <span class="organ-val">${Math.round((risks.renal || 0.18) * 100)}% risk</span>
              <span class="organ-badge">${kidneys.label}</span>
            </div>
            <div class="organ-chip chip-normal">
              <span class="organ-name">Respiratory / SpO2</span>
              <span class="organ-val">${Math.round((risks.respiratory || 0.05) * 100)}% risk</span>
              <span class="organ-badge">${lungs.label}</span>
            </div>
            <div class="organ-chip chip-normal">
              <span class="organ-name">Cerebrovascular</span>
              <span class="organ-val">${Math.round((risks.cerebral || 0.12) * 100)}% risk</span>
              <span class="organ-badge">${brain.label}</span>
            </div>
            <div class="organ-chip ${vascular.label === 'High Risk' || vascular.label === 'Elevated' ? 'chip-elevated' : 'chip-normal'}">
              <span class="organ-name">Vascular Load</span>
              <span class="organ-val">${Math.round((risks.vascular || 0.28) * 100)}% risk</span>
              <span class="organ-badge">${vascular.label}</span>
            </div>
          </div>
        </div>
      </div>
    `;
  }
};
