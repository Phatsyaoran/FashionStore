<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
          Tổng Quan Quản Trị
        </h1>
        <p class="text-xs text-slate-500 mt-0.5">
          Theo dõi doanh thu thực tế, tiến độ xử lý đơn hàng và cảnh báo tồn kho thời gian thực
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <router-link
          to="/admin/orders"
          class="h-9 px-3.5 bg-white hover:bg-slate-50 text-slate-700 border border-slate-200 rounded-lg text-xs font-semibold transition-colors inline-flex items-center gap-1.5"
        >
          <span>📦</span> Đơn hàng
        </router-link>
        <router-link
          to="/admin/products"
          class="h-9 px-3.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-semibold transition-colors inline-flex items-center gap-1.5"
        >
          <span>👔</span> Quản lý Sản phẩm
        </router-link>
      </div>
    </div>

    <!-- Stat Cards 4 Metrics -->
    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <!-- Card 1: Revenue -->
      <div class="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between gap-3">
        <div class="flex justify-between items-center">
          <span class="text-xs font-medium text-slate-500">Doanh thu thực nhận</span>
          <span class="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center text-sm">💰</span>
        </div>
        <div>
          <div class="text-2xl font-bold text-slate-900 tracking-tight">
            {{ formatVND(stats.revenue) }}
          </div>
          <p class="text-[11px] text-emerald-600 font-medium mt-1">
            ✓ Từ các đơn đã thanh toán (PAID)
          </p>
        </div>
      </div>

      <!-- Card 2: Total Orders -->
      <div class="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between gap-3">
        <div class="flex justify-between items-center">
          <span class="text-xs font-medium text-slate-500">Tổng số đơn hàng</span>
          <span class="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center text-sm">📦</span>
        </div>
        <div>
          <div class="text-2xl font-bold text-slate-900 tracking-tight">
            {{ stats.totalOrders }} <span class="text-sm font-medium text-slate-400">đơn</span>
          </div>
          <p class="text-[11px] text-slate-400 mt-1">Toàn bộ lịch sử giao dịch</p>
        </div>
      </div>

      <!-- Card 3: Pending Orders -->
      <div class="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between gap-3">
        <div class="flex justify-between items-center">
          <span class="text-xs font-medium text-slate-500">Đơn cần xử lý</span>
          <span class="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center text-sm">⏳</span>
        </div>
        <div>
          <div class="text-2xl font-bold text-amber-600 tracking-tight">
            {{ stats.pendingOrders }} <span class="text-sm font-medium text-amber-500/80">đơn</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-1">Chờ xác nhận & đóng gói</p>
        </div>
      </div>

      <!-- Card 4: Low-stock count -->
      <div class="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between gap-3">
        <div class="flex justify-between items-center">
          <span class="text-xs font-medium text-slate-500">Cảnh báo tồn kho</span>
          <span class="w-8 h-8 rounded-lg bg-red-50 text-red-600 flex items-center justify-center text-sm">⚠️</span>
        </div>
        <div>
          <div class="text-2xl font-bold tracking-tight" :class="lowStockList.length > 0 ? 'text-red-600' : 'text-slate-900'">
            {{ lowStockList.length }} <span class="text-sm font-medium text-slate-400">biến thể</span>
          </div>
          <p class="text-[11px] text-red-600 font-medium mt-1">Tồn kho ≤ 5 chiếc</p>
        </div>
      </div>
    </div>

    <!-- BIỂU ĐỒ DOANH THU THEO NGÀY (Chart.js — 7 ngày / 30 ngày) -->
    <div class="bg-white rounded-xl p-5 sm:p-6 border border-slate-200 shadow-sm space-y-5">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h3 class="text-sm font-bold text-slate-900">
            Biểu Đồ Doanh Thu & Đơn Hàng Theo Ngày
          </h3>
          <p class="text-xs text-slate-500 mt-0.5">
            Tổng doanh thu thực tế (loại trừ đơn hủy) trong {{ selectedDays }} ngày gần nhất
          </p>
        </div>

        <div class="inline-flex items-center bg-slate-100 p-1 rounded-lg self-start sm:self-auto">
          <button
            type="button"
            @click="switchRevenueDays(7)"
            class="px-3 py-1.5 rounded-md text-xs font-semibold transition-all"
            :class="selectedDays === 7 ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-900'"
          >
            7 ngày qua
          </button>
          <button
            type="button"
            @click="switchRevenueDays(30)"
            class="px-3 py-1.5 rounded-md text-xs font-semibold transition-all"
            :class="selectedDays === 30 ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-900'"
          >
            30 ngày qua
          </button>
        </div>
      </div>

      <div class="relative h-64 sm:h-72 w-full overflow-hidden">
        <div
          v-if="loadingRevenue"
          class="absolute inset-0 bg-white/80 backdrop-blur-[1px] z-10 flex items-center justify-center text-xs text-slate-500 font-medium"
        >
          <span class="w-4 h-4 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mr-2"></span>
          Đang tải biểu đồ doanh thu...
        </div>
        <canvas ref="revenueChartCanvas"></canvas>
      </div>
    </div>

    <!-- BIỂU ĐỒ TRỰC QUAN DOANH THU & SỐ LƯỢNG ĐƠN THEO TRẠNG THÁI (Yêu cầu Phase 7) -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-5">
      <!-- Status Distribution Bar Chart -->
      <div class="lg:col-span-2 bg-white rounded-xl p-5 sm:p-6 border border-slate-200 shadow-sm space-y-5">
        <div class="flex justify-between items-center">
          <div>
            <h3 class="text-sm font-bold text-slate-900">
              Phân Bổ Đơn Hàng Theo Trạng Thái
            </h3>
            <p class="text-xs text-slate-500 mt-0.5">Tỷ lệ xử lý đơn hàng trên chu trình giao vận</p>
          </div>
          <span class="text-xs font-semibold text-slate-500 bg-slate-100 px-2.5 py-1 rounded-md">Tổng: {{ stats.totalOrders }} đơn</span>
        </div>

        <!-- Visual Horizontal Progress Bars -->
        <div class="space-y-4">
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-medium">
              <span class="flex items-center gap-2 text-slate-700">
                <span class="w-2 h-2 rounded-full bg-amber-400"></span> Chờ xử lý (PENDING)
              </span>
              <span class="text-slate-900 font-semibold">{{ orderStatusCounts.PENDING || 0 }} đơn ({{ calcPercent(orderStatusCounts.PENDING) }}%)</span>
            </div>
            <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div class="bg-amber-400 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.PENDING)}%` }"></div>
            </div>
          </div>

          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-medium">
              <span class="flex items-center gap-2 text-slate-700">
                <span class="w-2 h-2 rounded-full bg-sky-500"></span> Đã xác nhận (CONFIRMED)
              </span>
              <span class="text-slate-900 font-semibold">{{ orderStatusCounts.CONFIRMED || 0 }} đơn ({{ calcPercent(orderStatusCounts.CONFIRMED) }}%)</span>
            </div>
            <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div class="bg-sky-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.CONFIRMED)}%` }"></div>
            </div>
          </div>

          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-medium">
              <span class="flex items-center gap-2 text-slate-700">
                <span class="w-2 h-2 rounded-full bg-violet-500"></span> Đang đóng gói (PACKING)
              </span>
              <span class="text-slate-900 font-semibold">{{ orderStatusCounts.PACKING || 0 }} đơn ({{ calcPercent(orderStatusCounts.PACKING) }}%)</span>
            </div>
            <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div class="bg-violet-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.PACKING)}%` }"></div>
            </div>
          </div>

          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-medium">
              <span class="flex items-center gap-2 text-slate-700">
                <span class="w-2 h-2 rounded-full bg-blue-600"></span> Đang giao hàng (SHIPPING)
              </span>
              <span class="text-slate-900 font-semibold">{{ orderStatusCounts.SHIPPING || 0 }} đơn ({{ calcPercent(orderStatusCounts.SHIPPING) }}%)</span>
            </div>
            <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div class="bg-blue-600 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.SHIPPING)}%` }"></div>
            </div>
          </div>

          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-medium">
              <span class="flex items-center gap-2 text-slate-700">
                <span class="w-2 h-2 rounded-full bg-emerald-500"></span> Đã hoàn thành (COMPLETED)
              </span>
              <span class="text-slate-900 font-semibold">{{ orderStatusCounts.COMPLETED || 0 }} đơn ({{ calcPercent(orderStatusCounts.COMPLETED) }}%)</span>
            </div>
            <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div class="bg-emerald-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.COMPLETED)}%` }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Operations Summary -->
      <div class="bg-white rounded-xl p-5 sm:p-6 border border-slate-200 shadow-sm flex flex-col justify-between space-y-5">
        <div>
          <h3 class="text-sm font-bold text-slate-900">
            Truy Cập Nhanh
          </h3>
          <p class="text-xs text-slate-500 mt-0.5">Các tác vụ quản trị thường dùng</p>

          <div class="mt-4 space-y-2.5">
            <router-link
              to="/admin/orders"
              class="w-full p-3.5 rounded-lg bg-slate-50 hover:bg-slate-100 border border-slate-200/80 flex items-center justify-between transition-colors"
            >
              <div class="flex items-center gap-3">
                <span class="text-base">🖨️</span>
                <div class="text-left">
                  <div class="text-xs font-semibold text-slate-900">In Phiếu Giao A6</div>
                  <div class="text-[11px] text-slate-500">Chuẩn nhãn nhiệt TMĐT</div>
                </div>
              </div>
              <span class="text-slate-400 text-xs">→</span>
            </router-link>

            <router-link
              to="/admin/products"
              class="w-full p-3.5 rounded-lg bg-slate-50 hover:bg-slate-100 border border-slate-200/80 flex items-center justify-between transition-colors"
            >
              <div class="flex items-center gap-3">
                <span class="text-base">👕</span>
                <div class="text-left">
                  <div class="text-xs font-semibold text-slate-900">Kho Sản Phẩm & Biến Thể</div>
                  <div class="text-[11px] text-slate-500">Cập nhật giá, ảnh & tồn kho</div>
                </div>
              </div>
              <span class="text-slate-400 text-xs">→</span>
            </router-link>

            <router-link
              to="/admin/coupons"
              class="w-full p-3.5 rounded-lg bg-slate-50 hover:bg-slate-100 border border-slate-200/80 flex items-center justify-between transition-colors"
            >
              <div class="flex items-center gap-3">
                <span class="text-base">🎟️</span>
                <div>
                  <p class="font-semibold text-xs text-slate-900">Quản Lý Mã Giảm Giá</p>
                  <p class="text-[11px] text-slate-500">Tạo, sửa & bật/tắt Coupon</p>
                </div>
              </div>
              <span class="text-slate-400 text-xs">→</span>
            </router-link>

            <router-link
              to="/admin/categories"
              class="w-full p-3.5 rounded-lg bg-slate-50 hover:bg-slate-100 border border-slate-200/80 flex items-center justify-between transition-colors"
            >
              <div class="flex items-center gap-3">
                <span class="text-base">🗂️</span>
                <div>
                  <p class="font-semibold text-xs text-slate-900">Quản Lý Danh Mục</p>
                  <p class="text-[11px] text-slate-500">Thêm, sửa & phân loại nhóm hàng</p>
                </div>
              </div>
              <span class="text-slate-400 text-xs">→</span>
            </router-link>
          </div>
        </div>

        <div class="p-3.5 bg-slate-50 border border-slate-200 rounded-lg text-[11px] text-slate-600">
          <p class="font-semibold text-slate-900">Cơ sở dữ liệu MS SQL Server</p>
          <p class="mt-0.5">FashionStoreDB hoạt động ổn định (Vietnamese_CI_AS).</p>
        </div>
      </div>
    </div>

    <!-- BẢNG CẢNH BÁO BIẾN THỂ SẮP HẾT HÀNG (Yêu cầu Phase 7) -->
    <div class="space-y-3">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h3 class="text-sm font-bold text-slate-900 flex items-center gap-2">
            <span>⚠️</span>
            <span>Cảnh Báo Biến Thể Sắp Hết Hàng (≤ 5 chiếc)</span>
          </h3>
          <p class="text-xs text-slate-500 mt-0.5">
            Cập nhật nhanh số lượng tồn kho trực tiếp để không gián đoạn đơn hàng
          </p>
        </div>

        <button
          type="button"
          @click="loadLowStock"
          class="h-8 px-3 bg-white hover:bg-slate-50 border border-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition-colors self-start sm:self-auto inline-flex items-center gap-1.5"
        >
          <span>🔄</span> Làm mới
        </button>
      </div>

      <div class="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
        <div v-if="loadingLowStock" class="p-8 text-center text-xs text-slate-500">
          Đang nạp danh sách cảnh báo tồn kho...
        </div>

        <div v-else-if="lowStockError" class="p-8 text-center text-xs text-red-600 font-medium bg-red-50/40">
          ⚠️ {{ lowStockError }}
        </div>

        <div v-else-if="lowStockList.length === 0" class="p-10 text-center text-xs text-emerald-700 font-medium bg-emerald-50/30">
          🎉 Hiện tại tất cả các biến thể đều có tồn kho dồi dào trên 5 chiếc!
        </div>

        <table v-else class="w-full min-w-[680px] text-left text-xs">
          <thead class="bg-slate-50 text-xs uppercase tracking-wider text-slate-500 border-b border-slate-200">
            <tr>
              <th class="px-4 py-3 font-semibold">Biến thể</th>
              <th class="px-4 py-3 font-semibold">Tên sản phẩm</th>
              <th class="px-4 py-3 font-semibold">SKU</th>
              <th class="px-4 py-3 font-semibold">Phân loại</th>
              <th class="px-4 py-3 font-semibold">Giá bán</th>
              <th class="px-4 py-3 font-semibold">Tồn kho</th>
              <th class="px-4 py-3 font-semibold text-right">Cập nhật nhanh</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-200/80">
            <tr
              v-for="item in lowStockList"
              :key="item.variantId"
              class="hover:bg-slate-50/80 transition-colors"
            >
              <td class="px-4 py-3">
                <img
                  :src="item.thumbnailUrl || 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=400&q=80'"
                  :alt="item.productName"
                  @error="onImageError"
                  class="w-10 h-12 object-cover rounded-lg bg-slate-100 border border-slate-200"
                />
              </td>
              <td class="px-4 py-3 font-semibold text-slate-900 max-w-xs truncate">
                {{ item.productName }}
              </td>
              <td class="px-4 py-3 font-mono text-slate-600 text-[11px]">
                {{ item.sku }}
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <span class="bg-slate-100 font-semibold px-2 py-0.5 rounded text-[11px] text-slate-700">Size {{ item.size }}</span>
                  <span class="flex items-center gap-1 text-slate-600">
                    <span class="w-3 h-3 rounded-full border border-slate-300" :style="{ backgroundColor: item.colorHex }"></span>
                    <span>{{ item.colorName }}</span>
                  </span>
                </div>
              </td>
              <td class="px-4 py-3 font-semibold text-slate-900">
                {{ formatVND(item.price) }}
              </td>
              <td class="px-4 py-3">
                <span
                  class="px-2.5 py-0.5 rounded-full font-semibold text-[11px] inline-flex items-center gap-1"
                  :class="item.stockQuantity === 0 ? 'bg-red-50 text-red-700 border border-red-200' : 'bg-amber-50 text-amber-700 border border-amber-200'"
                >
                  <span v-if="item.stockQuantity === 0">Hết hàng (0)</span>
                  <span v-else>Còn {{ item.stockQuantity }}</span>
                </span>
              </td>
              
              <!-- NÚT SỬA NHANH SỐ LƯỢNG TỒN KHO TẠI CHỖ (Yêu cầu Phase 7) -->
              <td class="px-4 py-3 text-right">
                <div class="inline-flex items-center gap-1.5">
                  <input
                    type="number"
                    min="0"
                    max="999"
                    v-model.number="editStockInputs[item.variantId]"
                    class="w-16 h-8 px-2 border border-slate-200 rounded-lg text-xs font-semibold text-center focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                  />
                  <button
                    type="button"
                    @click="saveStock(item.variantId)"
                    :disabled="savingVariantId === item.variantId"
                    class="h-8 px-3 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-semibold transition-colors disabled:opacity-50"
                  >
                    <span v-if="savingVariantId === item.variantId">...</span>
                    <span v-else>Lưu</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted, nextTick } from 'vue';
