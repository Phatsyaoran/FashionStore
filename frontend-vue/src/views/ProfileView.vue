<template>
  <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">

    <!-- Account Header Card -->
    <div class="bg-white rounded-xl border border-neutral-200/80 shadow-sm p-6 sm:p-7 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex items-center gap-4">
        <div class="w-14 h-14 rounded-xl luxury-gradient text-[#FFFDF9] flex items-center justify-center text-xl font-bold shrink-0 shadow-sm">
          {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
        </div>
        <div class="min-w-0">
          <div class="flex items-center gap-2 flex-wrap">
            <h1 class="text-xl sm:text-2xl font-bold text-[#18181B] truncate tracking-tight">
              {{ authStore.user?.fullName || 'Khách hàng' }}
            </h1>
            <span class="px-2.5 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider bg-[#7A1032]/10 text-[#7A1032] border border-[#7A1032]/20">
              {{ authStore.isAdmin ? 'Quản trị viên' : 'Thành viên' }}
            </span>
          </div>
          <p class="text-xs text-[#6B7280] mt-0.5 truncate">{{ authStore.user?.email }}</p>
          <p v-if="authStore.user?.phone" class="text-[11px] text-[#6B7280] mt-0.5">
            📞 {{ authStore.user.phone }}
            <span v-if="authStore.user?.address"> • 📍 {{ authStore.user.address }}</span>
          </p>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <router-link
          v-if="authStore.isAdmin"
          to="/admin"
          class="h-10 px-4 inline-flex items-center rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold transition-colors"
        >
          🛠️ Vào Admin
        </router-link>
        <button
          type="button"
          @click="handleLogout"
          class="h-10 px-4 rounded-lg border border-neutral-200 bg-white hover:border-[#7A1032] hover:text-[#7A1032] text-[#18181B] text-xs font-semibold transition-colors"
        >
          Đăng xuất
        </button>
      </div>
    </div>

    <!-- Tabs Navigation -->
    <div class="flex items-center gap-6 border-b border-neutral-200/80">
      <button
        type="button"
        @click="activeTab = 'orders'"
        class="pb-3.5 text-xs sm:text-sm font-bold border-b-2 transition-colors flex items-center gap-2"
        :class="activeTab === 'orders' ? 'border-[#7A1032] text-[#7A1032]' : 'border-transparent text-[#6B7280] hover:text-[#18181B]'"
      >
        <span>📦</span>
        <span>Đơn hàng của tôi ({{ orders.length }})</span>
      </button>
      <button
        type="button"
        @click="activeTab = 'password'"
        class="pb-3.5 text-xs sm:text-sm font-bold border-b-2 transition-colors flex items-center gap-2"
        :class="activeTab === 'password' ? 'border-[#7A1032] text-[#7A1032]' : 'border-transparent text-[#6B7280] hover:text-[#18181B]'"
      >
        <span>🔒</span>
        <span>Đổi mật khẩu</span>
      </button>
    </div>

    <!-- TAB 1: MY ORDERS -->
    <div v-if="activeTab === 'orders'" class="space-y-4">
      <div v-if="loadingOrders" class="bg-white rounded-xl border border-neutral-200/80 p-12 text-center text-xs text-[#6B7280]">
        <span class="inline-block w-6 h-6 border-2 border-[#7A1032] border-t-transparent rounded-full animate-spin mb-2"></span>
        <div>Đang tải danh sách đơn hàng của bạn...</div>
      </div>

      <div v-else-if="orders.length === 0" class="bg-white rounded-xl border border-neutral-200/80 shadow-sm p-12 text-center space-y-3">
        <span class="text-4xl block">🛍️</span>
        <h3 class="text-base font-bold text-[#18181B]">Bạn chưa có đơn hàng nào</h3>
        <p class="text-xs text-[#6B7280] max-w-sm mx-auto">
          Hãy khám phá bộ sưu tập mới nhất của FashionStore và đặt ngay những sản phẩm ưng ý!
        </p>
        <div class="pt-2">
          <router-link
            to="/catalog"
            class="btn-sheen inline-flex items-center justify-center h-11 px-6 bg-[#7A1032] hover:bg-[#5C0B27] text-white rounded-lg text-xs font-bold uppercase tracking-wider transition-colors"
          >
            Khám phá cửa hàng →
          </router-link>
        </div>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id || order.orderCode"
          class="bg-white rounded-xl border border-neutral-200/80 shadow-sm overflow-hidden"
        >
          <!-- Order Top Bar -->
          <div class="p-4 sm:p-5 bg-[#FAFAFA] border-b border-neutral-200/80 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="flex flex-wrap items-center gap-2">
              <span class="font-mono font-bold text-sm text-[#7A1032]">#{{ order.orderCode }}</span>
              <span
                class="px-2.5 py-0.5 rounded-md text-[10px] font-bold"
                :class="statusBadgeClass(order.orderStatus)"
              >
                {{ translateStatus(order.orderStatus) }}
              </span>
              <span
                class="px-2.5 py-0.5 rounded-md text-[10px] font-semibold"
                :class="order.paymentStatus === 'PAID' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-amber-50 text-amber-800 border border-amber-200'"
              >
                {{ order.paymentStatus === 'PAID' ? '✓ Đã thanh toán' : '⏳ Chưa thanh toán' }} ({{ order.paymentMethod }})
              </span>
            </div>
            <div class="text-[11px] text-[#6B7280]">
              Ngày đặt: {{ formatDateTime(order.createdAt) }}
            </div>
          </div>

          <!-- Order Items -->
          <div class="p-4 sm:p-5 divide-y divide-neutral-200/80 text-xs">
            <div
              v-for="item in (order.orderItems || [])"
              :key="item.id"
              class="py-2.5 first:pt-0 last:pb-0 flex items-center justify-between gap-4"
            >
              <div class="min-w-0">
                <p class="font-semibold text-[#18181B] truncate">{{ item.productName }}</p>
                <p class="text-[11px] text-[#6B7280] mt-0.5">
                  Size {{ item.size }} • {{ item.colorName }} × {{ item.quantity }}
                </p>
              </div>
              <div class="font-bold text-[#7A1032] shrink-0">
                {{ formatVND((item.unitPrice || 0) * (item.quantity || 1)) }}
              </div>
            </div>
          </div>

          <!-- Order Footer -->
          <div class="p-4 sm:p-5 bg-[#FAFAFA] border-t border-neutral-200/80 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="text-[11px] text-[#6B7280] space-y-0.5">
              <p>📍 {{ order.shippingAddress }}</p>
              <p v-if="order.couponCode" class="text-emerald-700 font-medium">
                🎟️ Đã áp dụng mã: {{ order.couponCode }} (-{{ formatVND(order.discountAmount || 0) }})
              </p>
            </div>

            <div class="flex flex-wrap items-center gap-3 justify-end">
              <div class="text-right">
                <span class="text-[10px] text-[#6B7280] block">Tổng thanh toán</span>
                <span class="text-base font-extrabold text-[#7A1032]">{{ formatVND(order.totalAmount) }}</span>
              </div>

              <router-link
                :to="`/order-tracking?orderCode=${order.orderCode}`"
                class="h-9 px-3.5 inline-flex items-center rounded-lg bg-white border border-neutral-200 hover:border-[#7A1032] hover:text-[#7A1032] text-[#18181B] text-xs font-semibold transition-colors"
              >
                Theo dõi đơn →
              </router-link>

              <button
                v-if="order.orderStatus === 'PENDING'"
                type="button"
                @click="cancelOrder(order)"
                :disabled="cancellingCode === order.orderCode"
                class="h-9 px-3.5 rounded-lg bg-rose-50 hover:bg-rose-600 text-rose-700 hover:text-white border border-rose-200 text-xs font-semibold transition-colors disabled:opacity-50"
              >
                {{ cancellingCode === order.orderCode ? 'Đang hủy...' : 'Hủy đơn hàng' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB 2: CHANGE PASSWORD -->
    <div v-else class="bg-white rounded-xl border border-neutral-200/80 shadow-sm p-6 sm:p-7 max-w-lg">
      <h2 class="text-base font-bold text-[#18181B]">Đổi Mật Khẩu Tài Khoản</h2>
      <p class="text-xs text-[#6B7280] mt-1">
        Để bảo mật tài khoản, hãy đặt mật khẩu tối thiểu 6 ký tự và không chia sẻ cho người khác.
      </p>

      <form @submit.prevent="handleChangePassword" class="mt-6 space-y-4">
        <div class="space-y-1.5">
          <label class="block text-xs font-medium text-[#6B7280]">Mật khẩu hiện tại</label>
          <input
            v-model="pwForm.currentPassword"
            type="password"
            required
            placeholder="Nhập mật khẩu hiện tại..."
            class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
          />
        </div>

        <div class="space-y-1.5">
          <label class="block text-xs font-medium text-[#6B7280]">Mật khẩu mới</label>
          <input
            v-model="pwForm.newPassword"
            type="password"
            required
            minlength="6"
            placeholder="Tối thiểu 6 ký tự..."
            class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
          />
        </div>

        <div class="space-y-1.5">
          <label class="block text-xs font-medium text-[#6B7280]">Xác nhận mật khẩu mới</label>
          <input
            v-model="pwForm.confirmPassword"
            type="password"
            required
            minlength="6"
            placeholder="Nhập lại mật khẩu mới..."
            class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
          />
        </div>

        <button
          type="submit"
          :disabled="changingPassword"
          class="btn-sheen silky-hover active:scale-[0.98] w-full h-11 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold text-xs uppercase tracking-wider"
        >
          {{ changingPassword ? 'Đang cập nhật...' : 'Lưu Mật Khẩu Mới' }}
        </button>
      </form>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, formatDateTime } from '@/services/formatters';

const router = useRouter();
const authStore = useAuthStore();
const toastStore = useToastStore();

const activeTab = ref('orders');
const orders = ref([]);
const loadingOrders = ref(false);
const cancellingCode = ref(null);
const changingPassword = ref(false);

const pwForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

function translateStatus(status) {
  const map = {
    PENDING: '⏳ Chờ xác nhận',
    CONFIRMED: '✅ Đã xác nhận',
    PACKING: '📦 Đang đóng gói',
    SHIPPING: '🚚 Đang giao hàng',
    COMPLETED: '🎉 Hoàn thành',
    CANCELLED: '❌ Đã hủy'
  };
  return map[status] || status;
}

function statusBadgeClass(status) {
  if (status === 'COMPLETED') return 'bg-emerald-100 text-emerald-800';
  if (status === 'CANCELLED') return 'bg-rose-100 text-rose-700';
  if (status === 'SHIPPING') return 'bg-indigo-100 text-indigo-800';
  if (status === 'CONFIRMED' || status === 'PACKING') return 'bg-blue-100 text-blue-800';
  return 'bg-amber-100 text-amber-800';
}

async function loadMyOrders() {
  loadingOrders.value = true;
  try {
    const res = await api.get('/orders/my-orders');
    if (res.success && Array.isArray(res.data)) {
      orders.value = res.data;
    } else {
      orders.value = [];
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể tải danh sách đơn hàng của bạn.');
  } finally {
    loadingOrders.value = false;
  }
}

async function cancelOrder(order) {
  if (cancellingCode.value) return;
  if (!confirm(`Bạn có chắc chắn muốn hủy đơn hàng #${order.orderCode}? Tồn kho sẽ được hoàn lại tự động.`)) {
    return;
  }

  cancellingCode.value = order.orderCode;
  try {
    const res = await api.post(`/orders/${order.orderCode}/cancel`);
    if (res.success && res.data) {
      order.orderStatus = res.data.orderStatus;
      toastStore.success(res.message || `Đã hủy đơn hàng #${order.orderCode} thành công.`);
    } else {
      toastStore.error(res.message || 'Không thể hủy đơn hàng.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể hủy đơn hàng này.');
  } finally {
    cancellingCode.value = null;
  }
}

async function handleChangePassword() {
  if (changingPassword.value) return;

  if (!pwForm.currentPassword || !pwForm.newPassword) {
    toastStore.error('Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới.');
    return;
  }
  if (pwForm.newPassword.length < 6) {
    toastStore.error('Mật khẩu mới phải có tối thiểu 6 ký tự.');
    return;
  }
  if (pwForm.newPassword !== pwForm.confirmPassword) {
    toastStore.error('Xác nhận mật khẩu mới không trùng khớp.');
    return;
  }

  changingPassword.value = true;
  try {
    const res = await api.put('/auth/change-password', {
      currentPassword: pwForm.currentPassword,
      newPassword: pwForm.newPassword
    });
    if (res.success) {
      toastStore.success(res.message || 'Đổi mật khẩu thành công!');
      pwForm.currentPassword = '';
      pwForm.newPassword = '';
      pwForm.confirmPassword = '';
    } else {
      toastStore.error(res.message || 'Không thể đổi mật khẩu.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Mật khẩu hiện tại không đúng hoặc có lỗi máy chủ.');
  } finally {
    changingPassword.value = false;
  }
}

function handleLogout() {
  authStore.logout();
  toastStore.info('Đã đăng xuất khỏi tài khoản.');
  router.push('/');
}

onMounted(() => {
  loadMyOrders();
});
</script>
