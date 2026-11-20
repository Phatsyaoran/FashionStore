<template>
  <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">

    <!-- Account Header Card -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-6 sm:p-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div class="flex items-center gap-4">
        <div class="w-14 h-14 rounded-2xl bg-gradient-to-tr from-rose-600 to-amber-500 text-white flex items-center justify-center text-xl font-black shadow-md shadow-rose-200 shrink-0">
          {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
        </div>
        <div class="min-w-0">
          <div class="flex items-center gap-2 flex-wrap">
            <h1 class="text-xl sm:text-2xl font-black text-gray-900 truncate">
              {{ authStore.user?.fullName || 'Khách hàng' }}
            </h1>
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold uppercase bg-rose-50 text-rose-700 border border-rose-100">
              {{ authStore.isAdmin ? 'Quản trị viên' : 'Thành viên' }}
            </span>
          </div>
          <p class="text-xs text-gray-500 mt-0.5 truncate">{{ authStore.user?.email }}</p>
          <p v-if="authStore.user?.phone" class="text-[11px] text-gray-400 mt-0.5">
            📞 {{ authStore.user.phone }}
            <span v-if="authStore.user?.address"> • 📍 {{ authStore.user.address }}</span>
          </p>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <router-link
          v-if="authStore.isAdmin"
          to="/admin"
          class="px-4 py-2 rounded-xl bg-gray-900 hover:bg-gray-800 text-white text-xs font-bold transition-colors"
        >
          🛠️ Vào Admin
        </router-link>
        <button
          type="button"
          @click="handleLogout"
          class="px-4 py-2 rounded-xl border border-rose-200 bg-rose-50 hover:bg-rose-600 hover:text-white text-rose-600 text-xs font-bold transition-colors"
        >
          Đăng xuất
        </button>
      </div>
    </div>

    <!-- Tabs Navigation -->
    <div class="flex items-center gap-2 border-b border-gray-200">
      <button
        type="button"
        @click="activeTab = 'orders'"
        class="px-5 py-3 text-xs sm:text-sm font-bold border-b-2 transition-colors flex items-center gap-2"
        :class="activeTab === 'orders' ? 'border-rose-600 text-rose-600' : 'border-transparent text-gray-500 hover:text-gray-800'"
      >
        <span>📦</span>
        <span>Đơn hàng của tôi ({{ orders.length }})</span>
      </button>
      <button
        type="button"
        @click="activeTab = 'password'"
        class="px-5 py-3 text-xs sm:text-sm font-bold border-b-2 transition-colors flex items-center gap-2"
        :class="activeTab === 'password' ? 'border-rose-600 text-rose-600' : 'border-transparent text-gray-500 hover:text-gray-800'"
      >
        <span>🔒</span>
        <span>Đổi mật khẩu</span>
      </button>
    </div>

    <!-- TAB 1: MY ORDERS -->
    <div v-if="activeTab === 'orders'" class="space-y-4">
      <div v-if="loadingOrders" class="bg-white rounded-3xl border border-gray-100 p-12 text-center text-xs text-gray-500">
        <span class="inline-block w-6 h-6 border-2 border-rose-500 border-t-transparent rounded-full animate-spin mb-2"></span>
        <div>Đang tải danh sách đơn hàng của bạn...</div>
      </div>

      <div v-else-if="orders.length === 0" class="bg-white rounded-3xl border border-gray-100 p-12 text-center space-y-3">
        <span class="text-4xl block">🛍️</span>
        <h3 class="text-base font-extrabold text-gray-900">Bạn chưa có đơn hàng nào</h3>
        <p class="text-xs text-gray-500 max-w-sm mx-auto">
          Hãy khám phá bộ sưu tập mới nhất của FashionStore và đặt ngay những sản phẩm ưng ý!
        </p>
        <div class="pt-2">
          <router-link
            to="/catalog"
            class="inline-flex items-center gap-2 px-6 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-sm transition-colors"
          >
            Khám phá cửa hàng →
          </router-link>
        </div>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id || order.orderCode"
          class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden"
        >
          <!-- Order Top Bar -->
          <div class="p-4 sm:p-5 bg-gray-50/70 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="flex flex-wrap items-center gap-2.5">
              <span class="font-mono font-extrabold text-sm text-rose-600">#{{ order.orderCode }}</span>
              <span
                class="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold"
                :class="statusBadgeClass(order.orderStatus)"
              >
                {{ translateStatus(order.orderStatus) }}
              </span>
              <span
                class="px-2.5 py-0.5 rounded-full text-[10px] font-bold"
                :class="order.paymentStatus === 'PAID' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'"
              >
                {{ order.paymentStatus === 'PAID' ? '✓ Đã thanh toán' : '⏳ Chưa thanh toán' }} ({{ order.paymentMethod }})
              </span>
            </div>
            <div class="text-[11px] text-gray-500 font-medium">
              Ngày đặt: {{ formatDateTime(order.createdAt) }}
            </div>
          </div>

          <!-- Order Items -->
          <div class="p-4 sm:p-5 divide-y divide-gray-100 text-xs">
            <div
              v-for="item in (order.orderItems || [])"
              :key="item.id"
              class="py-2.5 first:pt-0 last:pb-0 flex items-center justify-between gap-4"
            >
              <div class="min-w-0">
                <p class="font-bold text-gray-900 truncate">{{ item.productName }}</p>
                <p class="text-[11px] text-gray-500 mt-0.5">
                  Size {{ item.size }} • {{ item.colorName }} × {{ item.quantity }}
                </p>
              </div>
              <div class="font-bold text-gray-900 shrink-0">
                {{ formatVND((item.unitPrice || 0) * (item.quantity || 1)) }}
              </div>
            </div>
          </div>

          <!-- Order Footer -->
          <div class="p-4 sm:p-5 bg-gray-50/40 border-t border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="text-[11px] text-gray-500 space-y-0.5">
              <p>📍 {{ order.shippingAddress }}</p>
              <p v-if="order.couponCode" class="text-emerald-700 font-semibold">
                🎟️ Đã áp dụng mã: {{ order.couponCode }} (-{{ formatVND(order.discountAmount || 0) }})
              </p>
            </div>

            <div class="flex flex-wrap items-center gap-3 justify-end">
              <div class="text-right">
                <span class="text-[10px] text-gray-400 block">Tổng thanh toán</span>
                <span class="text-base font-black text-rose-600">{{ formatVND(order.totalAmount) }}</span>
              </div>

              <router-link
                :to="`/order-tracking?orderCode=${order.orderCode}`"
                class="px-3.5 py-2 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-800 text-xs font-bold transition-colors"
              >
                Theo dõi đơn →
              </router-link>

              <button
                v-if="order.orderStatus === 'PENDING'"
                type="button"
                @click="cancelOrder(order)"
                :disabled="cancellingCode === order.orderCode"
                class="px-3.5 py-2 rounded-xl bg-rose-50 hover:bg-rose-600 text-rose-600 hover:text-white border border-rose-200 text-xs font-bold transition-colors disabled:opacity-50"
              >
                {{ cancellingCode === order.orderCode ? 'Đang hủy...' : 'Hủy đơn hàng' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB 2: CHANGE PASSWORD -->
    <div v-else class="bg-white rounded-3xl border border-gray-100 shadow-sm p-6 sm:p-8 max-w-lg">
      <h2 class="text-base font-extrabold text-gray-900">Đổi Mật Khẩu Tài Khoản</h2>
      <p class="text-xs text-gray-500 mt-1">
        Để bảo mật tài khoản, hãy đặt mật khẩu tối thiểu 6 ký tự và không chia sẻ cho người khác.
      </p>

      <form @submit.prevent="handleChangePassword" class="mt-6 space-y-4">
        <div>
          <label class="block text-xs font-bold text-gray-700 mb-1.5">Mật khẩu hiện tại</label>
          <input
            v-model="pwForm.currentPassword"
            type="password"
            required
            placeholder="Nhập mật khẩu hiện tại..."
            class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-gray-700 mb-1.5">Mật khẩu mới</label>
          <input
            v-model="pwForm.newPassword"
            type="password"
            required
            minlength="6"
            placeholder="Tối thiểu 6 ký tự..."
            class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-gray-700 mb-1.5">Xác nhận mật khẩu mới</label>
          <input
            v-model="pwForm.confirmPassword"
            type="password"
            required
            minlength="6"
            placeholder="Nhập lại mật khẩu mới..."
            class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
          />
        </div>

        <button
          type="submit"
          :disabled="changingPassword"
          class="w-full py-3 rounded-xl bg-rose-600 hover:bg-rose-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold text-xs shadow-md shadow-rose-200 transition-all"
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
