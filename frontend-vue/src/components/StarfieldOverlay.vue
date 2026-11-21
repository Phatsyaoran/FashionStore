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
    const homeX = Math.random() * width;
    const homeY = Math.random() * height;
    // 92% small white circular dots, 8% slender 4-point diamond sparkles (never 5-point)
    const isDiamond = Math.random() < 0.08;

    return {
      homeX,
      homeY,
      x: homeX,
      y: homeY,
      type: isDiamond ? 'diamond' : 'dot',
      // Dots: radius 0.5px–1.5px; Diamonds: slender 3px–5px
      size: isDiamond ? 3 + Math.random() * 2 : 0.5 + Math.random() * 1.0,
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

function renderFrame(timestamp) {
  const canvas = canvasRef.value;
  if (!ctx || !canvas || width <= 0 || height <= 0) return;

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
    let targetX = s.homeX;
    let targetY = s.homeY;
    let pushed = false;

    if (pointerNear) {
      const dx = s.homeX - mx;
      const dy = s.homeY - my;
      const distSq = dx * dx + dy * dy;

      if (distSq < REPEL_RADIUS_SQ && distSq > 0.01) {
        const dist = Math.sqrt(distSq);
        const push = (1 - dist / REPEL_RADIUS) * MAX_PUSH;
        targetX = s.homeX + (dx / dist) * push;
        targetY = s.homeY + (dy / dist) * push;
        pushed = true;
      }
    }

    // Smooth lerp: responsive push (0.14), dreamy float back (0.04)
    const ease = pushed ? 0.14 : 0.04;
    s.x += (targetX - s.x) * ease;
    s.y += (targetY - s.y) * ease;

    // Continuous smooth twinkle between 0.15 and 1.0
    const wave = (Math.sin(timestamp * s.twinkleSpeed + s.phase) + 1) * 0.5;
    const alpha = 0.15 + wave * 0.85;

    if (s.type === 'diamond') {
      drawFourPointDiamond(ctx, s.x, s.y, s.size, alpha, s.warmTint);
    } else {
      // Soft white halo around each tiny star dot
      ctx.beginPath();
      ctx.arc(s.x, s.y, s.size * 2.5, 0, Math.PI * 2);
      ctx.fillStyle = s.warmTint
        ? `rgba(248, 228, 178, ${(alpha * 0.18).toFixed(3)})`
        : `rgba(255, 255, 255, ${(alpha * 0.18).toFixed(3)})`;
      ctx.fill();

      // Crisp bright star core
      ctx.beginPath();
      ctx.arc(s.x, s.y, s.size, 0, Math.PI * 2);
      ctx.fillStyle = s.warmTint
        ? `rgba(252, 236, 196, ${alpha.toFixed(3)})`
        : `rgba(255, 255, 255, ${alpha.toFixed(3)})`;
      ctx.fill();
    }
  }
}

function loop(timestamp) {
  if (!isVisible) {
    rafId = null;
    return;
  }
  renderFrame(timestamp);
  rafId = requestAnimationFrame(loop);
}

function startLoop() {
  if (!rafId && isVisible) {
    rafId = requestAnimationFrame(loop);
  }
}

function stopLoop() {
  if (rafId) {
    cancelAnimationFrame(rafId);
    rafId = null;
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
