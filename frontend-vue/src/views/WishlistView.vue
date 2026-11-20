<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight flex items-center gap-2.5">
          <span>❤️</span>
          <span>Danh Sách Yêu Thích</span>
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Bạn đang lưu <strong>{{ wishlistStore.totalWishlist }}</strong> sản phẩm yêu thích trong tài khoản
        </p>
      </div>

      <div class="flex items-center gap-3">
        <button
          v-if="wishlistStore.items.length > 0"
          type="button"
          @click="handleClearAll"
          class="px-4 py-2 rounded-xl border border-rose-200 bg-rose-50 hover:bg-rose-600 hover:text-white text-rose-600 text-xs font-bold transition-colors"
        >
          🗑️ Xóa tất cả
        </button>
        <router-link
          to="/catalog"
          class="px-4 py-2 rounded-xl bg-gray-900 hover:bg-gray-800 text-white text-xs font-bold transition-colors"
        >
          Tiếp tục mua sắm →
        </router-link>
      </div>
    </div>

    <!-- Empty State -->
    <div
      v-if="wishlistStore.items.length === 0"
      class="bg-white rounded-3xl border border-gray-100 shadow-sm py-20 px-6 text-center space-y-4"
    >
      <span class="text-5xl block">🤍</span>
      <h2 class="text-lg font-extrabold text-gray-900">Chưa có sản phẩm yêu thích nào</h2>
      <p class="text-xs text-gray-500 max-w-md mx-auto">
        Hãy bấm biểu tượng trái tim trên những mẫu thiết kế bạn ưng ý để dễ dàng xem lại và đặt mua bất cứ lúc nào.
      </p>
      <div class="pt-2">
        <router-link
          to="/catalog"
          class="inline-flex items-center gap-2 px-6 py-3 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold shadow-md shadow-rose-200 transition-all"
        >
          Khám phá cửa hàng ngay →
        </router-link>
      </div>
    </div>

    <!-- Wishlist Grid -->
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
      <div
        v-for="item in wishlistStore.items"
        :key="item.id"
        class="group bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg transition-all overflow-hidden flex flex-col justify-between"
      >
        <div class="relative aspect-[3/4] bg-gray-100 overflow-hidden">
          <router-link :to="`/product/${item.slug}`" class="block w-full h-full">
            <img
              :src="item.thumbnailUrl"
              :alt="item.name"
              @error="onImageError"
              loading="lazy"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            />
          </router-link>

          <button
            type="button"
            @click="handleRemove(item)"
            class="absolute top-2.5 right-2.5 w-8 h-8 rounded-full bg-white/95 hover:bg-rose-600 text-rose-600 hover:text-white shadow-sm flex items-center justify-center text-xs font-bold transition-colors"
            title="Xóa khỏi yêu thích"
          >
            ✕
          </button>

          <span
            v-if="item.isFlashSale"
            class="absolute top-2.5 left-2.5 bg-gradient-to-r from-rose-600 to-orange-500 text-white text-[10px] font-black uppercase px-2 py-0.5 rounded-full shadow"
          >
            ⚡ Flash Sale
          </span>
        </div>

        <div class="p-3.5 sm:p-4 flex flex-col justify-between flex-1 space-y-3">
          <div>
            <span class="text-[10px] font-bold uppercase tracking-wider text-gray-400">
              {{ item.categoryName || 'Thời Trang' }}
            </span>
            <router-link :to="`/product/${item.slug}`">
              <h3 class="text-xs sm:text-sm font-bold text-gray-900 hover:text-rose-600 line-clamp-2 mt-0.5 transition-colors">
                {{ item.name }}
              </h3>
            </router-link>
          </div>

          <div class="space-y-2.5 pt-2 border-t border-gray-50">
            <div class="flex items-baseline gap-2">
              <span class="text-sm sm:text-base font-extrabold text-rose-600">
                {{ formatVND(item.salePrice || item.basePrice) }}
              </span>
              <span
                v-if="item.salePrice && item.basePrice > item.salePrice"
                class="text-[11px] text-gray-400 line-through"
              >
                {{ formatVND(item.basePrice) }}
              </span>
            </div>

            <button
              type="button"
              @click="productStore.openQuickView(item)"
              class="w-full py-2 rounded-xl bg-rose-50 hover:bg-rose-600 text-rose-600 hover:text-white text-xs font-bold transition-colors"
            >
              🛍️ Chọn size & Thêm giỏ
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
