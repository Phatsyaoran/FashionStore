<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 pb-6 border-b border-neutral-200/80">
      <div>
        <span class="text-[11px] font-bold uppercase tracking-[0.18em] text-[#7A1032]">Saved Items</span>
        <h1 class="text-2xl sm:text-3xl font-bold text-[#18181B] tracking-tight mt-1">
          Danh Sách Yêu Thích
        </h1>
        <p class="text-xs text-[#6B7280] mt-1">
          Bạn đang lưu <strong class="text-[#7A1032]">{{ wishlistStore.totalWishlist }}</strong> sản phẩm yêu thích trong tài khoản
        </p>
      </div>

      <div class="flex items-center gap-2.5">
        <button
          v-if="wishlistStore.items.length > 0"
          type="button"
          @click="handleClearAll"
          class="h-10 px-4 rounded-lg border border-neutral-200 bg-white hover:border-[#7A1032] hover:text-[#7A1032] text-[#6B7280] text-xs font-semibold transition-colors"
        >
          Xóa tất cả
        </button>
        <router-link
          to="/catalog"
          class="btn-sheen h-10 px-4 inline-flex items-center rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold transition-colors"
        >
          Tiếp tục mua sắm →
        </router-link>
      </div>
    </div>

    <!-- Empty State -->
    <div
      v-if="wishlistStore.items.length === 0"
      class="bg-white rounded-2xl border border-neutral-200/80 shadow-sm py-20 px-6 text-center space-y-4"
    >
      <span class="text-4xl block">🤍</span>
      <h2 class="text-lg font-bold text-[#18181B]">Chưa có sản phẩm yêu thích nào</h2>
      <p class="text-xs text-[#6B7280] max-w-md mx-auto">
        Hãy bấm biểu tượng trái tim trên những mẫu thiết kế bạn ưng ý để dễ dàng xem lại và đặt mua bất cứ lúc nào.
      </p>
      <div class="pt-2">
        <router-link
          to="/catalog"
          class="btn-sheen inline-flex items-center justify-center h-11 px-6 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold uppercase tracking-wider transition-all"
        >
          Khám phá cửa hàng ngay →
        </router-link>
      </div>
    </div>

    <!-- Wishlist Grid -->
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-6">
      <div
        v-for="item in wishlistStore.items"
        :key="item.id"
        class="group bg-white rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/60 hover:-translate-y-1.5 hover:shadow-[0_16px_40px_-12px_rgba(92,11,39,0.14)] silky-hover overflow-hidden flex flex-col justify-between"
      >
        <div class="relative aspect-[3/4] bg-neutral-100 overflow-hidden">
          <router-link :to="`/product/${item.slug}`" class="block w-full h-full">
            <img
              :src="item.thumbnailUrl"
              :alt="item.name"
              @error="onImageError"
              loading="lazy"
              class="w-full h-full object-cover group-hover:scale-[1.04] transition-transform duration-700 ease-[cubic-bezier(0.22,1,0.36,1)]"
            />
          </router-link>

          <button
            type="button"
            @click="handleRemove(item)"
            class="absolute top-2.5 right-2.5 w-8 h-8 rounded-full bg-white/90 backdrop-blur-sm border border-neutral-200 hover:bg-[#7A1032] hover:text-white hover:border-[#7A1032] text-[#18181B] flex items-center justify-center text-xs font-bold transition-colors shadow-sm"
            title="Xóa khỏi yêu thích"
          >
            ✕
          </button>

          <span
            v-if="item.isFlashSale"
            class="absolute top-2.5 left-2.5 bg-[#7A1032] text-white text-[10px] font-bold uppercase px-2 py-0.5 rounded-md shadow-sm"
          >
            ⚡ Flash Sale
          </span>
        </div>

        <div class="p-3.5 sm:p-4 flex flex-col justify-between flex-1 space-y-3">
          <div>
            <span class="text-[11px] font-medium uppercase tracking-wider text-[#6B7280]">
              {{ item.categoryName || 'Thời Trang' }}
            </span>
            <router-link :to="`/product/${item.slug}`">
              <h3 class="text-xs sm:text-sm font-medium text-[#18181B] hover:text-[#7A1032] line-clamp-2 mt-1 transition-colors">
                {{ item.name }}
              </h3>
            </router-link>
          </div>

          <div class="space-y-2.5 pt-2.5 border-t border-neutral-200/80">
            <div class="flex items-baseline gap-2">
              <span class="text-sm sm:text-base font-bold text-[#7A1032]">
                {{ formatVND(item.salePrice || item.basePrice) }}
              </span>
              <span
                v-if="item.salePrice && item.basePrice > item.salePrice"
                class="text-[11px] text-neutral-400 line-through"
              >
                {{ formatVND(item.basePrice) }}
              </span>
            </div>

            <button
              type="button"
              @click="productStore.openQuickView(item)"
              class="btn-sheen silky-hover active:scale-[0.98] w-full h-10 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold"
            >
              Chọn size & Thêm giỏ
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useWishlistStore } from '@/stores/wishlistStore';
import { useProductStore } from '@/stores/productStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';

const wishlistStore = useWishlistStore();
const productStore = useProductStore();
const toastStore = useToastStore();

function handleRemove(item) {
  wishlistStore.removeFromWishlist(item.id);
  toastStore.info(`Đã xóa "${item.name}" khỏi danh sách yêu thích.`);
}

function handleClearAll() {
  if (!confirm('Bạn có chắc chắn muốn xóa toàn bộ danh sách yêu thích?')) return;
  wishlistStore.clearWishlist();
  toastStore.info('Đã dọn sạch danh sách yêu thích.');
}
</script>
