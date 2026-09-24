/**
 * Live 60 FPS Cardiac Telemetry / ECG Waveform Oscilloscope Monitor
 */
const EcgMonitor = {
  activeAnimation: null,

  init(canvasId, rhythmType = 'AFib', heartRate = 145) {
    if (this.activeAnimation) {
      cancelAnimationFrame(this.activeAnimation);
    }

    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = (rect.width || 600) * dpr;
    canvas.height = (rect.height || 140) * dpr;
    ctx.scale(dpr, dpr);

    const width = rect.width || 600;
    const height = rect.height || 140;

    let x = 0;
    let prevY = height / 2;
    let tick = 0;
    const isAfib = rhythmType.includes('AFib') || rhythmType.includes('Fibrillation');
    const speed = isAfib ? 2.8 : 2.0;

    function drawGrid() {
      ctx.fillStyle = '#060B13';
      ctx.fillRect(0, 0, width, height);

      ctx.strokeStyle = 'rgba(14, 165, 233, 0.08)';
      ctx.lineWidth = 1;

      for (let gx = 0; gx < width; gx += 20) {
        ctx.beginPath();
        ctx.moveTo(gx, 0);
        ctx.lineTo(gx, height);
        ctx.stroke();
      }
      for (let gy = 0; gy < height; gy += 20) {
        ctx.beginPath();
        ctx.moveTo(0, gy);
        ctx.lineTo(width, gy);
        ctx.stroke();
      }

      ctx.strokeStyle = 'rgba(14, 165, 233, 0.2)';
      ctx.beginPath();
      ctx.moveTo(0, height / 2);
      ctx.lineTo(width, height / 2);
      ctx.stroke();
    }

    drawGrid();

    const render = () => {
      ctx.fillStyle = '#060B13';
      ctx.fillRect(x, 0, 14, height);

      ctx.strokeStyle = 'rgba(14, 165, 233, 0.08)';
      for (let gx = Math.floor(x / 20) * 20; gx < x + 14; gx += 20) {
        if (gx >= 0 && gx < width) {
          ctx.beginPath();
          ctx.moveTo(gx, 0);
          ctx.lineTo(gx, height);
          ctx.stroke();
        }
      }

      let v = 0;
      tick += 1;
      const cycleLen = isAfib ? 42 : 65;
      const phase = (tick % cycleLen);

      if (isAfib) {
        v += Math.sin(tick * 0.45) * 5 + Math.sin(tick * 1.2) * 4;
        if (phase === 10) v -= 8;
        else if (phase === 12) v += 48;
        else if (phase === 14) v -= 16;
        else if (phase >= 20 && phase <= 28) v += Math.sin((phase - 20) / 8 * Math.PI) * 10;
      } else {
        if (phase >= 6 && phase <= 14) v += Math.sin((phase - 6) / 8 * Math.PI) * 7;
        else if (phase === 22) v -= 7;
        else if (phase === 24) v += 42;
        else if (phase === 26) v -= 14;
        else if (phase >= 34 && phase <= 48) v += Math.sin((phase - 34) / 14 * Math.PI) * 12;
      }

      const y = (height / 2) - v;

      ctx.beginPath();
      ctx.moveTo(x === 0 ? x : x - speed, prevY);
      ctx.lineTo(x, y);
      ctx.strokeStyle = isAfib ? '#EF4444' : '#10B981';
      ctx.shadowColor = isAfib ? 'rgba(239, 68, 68, 0.8)' : 'rgba(16, 185, 129, 0.8)';
      ctx.shadowBlur = 6;
      ctx.lineWidth = 2.0;
      ctx.stroke();
      ctx.shadowBlur = 0;

      ctx.fillStyle = isAfib ? '#FCA5A5' : '#6EE7B7';
      ctx.beginPath();
      ctx.arc(x, y, 2.5, 0, Math.PI * 2);
      ctx.fill();

      prevY = y;
      x += speed;

      if (x >= width) {
        x = 0;
        prevY = height / 2;
      }

      EcgMonitor.activeAnimation = requestAnimationFrame(render);
    };

    render();
  },

  stop() {
    if (this.activeAnimation) {
      cancelAnimationFrame(this.activeAnimation);
      this.activeAnimation = null;
    }
  }
};
