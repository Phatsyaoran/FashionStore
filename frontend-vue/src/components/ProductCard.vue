<template>
  <div
    class="fs-product-card group relative bg-white rounded-xl border border-neutral-200/80 overflow-hidden shadow-sm flex flex-col justify-between"
  >
    <!-- Thumbnail & Badges -->
    <div class="relative overflow-hidden bg-neutral-100 aspect-[3/4] rounded-t-xl">
      <router-link :to="`/product/${product.slug}`" class="block w-full h-full relative">
        <img
          :src="product.thumbnailUrl"
          :alt="product.name"
          @error="handleImageError"
          class="fs-product-img w-full h-full object-cover"
          loading="lazy"
        />
        <!-- Secondary Gallery Image Crossfade on Hover -->
        <img
          v-if="secondaryImageUrl"
          :src="secondaryImageUrl"
          :alt="`${product.name} - góc nhìn khác`"
          @error="handleImageError"
          class="fs-product-img fs-product-img-secondary absolute inset-0 w-full h-full object-cover"
          loading="lazy"
        />
      </router-link>

      <!-- Flash Sale & Discount Badges -->
      <div
        v-if="product.isFlashSale"
        class="absolute top-2.5 left-2.5 flex flex-col gap-1 items-start z-10"
      >
        <span class="bg-[#7A1032] text-white text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md shadow-sm flex items-center gap-1">
          <span class="text-[#F3D078]">⚡</span> Flash Sale
        </span>
        <span
          v-if="product.discountPercent || (product.basePrice && product.salePrice && product.basePrice > product.salePrice)"
          class="bg-[#18181B] text-[#F3D078] text-[10px] font-bold px-2 py-0.5 rounded-md"
        >
          -{{ product.discountPercent || Math.round(((product.basePrice - product.salePrice) / product.basePrice) * 100) }}%
        </span>
      </div>

      <!-- Regular Discount Tag -->
      <span
        v-else-if="product.discountPercent"
        class="absolute top-2.5 left-2.5 bg-[#7A1032] text-white text-[10px] font-bold px-2 py-0.5 rounded-md shadow-sm z-10"
      >
        -{{ product.discountPercent }}%
      </span>

      <!-- Wishlist Heart Button -->
      <button
        type="button"
        @click.stop="handleToggleWishlist"
        :title="isFavorited ? 'Bỏ thích' : 'Thêm vào yêu thích'"
        class="silky-hover absolute top-2.5 right-2.5 z-10 w-8 h-8 rounded-full bg-white/90 backdrop-blur-md border border-neutral-200/80 flex items-center justify-center shadow-sm hover:border-[#C9963B]/60 hover:scale-105 active:scale-95"
        :class="{ 'animate-heart-pop': heartPopping }"
      >
        <svg
          class="w-4 h-4 transition-colors duration-300"
          :class="isFavorited ? 'text-[#7A1032] fill-[#7A1032]' : 'text-neutral-500 hover:text-[#7A1032] fill-none'"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="1.9"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
          />
        </svg>
      </button>

      <!-- Quick View Action (High-contrast luxury dark wine pill slide-up) -->
      <div class="fs-product-quickview absolute bottom-2.5 inset-x-2.5 z-10">
        <button
          type="button"
          @click.stop="productStore.openQuickView(product)"
          title="Xem nhanh & chọn size"
          class="w-full py-2 px-3 bg-[#180914]/88 hover:bg-[#5C0B27] text-white border border-white/25 shadow-lg backdrop-blur-md rounded-lg font-medium text-[11px] tracking-wide active:scale-[0.98] flex items-center justify-center gap-1.5 transition-colors duration-300"
        >
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          <span>Xem nhanh</span>
        </button>
      </div>
    </div>

    <!-- Product Meta -->
    <div class="p-3.5 sm:p-4 flex flex-col justify-between flex-1">
      <div>
        <div class="flex items-center justify-between gap-2 mb-1">
          <span class="text-[11px] font-semibold tracking-wider uppercase text-[#6B7280] truncate">
            {{ product.categoryName || 'Thời Trang' }}
          </span>
          <span v-if="product.ratingAverage" class="flex items-center gap-0.5 text-[#18181B] font-semibold text-[11px] shrink-0">
            <span class="text-[#C9963B]">★</span> {{ product.ratingAverage }}
          </span>
        </div>

        <router-link :to="`/product/${product.slug}`">
          <h3 class="text-xs sm:text-sm font-medium text-[#18181B] line-clamp-2 hover:text-[#7A1032] transition-colors duration-300 leading-snug min-h-[2.25rem] sm:min-h-[2.5rem]">
            {{ product.name }}
          </h3>
        </router-link>

        <!-- Color dots -->
        <div class="flex items-center gap-1.5 mt-2">
          <span
            v-for="(hex, idx) in (product.availableColorHexes || ['#18181B']).slice(0, 5)"
            :key="idx"
            class="w-3 h-3 rounded-full border border-neutral-300 transition-transform duration-300 hover:scale-125"
            :style="{ backgroundColor: hex }"
            :title="`Màu ${idx + 1}`"
          ></span>
          <span v-if="product.availableColorHexes && product.availableColorHexes.length > 5" class="text-[10px] text-[#6B7280] font-medium ml-0.5">
            +{{ product.availableColorHexes.length - 5 }}
          </span>
        </div>
      </div>

      <!-- Prices & Stock -->
      <div class="mt-3 pt-3 border-t border-neutral-100 flex items-end justify-between gap-2">
        <div class="min-w-0">
          <div
            class="text-sm sm:text-base font-extrabold tracking-tight"
            :class="product.salePrice && product.basePrice > product.salePrice ? 'text-[#7A1032]' : 'text-[#18181B]'"
          >
            {{ formatVND(product.salePrice || product.basePrice) }}
          </div>
          <div v-if="product.salePrice && product.basePrice > product.salePrice" class="text-[11px] text-[#6B7280]/75 line-through">
            {{ formatVND(product.basePrice) }}
          </div>
        </div>

        <!-- Real Stock Badge -->
        <span
          v-if="isInStock"
          class="text-[10px] font-semibold text-[#6B7280] bg-neutral-100 px-2 py-0.5 rounded-md shrink-0"
        >
          Còn hàng
        </span>
        <span
          v-else
          class="text-[10px] font-bold text-[#7A1032] bg-rose-50 px-2 py-0.5 rounded-md border border-rose-200 shrink-0"
        >
          Hết hàng
        </span>
      </div>

      <!-- Flash Sale Progress Bar -->
      <div v-if="showFlashSaleBar || product.isFlashSale" class="mt-2.5 pt-2 border-t border-neutral-100">
        <div class="flex items-center justify-between text-[10px] font-semibold text-[#6B7280] mb-1">
          <span class="text-[#7A1032] font-bold">🔥 Đang bán chạy</span>
          <span>Sắp hết</span>
        </div>
        <div class="w-full bg-neutral-200/70 rounded-full h-1.5 overflow-hidden">
          <div
            class="luxury-gradient h-full rounded-full transition-all duration-700"
            :style="{ width: `${progressPercent}%` }"
          ></div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { useProductStore } from '@/stores/productStore';