import { Chart, registerables } from 'chart.js';
import api from '@/services/api';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';

Chart.register(...registerables);

const toastStore = useToastStore();

const revenueChartCanvas = ref(null);
const selectedDays = ref(7);
const loadingRevenue = ref(false);
let revenueChartInstance = null;

const stats = reactive({
  revenue: 0,
  totalOrders: 0,
  pendingOrders: 0,
  totalProducts: 0
});

const orderStatusCounts = reactive({
  PENDING: 0,
  CONFIRMED: 0,
  PACKING: 0,
  SHIPPING: 0,
  COMPLETED: 0
});

const lowStockList = ref([]);
const loadingLowStock = ref(false);
const lowStockError = ref('');
const savingVariantId = ref(null);
const editStockInputs = reactive({});

function calcPercent(count) {
  if (!stats.totalOrders || !count) return 0;
  return Math.round((count / stats.totalOrders) * 100);
}

async function loadStats() {
  try {
    const res = await api.get('/admin/dashboard/stats');
    if (res.success && res.data) {
      stats.revenue = res.data.totalRevenue || 0;
      stats.totalOrders = res.data.totalOrders || 0;
      stats.pendingOrders = res.data.pendingOrders || 0;
      stats.totalProducts = res.data.totalProducts || 0;
      if (res.data.ordersByStatus) {
        Object.assign(orderStatusCounts, res.data.ordersByStatus);
      }
    }
  } catch (err) {
    console.error('Lỗi nạp dashboard stats:', err);
  }
}

