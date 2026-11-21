<template>
  <canvas
    ref="canvasRef"
    class="absolute inset-0 w-full h-full pointer-events-none z-0"
    aria-hidden="true"
  ></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  minStars: {
    type: Number,
    default: 45
  },
  driftSpeed: {
    type: Number,
    default: 22
  }
});

const canvasRef = ref(null);

let ctx = null;
let parentEl = null;
let width = 0;
let height = 0;
let dpr = 1;
let stars = [];
let rafId = null;
let lastTime = 0;
let isVisible = true;
let resizeObserver = null;
let intersectionObserver = null;

// Global viewport pointer coordinates (tracks reliably even with pointer-events: none on canvas)
const globalPointer = {
  x: -9999,
  y: -9999,
  active: false
};

const REPEL_RADIUS = 110;
const REPEL_RADIUS_SQ = REPEL_RADIUS * REPEL_RADIUS;
const MAX_PUSH = 48;

function buildStars() {
  if (width <= 0 || height <= 0) return;

  const count = Math.max(props.minStars, Math.min(220, Math.floor((width * height) / 1600)));

  stars = Array.from({ length: count }, () => {
    const baseX = Math.random() * width;
    const baseY = Math.random() * height;
    // 92% small white circular dots, 8% slender 4-point diamond sparkles (never 5-point)
    const isDiamond = Math.random() < 0.08;
    // Dots: radius 0.5px–1.5px; Diamonds: slender 3px–5px
    const size = isDiamond ? 3 + Math.random() * 2 : 0.5 + Math.random() * 1.0;
    // Subtle parallax depth (0.7 – 1.15) correlated with star size
    const sizeNorm = isDiamond ? (size - 3) / 2 : (size - 0.5) / 1.0;
    const depth = 0.7 + sizeNorm * 0.45;

    return {
      baseX,
      baseY,
      offsetX: 0,
      offsetY: 0,
      depth,
      type: isDiamond ? 'diamond' : 'dot',
      size,
      twinkleSpeed: 0.0012 + Math.random() * 0.0026,
      phase: Math.random() * Math.PI * 2,
      warmTint: Math.random() < 0.22
    };
  });
}

function drawFourPointDiamond(c, x, y, r, alpha, warmTint) {
  c.save();
  c.translate(x, y);

  // Subtle soft glow behind the 4-point sparkle
  c.beginPath();
  c.arc(0, 0, r * 0.85, 0, Math.PI * 2);
  c.fillStyle = warmTint
    ? `rgba(248, 226, 168, ${(alpha * 0.16).toFixed(3)})`
    : `rgba(255, 255, 255, ${(alpha * 0.16).toFixed(3)})`;
  c.fill();

  // Sharp, slender 4-point concave diamond sparkle (✦)
  const inner = r * 0.14;
  c.beginPath();
  c.moveTo(0, -r);
  c.quadraticCurveTo(inner, -inner, r, 0);
  c.quadraticCurveTo(inner, inner, 0, r);
  c.quadraticCurveTo(-inner, inner, -r, 0);
  c.quadraticCurveTo(-inner, -inner, 0, -r);
  c.closePath();

  c.fillStyle = warmTint
    ? `rgba(250, 232, 185, ${alpha.toFixed(3)})`
    : `rgba(255, 253, 250, ${alpha.toFixed(3)})`;
  c.fill();
  c.restore();
}

