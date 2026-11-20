<template>
  <div v-if="productStore.recentlyViewed && productStore.recentlyViewed.length > 0" class="mt-16 pt-12 border-t border-gray-200">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-xl font-bold text-gray-900 tracking-tight flex items-center gap-2">
          <span>👀</span> Sản Phẩm Bạn Vừa Xem
        </h3>
        <p class="text-xs text-gray-500 mt-0.5">Tiếp tục khám phá các mẫu thời trang bạn vừa quan tâm</p>
      </div>

      <button
        v-if="productStore.recentlyViewed.length > 2"
        @click="productStore.clearRecentlyViewed"
        class="text-xs text-gray-400 hover:text-rose-600 transition-colors"
      >
        Xóa lịch sử xem
      </button>
    </div>

    <!-- Grid Cards -->
    <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-4 gap-4">
      <div
        v-for="item in productStore.recentlyViewed"
        :key="item.id"
        class="group relative bg-white rounded-2xl border border-gray-100 p-2.5 shadow-sm hover:shadow-md transition-all duration-300 flex flex-col justify-between"
      >
        <!-- Thumbnail -->
        <div class="relative overflow-hidden rounded-xl bg-gray-100 aspect-[3/4]">
          <router-link :to="`/product/${item.slug}`" class="block w-full h-full">
            <img
              :src="item.thumbnailUrl"
              :alt="item.name"
              @error="onImageError"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
              loading="lazy"
            />
          </router-link>

          <!-- Quick View floating button -->
          <button
            type="button"
            @click.stop="productStore.openQuickView(item)"
            class="absolute bottom-2 inset-x-2 py-1.5 px-3 bg-white/90 backdrop-blur-md rounded-xl text-gray-800 text-xs font-bold shadow-md opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-rose-600 hover:text-white flex items-center justify-center gap-1.5"
          >
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            <span>Xem nhanh</span>
          </button>
        </div>

        <!-- Info -->
        <div class="mt-2.5 px-1">
          <span class="text-[10px] uppercase font-bold text-gray-400">
            {{ item.categoryName || 'Thời Trang' }}
          </span>
          <router-link :to="`/product/${item.slug}`" class="block">
            <h4 class="text-xs font-semibold text-gray-800 line-clamp-1 hover:text-rose-600 transition-colors">
              {{ item.name }}
            </h4>
          </router-link>
          
          <div class="mt-1 flex items-baseline gap-1.5">
            <span class="text-xs font-bold text-rose-600">
              {{ formatVND(item.salePrice || item.basePrice) }}
            </span>
            <span v-if="item.salePrice && item.basePrice > item.salePrice" class="text-[10px] text-gray-400 line-through">
              {{ formatVND(item.basePrice) }}
            </span>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { useProductStore } from '@/stores/productStore';
import { formatVND, onImageError } from '@/services/formatters';

const productStore = useProductStore();
</script>
