<template>
  <div class="p-3.5 rounded-xl border transition-all duration-300"
       :class="cartStore.isFreeShip ? 'bg-emerald-50/80 border-emerald-200' : 'bg-amber-50/80 border-amber-200'">
    <!-- Header Message -->
    <div class="flex items-center gap-2 mb-2 text-xs font-medium">
      <span v-if="cartStore.isFreeShip" class="text-base">🎉</span>
      <span v-else class="text-base">🚚</span>

      <p v-if="cartStore.isFreeShip" class="text-emerald-800 font-semibold leading-tight">
        Chúc mừng! Đơn hàng của bạn đã được <span class="text-emerald-700 underline font-bold">Miễn phí vận chuyển</span> (Tiết kiệm 30.000đ)
      </p>
      <p v-else class="text-amber-900 leading-tight">
        Mua thêm <strong class="text-rose-600 font-bold text-sm">{{ formatVND(cartStore.missingForFreeShip) }}</strong> nữa để được <strong class="text-amber-950 uppercase">MIỄN PHÍ VẬN CHUYỂN</strong> toàn quốc!
      </p>
    </div>

    <!-- Progress Bar Track -->
    <div class="w-full bg-gray-200/80 rounded-full h-2.5 overflow-hidden shadow-inner">
      <div
        class="h-full rounded-full transition-all duration-500 ease-out"
        :class="cartStore.isFreeShip ? 'bg-gradient-to-r from-emerald-500 to-teal-500' : 'bg-gradient-to-r from-amber-500 to-rose-500'"
        :style="{ width: `${cartStore.freeShipPercent}%` }"
      ></div>
    </div>

    <!-- Sub Info -->
    <div class="flex justify-between items-center mt-1.5 text-[11px] text-gray-500">
      <span>Tạm tính: {{ formatVND(cartStore.subTotal) }}</span>
      <span class="font-semibold text-gray-700">Mốc Freeship: 500.000đ</span>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '@/stores/cartStore';
import { formatVND } from '@/services/formatters';

const cartStore = useCartStore();
</script>