function renderFrame(now) {
  const canvas = canvasRef.value;
  if (!ctx || !canvas || width <= 0 || height <= 0) return;

  const deltaSec = lastTime ? Math.min((now - lastTime) / 1000, 0.05) : 0.016;
  lastTime = now;

  ctx.clearRect(0, 0, width, height);

  let mx = -9999;
  let my = -9999;
  let pointerNear = false;

  if (globalPointer.active) {
    const rect = canvas.getBoundingClientRect();
    mx = globalPointer.x - rect.left;
    my = globalPointer.y - rect.top;
    if (
      mx >= -REPEL_RADIUS &&
      mx <= rect.width + REPEL_RADIUS &&
      my >= -REPEL_RADIUS &&
      my <= rect.height + REPEL_RADIUS
    ) {
      pointerNear = true;
    }
  }

  for (let i = 0; i < stars.length; i++) {
    const s = stars[i];

    // 1. Continuous right-to-left parallax drift
    s.baseX -= props.driftSpeed * s.depth * deltaSec;
    if (s.baseX < -8) {
      s.baseX = width + 8;
      s.offsetX = 0;
      s.offsetY = 0;
    }

    // 2. Interactive pointer scatter using offsetX / offsetY so drift & repel compose smoothly
    let targetOffsetX = 0;
    let targetOffsetY = 0;
    let pushed = false;

    if (pointerNear) {
      const curX = s.baseX + s.offsetX;
      const curY = s.baseY + s.offsetY;
      const dx = curX - mx;
      const dy = curY - my;
      const distSq = dx * dx + dy * dy;

      if (distSq < REPEL_RADIUS_SQ && distSq > 0.01) {
        const dist = Math.sqrt(distSq);
        const push = (1 - dist / REPEL_RADIUS) * MAX_PUSH;
        targetOffsetX = (dx / dist) * push;
        targetOffsetY = (dy / dist) * push;
        pushed = true;
      }
    }

    const ease = pushed ? 0.14 : 0.04;
    s.offsetX += (targetOffsetX - s.offsetX) * ease;
    s.offsetY += (targetOffsetY - s.offsetY) * ease;

    const drawX = s.baseX + s.offsetX;
    const drawY = s.baseY + s.offsetY;

    // 3. Continuous smooth twinkle between 0.15 and 1.0
    const wave = (Math.sin(now * s.twinkleSpeed + s.phase) + 1) * 0.5;
    const alpha = 0.15 + wave * 0.85;

    if (s.type === 'diamond') {
      drawFourPointDiamond(ctx, drawX, drawY, s.size, alpha, s.warmTint);
    } else {
      // Soft white halo around each tiny star dot
      ctx.beginPath();
      ctx.arc(drawX, drawY, s.size * 2.5, 0, Math.PI * 2);
      ctx.fillStyle = s.warmTint
        ? `rgba(248, 228, 178, ${(alpha * 0.18).toFixed(3)})`
        : `rgba(255, 255, 255, ${(alpha * 0.18).toFixed(3)})`;
      ctx.fill();

      // Crisp bright star core
      ctx.beginPath();
      ctx.arc(drawX, drawY, s.size, 0, Math.PI * 2);
      ctx.fillStyle = s.warmTint
        ? `rgba(252, 236, 196, ${alpha.toFixed(3)})`
        : `rgba(255, 255, 255, ${alpha.toFixed(3)})`;
      ctx.fill();
    }
  }
}

function loop(now) {
  if (!isVisible) {
    rafId = null;
    lastTime = 0;
    return;
  }
  renderFrame(now);
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
  if (nextW === width && nextH === height && stars.length > 0) return;

  width = nextW;
  height = nextH;
  dpr = Math.min(window.devicePixelRatio || 1, 2);

  canvas.width = Math.max(1, Math.floor(width * dpr));
  canvas.height = Math.max(1, Math.floor(height * dpr));
  ctx = canvas.getContext('2d');
  if (ctx) {
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  buildStars();
}

function onWindowPointerMove(e) {
  globalPointer.x = e.clientX;
  globalPointer.y = e.clientY;
  globalPointer.active = true;
}

function onWindowPointerLeave() {
  globalPointer.active = false;
  globalPointer.x = -9999;
  globalPointer.y = -9999;
}

function onWindowTouchMove(e) {
  if (e.touches && e.touches.length > 0) {
    globalPointer.x = e.touches[0].clientX;
    globalPointer.y = e.touches[0].clientY;
    globalPointer.active = true;
  }
}

function onWindowTouchEnd() {
  globalPointer.active = false;
  globalPointer.x = -9999;
  globalPointer.y = -9999;
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

  window.addEventListener('pointermove', onWindowPointerMove, { passive: true });
  window.addEventListener('pointerleave', onWindowPointerLeave, { passive: true });
  document.addEventListener('mouseleave', onWindowPointerLeave, { passive: true });
  window.addEventListener('touchmove', onWindowTouchMove, { passive: true });
  window.addEventListener('touchend', onWindowTouchEnd, { passive: true });
  window.addEventListener('touchcancel', onWindowTouchEnd, { passive: true });

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
  window.removeEventListener('pointermove', onWindowPointerMove);
  window.removeEventListener('pointerleave', onWindowPointerLeave);
  document.removeEventListener('mouseleave', onWindowPointerLeave);
  window.removeEventListener('touchmove', onWindowTouchMove);
  window.removeEventListener('touchend', onWindowTouchEnd);
  window.removeEventListener('touchcancel', onWindowTouchEnd);
});
</script>
