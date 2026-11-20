<template>
  <Teleport to="body">
    <div v-if="productStore.isQuickViewOpen && product" class="fixed inset-0 z-[9990] overflow-y-auto no-print" role="dialog" aria-modal="true">
      <!-- Backdrop -->
      <div class="fixed inset-0 z-[9990] bg-black/60 backdrop-blur-sm transition-opacity" @click="productStore.closeQuickView"></div>

      <div class="relative z-[9995] flex min-h-screen items-center justify-center p-4 text-center sm:p-0">
        <div class="relative transform overflow-hidden rounded-3xl bg-white text-left shadow-2xl transition-all sm:my-8 sm:w-full sm:max-w-3xl border border-gray-100 max-h-[90vh] overflow-y-auto w-full">
        
        <!-- Close button -->
        <button
          @click="productStore.closeQuickView"
          class="absolute top-4 right-4 z-10 text-gray-400 hover:text-gray-700 bg-white/80 backdrop-blur-md rounded-full p-2 hover:bg-gray-100 transition-all shadow-sm"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        <div class="grid grid-cols-1 md:grid-cols-2">
          <!-- Left: Thumbnail Image -->
          <div class="relative bg-gray-100 flex items-center justify-center p-6">
            <img
              :src="product.thumbnailUrl"
              :alt="product.name"
              @error="onImageError"
              class="w-full max-h-[380px] object-cover rounded-2xl shadow-sm"
              loading="lazy"
            />
            <span
              v-if="product.isFlashSale"
              class="absolute top-8 left-8 bg-rose-600 text-white text-xs font-extrabold uppercase px-3 py-1 rounded-full shadow-md animate-pulse"
            >
              ⚡ Flash Sale
            </span>
          </div>

          <!-- Right: Details & Selectors -->
          <div class="p-6 md:p-8 flex flex-col justify-between">
            <div>
              <span class="text-xs font-semibold text-rose-600 uppercase tracking-wider">
                {{ product.categoryName || 'Thời Trang' }}
              </span>
              <h2 class="text-lg md:text-xl font-bold text-gray-900 mt-1 leading-snug">
                {{ product.name }}
              </h2>

              <!-- Price Box -->
              <div class="mt-3 flex items-baseline gap-2.5">
                <span class="text-2xl font-extrabold text-rose-600">
                  {{ formatVND(currentPrice) }}
                </span>
                <span v-if="product.salePrice && product.basePrice > product.salePrice" class="text-sm text-gray-400 line-through">
                  {{ formatVND(product.basePrice) }}
                </span>
                <span
                  v-if="product.discountPercent"
                  class="text-xs font-bold bg-rose-100 text-rose-700 px-2 py-0.5 rounded-full"
                >
                  -{{ product.discountPercent }}%
                </span>
              </div>

              <!-- Color Selector -->
              <div class="mt-5 space-y-2">
                <div class="flex justify-between items-center text-xs font-medium">
                  <span class="text-gray-700">Màu sắc: <strong class="text-gray-900">{{ selectedColor }}</strong></span>
                </div>
                <div class="flex flex-wrap gap-2">
                  <button
                    v-for="color in availableColors"
                    :key="color.name"
                    type="button"
                    @click="selectedColor = color.name"
                    class="flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-medium transition-all"
                    :class="selectedColor === color.name ? 'border-rose-600 bg-rose-50 text-rose-700 shadow-sm' : 'border-gray-200 text-gray-700 hover:bg-gray-50'"
                  >
                    <span class="w-3.5 h-3.5 rounded-full border border-gray-300 shadow-inner" :style="{ backgroundColor: color.hex }"></span>
                    <span>{{ color.name }}</span>
                  </button>
                </div>
              </div>

              <!-- Size Selector -->
              <div class="mt-4 space-y-2">
                <div class="flex justify-between items-center text-xs font-medium">
                  <span class="text-gray-700">Kích thước: <strong class="text-gray-900">{{ selectedSize }}</strong></span>
                </div>
                <div class="flex flex-wrap gap-2">
                  <button
                    v-for="size in availableSizes"
                    :key="size"
                    type="button"
                    @click="selectedSize = size"
                    class="w-10 h-10 rounded-xl border flex items-center justify-center text-xs font-bold transition-all"
                    :class="selectedSize === size ? 'border-rose-600 bg-rose-600 text-white shadow-sm' : 'border-gray-200 text-gray-700 hover:bg-gray-50'"
                  >
                    {{ size }}
                  </button>
                </div>
              </div>

              <!-- Stock Alert -->
              <div class="mt-4 text-xs">
                <span v-if="currentVariant && currentVariant.stockQuantity > 0" class="text-emerald-700 font-medium flex items-center gap-1">
                  <span>✓</span> Còn <strong>{{ currentVariant.stockQuantity }}</strong> sản phẩm trong kho
                </span>
                <span v-else class="text-rose-600 font-semibold flex items-center gap-1">
                  <span>✕</span> Biến thể này hiện đã hết hàng
                </span>
              </div>
            </div>

            <!-- Actions -->
            <div class="mt-6 pt-5 border-t border-gray-100 space-y-3">
              <div class="flex items-center gap-3">
                <!-- Quantity Counter -->
                <div class="flex items-center border border-gray-200 rounded-xl overflow-hidden bg-gray-50">
                  <button
                    type="button"
                    @click="quantity = Math.max(1, quantity - 1)"
                    class="w-9 h-9 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors font-bold text-sm"
                  >-</button>
                  <span class="w-10 text-center font-bold text-sm text-gray-900">{{ quantity }}</span>
                  <button
                    type="button"
                    @click="quantity = currentVariant ? Math.min(currentVariant.stockQuantity, quantity + 1) : quantity + 1"
                    class="w-9 h-9 flex items-center justify-center text-gray-600 hover:bg-gray-200 transition-colors font-bold text-sm"
                  >+</button>
                </div>

                <!-- Add to cart button -->
                <button
                  type="button"
                  @click="handleAddToCart"
                  :disabled="!currentVariant || currentVariant.stockQuantity <= 0"
                  class="flex-1 py-3 px-5 rounded-xl text-white font-bold text-xs uppercase tracking-wider transition-all shadow-md flex items-center justify-center gap-2"
                  :class="(!currentVariant || currentVariant.stockQuantity <= 0) ? 'bg-gray-300 cursor-not-allowed' : 'bg-rose-600 hover:bg-rose-700 active:scale-95 shadow-rose-200'"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                  <span>Thêm Vào Giỏ</span>
                </button>
              </div>

              <!-- Full Detail Link -->
              <router-link
                :to="`/product/${product.slug}`"
                @click="productStore.closeQuickView"
                class="block text-center text-xs text-rose-600 hover:text-rose-800 font-semibold hover:underline"
              >
                Xem đầy đủ chi tiết sản phẩm & hướng dẫn chọn size →
              </router-link>
            </div>
          </div>
        </div>

        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useProductStore } from '@/stores/productStore';
