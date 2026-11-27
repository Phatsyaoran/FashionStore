<template>
  <div class="space-y-14 sm:space-y-16">
    <!-- HERO BANNER (Clean White Editorial Luxury) -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6">
      <div class="relative bg-white text-[#18181B] rounded-2xl overflow-hidden shadow-[0_12px_40px_-16px_rgba(24,24,27,0.10)] border border-neutral-200/80">
        <!-- Subtle top luxury gradient accent line -->
        <div class="h-1 w-full luxury-gradient"></div>

        <div class="relative px-5 py-10 sm:px-9 sm:py-14 lg:px-12 lg:py-16 grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-8 items-center">
          <!-- Left Content -->
          <div class="lg:col-span-6 space-y-5 text-left">
            <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-[#7A1032]/[0.06] border border-[#C9963B]/40 text-[#7A1032] text-[11px] font-bold tracking-[0.16em] uppercase">
              <span class="w-1.5 h-1.5 rounded-full bg-[#C9963B]"></span>
              New Collection 2026
            </div>

            <h1 class="text-3xl sm:text-5xl lg:text-[52px] font-extrabold tracking-tight leading-[1.28] text-[#18181B]">
              <span class="block">Nâng Tầm Phong Cách</span>
              <span class="text-[#7A1032] font-light mt-2 pt-1.5 pb-1 inline-block">Chuẩn Form Dáng Việt</span>
            </h1>

            <p class="text-[#6B7280] text-sm sm:text-base max-w-xl leading-relaxed">
              Thiết kế tối giản đương đại từ sợi tre Bamboo chống nhăn, Cotton Compact 230gsm đến Lụa Satin mềm mát — tôn dáng tự nhiên từ công sở đến dạo phố.
            </p>

            <div class="flex flex-wrap items-center gap-3 pt-2">
              <router-link
                to="/catalog"
                class="btn-sheen silky-hover active:scale-[0.98] min-h-[46px] px-7 py-3 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs sm:text-sm tracking-wide shadow-sm hover:shadow-md inline-flex items-center gap-2"
              >
                <span>Khám Phá Bộ Sưu Tập</span>
                <span class="text-[#F3D078]">→</span>
              </router-link>

              <router-link
                to="/catalog?isFlashSale=true"
                class="silky-hover active:scale-[0.98] min-h-[46px] px-6 py-3 rounded-lg bg-white hover:bg-[#FAFAFA] text-[#7A1032] font-semibold text-xs sm:text-sm border border-neutral-200 hover:border-[#C9963B]/70 inline-flex items-center gap-2"
              >
                <span>⚡</span>
                <span>Flash Sale Hôm Nay</span>
              </router-link>
            </div>

            <!-- Subtle Metrics -->
            <div class="pt-5 flex flex-col sm:flex-row sm:flex-wrap sm:items-center gap-2 sm:gap-6 text-xs text-[#6B7280] border-t border-neutral-200/80">
              <div class="flex items-center gap-1.5 whitespace-nowrap">
                <span class="text-[#C9963B] font-bold text-sm">★ 4.9/5</span>
                <span>Đánh giá hài lòng</span>
              </div>
              <span class="hidden sm:inline text-neutral-300">•</span>
              <div class="flex items-center gap-1.5 whitespace-nowrap">
                <span class="text-[#18181B] font-bold text-sm">50.000+</span>
                <span>Khách hàng tin dùng</span>
              </div>
            </div>
          </div>

          <!-- Right Editorial Lookbook V-Shape Crossed Cards (6 cols, Editorial Stagger) -->
          <div class="lg:col-span-6 relative flex items-center justify-center py-3 lg:py-2">
            <div
              class="hero-v-group relative flex items-center justify-center gap-4 sm:gap-5 w-full mx-auto px-1"
              @mouseleave="activeHeroCard = null"
            >
              <!-- Left V-Card: BST Nữ 2026 -->
              <div
                @click="activeHeroCard = activeHeroCard === 'left' ? null : 'left'"
                class="hero-v-card hero-v-card-left relative w-[46%] sm:w-[220px] lg:w-[245px] aspect-[3/4] rounded-2xl overflow-hidden border border-white/75 ring-1 ring-neutral-200/80 shadow-xl cursor-pointer select-none shrink-0"
                :class="{ 'is-active': activeHeroCard === 'left' }"
              >
                <img
                  src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80"
                  alt="Fashion Editorial Look 1"
                  @error="onImageError"
                  class="w-full h-full object-cover"
                />
                <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/15 to-transparent"></div>
                <div class="absolute bottom-3.5 left-3.5 right-3.5 text-white">
                  <span class="text-[10px] font-semibold text-[#F3D078] uppercase tracking-widest block">Editorial</span>
                  <span class="text-xs sm:text-sm font-bold">BST Nữ 2026</span>
                </div>
              </div>

              <!-- Right V-Card: Phong Cách Nam -->
              <div
                @click="activeHeroCard = activeHeroCard === 'right' ? null : 'right'"
                class="hero-v-card hero-v-card-right relative w-[46%] sm:w-[220px] lg:w-[245px] aspect-[3/4] rounded-2xl overflow-hidden border border-white/75 ring-1 ring-neutral-200/80 shadow-xl cursor-pointer select-none shrink-0"
                :class="{ 'is-active': activeHeroCard === 'right' }"
              >
                <img
                  src="https://images.unsplash.com/photo-1539109136881-3be0616acf4b?auto=format&fit=crop&w=800&q=80"
                  alt="Fashion Editorial Look 2"
                  @error="onImageError"
                  class="w-full h-full object-cover"
                />
                <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/15 to-transparent"></div>
                <div class="absolute bottom-3.5 left-3.5 right-3.5 text-white">
                  <span class="text-[10px] font-semibold text-[#F3D078] uppercase tracking-widest block">Urban Minimal</span>
                  <span class="text-xs sm:text-sm font-bold">Phong Cách Nam</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- KINETIC EDITORIAL MARQUEE STRIP (Seamless Right-to-Left + Interactive Starfield) -->
    <div class="fs-marquee-wrapper overflow-hidden relative luxury-gradient text-[#FFFDF9] py-3.5 border-y border-white/15 select-none shadow-sm">
      <StarfieldOverlay />
      <div class="fs-marquee-track relative z-10 text-[11px] sm:text-xs font-bold tracking-[0.22em] uppercase text-[#FFFDF9]">
        <div class="fs-marquee-group">
          <span
            v-for="n in 4"
            :key="`m1-${n}`"
            class="px-5 whitespace-nowrap"
          >
            FASHIONSTORE 2026 <span class="text-[#F3D078] mx-2">✦</span> AI SMART SIZE <span class="text-[#F3D078] mx-2">✦</span> MIX &amp; MATCH STUDIO <span class="text-[#F3D078] mx-2">✦</span> FREESHIP 500K <span class="text-[#F3D078] mx-2">✦</span>
          </span>
        </div>
        <div class="fs-marquee-group" aria-hidden="true">
          <span
            v-for="n in 4"
            :key="`m2-${n}`"
            class="px-5 whitespace-nowrap"
          >
            FASHIONSTORE 2026 <span class="text-[#F3D078] mx-2">✦</span> AI SMART SIZE <span class="text-[#F3D078] mx-2">✦</span> MIX &amp; MATCH STUDIO <span class="text-[#F3D078] mx-2">✦</span> FREESHIP 500K <span class="text-[#F3D078] mx-2">✦</span>
          </span>
        </div>
      </div>
    </div>

    <!-- 4 SERVICE COMMITMENTS -->
    <section v-reveal class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-5">
        <div class="group bg-white p-3.5 sm:p-4 md:p-5 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/75 hover:-translate-y-1.5 hover:shadow-[0_14px_34px_-12px_rgba(74,8,32,0.12)] active:scale-[0.98] transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] flex flex-col md:flex-row items-center text-center md:text-left gap-2.5 md:gap-3.5">
          <div class="w-10 h-10 rounded-lg bg-[#7A1032]/[0.06] border border-[#7A1032]/15 group-hover:border-[#C9963B]/50 group-hover:scale-110 transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] text-[#7A1032] flex items-center justify-center text-lg shrink-0">🚚</div>
          <div class="min-w-0">
            <h4 class="text-xs font-bold text-[#18181B] group-hover:text-[#7A1032] transition-colors leading-snug md:truncate">Freeship toàn quốc</h4>
            <p class="text-[11px] text-[#6B7280] mt-0.5 leading-snug md:truncate">Đơn hàng từ 500k</p>
          </div>
        </div>

        <div class="group bg-white p-3.5 sm:p-4 md:p-5 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/75 hover:-translate-y-1.5 hover:shadow-[0_14px_34px_-12px_rgba(74,8,32,0.12)] active:scale-[0.98] transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] flex flex-col md:flex-row items-center text-center md:text-left gap-2.5 md:gap-3.5">
          <div class="w-10 h-10 rounded-lg bg-[#C9963B]/10 border border-[#C9963B]/25 group-hover:border-[#C9963B]/60 group-hover:scale-110 transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] text-[#9A6F20] flex items-center justify-center text-lg shrink-0">🔄</div>
          <div class="min-w-0">
            <h4 class="text-xs font-bold text-[#18181B] group-hover:text-[#7A1032] transition-colors leading-snug md:truncate">Đổi trả 60 ngày</h4>
            <p class="text-[11px] text-[#6B7280] mt-0.5 leading-snug md:truncate">Hỗ trợ đổi tận nơi</p>
          </div>
        </div>

        <div class="group bg-white p-3.5 sm:p-4 md:p-5 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/75 hover:-translate-y-1.5 hover:shadow-[0_14px_34px_-12px_rgba(74,8,32,0.12)] active:scale-[0.98] transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] flex flex-col md:flex-row items-center text-center md:text-left gap-2.5 md:gap-3.5">
          <div class="w-10 h-10 rounded-lg bg-[#5E1B55]/[0.08] border border-[#5E1B55]/20 group-hover:border-[#C9963B]/50 group-hover:scale-110 transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] text-[#5E1B55] flex items-center justify-center text-lg shrink-0">📏</div>
          <div class="min-w-0">
            <h4 class="text-xs font-bold text-[#18181B] group-hover:text-[#7A1032] transition-colors leading-snug md:truncate">AI tư vấn chọn size</h4>
            <p class="text-[11px] text-[#6B7280] mt-0.5 leading-snug md:truncate">Chuẩn form dáng ~95%</p>
          </div>
        </div>

        <div class="group bg-white p-3.5 sm:p-4 md:p-5 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/75 hover:-translate-y-1.5 hover:shadow-[0_14px_34px_-12px_rgba(74,8,32,0.12)] active:scale-[0.98] transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] flex flex-col md:flex-row items-center text-center md:text-left gap-2.5 md:gap-3.5">
          <div class="w-10 h-10 rounded-lg bg-[#7A1032]/[0.06] border border-[#7A1032]/15 group-hover:border-[#C9963B]/50 group-hover:scale-110 transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] text-[#7A1032] flex items-center justify-center text-lg shrink-0">💳</div>
          <div class="min-w-0">
            <h4 class="text-xs font-bold text-[#18181B] group-hover:text-[#7A1032] transition-colors leading-snug md:truncate">VietQR & COD</h4>
            <p class="text-[11px] text-[#6B7280] mt-0.5 leading-snug md:truncate">Thanh toán linh hoạt</p>
          </div>
        </div>
      </div>
    </section>

    <!-- FLASH SALE SECTION (Unified Starfield + Inset Sparkling Star Border + Matched Center Divider) -->
    <section v-if="flashSales.length > 0" v-reveal class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="relative rounded-2xl overflow-hidden luxury-gradient shadow-[0_20px_50px_-15px_rgba(74,8,32,0.35)]">
        <!-- Unified Interactive Starfield covering Header + Product Grid -->
        <StarfieldOverlay :min-stars="95" />

        <!-- Inset Sparkling Starlight Border (8px inset ~2mm, 12px corner radius) -->
        <SparkleBorderOverlay :inset="8" :radius="12" />

        <!-- Header Band -->
        <div class="relative z-20 px-5 pt-6 pb-4 sm:px-8 sm:pt-7 sm:pb-5 text-[#FFFDF9] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div class="flex items-center gap-2.5">
              <span class="px-2 py-0.5 text-[10px] font-bold bg-white/15 text-[#F3D078] border border-[#F3D078]/40 rounded uppercase tracking-wider">LIVE</span>
              <h2 class="text-xl sm:text-2xl font-extrabold tracking-tight uppercase text-[#FFFDF9]">
                Flash Sale Giờ Vàng
              </h2>
            </div>
            <p class="text-xs text-white/80 mt-1">Ưu đãi giới hạn trong ngày — số lượng có hạn</p>
          </div>

          <!-- Crisp Countdown Clock -->
          <div class="flex items-center gap-2.5 bg-white/10 backdrop-blur-sm px-4 py-2.5 rounded-xl border border-white/20 self-start sm:self-auto">
            <span class="text-[11px] font-medium text-white/85">Kết thúc sau</span>
            <div class="flex items-center gap-1 font-mono text-sm font-extrabold">
              <span class="bg-black/35 text-[#F3D078] border border-white/15 px-2 py-1 rounded-md min-w-[2rem] text-center">{{ hoursStr }}</span>
              <span class="text-[#F3D078]">:</span>
              <span class="bg-black/35 text-[#F3D078] border border-white/15 px-2 py-1 rounded-md min-w-[2rem] text-center">{{ minutesStr }}</span>
              <span class="text-[#F3D078]">:</span>
              <span class="bg-[#7A1032] text-[#F3D078] border border-[#F3D078]/40 px-2 py-1 rounded-md min-w-[2rem] text-center">{{ secondsStr }}</span>
            </div>
          </div>
        </div>

        <!-- Single Standalone Sparkling Star Divider (aligned 8px inset with SparkleBorderOverlay) -->
        <SparkleDivider class="relative z-20 mx-[8px] my-3" />

        <!-- Product Grid (Crisp White ProductCards floating cleanly inside the inset star border) -->
        <div class="relative z-20 p-5 sm:p-6 pt-3 sm:pt-4">
          <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-5">
            <ProductCard
              v-for="(product, idx) in flashSales"
              :key="product.id"
              v-reveal="idx * 65"
              :product="product"
              :show-flash-sale-bar="true"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Grid -->
    <section v-reveal class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-end mb-6">
        <div>
          <span class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Danh mục</span>
          <h2 class="text-xl sm:text-2xl font-extrabold text-[#18181B] tracking-tight mt-0.5">Danh Mục Nổi Bật</h2>
        </div>
        <router-link to="/catalog" class="text-xs font-bold text-[#7A1032] hover:text-[#5C0B27] hover:underline transition-colors">
          Xem tất cả →
        </router-link>
      </div>

      <div class="flex md:grid md:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4 overflow-x-auto md:overflow-visible snap-x snap-mandatory no-scrollbar pb-1 md:pb-0 -mx-4 px-4 md:mx-0 md:px-0">
        <router-link
          v-for="(cat, idx) in categories"
          :key="cat.id"
          v-reveal="idx * 50"
          :to="`/catalog?categorySlug=${cat.slug}`"
          class="group bg-white p-3.5 md:p-4 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/60 hover:-translate-y-1 hover:shadow-[0_14px_32px_-12px_rgba(92,11,39,0.12)] silky-hover text-center flex flex-col items-center justify-center space-y-1.5 md:space-y-2 shrink-0 w-[124px] sm:w-[140px] md:w-auto snap-start"
        >
          <div class="w-10 h-10 md:w-11 md:h-11 rounded-lg bg-[#FAFAFA] border border-neutral-200/80 group-hover:bg-[#7A1032] group-hover:border-[#7A1032] text-[#7A1032] group-hover:text-white flex items-center justify-center text-base md:text-lg transition-colors duration-300">
            👔
          </div>
          <h3 class="text-xs font-bold text-[#18181B] group-hover:text-[#7A1032] transition-colors leading-snug">
            {{ cat.name }}
          </h3>
          <span class="text-[11px] text-[#6B7280] font-medium">
            {{ cat.productCount || 0 }} sản phẩm
          </span>
        </router-link>
      </div>
    </section>

    <!-- New Arrivals Section -->
    <section v-reveal class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-end mb-6">
        <div>
          <span class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">New Arrivals</span>
          <h2 class="text-xl sm:text-2xl font-extrabold text-[#18181B] tracking-tight mt-0.5">Sản Phẩm Mới Nhất</h2>
        </div>
        <router-link to="/catalog" class="text-xs font-bold text-[#7A1032] hover:text-[#5C0B27] hover:underline transition-colors">
          Khám phá trọn bộ →
        </router-link>
      </div>

      <!-- Skeleton Loading Shimmer -->
      <div v-if="loadingHome" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
        <div
          v-for="n in 8"
          :key="n"
          class="bg-white rounded-xl border border-neutral-200/80 overflow-hidden"
        >
          <div class="aspect-[3/4] skeleton-shimmer"></div>
          <div class="p-4 space-y-2.5">
            <div class="h-3 w-1/3 rounded skeleton-shimmer"></div>
            <div class="h-4 w-5/6 rounded skeleton-shimmer"></div>
            <div class="h-4 w-1/2 rounded skeleton-shimmer"></div>
          </div>
        </div>
      </div>

      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
        <ProductCard
          v-for="(product, idx) in latestProducts"
          :key="product.id"
          v-reveal="(idx % 4) * 65"
          :product="product"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import api from '@/services/api';
import ProductCard from '@/components/ProductCard.vue';
import StarfieldOverlay from '@/components/StarfieldOverlay.vue';
import SparkleBorderOverlay from '@/components/SparkleBorderOverlay.vue';
import SparkleDivider from '@/components/SparkleDivider.vue';
import { onImageError } from '@/services/formatters';

const activeHeroCard = ref(null);
const categories = ref([]);
const flashSales = ref([]);
const latestProducts = ref([]);
const loadingHome = ref(true);

// Real-time Countdown Timer (Phase 8)
const hoursStr = ref('02');
const minutesStr = ref('45');
const secondsStr = ref('18');
let timer = null;

function updateCountdown() {
  const now = new Date();
  let target = new Date();

  // Find valid future flashSaleEndTime if any
  const futureEnds = flashSales.value
    .map(p => p.flashSaleEndTime ? new Date(p.flashSaleEndTime) : null)
    .filter(d => d && d > now);

  if (futureEnds.length > 0) {
    target = new Date(Math.min(...futureEnds.map(d => d.getTime())));
  } else {
    // Default to tonight 23:59:59
    target.setHours(23, 59, 59, 999);
  }

  const diffMs = target.getTime() - now.getTime();
  if (diffMs <= 0) {
    hoursStr.value = '00';
    minutesStr.value = '00';
    secondsStr.value = '00';
    return;
  }

  const totalSec = Math.floor(diffMs / 1000);
  const h = Math.floor(totalSec / 3600);
  const m = Math.floor((totalSec % 3600) / 60);
  const s = totalSec % 60;

  hoursStr.value = String(h).padStart(2, '0');
  minutesStr.value = String(m).padStart(2, '0');
  secondsStr.value = String(s).padStart(2, '0');
}

onMounted(async () => {
  try {
    const [catRes, flashRes, latestRes] = await Promise.all([
      api.get('/categories'),
      api.get('/products/flash-sale'),
      api.get('/products/latest?limit=8')
    ]);

    if (catRes.success) categories.value = catRes.data || [];
    if (flashRes.success) flashSales.value = (flashRes.data || []).slice(0, 4);
    if (latestRes.success) latestProducts.value = latestRes.data || [];
  } catch (err) {
    console.error('Lỗi nạp dữ liệu trang chủ:', err);
  } finally {
    loadingHome.value = false;
  }

  updateCountdown();
  timer = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.hero-v-card {
  transition:
    transform 0.55s cubic-bezier(0.25, 0.8, 0.25, 1),
    box-shadow 0.55s cubic-bezier(0.25, 0.8, 0.25, 1),
    filter 0.55s ease !important;
  will-change: transform;
}

/* Thẻ trái cao hơn nhẹ, thẻ phải thấp hơn nhẹ tạo nhịp tạp chí + dáng chữ V rõ nét */
.hero-v-card-left {
  transform-origin: center bottom;
  transform: rotate(-5.5deg) translate3d(0, -10px, 0) scale(1);
}
.hero-v-card-right {
  transform-origin: center bottom;
  transform: rotate(5.5deg) translate3d(0, 12px, 0) scale(1);
}

/* Hover/chạm thẻ TRÁI: thẻ trái thẳng lên, thẻ phải né nhẹ sang phải */
.hero-v-group:has(.hero-v-card-left:hover) .hero-v-card-left,
.hero-v-group:has(.hero-v-card-left.is-active) .hero-v-card-left {
  transform: rotate(0deg) translate3d(-8px, -16px, 0) scale(1.04);
  box-shadow: 0 26px 50px -14px rgba(74, 8, 32, 0.28);
}
.hero-v-group:has(.hero-v-card-left:hover) .hero-v-card-right,
.hero-v-group:has(.hero-v-card-left.is-active) .hero-v-card-right {
  transform: rotate(7deg) translate3d(12px, 16px, 0) scale(0.96);
  filter: brightness(0.9);
}

/* Hover/chạm thẻ PHẢI: thẻ phải thẳng lên, thẻ trái né nhẹ sang trái */
.hero-v-group:has(.hero-v-card-right:hover) .hero-v-card-right,
.hero-v-group:has(.hero-v-card-right.is-active) .hero-v-card-right {
  transform: rotate(0deg) translate3d(8px, 4px, 0) scale(1.04);
  box-shadow: 0 26px 50px -14px rgba(74, 8, 32, 0.28);
}
.hero-v-group:has(.hero-v-card-right:hover) .hero-v-card-left,
.hero-v-group:has(.hero-v-card-right.is-active) .hero-v-card-left {
  transform: rotate(-7deg) translate3d(-12px, -6px, 0) scale(0.96);
  filter: brightness(0.9);
}

.hero-v-card img {
  transition: transform 0.65s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
.hero-v-card:hover img,
.hero-v-card.is-active img {
  transform: scale(1.05);
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
