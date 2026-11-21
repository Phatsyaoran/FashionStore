<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-8">
    
    <!-- Header -->
    <div class="text-center space-y-2">
      <span class="text-[11px] font-bold uppercase tracking-[0.18em] text-[#7A1032]">Order Tracking</span>
      <h1 class="text-2xl sm:text-3xl font-bold text-[#18181B] tracking-tight">
        Tra Cứu Tiến Độ Đơn Hàng
      </h1>
      <p class="text-xs text-[#6B7280] max-w-md mx-auto">
        Nhập Mã đơn hàng (VD: <code class="font-mono text-[#7A1032] font-bold">DH2026111501</code>) hoặc Số điện thoại đặt hàng để kiểm tra lộ trình giao vận.
      </p>
    </div>

    <!-- Search Form -->
    <form @submit.prevent="handleSearch" class="bg-white p-4 sm:p-5 rounded-xl border border-neutral-200/80 shadow-sm flex flex-col sm:flex-row gap-3">
      <input
        type="text"
        v-model="searchQuery"
        required
        placeholder="Nhập mã đơn hàng hoặc số điện thoại..."
        class="flex-1 h-11 px-4 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none uppercase font-semibold"
      />
      <button
        type="submit"
        :disabled="loading"
        class="btn-sheen silky-hover active:scale-[0.98] h-11 px-7 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shrink-0 disabled:opacity-50"
      >
        <span v-if="loading">Đang tra cứu...</span>
        <span v-else>Tìm Đơn Hàng</span>
      </button>
    </form>

    <!-- Error message -->
    <div v-if="errorMessage" class="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs font-semibold text-center">
      {{ errorMessage }}
    </div>

    <!-- Multiple Orders Selector -->
    <div v-if="ordersList.length > 1" class="bg-white rounded-xl border border-neutral-200/80 shadow-sm p-5 sm:p-6 space-y-3">
      <div class="flex items-center justify-between">
        <h3 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">
          Tìm thấy {{ ordersList.length }} đơn hàng — Chọn đơn để xem chi tiết
        </h3>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <button
          v-for="item in ordersList"
          :key="item.id || item.orderCode"
          type="button"
          @click="order = item"
          class="text-left p-4 rounded-xl border transition-all flex items-center justify-between gap-3"
          :class="order?.orderCode === item.orderCode
            ? 'border-[#7A1032] bg-[#7A1032]/[0.04]'
            : 'border-neutral-200 hover:border-[#C9963B] bg-white'"
        >
          <div class="min-w-0">
            <div class="font-mono font-bold text-xs text-[#7A1032]">#{{ item.orderCode }}</div>
            <div class="text-[11px] text-[#6B7280] mt-0.5">{{ formatDateTime(item.createdAt) }}</div>
            <span
              class="inline-block mt-1.5 text-[10px] font-bold px-2 py-0.5 rounded-md"
              :class="item.orderStatus === 'CANCELLED' ? 'bg-rose-50 text-rose-700' : item.orderStatus === 'COMPLETED' ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-800'"
            >
              {{ item.orderStatus }}
            </span>
          </div>
          <div class="text-right shrink-0">
            <div class="font-bold text-xs text-[#18181B]">{{ formatVND(item.totalAmount) }}</div>
            <div class="text-[10px] text-[#6B7280] mt-0.5">{{ (item.orderItems || []).length }} sản phẩm</div>
          </div>
        </button>
      </div>
    </div>

    <!-- Order Result Box -->
    <div v-if="order" class="bg-white rounded-xl border border-neutral-200/80 shadow-sm p-5 sm:p-8 space-y-8">
      
      <!-- Order Header Summary -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-neutral-200/80 gap-4">
        <div>
          <span class="text-xs text-[#6B7280] font-medium">Chi tiết mã vận đơn</span>
          <h2 class="text-xl font-bold text-[#18181B] mt-0.5 flex flex-wrap items-center gap-2">
            <span class="font-mono text-[#7A1032]">#{{ order.orderCode }}</span>
            <span
              class="text-[10px] font-bold px-2.5 py-0.5 rounded-md"
              :class="order.paymentStatus === 'PAID' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-amber-50 text-amber-800 border border-amber-200'"
            >
              {{ order.paymentStatus === 'PAID' ? '✓ ĐÃ THANH TOÁN' : '⏳ CHƯA THANH TOÁN' }}
            </span>
          </h2>
          <p class="text-xs text-[#6B7280] mt-1">Ngày tạo: {{ formatDateTime(order.createdAt) }}</p>
        </div>

        <div class="text-left sm:text-right">
          <span class="text-xs text-[#6B7280]">Tổng giá trị đơn hàng</span>
          <div class="text-2xl font-extrabold text-[#7A1032]">{{ formatVND(order.totalAmount) }}</div>
          <span class="text-[11px] text-[#6B7280] font-medium uppercase">HTTT: {{ order.paymentMethod }}</span>
        </div>
      </div>

      <!-- VISUAL ORDER STEPPER -->
      <div v-if="order.orderStatus === 'CANCELLED'" class="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-center text-xs font-bold">
        ⚠️ ĐƠN HÀNG NÀY ĐÃ BỊ HỦY BỎ
      </div>

      <div v-else class="py-2">
        <h3 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032] mb-6 text-center">
          Tiến Trình Giao Vận Đơn Hàng
        </h3>

        <div class="relative flex items-start justify-between max-w-2xl mx-auto px-1">
          <!-- Background Line -->
          <div class="absolute left-5 right-5 top-4 sm:top-5 h-0.5 bg-neutral-200 z-0"></div>
          
          <!-- Active Line -->
          <div
            class="absolute left-5 top-4 sm:top-5 h-0.5 luxury-gradient z-0 transition-all duration-700"
            :style="{ width: `calc(${currentStepProgress}% - 40px * ${currentStepProgress / 100})` }"
          ></div>

          <div class="relative z-10 flex flex-col items-center w-16 sm:w-24 text-center">
            <div
              class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 border"
              :class="stepIndex >= 0 ? 'bg-[#7A1032] border-[#7A1032] text-white shadow-sm' : 'bg-white border-neutral-200 text-neutral-400'"
            >
              📝
            </div>
            <span class="text-[10px] sm:text-[11px] font-semibold mt-2 leading-tight" :class="stepIndex >= 0 ? 'text-[#18181B]' : 'text-neutral-400'">
              Đã đặt đơn
            </span>
          </div>

          <div class="relative z-10 flex flex-col items-center w-16 sm:w-24 text-center">
            <div
              class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 border"
              :class="stepIndex >= 1 ? 'bg-[#7A1032] border-[#7A1032] text-white shadow-sm' : 'bg-white border-neutral-200 text-neutral-400'"
            >
              ✓
            </div>
            <span class="text-[10px] sm:text-[11px] font-semibold mt-2 leading-tight" :class="stepIndex >= 1 ? 'text-[#18181B]' : 'text-neutral-400'">
              Đã xác nhận
            </span>
          </div>

          <div class="relative z-10 flex flex-col items-center w-16 sm:w-24 text-center">
            <div
              class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 border"
              :class="stepIndex >= 2 ? 'bg-[#7A1032] border-[#7A1032] text-white shadow-sm' : 'bg-white border-neutral-200 text-neutral-400'"
            >
              📦
            </div>
            <span class="text-[10px] sm:text-[11px] font-semibold mt-2 leading-tight" :class="stepIndex >= 2 ? 'text-[#18181B]' : 'text-neutral-400'">
              Đóng gói
            </span>
          </div>

          <div class="relative z-10 flex flex-col items-center w-16 sm:w-24 text-center">
            <div
              class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 border"
              :class="stepIndex >= 3 ? 'bg-[#7A1032] border-[#7A1032] text-white shadow-sm' : 'bg-white border-neutral-200 text-neutral-400'"
            >
              🚚
            </div>
            <span class="text-[10px] sm:text-[11px] font-semibold mt-2 leading-tight" :class="stepIndex >= 3 ? 'text-[#18181B]' : 'text-neutral-400'">
              Đang giao
            </span>
          </div>

          <div class="relative z-10 flex flex-col items-center w-16 sm:w-24 text-center">
            <div
              class="w-8 h-8 sm:w-10 sm:h-10 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 border"
              :class="stepIndex >= 4 ? 'bg-emerald-600 border-emerald-600 text-white shadow-sm' : 'bg-white border-neutral-200 text-neutral-400'"
            >
              🎉
            </div>
            <span class="text-[10px] sm:text-[11px] font-semibold mt-2 leading-tight" :class="stepIndex >= 4 ? 'text-emerald-700 font-bold' : 'text-neutral-400'">
              Hoàn thành
            </span>
          </div>
        </div>
      </div>

      <!-- Shipping Destination Info -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 bg-[#FAFAFA] p-5 rounded-xl text-xs border border-neutral-200/80">
        <div>
          <h4 class="font-bold text-[#7A1032] mb-1">Người nhận hàng</h4>
          <p class="text-[#18181B] font-medium">{{ order.customerName }} - {{ order.customerPhone }}</p>
          <p class="text-[#6B7280] mt-1">📍 {{ order.shippingAddress }}</p>
        </div>
        <div>
          <h4 class="font-bold text-[#7A1032] mb-1">Ghi chú đơn</h4>
          <p class="text-[#6B7280] italic">{{ order.note || 'Không có ghi chú' }}</p>
        </div>
      </div>

      <!-- Order Items Detail -->
      <div class="space-y-3">
        <h3 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Danh Sách Sản Phẩm</h3>
        <div class="divide-y divide-neutral-200/80 text-xs">
          <div
            v-for="item in (order.orderItems || [])"
            :key="item.id"
            class="py-3 flex justify-between items-center gap-4"
          >
            <div class="min-w-0">
              <h4 class="font-semibold text-[#18181B]">{{ item.productName }}</h4>
              <p class="text-[#6B7280] mt-0.5">Phân loại: Size {{ item.size }} | {{ item.colorName }} (Số lượng: {{ item.quantity }})</p>
            </div>
            <span class="font-bold text-[#7A1032] shrink-0">{{ formatVND(item.unitPrice * item.quantity) }}</span>
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