async function loadLowStock() {
  loadingLowStock.value = true;
  lowStockError.value = '';
  try {
    const res = await api.get('/admin/dashboard/low-stock?threshold=5');
    if (res.success && res.data) {
      lowStockList.value = res.data || [];
      // Khởi tạo giá trị input sửa nhanh
      lowStockList.value.forEach(item => {
        editStockInputs[item.variantId] = item.stockQuantity;
      });
    }
  } catch (err) {
    console.error('Lỗi nạp danh sách sắp hết hàng:', err);
    lowStockList.value = [];
    lowStockError.value = err.response?.data?.message || err.friendlyMessage || 'Không thể tải danh sách cảnh báo tồn kho từ máy chủ.';
  } finally {
    loadingLowStock.value = false;
  }
}

async function saveStock(variantId) {
  if (savingVariantId.value) return;
  const newQty = editStockInputs[variantId];
  if (newQty === undefined || newQty < 0) {
    toastStore.error('Số lượng tồn kho không hợp lệ');
    return;
  }

  savingVariantId.value = variantId;
  try {
    const res = await api.patch(`/admin/variants/${variantId}/stock`, {
      stockQuantity: Number(newQty)
    });
    if (res.success) {
      toastStore.success(res.message || 'Cập nhật số lượng tồn kho thành công!');
      await loadLowStock();
    } else {
      throw new Error(res.message || 'Cập nhật tồn kho thất bại');
    }
  } catch (err) {
    const found = lowStockList.value.find(i => i.variantId === variantId);
    if (found) {
      editStockInputs[variantId] = found.stockQuantity;
    }
    toastStore.error(err.response?.data?.message || err.friendlyMessage || err.message || 'Không thể cập nhật tồn kho. Vui lòng thử lại!');
  } finally {
    savingVariantId.value = null;
  }
}

