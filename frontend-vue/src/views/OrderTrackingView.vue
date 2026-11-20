<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-8">
    
    <!-- Header -->
    <div class="text-center space-y-2">
      <span class="text-3xl">📦</span>
      <h1 class="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
        Tra Cứu Tiến Độ Đơn Hàng
      </h1>
      <p class="text-xs text-gray-500 max-w-md mx-auto">
        Nhập Mã đơn hàng (VD: <code class="font-mono text-rose-600 font-bold">DH2026111501</code>) hoặc Số điện thoại đặt hàng để kiểm tra lộ trình giao vận.
      </p>
    </div>

    <!-- Search Form -->
    <form @submit.prevent="handleSearch" class="bg-white p-4 sm:p-6 rounded-3xl border border-gray-100 shadow-sm flex flex-col sm:flex-row gap-3">
      <input
        type="text"
        v-model="searchQuery"
        required
        placeholder="Nhập mã đơn hàng hoặc số điện thoại..."
        class="flex-1 px-4 py-3 rounded-2xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none uppercase font-semibold"
      />
      <button
        type="submit"
        :disabled="loading"
        class="px-8 py-3 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs shadow-md shadow-rose-200 transition-all flex items-center justify-center gap-2"
      >
        <span v-if="loading">Đang tra cứu...</span>
        <span v-else>🔍 Tìm Đơn Hàng</span>
      </button>
    </form>

    <!-- Error message -->
    <div v-if="errorMessage" class="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-700 text-xs font-semibold text-center">
      {{ errorMessage }}
    </div>

    <!-- Multiple Orders Selector (when searching by Phone) -->
    <div v-if="ordersList.length > 1" class="bg-white rounded-3xl border border-gray-100 shadow-sm p-5 sm:p-6 space-y-3">
      <div class="flex items-center justify-between">
        <h3 class="text-xs font-bold uppercase tracking-wider text-gray-500">
          Tìm thấy {{ ordersList.length }} đơn hàng — Chọn đơn để xem chi tiết
        </h3>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <button
          v-for="item in ordersList"
          :key="item.id || item.orderCode"
          type="button"
          @click="order = item"
          class="text-left p-4 rounded-2xl border transition-all flex items-center justify-between gap-3"
          :class="order?.orderCode === item.orderCode
            ? 'border-rose-500 bg-rose-50/50 ring-2 ring-rose-500/20'
            : 'border-gray-200 hover:border-rose-300 bg-white'"
        >
          <div class="min-w-0">
            <div class="font-mono font-extrabold text-xs text-gray-900">#{{ item.orderCode }}</div>
            <div class="text-[11px] text-gray-500 mt-0.5">{{ formatDateTime(item.createdAt) }}</div>
            <span
              class="inline-block mt-1.5 text-[10px] font-bold px-2 py-0.5 rounded-full"
              :class="item.orderStatus === 'CANCELLED' ? 'bg-rose-100 text-rose-700' : item.orderStatus === 'COMPLETED' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-800'"
            >
              {{ item.orderStatus }}
            </span>
          </div>
          <div class="text-right shrink-0">
            <div class="font-black text-xs text-rose-600">{{ formatVND(item.totalAmount) }}</div>
            <div class="text-[10px] text-gray-400 mt-0.5">{{ (item.orderItems || []).length }} sản phẩm</div>
          </div>
        </button>
      </div>
    </div>

    <!-- Order Result Box -->
    <div v-if="order" class="bg-white rounded-3xl border border-gray-100 shadow-lg p-6 sm:p-8 space-y-8">
      
      <!-- Order Header Summary -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-gray-100 gap-4">
        <div>
          <span class="text-xs text-gray-400 font-medium">Chi tiết mã vận đơn</span>
          <h2 class="text-xl font-extrabold text-gray-900 mt-0.5 flex items-center gap-2">
            <span>#{{ order.orderCode }}</span>
            <span
              class="text-[11px] font-bold px-2.5 py-0.5 rounded-full"
              :class="order.paymentStatus === 'PAID' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'"
            >
              {{ order.paymentStatus === 'PAID' ? '✓ ĐÃ THANH TOÁN' : '⏳ CHƯA THANH TOÁN' }}
            </span>
          </h2>
          <p class="text-xs text-gray-500 mt-1">Ngày tạo: {{ formatDateTime(order.createdAt) }}</p>
        </div>

        <div class="text-left sm:text-right">
          <span class="text-xs text-gray-400">Tổng giá trị đơn hàng:</span>
          <div class="text-2xl font-black text-rose-600">{{ formatVND(order.totalAmount) }}</div>
          <span class="text-[11px] text-gray-500 font-medium uppercase">HTTT: {{ order.paymentMethod }}</span>
        </div>
      </div>

      <!-- VISUAL ORDER STEPPER (5 BƯỚC THEO ORDERSTATUS) -->
      <div v-if="order.orderStatus === 'CANCELLED'" class="p-4 rounded-2xl bg-rose-100 border border-rose-300 text-rose-800 text-center text-xs font-bold">
        ⚠️ ĐƠN HÀNG NÀY ĐÃ BỊ HỦY BỎ
      </div>

      <div v-else class="py-4">
        <h3 class="text-xs font-bold uppercase tracking-wider text-gray-400 mb-6 text-center">
          Tiến Trình Giao Vận Đơn Hàng
        </h3>

        <!-- Stepper Track -->
        <div class="relative flex items-center justify-between max-w-2xl mx-auto">
          <!-- Background Line -->
          <div class="absolute left-0 top-1/2 -translate-y-1/2 w-full h-1 bg-gray-200 z-0"></div>
          
          <!-- Active Line -->
          <div
            class="absolute left-0 top-1/2 -translate-y-1/2 h-1 bg-gradient-to-r from-rose-600 to-emerald-500 z-0 transition-all duration-700"
            :style="{ width: `${currentStepProgress}%` }"
          ></div>

          <!-- Step 1: PENDING -->
          <div class="relative z-10 flex flex-col items-center">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300"
              :class="stepIndex >= 0 ? 'bg-rose-600 text-white shadow-md shadow-rose-200' : 'bg-gray-200 text-gray-500'"
            >
              📝
            </div>
            <span class="text-[11px] font-bold mt-2" :class="stepIndex >= 0 ? 'text-gray-900' : 'text-gray-400'">
              Đã đặt đơn
            </span>
          </div>

          <!-- Step 2: CONFIRMED -->
          <div class="relative z-10 flex flex-col items-center">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300"
              :class="stepIndex >= 1 ? 'bg-rose-600 text-white shadow-md shadow-rose-200' : 'bg-gray-200 text-gray-500'"
            >
              ✓
            </div>
            <span class="text-[11px] font-bold mt-2" :class="stepIndex >= 1 ? 'text-gray-900' : 'text-gray-400'">
              Đã xác nhận
            </span>
          </div>

          <!-- Step 3: PACKING -->
          <div class="relative z-10 flex flex-col items-center">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300"
              :class="stepIndex >= 2 ? 'bg-rose-600 text-white shadow-md shadow-rose-200' : 'bg-gray-200 text-gray-500'"
            >
              📦
            </div>
            <span class="text-[11px] font-bold mt-2" :class="stepIndex >= 2 ? 'text-gray-900' : 'text-gray-400'">
              Đang đóng gói
            </span>
          </div>

          <!-- Step 4: SHIPPING -->
          <div class="relative z-10 flex flex-col items-center">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300"
              :class="stepIndex >= 3 ? 'bg-rose-600 text-white shadow-md shadow-rose-200' : 'bg-gray-200 text-gray-500'"
            >
              🚚
            </div>
            <span class="text-[11px] font-bold mt-2" :class="stepIndex >= 3 ? 'text-gray-900' : 'text-gray-400'">
              Đang giao hàng
            </span>
          </div>

          <!-- Step 5: COMPLETED -->
          <div class="relative z-10 flex flex-col items-center">
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300"
              :class="stepIndex >= 4 ? 'bg-emerald-600 text-white shadow-md shadow-emerald-200' : 'bg-gray-200 text-gray-500'"
            >
              🎉
            </div>
            <span class="text-[11px] font-bold mt-2" :class="stepIndex >= 4 ? 'text-emerald-700 font-extrabold' : 'text-gray-400'">
              Hoàn thành
            </span>
          </div>
        </div>
      </div>

      <!-- Shipping Destination Info -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 bg-gray-50 p-5 rounded-2xl text-xs border border-gray-100">
        <div>
          <h4 class="font-bold text-gray-900 mb-1">Người nhận hàng:</h4>
          <p class="text-gray-700">{{ order.customerName }} - {{ order.customerPhone }}</p>
          <p class="text-gray-500 mt-1">📍 {{ order.shippingAddress }}</p>
        </div>
        <div>
          <h4 class="font-bold text-gray-900 mb-1">Ghi chú đơn:</h4>
          <p class="text-gray-600 italic">{{ order.note || 'Không có ghi chú' }}</p>
        </div>
      </div>

      <!-- Order Items Detail -->
      <div class="space-y-3">
        <h3 class="text-xs font-bold uppercase tracking-wider text-gray-400">Danh Sách Sản Phẩm</h3>
        <div class="divide-y divide-gray-100 text-xs">
          <div
            v-for="item in (order.orderItems || [])"
            :key="item.id"
            class="py-3 flex justify-between items-center"
          >
            <div>
              <h4 class="font-bold text-gray-900">{{ item.productName }}</h4>
              <p class="text-gray-500">Phân loại: Size {{ item.size }} | {{ item.colorName }} (Số lượng: {{ item.quantity }})</p>
            </div>
            <span class="font-bold text-gray-900">{{ formatVND(item.unitPrice * item.quantity) }}</span>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/services/api';
