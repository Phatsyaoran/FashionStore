<template>
  <div class="p-3.5 rounded-xl border transition-all duration-400"
       :class="cartStore.isFreeShip ? 'bg-emerald-50 border-emerald-200' : 'bg-white border-neutral-200/80'">
    <!-- Header Message -->
    <div class="flex items-start gap-2.5 mb-2.5 text-xs sm:text-sm font-medium">
      <span v-if="cartStore.isFreeShip" class="text-base leading-none mt-0.5">🎉</span>
      <span v-else class="text-base leading-none mt-0.5">🚚</span>

      <p v-if="cartStore.isFreeShip" class="text-emerald-900 font-semibold leading-snug">
        Chúc mừng! Đơn hàng đã được <span class="underline font-bold text-emerald-700">Miễn phí vận chuyển</span> (Tiết kiệm 30.000đ)
      </p>
      <p v-else class="text-[#18181B] leading-snug">
        Mua thêm <strong class="text-[#7A1032] font-extrabold">{{ formatVND(cartStore.missingForFreeShip) }}</strong> để được <strong class="text-[#18181B] font-bold">MIỄN PHÍ VẬN CHUYỂN</strong>!
      </p>
    </div>

    <!-- Progress Bar Track -->
    <div class="w-full bg-neutral-200/80 rounded-full h-2.5 overflow-hidden">
      <div
        class="h-full rounded-full transition-all duration-500 ease-out"
        :class="cartStore.isFreeShip ? 'bg-emerald-600' : 'luxury-gradient'"
        :style="{ width: `${cartStore.freeShipPercent}%` }"
      ></div>
    </div>

    <!-- Sub Info -->
    <div class="flex justify-between items-center mt-2 text-xs text-[#6B7280]">
      <span>Tạm tính: <strong class="text-[#18181B] font-bold">{{ formatVND(cartStore.subTotal) }}</strong></span>
      <span class="font-semibold text-[#18181B]">Mốc Freeship: 500.000đ</span>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '@/stores/cartStore';
import { formatVND } from '@/services/formatters';

const cartStore = useCartStore();
</script>
