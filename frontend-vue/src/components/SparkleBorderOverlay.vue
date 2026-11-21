<template>
  <canvas
    ref="canvasRef"
    class="absolute inset-0 w-full h-full pointer-events-none z-[15]"
    aria-hidden="true"
  ></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  inset: {
    type: Number,
    default: 8
  },
  radius: {
    type: Number,
    default: 12
  }
});

const STAR_SPEED_PX_PER_SEC = 74;

const canvasRef = ref(null);

let ctx = null;
let parentEl = null;
let width = 0;
let height = 0;
let dpr = 1;
let perimeter = 0;
let geom = null;
let particles = [];
let rafId = null;
let lastTime = 0;
let isVisible = true;
let resizeObserver = null;
let intersectionObserver = null;

function computeGeometry() {
  const ins = props.inset;
  const x0 = ins;
  const y0 = ins;
  const x1 = Math.max(ins + 4, width - ins);
  const y1 = Math.max(ins + 4, height - ins);
  const w = x1 - x0;
  const h = y1 - y0;
  const r = Math.min(props.radius, w * 0.5, h * 0.5);

  const topLen = Math.max(0, w - 2 * r);
  const leftLen = Math.max(0, h - 2 * r);
  const bottomLen = topLen;
  const rightLen = leftLen;
  const arcLen = (Math.PI * 0.5) * r;

  perimeter = 2 * topLen + 2 * leftLen + 4 * arcLen;

  geom = {
    x0,
    y0,
    x1,
    y1,
    r,
    topLen,
    leftLen,
    bottomLen,
    rightLen,
    arcLen
  };
}

// Counter-clockwise point & outward normal along the inset rounded rectangle
// Top edge moves right -> left so it visually matches the middle sparkle bar
function sampleBorderPoint(dist) {
  const g = geom;
  let d = ((dist % perimeter) + perimeter) % perimeter;

  // 1. Top edge (right -> left)
  if (d <= g.topLen) {
    return { x: g.x1 - g.r - d, y: g.y0, nx: 0, ny: -1 };
  }
  d -= g.topLen;

  // 2. Top-left arc (-pi/2 -> -pi)
  if (d <= g.arcLen && g.r > 0) {
    const a = -Math.PI * 0.5 - d / g.r;
    const cos = Math.cos(a);
    const sin = Math.sin(a);
    return { x: g.x0 + g.r + g.r * cos, y: g.y0 + g.r + g.r * sin, nx: cos, ny: sin };
  }
  d -= g.arcLen;

  // 3. Left edge (top -> bottom)
  if (d <= g.leftLen) {
    return { x: g.x0, y: g.y0 + g.r + d, nx: -1, ny: 0 };
  }
  d -= g.leftLen;

  // 4. Bottom-left arc (pi -> pi/2)
  if (d <= g.arcLen && g.r > 0) {
    const a = Math.PI - d / g.r;
    const cos = Math.cos(a);
    const sin = Math.sin(a);
    return { x: g.x0 + g.r + g.r * cos, y: g.y1 - g.r + g.r * sin, nx: cos, ny: sin };
  }
  d -= g.arcLen;

  // 5. Bottom edge (left -> right)
  if (d <= g.bottomLen) {
    return { x: g.x0 + g.r + d, y: g.y1, nx: 0, ny: 1 };
  }
  d -= g.bottomLen;

  // 6. Bottom-right arc (pi/2 -> 0)
  if (d <= g.arcLen && g.r > 0) {
    const a = Math.PI * 0.5 - d / g.r;
    const cos = Math.cos(a);
    const sin = Math.sin(a);
    return { x: g.x1 - g.r + g.r * cos, y: g.y1 - g.r + g.r * sin, nx: cos, ny: sin };
  }
  d -= g.arcLen;

  // 7. Right edge (bottom -> top)
  if (d <= g.rightLen) {
    return { x: g.x1, y: g.y1 - g.r - d, nx: 1, ny: 0 };
  }
  d -= g.rightLen;

  // 8. Top-right arc (0 -> -pi/2)
  const a = g.r > 0 ? -d / g.r : 0;
  const cos = Math.cos(a);
  const sin = Math.sin(a);
  return { x: g.x1 - g.r + g.r * cos, y: g.y0 + g.r + g.r * sin, nx: cos, ny: sin };
}