import { useWishlistStore } from '@/stores/wishlistStore';
import { useToastStore } from '@/stores/toastStore';
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

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const productStore = useProductStore();
const wishlistStore = useWishlistStore();
const toastStore = useToastStore();

const heartPopping = ref(false);

const secondaryImageUrl = computed(() => {
  const gallery = props.product.galleryImages;
  if (Array.isArray(gallery) && gallery.length > 0) {
    const second = gallery.find((url) => url && url !== props.product.thumbnailUrl);
    return second || null;
  }
  return null;
});

const isFavorited = computed(() => wishlistStore.isInWishlist(props.product.id));

function handleToggleWishlist() {
  if (!authStore.isAuthenticated) {
    toastStore.info('Vui lòng đăng nhập để sử dụng danh sách yêu thích');
    router.push({ name: 'login', query: { redirect: route.fullPath } });
    return;
  }
  heartPopping.value = true;
  setTimeout(() => {
    heartPopping.value = false;
  }, 450);
  const res = wishlistStore.toggleWishlist(props.product);
  if (res?.added) {
    toastStore.success(`Đã thêm "${props.product.name}" vào danh sách yêu thích`);
  } else {
    toastStore.info(`Đã bỏ "${props.product.name}" khỏi danh sách yêu thích`);
  }
}

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

<style scoped>
.fs-product-card {
  transition:
    transform 0.55s cubic-bezier(0.25, 0.8, 0.25, 1),
    box-shadow 0.55s cubic-bezier(0.25, 0.8, 0.25, 1),
    border-color 0.45s ease !important;
  will-change: transform;
}
.fs-product-card:hover {
  transform: translate3d(0, -6px, 0);
  box-shadow: 0 20px 40px -14px rgba(74, 8, 32, 0.16);
  border-color: rgba(143, 90, 50, 0.35);
}

.fs-product-img {
  transition:
    transform 0.75s cubic-bezier(0.25, 0.8, 0.25, 1),
    opacity 0.55s ease !important;
  will-change: transform;
}
.fs-product-img-secondary {
  opacity: 0;
}
.fs-product-card:hover .fs-product-img {
  transform: scale(1.045);
}
.fs-product-card:hover .fs-product-img-secondary {
  opacity: 1;
}

.fs-product-quickview {
  opacity: 0;
  transform: translate3d(0, 12px, 0);
  transition:
    opacity 0.45s cubic-bezier(0.25, 0.8, 0.25, 1),
    transform 0.45s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
.fs-product-card:hover .fs-product-quickview {
  opacity: 1;
  transform: translate3d(0, 0, 0);
}

/* Keep Quick View accessible on touch mobile devices */
@media (max-width: 767px) {
  .fs-product-quickview {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}
</style>
