<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">
    <!-- Breadcrumb -->
    <div>
      <div class="text-xs text-[#6B7280] flex items-center gap-2 mb-1.5">
        <router-link to="/cart" class="hover:text-[#7A1032] transition-colors">Giỏ hàng</router-link>
        <span class="text-neutral-300">/</span>
        <span class="text-[#18181B] font-medium">Thanh toán</span>
      </div>
      <h1 class="text-2xl sm:text-3xl font-bold text-[#18181B] tracking-tight">
        Xác Nhận & Đặt Hàng
      </h1>
    </div>

    <form @submit.prevent="handleCreateOrder" class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
      
      <!-- Left: Customer Information & Payment Method -->
      <div class="lg:col-span-2 space-y-6">
        
        <!-- Section 1: Customer details -->
        <div class="bg-white rounded-xl p-6 sm:p-7 border border-neutral-200/80 shadow-sm space-y-4">
          <h2 class="text-base font-bold text-[#18181B] flex items-center gap-2">
            <span>📍</span> Thông Tin Giao Hàng
          </h2>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="block text-xs font-medium text-[#6B7280]">Họ và tên người nhận *</label>
              <input
                type="text"
                v-model="form.customerName"
                required
                placeholder="Nguyễn Văn A"
                class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
              />
            </div>

            <div class="space-y-1.5">
              <label class="block text-xs font-medium text-[#6B7280]">Số điện thoại *</label>
              <input
                type="tel"
                v-model="form.customerPhone"
                required
                placeholder="0912345678"
                class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
              />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="block text-xs font-medium text-[#6B7280]">Địa chỉ giao hàng chi tiết *</label>
            <input
              type="text"
              v-model="form.shippingAddress"
              required
              placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố"
              class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
            />
          </div>

          <div class="space-y-1.5">
            <label class="block text-xs font-medium text-[#6B7280]">Ghi chú giao hàng (Tùy chọn)</label>
            <textarea
              v-model="form.note"
              rows="2"
              placeholder="Giao giờ hành chính, gọi trước khi đến..."
              class="w-full px-3.5 py-2.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
            ></textarea>
          </div>
        </div>

        <!-- Section 2: Payment Methods -->
        <div class="bg-white rounded-xl p-6 sm:p-7 border border-neutral-200/80 shadow-sm space-y-4">
          <h2 class="text-base font-bold text-[#18181B] flex items-center gap-2">
            <span>💳</span> Phương Thức Thanh Toán
          </h2>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- Option 1: COD -->
            <label
              class="relative flex flex-col p-4 rounded-xl border-2 cursor-pointer transition-all"
              :class="form.paymentMethod === 'COD' ? 'border-[#7A1032] bg-[#7A1032]/[0.04] text-[#18181B]' : 'border-neutral-200 bg-white hover:border-[#C9963B]/70 text-[#6B7280]'"
            >
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold flex items-center gap-2 text-[#18181B]">
                  <span>📦</span> COD (Thanh toán khi nhận)
                </span>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="COD"
                  v-model="form.paymentMethod"
                  class="accent-[#7A1032]"
                />
              </div>
              <p class="text-[11px] text-[#6B7280] mt-2 leading-relaxed">
                Thanh toán tiền mặt cho nhân viên giao hàng khi nhận và kiểm tra kiện hàng.
              </p>
            </label>

            <!-- Option 2: VietQR -->
            <label
              class="relative flex flex-col p-4 rounded-xl border-2 cursor-pointer transition-all"
              :class="form.paymentMethod === 'VIETQR' ? 'border-[#7A1032] bg-[#7A1032]/[0.04] text-[#18181B]' : 'border-neutral-200 bg-white hover:border-[#C9963B]/70 text-[#6B7280]'"
            >
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold flex items-center gap-2 text-[#18181B]">
                  <span>⚡</span> Chuyển khoản VietQR QuickLink
                </span>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="VIETQR"
                  v-model="form.paymentMethod"
                  class="accent-[#7A1032]"
                />
              </div>
              <p class="text-[11px] text-[#6B7280] mt-2 leading-relaxed">
                Quét mã QR tự động qua app ngân hàng MB Bank, tự điền đúng số tiền và nội dung.
              </p>
            </label>
          </div>
        </div>

      </div>

      <!-- Right: Order Summary Review (Sticky on Desktop) -->
      <div class="lg:sticky lg:top-24 bg-white rounded-xl p-6 border border-neutral-200/80 shadow-sm space-y-6">
        <h3 class="text-base font-bold text-[#18181B]">Chi Tiết Đơn Hàng</h3>

        <div class="max-h-60 overflow-y-auto space-y-3 pr-1 text-xs">
          <div
            v-for="item in cartStore.items"
            :key="item.variantId"
            class="flex items-center gap-3 pb-3 border-b border-neutral-200/80 last:border-b-0"
          >
            <img :src="item.thumbnailUrl" @error="onImageError" class="w-12 h-14 object-cover rounded-lg bg-neutral-100 border border-neutral-200/80 flex-shrink-0" />
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-[#18181B] line-clamp-1">{{ item.productName }}</h4>
              <p class="text-[11px] text-[#6B7280]">Size {{ item.size }} | {{ item.colorName }} × {{ item.quantity }}</p>
            </div>
            <div class="font-bold text-[#7A1032] shrink-0">
              {{ formatVND(item.price * item.quantity) }}
            </div>
          </div>
        </div>

        <div class="space-y-2.5 text-xs pt-2 border-t border-neutral-200/80">
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
            <span>Giảm giá:</span>
            <span>-{{ formatVND(cartStore.discountAmount) }}</span>
          </div>
          <div class="pt-3 border-t border-neutral-200/80 flex justify-between items-baseline">
            <span class="text-sm font-bold text-[#18181B]">Tổng thanh toán:</span>
            <span class="text-2xl font-extrabold text-[#7A1032]">{{ formatVND(cartStore.totalAmount) }}</span>
          </div>
        </div>

        <button
          type="submit"
          :disabled="submitting || cartStore.items.length === 0"
          class="btn-sheen silky-hover active:scale-[0.98] w-full h-12 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider text-center disabled:opacity-50 flex items-center justify-center gap-2 shadow-sm hover:shadow-md"
        >
          <span v-if="submitting">Đang xử lý đơn hàng...</span>
          <span v-else>Xác Nhận Đặt Hàng Ngay</span>
        </button>
      </div>

    </form>

    <!-- VIETQR POPUP MODAL (Clean White Luxury) -->
    <Teleport to="body">
      <div v-if="showVietQrModal" class="fixed inset-0 z-[9990] overflow-y-auto no-print" role="dialog" aria-modal="true">
        <div class="fixed inset-0 z-[9990] bg-black/45 backdrop-blur-[2px] transition-opacity"></div>

        <div class="relative z-[9995] flex min-h-screen items-center justify-center p-4 text-center sm:p-0">
          <div class="relative transform overflow-hidden rounded-2xl bg-white text-left text-[#18181B] shadow-2xl transition-all sm:my-8 sm:w-full sm:max-w-md border border-neutral-200/80 p-6 space-y-5 max-h-[90vh] overflow-y-auto w-full">
          
          <!-- Modal Header -->
          <div class="text-center space-y-1">
            <div class="inline-flex w-11 h-11 items-center justify-center rounded-xl luxury-gradient text-[#F3D078] mb-1 shadow-sm">
              <span class="text-lg">⚡</span>
            </div>
            <h3 class="text-lg font-bold text-[#18181B]">Quét Mã VietQR Chuyển Khoản</h3>
            <p class="text-xs text-[#6B7280]">Mã đơn hàng: <strong class="font-mono text-[#7A1032]">{{ createdOrder?.orderCode }}</strong></p>
          </div>

          <!-- Dynamic VietQR Image with Laser Scanline -->
          <div class="bg-[#FAFAFA] p-4 rounded-xl border border-neutral-200/80 flex flex-col items-center">
            <div class="relative overflow-hidden rounded-xl bg-white p-2 border border-neutral-200 shadow-sm">
              <img
                :src="vietQrUrl"
                alt="Mã QR Chuyển Khoản MB Bank"
                class="w-56 h-auto object-contain rounded-lg"
              />
              <!-- Animated Fintech Scanline -->
              <div class="absolute inset-x-0 h-1 bg-gradient-to-r from-transparent via-[#C9963B] to-transparent animate-scanline pointer-events-none"></div>
            </div>
            
            <!-- Countdown 10 mins -->
            <div class="mt-3 text-[11px] font-semibold text-[#7A1032] flex items-center gap-1.5 bg-[#7A1032]/[0.06] px-3 py-1 rounded-md border border-[#7A1032]/20">
              <span>⏱️</span> Mã QR hết hạn sau: <span class="font-mono font-bold">{{ formattedTimer }}</span>
            </div>
          </div>

          <!-- Transfer Info Details -->
          <div class="space-y-2 text-xs bg-[#FAFAFA] p-4 rounded-xl border border-neutral-200/80">
            <div class="flex justify-between items-center py-1 border-b border-neutral-200/80">
              <span class="text-[#6B7280]">Ngân hàng:</span>
              <span class="font-bold text-[#18181B]">MB Bank (Quân Đội)</span>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-neutral-200/80">
              <span class="text-[#6B7280]">Chủ tài khoản:</span>
              <span class="font-bold text-[#18181B]">NGUYEN VAN PHAT</span>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-neutral-200/80">
              <span class="text-[#6B7280]">Số tài khoản:</span>
              <div class="flex items-center gap-1.5">
                <span class="font-mono font-bold text-[#7A1032] text-sm">0123456789</span>
                <button
                  type="button"
                  @click="copyText('0123456789', 'account')"
                  class="text-[10px] font-semibold px-2 py-0.5 rounded transition-all duration-200 border"
                  :class="copiedField === 'account' ? 'bg-emerald-600 text-white border-emerald-500' : 'bg-white border-neutral-200 text-[#18181B] hover:border-[#7A1032]'"
                >
                  {{ copiedField === 'account' ? '✓ Đã chép' : 'Sao chép' }}
                </button>
              </div>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-neutral-200/80">
              <span class="text-[#6B7280]">Số tiền:</span>
              <span class="font-extrabold text-[#7A1032] text-sm">{{ formatVND(createdOrder?.totalAmount) }}</span>
            </div>
            <div class="flex justify-between items-center py-1">
              <span class="text-[#6B7280]">Nội dung CK:</span>
              <div class="flex items-center gap-1.5">
                <span class="font-mono font-bold text-[#18181B]">{{ createdOrder?.orderCode }}</span>
                <button
                  type="button"
                  @click="copyText(createdOrder?.orderCode, 'code')"
                  class="text-[10px] font-semibold px-2 py-0.5 rounded transition-all duration-200 border"
                  :class="copiedField === 'code' ? 'bg-emerald-600 text-white border-emerald-500' : 'bg-white border-neutral-200 text-[#18181B] hover:border-[#7A1032]'"
                >
                  {{ copiedField === 'code' ? '✓ Đã chép' : 'Sao chép' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Confirm button -->
          <div class="space-y-2">
            <button
              type="button"
              @click="handleConfirmPayment"
              :disabled="confirming"
              class="btn-sheen w-full h-11 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider transition-all flex items-center justify-center gap-2 disabled:opacity-50"
            >
              <span v-if="confirming">Đang xác nhận...</span>
              <span v-else>✓ Tôi Đã Chuyển Khoản</span>
            </button>
            <button
              type="button"
              @click="finishOrderWithoutWait"
              class="w-full py-2 text-xs text-[#6B7280] hover:text-[#18181B] text-center transition-colors"
            >
              Tôi sẽ thanh toán sau (Về trang tra cứu đơn)
            </button>
          </div>

        </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import { useAuthStore } from '@/stores/authStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';
import api from '@/services/api';

const router = useRouter();
const cartStore = useCartStore();
const authStore = useAuthStore();
const toastStore = useToastStore();

const submitting = ref(false);
const confirming = ref(false);
const showVietQrModal = ref(false);
const createdOrder = ref(null);

const form = reactive({
  customerName: authStore.user?.fullName || '',
  customerPhone: authStore.user?.phone || '',
  shippingAddress: authStore.user?.address || '',
  note: '',
  paymentMethod: 'COD'
});

// Dynamic VietQR QuickLink URL
const vietQrUrl = computed(() => {
  if (!createdOrder.value) return '';
  const bankId = 'MB';
  const accountNo = '0123456789';
  const accountName = encodeURIComponent('NGUYEN VAN PHAT');
  const amount = createdOrder.value.totalAmount;
  const orderCode = createdOrder.value.orderCode;
  return `https://img.vietqr.io/image/${bankId}-${accountNo}-compact2.png?amount=${amount}&addInfo=${orderCode}&accountName=${accountName}`;
});

// 10-minute timer for QR
const timeLeft = ref(600); // 10 minutes in seconds
let timerInterval = null;
const copiedField = ref(null);
let copyTimeout = null;

const formattedTimer = computed(() => {
  const m = Math.floor(timeLeft.value / 60);
  const s = timeLeft.value % 60;
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
});

function startTimer() {
  timeLeft.value = 600;
  clearInterval(timerInterval);
  timerInterval = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value -= 1;
    } else {
      clearInterval(timerInterval);
    }
  }, 1000);
}

onUnmounted(() => {
  clearInterval(timerInterval);
  if (copyTimeout) clearTimeout(copyTimeout);
});

function copyText(text, fieldName) {
  if (navigator.clipboard && text) {
    navigator.clipboard.writeText(text);
    copiedField.value = fieldName;
    if (copyTimeout) clearTimeout(copyTimeout);
    copyTimeout = setTimeout(() => {
      copiedField.value = null;
    }, 2000);
  }
}

async function handleCreateOrder() {
  if (submitting.value) return;

  if (cartStore.items.length === 0) {
    toastStore.error('Giỏ hàng trống! Vui lòng chọn sản phẩm trước khi thanh toán.');
    return;
  }

  if (!form.customerName.trim()) {
    toastStore.error('Vui lòng nhập họ và tên người nhận hàng.');
    return;
  }

  const cleanPhone = form.customerPhone.trim();
  if (!cleanPhone || !/^0\d{9,10}$/.test(cleanPhone)) {
    toastStore.error('Vui lòng nhập số điện thoại người nhận hợp lệ (10 chữ số, bắt đầu bằng số 0).');
    return;
  }

  if (!form.shippingAddress.trim()) {
    toastStore.error('Vui lòng nhập địa chỉ nhận hàng chi tiết.');
    return;
  }

  submitting.value = true;
  try {
    const payload = {
      customerName: form.customerName,
      customerPhone: form.customerPhone,
      shippingAddress: form.shippingAddress,
      note: form.note,
      paymentMethod: form.paymentMethod,
      couponCode: cartStore.coupon?.code || null,
      items: cartStore.items.map(i => ({
        productVariantId: i.variantId,
        quantity: i.quantity
      }))
    };

    const res = await api.post('/orders', payload);
    if (res.success && res.data) {
      createdOrder.value = res.data;
      cartStore.clearCart();

      if (form.paymentMethod === 'VIETQR') {
        showVietQrModal.value = true;
        startTimer();
      } else {
        toastStore.success('Đặt hàng thành công! Mã đơn: ' + res.data.orderCode);
        router.push(`/order-tracking?orderCode=${res.data.orderCode}`);
      }
    } else {
      toastStore.error(res.message || 'Không thể tạo đơn hàng');
    }
  } catch (err) {
    const msg = err.response?.data?.message || err.friendlyMessage || 'Lỗi khi tạo đơn hàng, vui lòng thử lại';
    toastStore.error(msg);
  } finally {
    submitting.value = false;
  }
}

async function handleConfirmPayment() {
  if (!createdOrder.value) return;
  confirming.value = true;
  try {
    const res = await api.post(`/orders/${createdOrder.value.orderCode}/confirm-payment`);
    if (res.success) {
      toastStore.success(res.message || 'Xác nhận thanh toán thành công!');
      showVietQrModal.value = false;
      router.push(`/order-tracking?orderCode=${createdOrder.value.orderCode}`);
    } else {
      toastStore.error(res.message || 'Không thể xác nhận thanh toán. Vui lòng thử lại!');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi kết nối khi xác nhận thanh toán. Vui lòng thử lại!');
  } finally {
    confirming.value = false;
  }
}

function finishOrderWithoutWait() {
  showVietQrModal.value = false;
  if (createdOrder.value) {
    router.push(`/order-tracking?orderCode=${createdOrder.value.orderCode}`);
  }
}
</script>