import { formatVND, formatDateTime } from '@/services/formatters';

const route = useRoute();
const searchQuery = ref(route.query.orderCode || '');
const loading = ref(false);
const order = ref(null);
const ordersList = ref([]);
const errorMessage = ref('');

const stepMap = {
  PENDING: 0,
  CONFIRMED: 1,
  PACKING: 2,
  SHIPPING: 3,
  COMPLETED: 4
};

const stepIndex = computed(() => {
  if (!order.value) return 0;
  return stepMap[order.value.orderStatus] !== undefined ? stepMap[order.value.orderStatus] : 0;
});

const currentStepProgress = computed(() => {
  return (stepIndex.value / 4) * 100;
});

async function handleSearch() {
  if (!searchQuery.value.trim()) return;
  loading.value = true;
  errorMessage.value = '';
  order.value = null;
  ordersList.value = [];

  try {
    const q = searchQuery.value.trim();
    const res = await api.get(`/orders/${q}`);
    if (res.success && res.data) {
      if (Array.isArray(res.data)) {
        if (res.data.length > 0) {
          ordersList.value = res.data;
          order.value = res.data[0];
        } else {
          errorMessage.value = `Không tìm thấy đơn hàng nào liên kết với số điện thoại "${q}".`;
        }
      } else {
        ordersList.value = [res.data];
        order.value = res.data;
      }
    } else {
      errorMessage.value = 'Không tìm thấy thông tin đơn hàng khớp với yêu cầu của bạn.';
    }
  } catch (err) {
    errorMessage.value = err.response?.data?.message || err.friendlyMessage || 'Không tìm thấy đơn hàng hoặc có lỗi máy chủ.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (searchQuery.value) {
    handleSearch();
  }
});
</script>
