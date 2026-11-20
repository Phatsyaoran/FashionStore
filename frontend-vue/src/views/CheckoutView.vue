<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
    <!-- Breadcrumb -->
    <div>
      <div class="text-xs text-gray-500 flex items-center gap-2 mb-1">
        <router-link to="/cart" class="hover:text-rose-600">Giỏ hàng</router-link>
        <span>/</span>
        <span class="text-gray-900 font-semibold">Thanh toán</span>
      </div>
      <h1 class="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
        Xác Nhận & Đặt Hàng
      </h1>
    </div>

    <form @submit.prevent="handleCreateOrder" class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
      
      <!-- Left: Customer Information & Payment Method -->
      <div class="lg:col-span-2 space-y-6">
        
        <!-- Section 1: Customer details -->
        <div class="bg-white rounded-3xl p-6 sm:p-8 border border-gray-100 shadow-sm space-y-4">
          <h2 class="text-base font-bold text-gray-900 flex items-center gap-2">
            <span>📍</span> Thông Tin Giao Hàng
          </h2>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="block text-xs font-semibold text-gray-700">Họ và tên người nhận *</label>
              <input
                type="text"
                v-model="form.customerName"
                required
                placeholder="Nguyễn Văn A"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            <div class="space-y-1">
              <label class="block text-xs font-semibold text-gray-700">Số điện thoại *</label>
              <input
                type="tel"
                v-model="form.customerPhone"
                required
                placeholder="0912345678"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>
          </div>

          <div class="space-y-1">
            <label class="block text-xs font-semibold text-gray-700">Địa chỉ giao hàng chi tiết *</label>
            <input
              type="text"
              v-model="form.shippingAddress"
              required
              placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố"
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div class="space-y-1">
            <label class="block text-xs font-semibold text-gray-700">Ghi chú giao hàng (Tùy chọn)</label>
            <textarea
              v-model="form.note"
              rows="2"
              placeholder="Giao giờ hành chính, gọi trước khi đến..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            ></textarea>
          </div>
        </div>

        <!-- Section 2: Payment Methods -->
        <div class="bg-white rounded-3xl p-6 sm:p-8 border border-gray-100 shadow-sm space-y-4">
          <h2 class="text-base font-bold text-gray-900 flex items-center gap-2">
            <span>💳</span> Phương Thức Thanh Toán
          </h2>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- Option 1: COD -->
            <label
              class="relative flex flex-col p-4 rounded-2xl border-2 cursor-pointer transition-all"
              :class="form.paymentMethod === 'COD' ? 'border-rose-600 bg-rose-50/50 text-rose-900 shadow-sm' : 'border-gray-200 hover:border-gray-300 text-gray-700'"
            >
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold flex items-center gap-2">
                  <span>📦</span> COD (Thanh toán khi nhận)
                </span>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="COD"
                  v-model="form.paymentMethod"
                  class="text-rose-600 focus:ring-rose-500"
                />
              </div>
              <p class="text-[11px] text-gray-500 mt-2">
                Thanh toán tiền mặt cho nhân viên giao hàng khi nhận và kiểm tra kiện hàng.
              </p>
            </label>

            <!-- Option 2: VietQR -->
            <label
              class="relative flex flex-col p-4 rounded-2xl border-2 cursor-pointer transition-all"
              :class="form.paymentMethod === 'VIETQR' ? 'border-rose-600 bg-rose-50/50 text-rose-900 shadow-sm' : 'border-gray-200 hover:border-gray-300 text-gray-700'"
            >
              <div class="flex items-center justify-between">
                <span class="text-xs font-bold flex items-center gap-2">
                  <span>⚡</span> Chuyển khoản VietQR QuickLink
                </span>
                <input
                  type="radio"
                  name="paymentMethod"
                  value="VIETQR"
                  v-model="form.paymentMethod"
                  class="text-rose-600 focus:ring-rose-500"
                />
              </div>
              <p class="text-[11px] text-gray-500 mt-2">
                Quét mã QR tự động qua app ngân hàng MB Bank, tự điền đúng số tiền và nội dung.
              </p>
            </label>
          </div>
        </div>

      </div>

      <!-- Right: Order Summary Review -->
      <div class="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm space-y-6">
        <h3 class="text-base font-bold text-gray-900">Chi Tiết Đơn Hàng</h3>

        <div class="max-h-60 overflow-y-auto space-y-3 pr-1 text-xs">
          <div
            v-for="item in cartStore.items"
            :key="item.variantId"
            class="flex items-center gap-3 pb-3 border-b border-gray-100"
          >
            <img :src="item.thumbnailUrl" @error="onImageError" class="w-12 h-14 object-cover rounded-xl bg-gray-50 flex-shrink-0" />
            <div class="flex-1">
              <h4 class="font-bold text-gray-800 line-clamp-1">{{ item.productName }}</h4>
              <p class="text-[11px] text-gray-400">Size {{ item.size }} | {{ item.colorName }} x {{ item.quantity }}</p>
            </div>
            <div class="font-bold text-gray-900">
              {{ formatVND(item.price * item.quantity) }}
            </div>
          </div>
        </div>

        <div class="space-y-2.5 text-xs pt-2">
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
            <span>Giảm giá:</span>
            <span>-{{ formatVND(cartStore.discountAmount) }}</span>
          </div>
          <div class="pt-3 border-t border-gray-200 flex justify-between items-baseline">
            <span class="text-sm font-bold text-gray-900">Tổng thanh toán:</span>
            <span class="text-2xl font-black text-rose-600">{{ formatVND(cartStore.totalAmount) }}</span>
          </div>
        </div>

        <button
          type="submit"
          :disabled="submitting || cartStore.items.length === 0"
          class="w-full py-4 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs uppercase tracking-wider text-center shadow-lg shadow-rose-200 disabled:opacity-50 transition-all flex items-center justify-center gap-2"
        >
          <span v-if="submitting">Đang xử lý đơn hàng...</span>
          <span v-else>Xác Nhận Đặt Hàng Ngay</span>
        </button>
      </div>

    </form>

    <!-- VIETQR POPUP MODAL (Chuẩn VietQR QuickLink MB Bank) -->
    <div v-if="showVietQrModal" class="fixed inset-0 z-50 overflow-y-auto" role="dialog" aria-modal="true">
      <div class="fixed inset-0 bg-black/70 backdrop-blur-sm transition-opacity"></div>

      <div class="flex min-h-screen items-center justify-center p-4 text-center sm:p-0">
        <div class="relative transform overflow-hidden rounded-3xl bg-white text-left shadow-2xl transition-all sm:my-8 sm:w-full sm:max-w-md border border-gray-100 p-6 space-y-6 max-h-[90vh] overflow-y-auto w-full">
          
          <!-- Modal Header -->
          <div class="text-center space-y-1">
            <div class="inline-flex p-3 rounded-2xl bg-rose-50 text-rose-600 mb-1">
              <span class="text-2xl">⚡</span>
            </div>
            <h3 class="text-lg font-bold text-gray-900">Quét Mã VietQR Chuyển Khoản</h3>
            <p class="text-xs text-gray-500">Mã đơn hàng: <strong class="text-rose-600">{{ createdOrder?.orderCode }}</strong></p>
          </div>

          <!-- Dynamic VietQR Image with Fintech Laser Scanline -->
          <div class="bg-gray-50 p-4 rounded-2xl border border-gray-200 flex flex-col items-center">
            <div class="relative overflow-hidden rounded-xl bg-white p-2 border border-gray-200 shadow-md">
              <img
                :src="vietQrUrl"
                alt="Mã QR Chuyển Khoản MB Bank"
                class="w-56 h-auto object-contain rounded-lg"
              />
              <!-- Animated Fintech Scanline -->
              <div class="absolute inset-x-0 h-1 bg-gradient-to-r from-transparent via-rose-500 to-transparent shadow-[0_0_12px_#f43f5e] animate-scanline pointer-events-none"></div>
            </div>
            
            <!-- Countdown 10 mins -->
            <div class="mt-3 text-[11px] font-semibold text-rose-600 flex items-center gap-1.5 bg-rose-50 px-3 py-1 rounded-full border border-rose-200">
              <span>⏱️</span> Mã QR hết hạn sau: <span class="font-mono font-bold">{{ formattedTimer }}</span>
            </div>
          </div>

          <!-- Transfer Info Details -->
          <div class="space-y-2 text-xs bg-gray-50 p-4 rounded-2xl border border-gray-100">
            <div class="flex justify-between items-center py-1 border-b border-gray-200">
              <span class="text-gray-500">Ngân hàng:</span>
              <span class="font-bold text-gray-900">MB Bank (Quân Đội)</span>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-gray-200">
              <span class="text-gray-500">Chủ tài khoản:</span>
              <span class="font-bold text-gray-900">NGUYEN VAN PHAT</span>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-gray-200">
              <span class="text-gray-500">Số tài khoản:</span>
              <div class="flex items-center gap-1.5">
                <span class="font-mono font-bold text-rose-600 text-sm">0123456789</span>
                <button
                  type="button"
                  @click="copyText('0123456789', 'account')"
                  class="text-[10px] font-semibold px-2 py-0.5 rounded transition-all duration-200 border"
                  :class="copiedField === 'account' ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm' : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-100'"
                >
                  {{ copiedField === 'account' ? '✓ Đã chép' : 'Sao chép' }}
                </button>
              </div>
            </div>
            <div class="flex justify-between items-center py-1 border-b border-gray-200">
              <span class="text-gray-500">Số tiền:</span>
              <span class="font-black text-rose-600 text-sm">{{ formatVND(createdOrder?.totalAmount) }}</span>
            </div>
            <div class="flex justify-between items-center py-1">
              <span class="text-gray-500">Nội dung CK:</span>
              <div class="flex items-center gap-1.5">
                <span class="font-mono font-bold text-gray-900">{{ createdOrder?.orderCode }}</span>
                <button
                  type="button"
                  @click="copyText(createdOrder?.orderCode, 'code')"
                  class="text-[10px] font-semibold px-2 py-0.5 rounded transition-all duration-200 border"
                  :class="copiedField === 'code' ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm' : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-100'"
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
              class="w-full py-3.5 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs uppercase tracking-wider shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <span v-if="confirming">Đang xác nhận...</span>
              <span v-else>✓ Tôi Đã Chuyển Khoản</span>
            </button>
            <button
              type="button"
              @click="finishOrderWithoutWait"
              class="w-full py-2 text-xs text-gray-500 hover:text-gray-700 text-center"
            >
              Tôi sẽ thanh toán sau (Về trang tra cứu đơn)
            </button>
          </div>

        </div>
      </div>
    </div>

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