function buildParticles() {
  if (!geom || perimeter <= 0) return;

  const count = Math.max(75, Math.min(100, Math.round(perimeter / 36)));
  const step = perimeter / count;

  particles = Array.from({ length: count }, (_, idx) => {
    // ~7.5% slender 4-point diamonds (4px-6px), ~92.5% tiny glowing white circular dots
    const isDiamond = Math.random() < 0.075;
    return {
      dist: idx * step + (Math.random() - 0.5) * step * 0.65,
      type: isDiamond ? 'diamond' : 'dot',
      size: isDiamond ? 4 + Math.random() * 2 : 0.75 + Math.random() * 0.85,
      twinkleSpeed: isDiamond ? 1.5 + Math.random() * 1.3 : 1.8 + Math.random() * 2.2,
      twinklePhase: Math.random() * Math.PI * 2,
      wobbleAmp: 0.4 + Math.random() * 0.8, // max ±1.2px
      wobbleSpeed: 0.0012 + Math.random() * 0.0018,
      wobblePhase: Math.random() * Math.PI * 2,
      warmTint: Math.random() < 0.3
    };
  });
}

function drawRoundedGuide(c) {
  const g = geom;
  c.beginPath();
  c.moveTo(g.x0 + g.r, g.y0);
  c.lineTo(g.x1 - g.r, g.y0);
  c.arcTo(g.x1, g.y0, g.x1, g.y0 + g.r, g.r);
  c.lineTo(g.x1, g.y1 - g.r);
  c.arcTo(g.x1, g.y1, g.x1 - g.r, g.y1, g.r);
  c.lineTo(g.x0 + g.r, g.y1);
  c.arcTo(g.x0, g.y1, g.x0, g.y1 - g.r, g.r);
  c.lineTo(g.x0, g.y0 + g.r);
  c.arcTo(g.x0, g.y0, g.x0 + g.r, g.y0, g.r);
  c.closePath();

  c.strokeStyle = 'rgba(255, 244, 220, 0.22)';
  c.lineWidth = 1;
  c.stroke();
}

function drawFourPointDiamond(c, x, y, r, alpha, warmTint) {
  c.save();
  c.translate(x, y);

  // Soft outer glow
  c.beginPath();
  c.arc(0, 0, r * 0.85, 0, Math.PI * 2);
  c.fillStyle = warmTint
    ? `rgba(255, 240, 200, ${(alpha * 0.22).toFixed(3)})`
    : `rgba(255, 255, 255, ${(alpha * 0.22).toFixed(3)})`;
  c.fill();

  // Ultra-slender 4-point concave diamond sparkle (✦ — strictly 4 points)
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
  if (!ctx || !geom || width <= 0 || height <= 0) return;

  const deltaSec = lastTime ? Math.min((now - lastTime) / 1000, 0.05) : 0.016;
  lastTime = now;
  const timeSec = now * 0.001;

  ctx.clearRect(0, 0, width, height);

  // 1. Subtle 1px inset rounded guide line
  drawRoundedGuide(ctx);

  // 2. Continuously orbiting sparkling starlight dust (synced px/sec across all refresh rates)
  const deltaDist = STAR_SPEED_PX_PER_SEC * deltaSec;
  for (let i = 0; i < particles.length; i++) {
    const p = particles[i];
    p.dist = (p.dist + deltaDist) % perimeter;

    const pt = sampleBorderPoint(p.dist);
    const wobble = Math.sin(now * p.wobbleSpeed + p.wobblePhase) * p.wobbleAmp;
    const x = pt.x + pt.nx * wobble;
    const y = pt.y + pt.ny * wobble;

    if (p.type === 'diamond') {
      const glint = Math.pow(Math.max(0, Math.sin(timeSec * p.twinkleSpeed + p.twinklePhase)), 6);
      const alpha = 0.08 + glint * 0.92;
      const drawSize = p.size * (0.35 + glint * 0.65);
      drawFourPointDiamond(ctx, x, y, drawSize, alpha, p.warmTint);
    } else {
      const wave = (Math.sin(timeSec * p.twinkleSpeed + p.twinklePhase) + 1) * 0.5;
      const alpha = 0.22 + wave * 0.78;

      // Soft halo around circular dot
      ctx.beginPath();
      ctx.arc(x, y, p.size * 2.3, 0, Math.PI * 2);
      ctx.fillStyle = p.warmTint
        ? `rgba(255, 240, 198, ${(alpha * 0.24).toFixed(3)})`
        : `rgba(255, 255, 255, ${(alpha * 0.24).toFixed(3)})`;
      ctx.fill();

      // Bright core dot
      ctx.beginPath();
      ctx.arc(x, y, p.size, 0, Math.PI * 2);
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
  const canvas = canvasRef.value;
  if (!canvas || !parentEl) return;

  const rect = parentEl.getBoundingClientRect();
  const nextW = Math.round(rect.width);
  const nextH = Math.round(rect.height);
  if (nextW <= 0 || nextH <= 0) return;
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

  computeGeometry();
  buildParticles();
}

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;
  parentEl = canvas.parentElement;
  if (!parentEl) return;

  resizeCanvas();

  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(() => {
      resizeCanvas();
    });
    resizeObserver.observe(parentEl);
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
    intersectionObserver.observe(parentEl);
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
