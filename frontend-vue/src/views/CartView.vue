<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">
    
    <!-- Breadcrumb & Title -->
    <div>
      <div class="text-xs text-[#6B7280] flex items-center gap-2 mb-1.5">
        <router-link to="/" class="hover:text-[#7A1032] transition-colors">Trang chủ</router-link>
        <span class="text-neutral-300">/</span>
        <span class="text-[#18181B] font-medium">Giỏ hàng của bạn</span>
      </div>
      <h1 class="text-2xl sm:text-3xl font-bold text-[#18181B] tracking-tight">
        Giỏ Hàng Mua Sắm <span class="text-[#7A1032] font-normal text-lg sm:text-xl">({{ cartStore.totalItems }})</span>
      </h1>
    </div>

    <!-- THANH TIẾN TRÌNH FREESHIP ĐỘNG -->
    <FreeShippingBar />

    <!-- Cart Layout -->
    <div v-if="cartStore.items.length > 0" class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
      
      <!-- Left: Items list -->
      <div class="lg:col-span-2 space-y-4">
        <div
          v-for="item in cartStore.items"
          :key="item.variantId"
          class="bg-white rounded-xl p-4 sm:p-5 border border-neutral-200/80 hover:border-[#C9963B]/60 shadow-sm silky-hover flex flex-row items-start sm:items-center gap-4 sm:gap-5"
        >
          <!-- Thumbnail -->
          <img
            :src="item.thumbnailUrl"
            :alt="item.productName"
            @error="onImageError"
            class="w-20 h-24 sm:w-24 sm:h-28 object-cover rounded-lg bg-neutral-100 border border-neutral-200/80 flex-shrink-0"
          />

          <!-- Details -->
          <div class="flex-1 w-full space-y-2 min-w-0">
            <div class="flex justify-between items-start gap-2">
              <router-link :to="`/product/${item.productSlug}`" class="hover:text-[#7A1032] transition-colors">
                <h3 class="text-sm font-semibold text-[#18181B] line-clamp-2">{{ item.productName }}</h3>
              </router-link>
              <button
                type="button"
                @click="cartStore.removeFromCart(item.variantId)"
                class="text-neutral-400 hover:text-[#7A1032] p-1 transition-colors shrink-0"
                title="Xóa khỏi giỏ"
              >
                ✕
              </button>
            </div>

            <!-- Size & Color -->
            <div class="flex items-center gap-3 text-xs text-[#6B7280]">
              <span class="bg-neutral-100 px-2 py-0.5 rounded font-medium text-[#18181B]">Size: {{ item.size }}</span>
              <span class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full border border-neutral-300" :style="{ backgroundColor: item.colorHex }"></span>
                <span>{{ item.colorName }}</span>
              </span>
            </div>

            <div class="flex justify-between items-center pt-2">
              <div class="text-sm font-bold text-[#7A1032]">
                {{ formatVND(item.price) }}
              </div>

              <!-- Counter -->
              <div class="flex items-center border border-neutral-200 rounded-lg overflow-hidden bg-white">
                <button
                  type="button"
                  @click="cartStore.updateQuantity(item.variantId, item.quantity - 1)"
                  class="w-8 h-8 flex items-center justify-center text-[#18181B] hover:bg-neutral-100 font-bold text-xs transition-colors"
                >-</button>
                <span class="w-9 text-center font-bold text-xs text-[#18181B]">{{ item.quantity }}</span>
                <button
                  type="button"
                  @click="cartStore.updateQuantity(item.variantId, item.quantity + 1)"
                  :disabled="item.quantity >= item.stockQuantity"
                  class="w-8 h-8 flex items-center justify-center text-[#18181B] hover:bg-neutral-100 font-bold text-xs disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >+</button>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between items-center pt-3">
          <router-link to="/catalog" class="text-xs font-semibold text-[#7A1032] hover:underline">
            ← Tiếp tục mua sắm
          </router-link>
          <button
            type="button"
            @click="cartStore.clearCart"
            class="text-xs text-[#6B7280] hover:text-[#7A1032] transition-colors"
          >
            Xóa toàn bộ giỏ hàng
          </button>
        </div>
      </div>

      <!-- Right: Summary & Coupon Box (Sticky on Desktop) -->
      <div class="lg:sticky lg:top-24 bg-white rounded-xl p-6 border border-neutral-200/80 shadow-sm space-y-6">
        <h3 class="text-base font-bold text-[#18181B]">Tóm Tắt Đơn Hàng</h3>

        <!-- Coupon input -->
        <div class="space-y-2">
          <label class="block text-xs font-medium text-[#6B7280]">Mã giảm giá (Coupon)</label>
          <div class="flex gap-2">
            <input
              type="text"
              v-model="couponInput"
              placeholder="VD: WELCOME10, SALE50K"
              class="flex-1 h-11 px-3.5 border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 rounded-lg text-xs uppercase font-semibold focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
            />
            <button
              type="button"
              @click="handleApplyCoupon"
              class="h-11 px-4 bg-white hover:bg-[#7A1032] border border-[#7A1032] text-[#7A1032] hover:text-white font-bold text-xs rounded-lg transition-colors shrink-0"
            >
              Áp dụng
            </button>
          </div>

          <!-- Active coupon badge -->
          <div v-if="cartStore.coupon" class="flex justify-between items-center p-2.5 rounded-lg bg-emerald-50 border border-emerald-200 text-xs text-emerald-700">
            <span>✓ Đã áp mã <strong>{{ cartStore.coupon.code }}</strong> (-{{ cartStore.coupon.discountPercent }}%)</span>
            <button @click="cartStore.removeCoupon" class="text-emerald-700 hover:text-rose-600 font-bold ml-2">✕</button>
          </div>
        </div>

        <!-- Breakdown -->
        <div class="space-y-3 pt-4 border-t border-neutral-200/80 text-xs">
          <div class="flex justify-between text-[#6B7280]">
            <span>Tạm tính tiền hàng:</span>
            <span class="font-semibold text-[#18181B]">{{ formatVND(cartStore.subTotal) }}</span>
          </div>

          <div class="flex justify-between text-[#6B7280]">
            <span>Phí vận chuyển:</span>
            <span v-if="cartStore.isFreeShip" class="font-bold text-emerald-700 uppercase">Miễn phí (0đ)</span>
            <span v-else class="font-medium text-[#18181B]">{{ formatVND(cartStore.shippingFee) }}</span>
          </div>

          <div v-if="cartStore.discountAmount > 0" class="flex justify-between text-emerald-700 font-medium">
            <span>Giảm giá khuyến mãi:</span>
            <span>-{{ formatVND(cartStore.discountAmount) }}</span>
          </div>

          <div class="pt-4 border-t border-neutral-200/80 flex justify-between items-baseline">
            <span class="text-sm font-bold text-[#18181B]">Tổng thanh toán:</span>
            <span class="text-2xl font-extrabold text-[#7A1032]">{{ formatVND(cartStore.totalAmount) }}</span>
          </div>
        </div>

        <button
          type="button"
          @click="handleCheckout"
          class="btn-sheen silky-hover active:scale-[0.98] w-full h-12 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider text-center shadow-sm hover:shadow-md"
        >
          Tiến Hành Đặt Hàng →
        </button>
      </div>

    </div>

    <!-- Empty State -->
    <div v-else class="py-20 text-center bg-white rounded-2xl border border-neutral-200/80 shadow-sm p-8 space-y-4">
      <span class="text-5xl block">🛍️</span>
      <h2 class="text-xl font-bold text-[#18181B]">Giỏ hàng của bạn đang trống</h2>
      <p class="text-xs text-[#6B7280] max-w-sm mx-auto">Hãy lựa chọn những bộ trang phục thời thượng và chất lượng từ bộ sưu tập của chúng tôi.</p>
      <div class="pt-2">
        <router-link
          to="/catalog"
          class="btn-sheen inline-flex items-center justify-center h-11 px-6 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider transition-all"
        >
          Khám phá bộ sưu tập ngay
        </router-link>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';
import FreeShippingBar from '@/components/FreeShippingBar.vue';

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const toastStore = useToastStore();
const couponInput = ref('');

function handleCheckout() {
  if (!authStore.isAuthenticated) {
    toastStore.info('Vui lòng đăng nhập để tiến hành đặt hàng');
    router.push({ name: 'login', query: { redirect: '/checkout' } });
    return;
  }
  router.push('/checkout');
}

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
