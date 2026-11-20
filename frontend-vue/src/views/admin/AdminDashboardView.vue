<template>
  <div class="space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Bảng Điều Khiển Quản Trị (Admin Dashboard)
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Theo dõi doanh thu thực tế, biểu đồ vận hành đơn hàng và cảnh báo tồn kho thời gian thực
        </p>
      </div>

      <div class="flex gap-2">
        <router-link
          to="/admin/orders"
          class="px-4 py-2 bg-gray-900 hover:bg-gray-800 text-white rounded-xl text-xs font-bold shadow-md transition-all flex items-center gap-1.5"
        >
          <span>📦</span> Quản lý Đơn hàng
        </router-link>
        <router-link
          to="/admin/products"
          class="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-md transition-all flex items-center gap-1.5"
        >
          <span>👔</span> Quản lý Sản phẩm
        </router-link>
      </div>
    </div>

    <!-- Stat Cards 4 Metrics -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
      <!-- Card 1: Revenue -->
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-2">
        <div class="flex justify-between items-center text-xs text-gray-500 font-bold uppercase">
          <span>Doanh thu thực nhận</span>
          <span class="p-2 bg-emerald-50 text-emerald-600 rounded-xl">💰</span>
        </div>
        <div class="text-2xl font-black text-emerald-600">
          {{ formatVND(stats.revenue) }}
        </div>
        <p class="text-[11px] text-gray-500 font-medium flex items-center gap-1">
          <span class="text-emerald-500">✓</span> Từ các đơn hàng đã thanh toán (PAID)
        </p>
      </div>

      <!-- Card 2: Total Orders -->
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-2">
        <div class="flex justify-between items-center text-xs text-gray-500 font-bold uppercase">
          <span>Tổng số đơn hàng</span>
          <span class="p-2 bg-rose-50 text-rose-600 rounded-xl">📦</span>
        </div>
        <div class="text-2xl font-black text-gray-900">
          {{ stats.totalOrders }} đơn
        </div>
        <p class="text-[11px] text-gray-400 font-medium">Toàn bộ lịch sử giao dịch</p>
      </div>

      <!-- Card 3: Pending Orders -->
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-2">
        <div class="flex justify-between items-center text-xs text-gray-500 font-bold uppercase">
          <span>Đơn cần xử lý</span>
          <span class="p-2 bg-amber-50 text-amber-600 rounded-xl">⏳</span>
        </div>
        <div class="text-2xl font-black text-amber-600">
          {{ stats.pendingOrders }} đơn
        </div>
        <p class="text-[11px] text-amber-700 font-medium">Chờ xác nhận & đóng gói</p>
      </div>

      <!-- Card 4: Low-stock count -->
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm space-y-2">
        <div class="flex justify-between items-center text-xs text-gray-500 font-bold uppercase">
          <span>Biến thể sắp hết</span>
          <span class="p-2 bg-rose-50 text-rose-600 rounded-xl">⚠️</span>
        </div>
        <div class="text-2xl font-black" :class="lowStockList.length > 0 ? 'text-rose-600' : 'text-gray-900'">
          {{ lowStockList.length }} biến thể
        </div>
        <p class="text-[11px] text-rose-600 font-medium">Tồn kho ≤ 5 chiếc</p>
      </div>
    </div>

    <!-- BIỂU ĐỒ TRỰC QUAN DOANH THU & SỐ LƯỢNG ĐƠN THEO TRẠNG THÁI (Yêu cầu Phase 7) -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Status Distribution Bar Chart -->
      <div class="lg:col-span-2 bg-white rounded-3xl p-6 sm:p-8 border border-gray-100 shadow-sm space-y-6">
        <div class="flex justify-between items-center">
          <div>
            <h3 class="text-base font-bold text-gray-900 flex items-center gap-2">
              <span>📊</span> Phân Bổ Đơn Hàng Theo Trạng Thái
            </h3>
            <p class="text-xs text-gray-500">Tỷ lệ xử lý đơn hàng trên chu trình giao vận</p>
          </div>
          <span class="text-xs font-bold text-gray-400">Tổng: {{ stats.totalOrders }} đơn</span>
        </div>

        <!-- Visual Horizontal Progress Bars -->
        <div class="space-y-4">
          <!-- Pending -->
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-semibold">
              <span class="flex items-center gap-1.5 text-amber-700">
                <span class="w-2.5 h-2.5 rounded-full bg-amber-400"></span> Chờ xử lý (PENDING)
              </span>
              <span class="text-gray-700 font-bold">{{ orderStatusCounts.PENDING || 0 }} đơn ({{ calcPercent(orderStatusCounts.PENDING) }}%)</span>
            </div>
            <div class="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
              <div class="bg-amber-400 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.PENDING)}%` }"></div>
            </div>
          </div>

          <!-- Confirmed -->
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-semibold">
              <span class="flex items-center gap-1.5 text-blue-700">
                <span class="w-2.5 h-2.5 rounded-full bg-blue-500"></span> Đã xác nhận (CONFIRMED)
              </span>
              <span class="text-gray-700 font-bold">{{ orderStatusCounts.CONFIRMED || 0 }} đơn ({{ calcPercent(orderStatusCounts.CONFIRMED) }}%)</span>
            </div>
            <div class="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
              <div class="bg-blue-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.CONFIRMED)}%` }"></div>
            </div>
          </div>

          <!-- Packing -->
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-semibold">
              <span class="flex items-center gap-1.5 text-purple-700">
                <span class="w-2.5 h-2.5 rounded-full bg-purple-500"></span> Đang đóng gói (PACKING)
              </span>
              <span class="text-gray-700 font-bold">{{ orderStatusCounts.PACKING || 0 }} đơn ({{ calcPercent(orderStatusCounts.PACKING) }}%)</span>
            </div>
            <div class="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
              <div class="bg-purple-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.PACKING)}%` }"></div>
            </div>
          </div>

          <!-- Shipping -->
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-semibold">
              <span class="flex items-center gap-1.5 text-indigo-700">
                <span class="w-2.5 h-2.5 rounded-full bg-indigo-500"></span> Đang giao hàng (SHIPPING)
              </span>
              <span class="text-gray-700 font-bold">{{ orderStatusCounts.SHIPPING || 0 }} đơn ({{ calcPercent(orderStatusCounts.SHIPPING) }}%)</span>
            </div>
            <div class="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
              <div class="bg-indigo-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.SHIPPING)}%` }"></div>
            </div>
          </div>

          <!-- Completed -->
          <div class="space-y-1.5">
            <div class="flex justify-between text-xs font-semibold">
              <span class="flex items-center gap-1.5 text-emerald-700">
                <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span> Đã hoàn thành (COMPLETED)
              </span>
              <span class="text-gray-700 font-bold">{{ orderStatusCounts.COMPLETED || 0 }} đơn ({{ calcPercent(orderStatusCounts.COMPLETED) }}%)</span>
            </div>
            <div class="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
              <div class="bg-emerald-500 h-full rounded-full transition-all duration-500" :style="{ width: `${calcPercent(orderStatusCounts.COMPLETED)}%` }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Operations Summary -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-gray-100 shadow-sm flex flex-col justify-between space-y-6">
        <div>
          <h3 class="text-base font-bold text-gray-900 flex items-center gap-2">
            <span>⚙️</span> Thao Tác Vận Hành
          </h3>
          <p class="text-xs text-gray-500 mt-1">Truy cập nhanh các chức năng cốt lõi</p>

          <div class="mt-6 space-y-3">
            <router-link
              to="/admin/orders"
              class="w-full p-4 rounded-2xl bg-gray-50 hover:bg-gray-100 border border-gray-100 flex items-center justify-between transition-all"
            >
              <div class="flex items-center gap-3">
                <span class="text-xl">🖨️</span>
                <div class="text-left">
                  <div class="text-xs font-bold text-gray-900">In Phiếu Giao A6</div>
                  <div class="text-[11px] text-gray-500">In phiếu theo chuẩn TMĐT</div>
                </div>
              </div>
              <span class="text-gray-400 font-bold">→</span>
            </router-link>

            <router-link
              to="/admin/products"
              class="w-full p-4 rounded-2xl bg-gray-50 hover:bg-gray-100 border border-gray-100 flex items-center justify-between transition-all"
            >
              <div class="flex items-center gap-3">
                <span class="text-xl">👕</span>
                <div class="text-left">
                  <div class="text-xs font-bold text-gray-900">26 Mẫu Sản Phẩm</div>
                  <div class="text-[11px] text-gray-500">68 biến thể size & màu</div>
                </div>
              </div>
              <span class="text-gray-400 font-bold">→</span>
            </router-link>
          </div>
        </div>

        <div class="p-4 bg-rose-50/70 border border-rose-100 rounded-2xl text-[11px] text-rose-800">
          <p class="font-bold">Hệ thống MS SQL Server:</p>
          <p class="text-rose-600 mt-0.5">FashionStoreDB đang hoạt động chuẩn Collation Vietnamese_CI_AS.</p>
        </div>
      </div>
    </div>

    <!-- BẢNG CẢNH BÁO BIẾN THỂ SẮP HẾT HÀNG (Yêu cầu Phase 7) -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <!-- Table Header -->
      <div class="p-6 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 class="text-base font-bold text-gray-900 flex items-center gap-2">
            <span class="text-rose-600">⚠️</span>
            <span>Cảnh Báo Biến Thể Sắp Hết Hàng (Dưới hoặc bằng 5 chiếc)</span>
          </h3>
          <p class="text-xs text-gray-500 mt-0.5">
            Cập nhật nhanh số lượng tồn kho trực tiếp để không bị gián đoạn đơn hàng của khách
          </p>
        </div>

        <button
          type="button"
          @click="loadLowStock"
          class="px-3.5 py-1.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-bold rounded-xl transition-colors self-start sm:self-auto flex items-center gap-1.5"
        >
          <span>🔄</span> Làm mới danh sách
        </button>
      </div>

      <!-- Table Body -->
      <div class="overflow-x-auto">
        <div v-if="loadingLowStock" class="p-8 text-center text-xs text-gray-500">
          Đang nạp danh sách cảnh báo tồn kho...
        </div>

        <div v-else-if="lowStockError" class="p-8 text-center text-xs text-rose-600 font-semibold bg-rose-50/40">
          ⚠️ {{ lowStockError }}
        </div>

        <div v-else-if="lowStockList.length === 0" class="p-12 text-center text-xs text-emerald-700 font-semibold bg-emerald-50/40">
          🎉 Hiện tại tất cả các biến thể đều có tồn kho dồi dào trên 5 chiếc!
        </div>

        <table v-else class="w-full min-w-[650px] text-left text-xs">
          <thead class="bg-gray-50 text-gray-400 font-bold uppercase tracking-wider border-b border-gray-100">
            <tr>
              <th class="p-4">Biến thể</th>
              <th class="p-4">Tên sản phẩm</th>
              <th class="p-4">SKU</th>
              <th class="p-4">Phân loại</th>
              <th class="p-4">Giá bán</th>
              <th class="p-4">Tồn kho hiện tại</th>
              <th class="p-4 text-right">Sửa nhanh tồn kho</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="item in lowStockList"
              :key="item.variantId"
              class="hover:bg-rose-50/30 transition-colors"
            >
              <td class="p-4">
                <img
                  :src="item.thumbnailUrl || 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=400&q=80'"
                  :alt="item.productName"
                  @error="onImageError"
                  class="w-12 h-14 object-cover rounded-xl bg-gray-100 border border-gray-100"
                />
              </td>
              <td class="p-4 font-bold text-gray-900 max-w-xs truncate">
                {{ item.productName }}
              </td>
              <td class="p-4 font-mono text-gray-600 font-semibold">
                {{ item.sku }}
              </td>
              <td class="p-4">
                <div class="flex items-center gap-2">
                  <span class="bg-gray-100 font-bold px-2 py-0.5 rounded text-[11px]">Size {{ item.size }}</span>
                  <span class="flex items-center gap-1 font-medium">
                    <span class="w-3 h-3 rounded-full border border-gray-300" :style="{ backgroundColor: item.colorHex }"></span>
                    <span>{{ item.colorName }}</span>
                  </span>
                </div>
              </td>
              <td class="p-4 font-bold text-gray-900">
                {{ formatVND(item.price) }}
              </td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-full font-black text-xs inline-flex items-center gap-1"
                  :class="item.stockQuantity === 0 ? 'bg-rose-600 text-white animate-pulse' : 'bg-amber-100 text-amber-900'"
                >
                  <span v-if="item.stockQuantity === 0">HẾT HÀNG (0)</span>
                  <span v-else>⚠️ Còn {{ item.stockQuantity }} cái</span>
                </span>
              </td>
              
              <!-- NÚT SỬA NHANH SỐ LƯỢNG TỒN KHO TẠI CHỖ (Yêu cầu Phase 7) -->
              <td class="p-4 text-right">
                <div class="inline-flex items-center gap-2">
                  <input
                    type="number"
                    min="0"
                    max="999"
                    v-model.number="editStockInputs[item.variantId]"
                    class="w-16 px-2.5 py-1 border border-gray-200 rounded-lg text-xs font-bold text-center focus:ring-2 focus:ring-rose-500 focus:outline-none"
                  />
                  <button
                    type="button"
                    @click="saveStock(item.variantId)"
                    :disabled="savingVariantId === item.variantId"
                    class="px-3 py-1 bg-gray-900 hover:bg-rose-600 text-white rounded-lg text-xs font-bold transition-colors shadow-sm disabled:opacity-50"
                  >
                    <span v-if="savingVariantId === item.variantId">Lưu...</span>
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
import { reactive, ref, onMounted } from 'vue';
import api from '@/services/api';
import { formatVND, onImageError } from '@/services/formatters';

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
    alert('Số lượng tồn kho không hợp lệ');
    return;
  }

  savingVariantId.value = variantId;
  try {
    const res = await api.patch(`/admin/variants/${variantId}/stock`, {
      stockQuantity: Number(newQty)
    });
    if (res.success) {
      alert(res.message || 'Cập nhật số lượng tồn kho thành công!');
      await loadLowStock();
    } else {
      throw new Error(res.message || 'Cập nhật tồn kho thất bại');
    }
  } catch (err) {
    const found = lowStockList.value.find(i => i.variantId === variantId);
    if (found) {
      editStockInputs[variantId] = found.stockQuantity;
    }
    alert(err.response?.data?.message || err.friendlyMessage || err.message || 'Không thể cập nhật tồn kho. Vui lòng thử lại!');
  } finally {
    savingVariantId.value = null;
  }
}

onMounted(() => {
  loadStats();
  loadLowStock();
});
</script>
