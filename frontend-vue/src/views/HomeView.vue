<template>
  <div class="space-y-16">
    <!-- HERO BANNER (Editorial Lookbook Split-Layout - Coolmate / Zara Style) -->
    <section class="relative bg-gradient-to-br from-gray-950 via-gray-900 to-rose-950 text-white rounded-[2.5rem] overflow-hidden shadow-2xl mx-4 sm:mx-6 lg:mx-8 mt-6 border border-white/10">
      <!-- Ambient decorative glow -->
      <div class="absolute -top-32 -left-32 w-96 h-96 bg-rose-600/20 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -bottom-32 -right-32 w-96 h-96 bg-amber-500/15 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute inset-0 opacity-15 bg-[radial-gradient(#f43f5e_1px,transparent_1px)] [background-size:20px_20px]"></div>

      <div class="relative max-w-7xl mx-auto px-6 py-16 sm:py-24 lg:px-12 grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
        <!-- Left Content (Col 1-7) -->
        <div class="lg:col-span-7 space-y-6 text-left">
          <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-rose-500/20 border border-rose-500/30 text-rose-300 text-xs font-extrabold tracking-wider uppercase backdrop-blur-md">
            <span class="animate-pulse">✨</span> NEW COLLECTION 2026
          </div>

          <h1 class="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-[1.15]">
            Nâng Tầm Phong Cách <br />
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-rose-400 via-amber-300 to-rose-200">
              Chuẩn Phom Dáng Việt
            </span>
          </h1>

          <p class="text-gray-300 text-sm sm:text-base max-w-xl leading-relaxed font-normal">
            Trải nghiệm các dòng thời trang tối giản, cao cấp: sợi tre Bamboo chống nhăn, Cotton Compact 100% 230gsm đến Lụa Satin mềm mát. Tôn dáng hoàn hảo cho mọi hoàn cảnh từ công sở tới dạo phố.
          </p>

          <div class="flex flex-wrap items-center gap-4 pt-2">
            <router-link
              to="/catalog"
              class="group px-8 py-4 rounded-2xl bg-gradient-to-r from-rose-600 to-orange-500 hover:from-rose-500 hover:to-orange-400 text-white font-black text-sm shadow-xl shadow-rose-600/30 hover:shadow-rose-600/50 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center gap-2.5"
            >
              <span>Khám Phá BST Ngay</span>
              <span class="group-hover:translate-x-1.5 transition-transform duration-300">→</span>
            </router-link>

            <router-link
              to="/catalog?isFlashSale=true"
              class="px-7 py-4 rounded-2xl bg-white/10 hover:bg-white/20 text-white font-bold text-sm backdrop-blur-md border border-white/20 hover:border-white/40 transition-all flex items-center gap-2 shadow-sm"
            >
              <span>⚡ Flash Sale Hôm Nay</span>
            </router-link>
          </div>

          <!-- Social Proof Micro-metrics -->
          <div class="pt-4 flex items-center gap-6 text-xs text-gray-400 border-t border-white/10">
            <div class="flex items-center gap-2">
              <span class="text-amber-400 font-extrabold text-sm">★ 4.9/5</span>
              <span>Đánh giá hài lòng</span>
            </div>
            <span>•</span>
            <div>
              <span class="text-white font-extrabold text-sm">50.000+</span>
              <span> Khách hàng tin dùng</span>
            </div>
          </div>
        </div>

        <!-- Right Lookbook Editorial Layout (Col 8-12) -->
        <div class="lg:col-span-5 relative hidden lg:block">
          <div class="relative z-10 grid grid-cols-2 gap-4">
            <!-- Model Card 1 -->
            <div class="relative rounded-3xl overflow-hidden shadow-2xl border-2 border-white/15 transform -rotate-2 hover:rotate-0 transition-transform duration-500 group">
              <img
                src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80"
                alt="Fashion Editorial Look 1"
                @error="onImageError"
                class="w-full h-80 object-cover group-hover:scale-105 transition-transform duration-700"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"></div>
              <div class="absolute bottom-3 left-3 right-3 text-white">
                <span class="text-[10px] font-bold text-rose-300 uppercase tracking-widest block">Lookbook</span>
                <span class="text-xs font-extrabold">Bộ Sưu Tập Nữ 2026</span>
              </div>
            </div>

            <!-- Model Card 2 -->
            <div class="relative rounded-3xl overflow-hidden shadow-2xl border-2 border-white/15 transform rotate-2 hover:rotate-0 transition-transform duration-500 mt-8 group">
              <img
                src="https://images.unsplash.com/photo-1539109136881-3be0616acf4b?auto=format&fit=crop&w=800&q=80"
                alt="Fashion Editorial Look 2"
                @error="onImageError"
                class="w-full h-80 object-cover group-hover:scale-105 transition-transform duration-700"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"></div>
              <div class="absolute bottom-3 left-3 right-3 text-white">
                <span class="text-[10px] font-bold text-amber-300 uppercase tracking-widest block">Urban Casual</span>
                <span class="text-xs font-extrabold">Phong Cách Nam Trẻ</span>
              </div>
            </div>
          </div>

          <!-- Floating Badge -->
          <div class="absolute -bottom-4 left-1/2 -translate-x-1/2 z-20 bg-white/95 backdrop-blur-md text-gray-900 px-4 py-2 rounded-2xl shadow-xl border border-gray-100 flex items-center gap-2 text-xs font-bold whitespace-nowrap">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
            <span>Chất vải tự nhiên 100% an toàn</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 4 SERVICE COMMITMENTS BANNER (Uniqlo / Coolmate Standard) -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 sm:gap-6">
        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-soft hover:shadow-md transition-all flex items-center gap-3.5 group">
          <div class="w-12 h-12 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            🚚
          </div>
          <div>
            <h4 class="text-xs font-bold text-gray-900 group-hover:text-rose-600 transition-colors">Miễn phí vận chuyển</h4>
            <p class="text-[11px] text-gray-500 mt-0.5">Đơn hàng từ 500k toàn quốc</p>
          </div>
        </div>

        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-soft hover:shadow-md transition-all flex items-center gap-3.5 group">
          <div class="w-12 h-12 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            🔄
          </div>
          <div>
            <h4 class="text-xs font-bold text-gray-900 group-hover:text-amber-600 transition-colors">Đổi trả tận nơi</h4>
            <p class="text-[11px] text-gray-500 mt-0.5">Trong vòng 60 ngày miễn phí</p>
          </div>
        </div>

        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-soft hover:shadow-md transition-all flex items-center gap-3.5 group">
          <div class="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            📏
          </div>
          <div>
            <h4 class="text-xs font-bold text-gray-900 group-hover:text-indigo-600 transition-colors">Trợ lý AI chọn size</h4>
            <p class="text-[11px] text-gray-500 mt-0.5">Chuẩn số đo vóc dáng ~95%</p>
          </div>
        </div>

        <div class="bg-white p-5 rounded-2xl border border-gray-100 shadow-soft hover:shadow-md transition-all flex items-center gap-3.5 group">
          <div class="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            💳
          </div>
          <div>
            <h4 class="text-xs font-bold text-gray-900 group-hover:text-emerald-600 transition-colors">Thanh toán VietQR</h4>
            <p class="text-[11px] text-gray-500 mt-0.5">Chuyển khoản 1 chạm tức thì</p>
          </div>
        </div>
      </div>
    </section>

    <!-- FLASH SALE SECTION (Yêu cầu Phase 8) -->
    <section v-if="flashSales.length > 0" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-gradient-to-r from-rose-900 via-rose-800 to-amber-900 rounded-3xl p-6 sm:p-8 text-white shadow-2xl border border-rose-700/50 relative overflow-hidden">
        <!-- Glow effect -->
        <div class="absolute -top-24 -right-24 w-96 h-96 bg-rose-500/20 rounded-full blur-3xl pointer-events-none"></div>

        <div class="relative z-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8 pb-6 border-b border-white/10">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-2xl bg-gradient-to-tr from-amber-400 to-rose-500 flex items-center justify-center text-2xl shadow-lg shadow-rose-600/50 animate-bounce">
              ⚡
            </div>
            <div>
              <div class="flex items-center gap-2">
                <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white uppercase">
                  FLASH SALE GIỜ VÀNG
                </h2>
                <span class="px-2 py-0.5 text-[10px] font-black bg-amber-400 text-gray-950 rounded-md tracking-wider">
                  LIVE
                </span>
              </div>
              <p class="text-xs text-rose-200 mt-0.5">Số lượng có hạn, ưu đãi cực sâu - Đang bán chạy nhất hôm nay</p>
            </div>
          </div>

          <!-- Real-time Live Countdown Clock -->
          <div class="flex items-center gap-3 bg-black/40 backdrop-blur-md px-5 py-3 rounded-2xl border border-rose-500/30 shadow-inner">
            <span class="text-xs font-bold text-rose-200">Kết thúc sau:</span>
            <div class="flex items-center gap-1.5 font-mono text-base font-black">
              <span class="bg-rose-600 text-white px-2.5 py-1 rounded-xl shadow-md min-w-[2.2rem] text-center">{{ hoursStr }}</span>
              <span class="text-rose-400 font-bold">:</span>
              <span class="bg-rose-600 text-white px-2.5 py-1 rounded-xl shadow-md min-w-[2.2rem] text-center">{{ minutesStr }}</span>
              <span class="text-rose-400 font-bold">:</span>
              <span class="bg-amber-400 text-gray-950 px-2.5 py-1 rounded-xl shadow-md min-w-[2.2rem] text-center animate-pulse">{{ secondsStr }}</span>
            </div>
          </div>
        </div>

        <div class="relative z-10 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
          <ProductCard
            v-for="product in flashSales"
            :key="product.id"
            :product="product"
            :show-flash-sale-bar="true"
          />
        </div>
      </div>
    </section>

    <!-- Categories Grid -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-end mb-6">
        <div>
          <h2 class="text-2xl font-extrabold text-gray-900 tracking-tight">Danh Mục Nổi Bật</h2>
          <p class="text-xs text-gray-500 mt-1">Lựa chọn trang phục phù hợp cho từng hoàn cảnh</p>
        </div>
        <router-link to="/catalog" class="text-xs font-bold text-rose-600 hover:underline">
          Xem tất cả →
        </router-link>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4">
        <router-link
          v-for="cat in categories"
          :key="cat.id"
          :to="`/catalog?categorySlug=${cat.slug}`"
          class="group bg-white p-4 rounded-2xl border border-gray-100 hover:border-rose-200 shadow-sm hover:shadow-md transition-all text-center flex flex-col items-center justify-center space-y-2"
        >
          <div class="w-12 h-12 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center text-xl group-hover:scale-110 transition-transform">
            👔
          </div>
          <h3 class="text-xs font-bold text-gray-800 group-hover:text-rose-600 transition-colors">
            {{ cat.name }}
          </h3>
          <span class="text-[10px] text-gray-400 font-medium">
            {{ cat.productCount || 0 }} sản phẩm
          </span>
        </router-link>
      </div>
    </section>

    <!-- New Arrivals Section -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-end mb-6">
        <div>
          <h2 class="text-2xl font-extrabold text-gray-900 tracking-tight">Sản Phẩm Mới Nhất</h2>
          <p class="text-xs text-gray-500 mt-1">Cập nhật xu hướng thời trang hiện đại mỗi ngày</p>
        </div>
        <router-link to="/catalog" class="text-xs font-bold text-rose-600 hover:underline">
          Khám phá trọn bộ →
        </router-link>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
        <ProductCard
          v-for="product in latestProducts"
          :key="product.id"
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
import { onImageError } from '@/services/formatters';

const categories = ref([]);
const flashSales = ref([]);
const latestProducts = ref([]);

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
