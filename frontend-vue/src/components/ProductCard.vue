<template>
  <div class="group relative bg-white rounded-2xl border border-gray-100 overflow-hidden shadow-card hover:shadow-float transition-all duration-300 flex flex-col justify-between">
    
    <!-- Thumbnail & Badges -->
    <div class="relative overflow-hidden bg-gray-100 aspect-[3/4]">
      <router-link :to="`/product/${product.slug}`" class="block w-full h-full">
        <img
          :src="product.thumbnailUrl"
          :alt="product.name"
          @error="handleImageError"
          class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
          loading="lazy"
        />
      </router-link>

      <!-- Flash Sale & Discount Badges -->
      <div
        v-if="product.isFlashSale"
        class="absolute top-3 left-3 flex flex-col gap-1 items-start z-10"
      >
        <span class="bg-gradient-to-r from-rose-600 to-orange-500 text-white text-[11px] font-extrabold uppercase px-2.5 py-0.5 rounded-full shadow-md animate-pulse flex items-center gap-1">
          <span>⚡</span> Flash Sale
        </span>
        <span v-if="product.discountPercent || (product.basePrice && product.salePrice && product.basePrice > product.salePrice)" class="bg-amber-500 text-white text-[10px] font-black px-2 py-0.5 rounded-full shadow-sm">
          -{{ product.discountPercent || Math.round(((product.basePrice - product.salePrice) / product.basePrice) * 100) }}%
        </span>
      </div>

      <!-- Regular Discount Tag -->
      <span
        v-else-if="product.discountPercent"
        class="absolute top-3 left-3 bg-gradient-to-r from-rose-600 to-orange-500 text-white text-[11px] font-bold px-2 py-0.5 rounded-full shadow-sm z-10"
      >
        -{{ product.discountPercent }}%
      </span>

      <!-- Wishlist Heart Button -->
      <button
        type="button"
        @click.stop="wishlistStore.toggleWishlist(product)"
        :title="isFavorited ? 'Bỏ thích' : 'Thêm vào yêu thích'"
        class="absolute top-3 right-3 z-10 w-8 h-8 rounded-full bg-white/90 backdrop-blur-md flex items-center justify-center shadow-sm hover:scale-110 active:scale-90 transition-all duration-200"
      >
        <svg
          class="w-4 h-4 transition-colors duration-200"
          :class="isFavorited ? 'text-rose-600 fill-rose-600' : 'text-gray-400 hover:text-rose-500 fill-none'"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
          />
        </svg>
      </button>

      <!-- Sliding Action Buttons on Hover -->
      <div class="absolute bottom-3 inset-x-3 flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-y-3 group-hover:translate-y-0 z-10">
        <button
          type="button"
          @click.stop="productStore.openQuickView(product)"
          title="Xem nhanh thông số & chọn size"
          class="flex-1 py-2 px-2 bg-white/95 backdrop-blur-md text-gray-900 rounded-xl font-bold text-xs shadow-md hover:bg-gray-900 hover:text-white transition-all flex items-center justify-center gap-1.5"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          <span>Xem nhanh</span>
        </button>

        <button
          type="button"
          @click.stop="productStore.openQuickView(product)"
          title="Chọn nhanh size và mua"
          class="flex-1 py-2 px-2 bg-rose-600 text-white rounded-xl font-bold text-xs shadow-md hover:bg-rose-700 transition-all flex items-center justify-center gap-1.5"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
          </svg>
          <span>Chọn nhanh</span>
        </button>
      </div>
    </div>

    <!-- Product Meta -->
    <div class="p-4 flex flex-col justify-between flex-1">
      <div>
        <div class="flex items-center justify-between text-xs text-gray-400 mb-1">
          <span class="uppercase font-bold tracking-wider text-[10px] text-gray-400">
            {{ product.categoryName || 'Thời Trang' }}
          </span>
          <span v-if="product.ratingAverage" class="flex items-center gap-1 text-amber-500 font-bold text-xs">
            <span>★</span> {{ product.ratingAverage }}
          </span>
        </div>

        <router-link :to="`/product/${product.slug}`">
          <h3 class="text-sm font-bold text-gray-900 line-clamp-2 hover:text-rose-600 transition-colors leading-snug">
            {{ product.name }}
          </h3>
        </router-link>

        <!-- Color dots directly below product name -->
        <div class="flex items-center gap-1.5 mt-2">
          <span
            v-for="(hex, idx) in (product.availableColorHexes || ['#1E293B']).slice(0, 5)"
            :key="idx"
            class="w-3.5 h-3.5 rounded-full border border-gray-200 shadow-sm transition-transform hover:scale-125"
            :style="{ backgroundColor: hex }"
            :title="`Màu ${idx + 1}`"
          ></span>
          <span v-if="product.availableColorHexes && product.availableColorHexes.length > 5" class="text-[10px] text-gray-400 font-semibold ml-0.5">
            +{{ product.availableColorHexes.length - 5 }}
          </span>
        </div>
      </div>

      <!-- Prices & Stock -->
      <div class="mt-3 pt-3 border-t border-gray-50 flex items-center justify-between">
        <!-- Prices -->
        <div>
          <div class="text-base font-extrabold text-rose-600">
            {{ formatVND(product.salePrice || product.basePrice) }}
          </div>
          <div v-if="product.salePrice && product.basePrice > product.salePrice" class="text-[11px] text-gray-400 line-through">
            {{ formatVND(product.basePrice) }}
          </div>
        </div>

        <!-- Real Stock Badge -->
        <span
          v-if="isInStock"
          class="text-[10px] font-semibold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-md"
        >
          Còn hàng
        </span>
        <span
          v-else
          class="text-[10px] font-bold text-rose-600 bg-rose-50 px-2 py-0.5 rounded-md border border-rose-100"
        >
          Hết hàng
        </span>
      </div>

      <!-- Flash Sale Burning Progress Bar -->
      <div v-if="showFlashSaleBar || product.isFlashSale" class="mt-2.5 pt-2 border-t border-rose-100">
        <div class="flex items-center justify-between text-[11px] font-bold text-rose-600 mb-1">
          <span class="flex items-center gap-1">
            <span class="animate-pulse">🔥</span> Đang bán chạy
          </span>
          <span class="text-rose-500 font-semibold text-[10px]">Sắp cháy hàng</span>
        </div>
        <div class="w-full bg-rose-100 rounded-full h-2 overflow-hidden shadow-inner">
          <div
            class="bg-gradient-to-r from-amber-400 via-rose-500 to-red-600 h-full rounded-full transition-all duration-1000 animate-pulse"
            :style="{ width: `${progressPercent}%` }"
          ></div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useProductStore } from '@/stores/productStore';
import { useWishlistStore } from '@/stores/wishlistStore';
import { formatVND } from '@/services/formatters';

const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  showFlashSaleBar: {
    type: Boolean,
    default: false
  }
});

const productStore = useProductStore();
const wishlistStore = useWishlistStore();

const isFavorited = computed(() => wishlistStore.isInWishlist(props.product.id));

const totalStock = computed(() => {
  if (typeof props.product.totalStock === 'number') {
    return props.product.totalStock;
  }
  if (Array.isArray(props.product.variants)) {
    return props.product.variants.reduce((sum, v) => sum + (v.stockQuantity || 0), 0);
  }
  return 0;
});

const isInStock = computed(() => totalStock.value > 0);

// Fallback image in case external URL fails
const FALLBACK_IMAGE = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80';

function handleImageError(e) {
  if (e && e.target && e.target.src !== FALLBACK_IMAGE) {
    e.target.src = FALLBACK_IMAGE;
  }
}

// Synthetic progress based on product ID for consistent organic look (75% - 94%)
const progressPercent = computed(() => {
  const seed = props.product.id ? Number(props.product.id) : 1;
  return 75 + (seed * 7) % 20;
});
</script>
