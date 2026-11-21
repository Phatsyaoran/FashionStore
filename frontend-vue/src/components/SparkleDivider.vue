<template>
  <div
    ref="wrapRef"
    class="relative block h-[14px] overflow-hidden pointer-events-none select-none"
    aria-hidden="true"
  >
    <canvas ref="canvasRef" class="block w-full h-full"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const wrapRef = ref(null);
const canvasRef = ref(null);

const STEP = 28;
const STAR_SPEED_PX_PER_SEC = 74;

let ctx = null;
let width = 0;
let height = 14;
let dpr = 1;
let count = 0;
let totalSpan = 0;
let particles = [];
let rafId = null;
let lastTime = 0;
let isVisible = true;
let resizeObserver = null;
let intersectionObserver = null;

function buildParticles() {
  if (width <= 0) return;

  count = Math.ceil(width / STEP) + 2;
  totalSpan = count * STEP;

  particles = Array.from({ length: count }, (_, i) => {
    // ~7.5% slender 4-point diamonds (4px–6px), ~92.5% tiny glowing white circular dots
    const isDiamond = Math.random() < 0.075;
    return {
      x: i * STEP + (Math.random() - 0.5) * 18, // stratified jitter ±9px
      yOffset: (Math.random() - 0.5) * 3.6, // subtle organic scatter ±1.8px around center line
      type: isDiamond ? 'diamond' : 'dot',
      size: isDiamond ? 4 + Math.random() * 2 : 0.75 + Math.random() * 0.85,
      twinklePhase: Math.random() * Math.PI * 2,
      twinkleSpeed: isDiamond ? 1.5 + Math.random() * 1.3 : 1.8 + Math.random() * 2.2,
      warmTint: Math.random() < 0.3
    };
  });
}

function drawFourPointDiamond(c, x, y, r, alpha, warmTint) {
  c.save();
  c.translate(x, y);

  // Soft glow behind the 4-point sparkle
  c.beginPath();
  c.arc(0, 0, r * 0.9, 0, Math.PI * 2);
  c.fillStyle = warmTint
    ? `rgba(255, 240, 200, ${(alpha * 0.24).toFixed(3)})`
    : `rgba(255, 255, 255, ${(alpha * 0.24).toFixed(3)})`;
  c.fill();

  // Sharp, slender 4-point concave diamond (✦ — strictly 4 points, never 5)
  const inner = r * 0.13;
  c.beginPath();
  c.moveTo(0, -r);
  c.quadraticCurveTo(inner, -inner, r, 0);
  c.quadraticCurveTo(inner, inner, 0, r);
  c.quadraticCurveTo(-inner, inner, -r, 0);
  c.quadraticCurveTo(-inner, -inner, 0, -r);
  c.closePath();

  c.fillStyle = warmTint
    ? `rgba(255, 242, 206, ${alpha.toFixed(3)})`
    : `rgba(255, 253, 248, ${alpha.toFixed(3)})`;
  c.fill();
  c.restore();
}

function renderFrame(now) {
  if (!ctx || width <= 0 || height <= 0) return;

  const deltaSec = lastTime ? Math.min((now - lastTime) / 1000, 0.05) : 0.016;
  lastTime = now;
  const timeSec = now * 0.001;

  ctx.clearRect(0, 0, width, height);

  const midY = Math.floor(height * 0.5) + 0.5;

  // 1. Hairline base guide matching SparkleBorderOverlay
  ctx.beginPath();
  ctx.moveTo(0, midY);
  ctx.lineTo(width, midY);
  ctx.strokeStyle = 'rgba(255, 244, 220, 0.24)';
  ctx.lineWidth = 1;
  ctx.stroke();

  // 2. Stratified natural star stream at exact same px/sec speed as SparkleBorderOverlay
  const deltaX = STAR_SPEED_PX_PER_SEC * deltaSec;

  for (let i = 0; i < particles.length; i++) {
    const p = particles[i];
    p.x -= deltaX;
    while (p.x < -14) {
      p.x += totalSpan;
    }

    const y = midY + p.yOffset;

    if (p.type === 'diamond') {
      const glint = Math.pow(Math.max(0, Math.sin(timeSec * p.twinkleSpeed + p.twinklePhase)), 6);
      const alpha = 0.08 + glint * 0.92;
      const drawSize = p.size * (0.35 + glint * 0.65);
      drawFourPointDiamond(ctx, p.x, y, drawSize, alpha, p.warmTint);
    } else {
      const wave = (Math.sin(timeSec * p.twinkleSpeed + p.twinklePhase) + 1) * 0.5;
      const alpha = 0.22 + wave * 0.78;

      // Soft halo around circular star dot
      ctx.beginPath();
      ctx.arc(p.x, y, p.size * 2.3, 0, Math.PI * 2);
      ctx.fillStyle = p.warmTint
        ? `rgba(255, 240, 198, ${(alpha * 0.24).toFixed(3)})`
        : `rgba(255, 255, 255, ${(alpha * 0.24).toFixed(3)})`;
      ctx.fill();

      // Bright core dot
      ctx.beginPath();
      ctx.arc(p.x, y, p.size, 0, Math.PI * 2);
      ctx.fillStyle = p.warmTint
        ? `rgba(255, 246, 218, ${alpha.toFixed(3)})`
        : `rgba(255, 255, 255, ${alpha.toFixed(3)})`;
      ctx.fill();
    }
  }
}

function loop(timestamp) {
  if (!isVisible) {
    rafId = null;
    lastTime = 0;
    return;
  }
  renderFrame(timestamp);
  rafId = requestAnimationFrame(loop);
}

function startLoop() {
  if (!rafId && isVisible) {
    lastTime = 0;
    rafId = requestAnimationFrame(loop);
  }
}

function stopLoop() {
  if (rafId) {
    cancelAnimationFrame(rafId);
    rafId = null;
    lastTime = 0;
  }
}

function resizeCanvas() {
  const wrap = wrapRef.value;
  const canvas = canvasRef.value;
  if (!wrap || !canvas) return;

  const rect = wrap.getBoundingClientRect();
  const nextW = Math.round(rect.width);
  const nextH = Math.round(rect.height) || 14;
  if (nextW <= 0) return;
  if (nextW === width && nextH === height && particles.length > 0) return;

  width = nextW;
  height = nextH;
  dpr = Math.min(window.devicePixelRatio || 1, 2);

  canvas.width = Math.max(1, Math.floor(width * dpr));
  canvas.height = Math.max(1, Math.floor(height * dpr));
  ctx = canvas.getContext('2d');
  if (ctx) {
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  buildParticles();
}

onMounted(() => {
  const wrap = wrapRef.value;
  if (!wrap) return;

  resizeCanvas();

  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(() => {
      resizeCanvas();
    });
    resizeObserver.observe(wrap);
  } else {
    window.addEventListener('resize', resizeCanvas);
  }

  if (typeof IntersectionObserver !== 'undefined') {
    intersectionObserver = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        isVisible = !!entry?.isIntersecting;
        if (isVisible) {
          startLoop();
        } else {
          stopLoop();
        }
      },
      { threshold: 0.01 }
    );
    intersectionObserver.observe(wrap);
  }

  startLoop();
});

onUnmounted(() => {
  stopLoop();
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  } else {
    window.removeEventListener('resize', resizeCanvas);
  }
  if (intersectionObserver) {
    intersectionObserver.disconnect();
    intersectionObserver = null;
  }
});
</script>