async function loadRevenueChart(days = selectedDays.value) {
  loadingRevenue.value = true;
  try {
    const res = await api.get('/admin/stats/revenue', { params: { days } });
    if (res.success && Array.isArray(res.data)) {
      await nextTick();
      renderRevenueChart(res.data);
    }
  } catch (err) {
    console.error('Lỗi nạp biểu đồ doanh thu:', err);
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể tải dữ liệu biểu đồ doanh thu.');
  } finally {
    loadingRevenue.value = false;
  }
}

function renderRevenueChart(items) {
  if (!revenueChartCanvas.value) return;
  if (revenueChartInstance) {
    revenueChartInstance.destroy();
    revenueChartInstance = null;
  }

  const labels = items.map(i => i.label);
  const revenues = items.map(i => Number(i.revenue || 0));
  const counts = items.map(i => Number(i.orderCount || 0));

  revenueChartInstance = new Chart(revenueChartCanvas.value, {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          label: 'Doanh thu (VNĐ)',
          data: revenues,
          backgroundColor: 'rgba(225, 29, 72, 0.78)',
          hoverBackgroundColor: 'rgba(225, 29, 72, 0.95)',
          borderRadius: 8,
          maxBarThickness: 32,
          yAxisID: 'y'
        },
        {
          label: 'Số đơn hàng',
          type: 'line',
          data: counts,
          borderColor: '#f59e0b',
          backgroundColor: 'rgba(245, 158, 11, 0.18)',
          borderWidth: 2.5,
          tension: 0.35,
          pointRadius: 3.5,
          pointBackgroundColor: '#f59e0b',
          yAxisID: 'y1'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: {
          position: 'top',
          labels: { usePointStyle: true, font: { size: 11, weight: 'bold' } }
        },
        tooltip: {
          callbacks: {
            label(ctx) {
              if (ctx.datasetIndex === 0) {
                return ` Doanh thu: ${formatVND(ctx.parsed.y)}`;
              }
              return ` Số đơn: ${ctx.parsed.y} đơn`;
            }
          }
        }
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { font: { size: 11 } }
        },
        y: {
          beginAtZero: true,
          position: 'left',
          ticks: {
            font: { size: 11 },
            callback(val) {
              if (val >= 1000000) return (val / 1000000).toFixed(1) + 'tr';
              if (val >= 1000) return Math.round(val / 1000) + 'k';
              return val;
            }
          }
        },
        y1: {
          beginAtZero: true,
          position: 'right',
          grid: { drawOnChartArea: false },
          ticks: { stepSize: 1, precision: 0, font: { size: 11 } }
        }
      }
    }
  });
}

function switchRevenueDays(days) {
  if (selectedDays.value === days) return;
  selectedDays.value = days;
  loadRevenueChart(days);
}

onMounted(() => {
  loadStats();
  loadLowStock();
  loadRevenueChart(selectedDays.value);
});

onUnmounted(() => {
  if (revenueChartInstance) {
    revenueChartInstance.destroy();
    revenueChartInstance = null;
  }
});
</script>
