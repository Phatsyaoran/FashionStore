<template>
  <Transition
    enter-active-class="transform transition ease-out duration-500"
    enter-from-class="translate-y-8 opacity-0 scale-95"
    enter-to-class="translate-y-0 opacity-100 scale-100"
    leave-active-class="transform transition ease-in duration-300"
    leave-from-class="translate-y-0 opacity-100 scale-100"
    leave-to-class="translate-y-6 opacity-0 scale-95"
  >
    <div
      v-if="visible && currentNotification"
      class="fixed bottom-4 left-4 sm:bottom-6 sm:left-6 z-40 max-w-[calc(100vw-2rem)] sm:max-w-sm bg-white/95 backdrop-blur-md rounded-2xl p-3 shadow-float border border-gray-100/80 flex items-center gap-3 pointer-events-auto no-print"
    >
      <!-- Product thumbnail -->
      <div class="relative w-12 h-14 rounded-xl overflow-hidden bg-gray-100 flex-shrink-0 border border-gray-200/50">
        <img
          :src="currentNotification.image"
          :alt="currentNotification.product"
          class="w-full h-full object-cover"
          @error="(e) => e.target.src = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=400&q=80'"
        />
        <div class="absolute top-1 left-1 w-2 h-2 rounded-full bg-emerald-500 animate-ping"></div>
      </div>

      <!-- Info -->
      <div class="flex-1 min-w-0 pr-1">
        <div class="flex items-center justify-between gap-1 text-[11px] text-gray-400">
          <span class="font-bold text-gray-800 truncate">
            {{ currentNotification.customer }} <span class="text-gray-400 font-normal">({{ currentNotification.location }})</span>
          </span>
          <span class="text-[10px] text-emerald-600 font-semibold whitespace-nowrap">
            {{ currentNotification.time }}
          </span>
        </div>
        <p class="text-xs font-semibold text-gray-700 line-clamp-1 mt-0.5">
          <span class="text-rose-600 font-bold">Vừa mua:</span> {{ currentNotification.product }}
        </p>
        <div class="flex items-center gap-2 mt-1">
          <span class="text-[10px] font-bold text-gray-900 bg-gray-100 px-1.5 py-0.5 rounded">
            Size {{ currentNotification.size }}
          </span>
          <span class="text-[10px] text-gray-400 font-medium">✓ Đã xác nhận đơn</span>
        </div>
      </div>

      <!-- Close button -->
      <button
        type="button"
        @click="dismiss"
        class="text-gray-400 hover:text-gray-700 p-1 rounded-full transition-colors self-start"
        title="Đóng thông báo"
      >
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const samplePurchases = [
  {
    customer: 'Anh Minh Trí',
    location: 'Quận 1, TP.HCM',
    product: 'Áo Thun Cotton Compact Phom Vừa Vặn',
    size: 'L',
    image: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=400&q=80',
    time: 'Vừa xong'
  },
  {
    customer: 'Chị Hương Lan',
    location: 'Cầu Giấy, Hà Nội',
    product: 'Áo Sơ Mi Oxford Dài Tay Chống Nhăn',
    size: 'M',
    image: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=400&q=80',
    time: '2 phút trước'
  },
  {
    customer: 'Bạn Hoàng Nam',
    location: 'Hải Châu, Đà Nẵng',
    product: 'Quần Jean Nam Slim Fit Co Giãn 4 Chiều',
    size: '32',
    image: 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=400&q=80',
    time: '3 phút trước'
  },
  {
    customer: 'Anh Tuấn Kiệt',
    location: 'Ninh Kiều, Cần Thơ',
    product: 'Áo Polo Thể Thao Pique Thoáng Mát',
    size: 'XL',
    image: 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=400&q=80',
    time: '4 phút trước'
  },
  {
    customer: 'Bạn Thanh Phong',
    location: 'Thủ Đức, TP.HCM',
    product: 'Áo Khoác Bomber Gió Siêu Nhẹ Kháng Nước',
    size: 'L',
    image: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=400&q=80',
    time: '5 phút trước'
  },
  {
    customer: 'Chị Mai Phương',
    location: 'Hồng Bàng, Hải Phòng',
    product: 'Quần Kaki Ống Đứng Smart Casual Co Giãn',
    size: '31',
    image: 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=400&q=80',
    time: '1 phút trước'
  }
];

const visible = ref(false);
const currentNotification = ref(null);
let displayTimer = null;
let intervalTimer = null;
let currentIndex = 0;

function showNextNotification() {
  currentNotification.value = samplePurchases[currentIndex % samplePurchases.length];
  currentIndex += 1;
  visible.value = true;

  // Auto-hide after 5 seconds
  if (displayTimer) clearTimeout(displayTimer);
  displayTimer = setTimeout(() => {
    visible.value = false;
  }, 5000);
}

function dismiss() {
  visible.value = false;
  if (displayTimer) clearTimeout(displayTimer);
}

onMounted(() => {
  // First show after 4 seconds of entering site
  const initialDelay = setTimeout(() => {
    showNextNotification();
    // Then show every 28-35 seconds
    intervalTimer = setInterval(() => {
      showNextNotification();
    }, 32000);
  }, 4000);

  // cleanup initialDelay if unmounted quickly
  displayTimer = initialDelay;
});

onUnmounted(() => {
  if (displayTimer) clearTimeout(displayTimer);
  if (intervalTimer) clearInterval(intervalTimer);
});
</script>