import { useCartStore } from '@/stores/cartStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';
import api from '@/services/api';

const productStore = useProductStore();
const toastStore = useToastStore();
const cartStore = useCartStore();

const product = computed(() => productStore.quickViewProduct);
const variants = ref([]);
const selectedSize = ref('');
const selectedColor = ref('');
const quantity = ref(1);

// Khi mở QuickView, tải chi tiết biến thể nếu chưa có
watch(() => productStore.quickViewProduct, async (newVal) => {
  if (newVal) {
    quantity.value = 1;
    try {
      const res = await api.get(`/products/${newVal.slug}`);
      if (res.success && res.data) {
        variants.value = res.data.variants || [];
        if (variants.value.length > 0) {
          selectedSize.value = variants.value[0].size;
          selectedColor.value = variants.value[0].colorName;
        }
      }
    } catch {
      variants.value = [];
    }
  }
});

const availableSizes = computed(() => {
  const set = new Set();
  variants.value.forEach(v => {
    if (v.size) set.add(v.size);
  });
  return Array.from(set);
});

const availableColors = computed(() => {
  const map = new Map();
  variants.value.forEach(v => {
    if (v.colorName && !map.has(v.colorName)) {
      map.set(v.colorName, { name: v.colorName, hex: v.colorHex || '#000000' });
    }
  });
  return Array.from(map.values());
});

const currentVariant = computed(() => {
  return variants.value.find(
    v => v.size === selectedSize.value && v.colorName === selectedColor.value
  ) || variants.value[0];
});

const currentPrice = computed(() => {
  if (currentVariant.value) return currentVariant.value.price;
  if (product.value) return product.value.salePrice || product.value.basePrice;
  return 0;
});

function handleAddToCart() {
  if (!product.value || !currentVariant.value) return;
  const res = cartStore.addToCart(product.value, currentVariant.value, quantity.value);
  if (res.success) {
    productStore.closeQuickView();
  } else {
    toastStore.error(res.message);
  }
}
</script>
