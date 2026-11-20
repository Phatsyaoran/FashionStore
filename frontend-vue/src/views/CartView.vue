<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
    
    <!-- Breadcrumb & Title -->
    <div>
      <div class="text-xs text-gray-500 flex items-center gap-2 mb-1">
        <router-link to="/" class="hover:text-rose-600">Trang chủ</router-link>
        <span>/</span>
        <span class="text-gray-900 font-semibold">Giỏ hàng của bạn</span>
      </div>
      <h1 class="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
        Giỏ Hàng Mua Sắm ({{ cartStore.totalItems }} sản phẩm)
      </h1>
    </div>

    <!-- THANH TIẾN TRÌNH FREESHIP ĐỘNG (Yêu cầu Phase 6) -->
    <FreeShippingBar />

    <!-- Cart Layout -->
    <div v-if="cartStore.items.length > 0" class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
      
      <!-- Left: Items list -->
      <div class="lg:col-span-2 space-y-4">
        <div
          v-for="item in cartStore.items"
          :key="item.variantId"
          class="bg-white rounded-3xl p-4 sm:p-6 border border-gray-100 shadow-sm flex flex-row items-start sm:items-center gap-4 sm:gap-5"
        >
          <!-- Thumbnail -->
          <img
            :src="item.thumbnailUrl"
            :alt="item.productName"
            @error="onImageError"
            class="w-20 h-24 sm:w-24 sm:h-28 object-cover rounded-2xl bg-gray-50 flex-shrink-0"
          />

          <!-- Details -->
          <div class="flex-1 w-full space-y-2">
            <div class="flex justify-between items-start">
              <router-link :to="`/product/${item.productSlug}`" class="hover:text-rose-600">
                <h3 class="text-sm font-bold text-gray-900">{{ item.productName }}</h3>
              </router-link>
              <button
                type="button"
                @click="cartStore.removeFromCart(item.variantId)"
                class="text-gray-400 hover:text-rose-600 p-1"
                title="Xóa khỏi giỏ"
              >
                ✕
              </button>
            </div>

            <!-- Size & Color -->
            <div class="flex items-center gap-3 text-xs text-gray-500">
              <span class="bg-gray-100 px-2 py-0.5 rounded-md font-semibold text-gray-700">Size: {{ item.size }}</span>
              <span class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full border border-gray-300" :style="{ backgroundColor: item.colorHex }"></span>
                <span>{{ item.colorName }}</span>
              </span>
            </div>

            <div class="flex justify-between items-center pt-2">
              <div class="text-sm font-bold text-rose-600">
                {{ formatVND(item.price) }}
              </div>

              <!-- Counter -->
              <div class="flex items-center border border-gray-200 rounded-xl overflow-hidden bg-gray-50">
                <button
                  type="button"
                  @click="cartStore.updateQuantity(item.variantId, item.quantity - 1)"
                  class="w-8 h-8 flex items-center justify-center text-gray-700 hover:bg-gray-200 font-bold text-xs"
                >-</button>
                <span class="w-9 text-center font-bold text-xs text-gray-900">{{ item.quantity }}</span>
                <button
                  type="button"
                  @click="cartStore.updateQuantity(item.variantId, item.quantity + 1)"
                  :disabled="item.quantity >= item.stockQuantity"
                  class="w-8 h-8 flex items-center justify-center text-gray-700 hover:bg-gray-200 font-bold text-xs disabled:opacity-40 disabled:cursor-not-allowed"
                >+</button>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between items-center pt-4">
          <router-link to="/catalog" class="text-xs font-bold text-rose-600 hover:underline">
            ← Tiếp tục mua sắm
          </router-link>
          <button
            type="button"
            @click="cartStore.clearCart"
            class="text-xs text-gray-400 hover:text-rose-600"
          >
            Xóa toàn bộ giỏ hàng
          </button>
        </div>
      </div>

      <!-- Right: Summary & Coupon Box -->
      <div class="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm space-y-6">
        <h3 class="text-base font-bold text-gray-900">Tóm Tắt Đơn Hàng</h3>

        <!-- Coupon input -->
        <div class="space-y-2">
          <label class="block text-xs font-medium text-gray-700">Mã giảm giá (Coupon):</label>
          <div class="flex gap-2">
            <input
              type="text"
              v-model="couponInput"
              placeholder="VD: WELCOME10, SALE50K"
              class="flex-1 px-3 py-2 border border-gray-200 rounded-xl text-xs uppercase font-bold focus:outline-none focus:ring-2 focus:ring-rose-500"
            />
            <button
              type="button"
              @click="handleApplyCoupon"
              class="px-4 py-2 bg-gray-900 hover:bg-rose-600 text-white font-bold text-xs rounded-xl transition-colors"
            >
              Áp dụng
            </button>
          </div>

          <!-- Active coupon badge -->
          <div v-if="cartStore.coupon" class="flex justify-between items-center p-2 rounded-xl bg-emerald-50 border border-emerald-200 text-xs text-emerald-800">
            <span>✓ Đã áp mã <strong>{{ cartStore.coupon.code }}</strong> (-{{ cartStore.coupon.discountPercent }}%)</span>
            <button @click="cartStore.removeCoupon" class="text-emerald-700 hover:text-rose-600 font-bold ml-2">✕</button>
          </div>
        </div>

        <!-- Breakdown -->
        <div class="space-y-3 pt-4 border-t border-gray-100 text-xs">
          <div class="flex justify-between text-gray-600">
            <span>Tạm tính tiền hàng:</span>
            <span class="font-bold text-gray-900">{{ formatVND(cartStore.subTotal) }}</span>
          </div>

          <div class="flex justify-between text-gray-600">
            <span>Phí vận chuyển:</span>
            <span v-if="cartStore.isFreeShip" class="font-bold text-emerald-600 uppercase">Miễn phí (0đ)</span>
            <span v-else class="font-medium text-gray-900">{{ formatVND(cartStore.shippingFee) }}</span>
          </div>

          <div v-if="cartStore.discountAmount > 0" class="flex justify-between text-emerald-600 font-medium">
            <span>Giảm giá khuyến mãi:</span>
            <span>-{{ formatVND(cartStore.discountAmount) }}</span>
          </div>

          <div class="pt-4 border-t border-gray-200 flex justify-between items-baseline">
            <span class="text-sm font-bold text-gray-900">Tổng thanh toán:</span>
            <span class="text-2xl font-black text-rose-600">{{ formatVND(cartStore.totalAmount) }}</span>
          </div>
        </div>

        <router-link
          to="/checkout"
          class="block w-full py-4 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs uppercase tracking-wider text-center shadow-lg shadow-rose-200 hover:shadow-rose-300 transition-all"
        >
          Tiến Hành Đặt Hàng →
        </router-link>
      </div>

    </div>

    <!-- Empty State -->
    <div v-else class="py-24 text-center bg-white rounded-3xl border border-gray-100 p-8 space-y-4">
      <span class="text-6xl block">🛍️</span>
      <h2 class="text-xl font-bold text-gray-900">Giỏ hàng của bạn đang trống</h2>
      <p class="text-xs text-gray-500 max-w-sm mx-auto">Hãy lựa chọn những bộ trang phục thời thượng và chất lượng từ bộ sưu tập của chúng tôi.</p>
      <router-link
        to="/catalog"
        class="inline-block px-6 py-3 rounded-2xl bg-rose-600 text-white font-bold text-xs shadow-md shadow-rose-200 hover:bg-rose-700 transition-all"
      >
        Khám phá bộ sưu tập ngay
      </router-link>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useCartStore } from '@/stores/cartStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';
import FreeShippingBar from '@/components/FreeShippingBar.vue';

const cartStore = useCartStore();
const toastStore = useToastStore();
const couponInput = ref('');

async function handleApplyCoupon() {
  if (!couponInput.value) return;
  const res = await cartStore.applyCouponCode(couponInput.value);
  if (res.success) {
    toastStore.success(res.message);
    couponInput.value = '';
  } else {
    toastStore.error(res.message);
  }
}
</script>
