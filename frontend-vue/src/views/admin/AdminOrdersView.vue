<template>
  <div class="space-y-6">
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Quản Lý Đơn Hàng & Vận Đơn
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Duyệt đơn, thay đổi trạng thái giao vận và in phiếu giao hàng A6 chuẩn thương mại điện tử
        </p>
      </div>

      <div class="flex items-center gap-3">
        <!-- Nút Xuất Excel / CSV (Yêu cầu Phase 11) -->
        <button
          type="button"
          @click="exportOrdersCsv"
          :disabled="isExporting"
          class="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-xl font-bold text-xs shadow-md shadow-emerald-600/20 transition-all flex items-center gap-2"
        >
          <span v-if="isExporting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>📥</span>
          <span>Xuất Excel (.CSV)</span>
        </button>

        <router-link to="/admin" class="text-xs font-bold text-gray-600 hover:text-gray-900 px-3 py-2 rounded-xl hover:bg-gray-100 transition-colors">
          ← Về Dashboard
        </router-link>
      </div>
    </div>

    <!-- Orders Table -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[750px] text-left text-xs">
          <thead class="bg-gray-50 border-b border-gray-100 text-gray-400 font-bold uppercase tracking-wider">
            <tr>
              <th class="p-4">Mã đơn hàng</th>
              <th class="p-4">Khách hàng</th>
              <th class="p-4">Số điện thoại</th>
              <th class="p-4">Tổng tiền (VNĐ)</th>
              <th class="p-4">Thanh toán</th>
              <th class="p-4">Trạng thái giao vận</th>
              <th class="p-4 text-right">In phiếu A6</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50 transition-colors">
              <td class="p-4 font-mono font-bold text-rose-600">
                #{{ order.orderCode }}
              </td>
              <td class="p-4 font-bold text-gray-900">
                {{ order.customerName }}
              </td>
              <td class="p-4 text-gray-600">
                {{ order.customerPhone }}
              </td>
              <td class="p-4 font-black text-gray-900">
                {{ formatVND(order.totalAmount) }}
              </td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-full font-bold text-[10px]"
                  :class="order.paymentStatus === 'PAID' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'"
                >
                  {{ order.paymentStatus === 'PAID' ? '✓ ĐÃ THANH TOÁN' : '⏳ CHƯA TT' }} ({{ order.paymentMethod }})
                </span>
              </td>
              <td class="p-4">
                <select
                  v-model="order.orderStatus"
                  @change="updateStatus(order)"
                  class="bg-gray-50 border border-gray-200 rounded-lg px-2.5 py-1.5 text-xs font-bold text-gray-800 focus:outline-none focus:ring-1 focus:ring-rose-500"
                >
                  <option value="PENDING">1. Đã đặt đơn (PENDING)</option>
                  <option value="CONFIRMED">2. Đã xác nhận (CONFIRMED)</option>
                  <option value="PACKING">3. Đang đóng gói (PACKING)</option>
                  <option value="SHIPPING">4. Đang giao hàng (SHIPPING)</option>
                  <option value="COMPLETED">5. Hoàn thành (COMPLETED)</option>
                  <option value="CANCELLED">Hủy đơn (CANCELLED)</option>
                </select>
              </td>
              <td class="p-4 text-right">
                <!-- Nút In phiếu giao hàng A6 (Yêu cầu Phase 7) -->
                <button
                  type="button"
                  @click="printLabel(order)"
                  class="px-3.5 py-1.5 bg-gray-900 hover:bg-rose-600 text-white rounded-xl font-bold transition-all inline-flex items-center gap-1.5 shadow-sm"
                >
                  <span>🖨️</span> In phiếu giao hàng
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- IN PHIẾU GIAO HÀNG CHUẨN KHỔ A6 (Yêu cầu Phase 7) -->
    <div v-if="selectedPrintOrder" class="fixed inset-0 z-50 overflow-y-auto bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
      <div class="bg-white rounded-3xl p-6 sm:p-8 max-w-lg max-h-[90vh] overflow-y-auto w-full space-y-4 shadow-2xl">
        <div class="flex justify-between items-center no-print pb-2 border-b border-gray-100">
          <div>
            <h3 class="font-bold text-base text-gray-900">Xem trước nhãn giao vận A6</h3>
            <p class="text-xs text-gray-500">Chuẩn in nhiệt TMĐT (Thermal Shipping Label)</p>
          </div>
          <button @click="selectedPrintOrder = null" class="text-gray-400 hover:text-gray-700 font-bold text-lg p-1">✕</button>
        </div>

        <!-- PRINTABLE THERMAL LABEL A6 -->
        <div id="thermal-label-a6" class="border-2 border-black p-5 rounded-2xl bg-white text-black space-y-3 font-sans">
          
          <!-- Header with Logo and Barcode -->
          <div class="flex justify-between items-center border-b-2 border-black pb-3">
            <div>
              <div class="flex items-center gap-1.5">
                <span class="w-6 h-6 rounded-lg bg-black text-white flex items-center justify-center font-black text-xs">F</span>
                <span class="font-black text-base tracking-tight">FASHION STORE</span>
              </div>
              <p class="text-[10px] text-gray-600 mt-0.5">88 Phố Huế, Hai Bà Trưng, Hà Nội | CSKH: 1900 8899</p>
            </div>

            <!-- Simulated Barcode -->
            <div class="text-right">
              <div class="font-mono text-base font-black tracking-widest leading-none">||||| | ||| |||| | ||</div>
              <div class="font-mono text-xs font-bold mt-1">#{{ selectedPrintOrder.orderCode }}</div>
            </div>
          </div>

          <!-- Sender & Receiver Grid -->
          <div class="grid grid-cols-2 gap-3 text-xs border-b-2 border-black pb-3">
            <div class="border-r border-gray-300 pr-2 space-y-0.5">
              <p class="font-bold text-[10px] text-gray-500 uppercase">Người gửi:</p>
              <p class="font-bold text-xs">Kho Vận FashionStore VN</p>
              <p class="text-[11px]">Hotline: 0901234567</p>
              <p class="text-[10px] text-gray-600">88 Phố Huế, Q. Hai Bà Trưng, Hà Nội</p>
            </div>
            <div class="space-y-0.5">
              <p class="font-bold text-[10px] text-gray-500 uppercase">Người nhận:</p>
              <p class="font-black text-sm text-black">{{ selectedPrintOrder.customerName }}</p>
              <p class="font-bold text-xs">{{ selectedPrintOrder.customerPhone }}</p>
              <p class="text-[11px] leading-tight text-gray-800">{{ selectedPrintOrder.shippingAddress }}</p>
            </div>
          </div>

          <!-- Products List (Tên + Size + Màu + SL) -->
          <div class="border-b-2 border-black pb-3 space-y-1.5 text-xs">
            <p class="font-bold text-[10px] text-gray-500 uppercase flex justify-between">
              <span>Nội dung sản phẩm:</span>
              <span>Số lượng</span>
            </p>
            <div
              v-for="it in (selectedPrintOrder.orderItems || [])"
              :key="it.id"
              class="flex justify-between items-baseline"
            >
              <span class="font-medium text-[11px]">
                • {{ it.productName }} <strong class="text-black">(Size {{ it.size }}, {{ it.colorName }})</strong>
              </span>
              <span class="font-black text-xs font-mono ml-2">x{{ it.quantity }}</span>
            </div>
          </div>

          <!-- Big COD / PAID Box (Yêu cầu Phase 7) -->
          <div
            class="p-3.5 rounded-xl border-2 text-center"
            :class="selectedPrintOrder.paymentStatus === 'PAID' ? 'border-emerald-600 bg-emerald-50' : 'border-black bg-gray-50'"
          >
            <div v-if="selectedPrintOrder.paymentStatus === 'PAID'">
              <span class="font-black text-emerald-800 text-sm block tracking-wider">
                ĐÃ THANH TOÁN (VIETQR)
              </span>
              <span class="font-black text-base text-emerald-900 block mt-0.5">
                THU HỘ: 0 VNĐ
              </span>
            </div>

            <div v-else>
              <span class="font-bold text-xs text-gray-700 block uppercase tracking-wider">
                TIỀN THU HỘ (COD):
              </span>
              <span class="font-black text-2xl text-black block mt-0.5 tracking-tight">
                {{ formatVND(selectedPrintOrder.totalAmount) }}
              </span>
            </div>
          </div>

          <!-- Footer sign box -->
          <div class="grid grid-cols-2 text-[9px] text-gray-500 pt-1">
            <div>
              <p>Chỉ dẫn giao hàng: Cho khách kiểm tra hàng trước khi nhận.</p>
            </div>
            <div class="text-right">
              <p>Chữ ký người nhận:</p>
              <p class="mt-4 font-mono">...........................................</p>
            </div>
          </div>
        </div>

        <!-- Buttons -->
        <div class="flex gap-2 justify-end no-print pt-2">
          <button
            type="button"
            @click="selectedPrintOrder = null"
            class="px-4 py-2 border border-gray-300 rounded-xl text-xs font-bold text-gray-700 hover:bg-gray-50"
          >
            Đóng
          </button>
          <button
            type="button"
            @click="triggerPrint"
            class="px-6 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold shadow-md shadow-rose-200 transition-all flex items-center gap-1.5"
          >
            <span>🖨️</span> In Ngay (Khổ A6)
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/services/api';
import { useToastStore } from '@/stores/toastStore';
import { formatVND } from '@/services/formatters';

const toastStore = useToastStore();
const orders = ref([]);
const selectedPrintOrder = ref(null);
const isExporting = ref(false);

async function exportOrdersCsv() {
  try {
    isExporting.value = true;
    const data = await api.get('/admin/export/orders-csv', {
      responseType: 'blob'
    });

    const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '');
    link.setAttribute('download', `don-hang-${dateStr}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.error('Lỗi xuất file CSV đơn hàng:', err);
    toastStore.error('Không thể xuất file CSV đơn hàng. Vui lòng thử lại!');
  } finally {
    isExporting.value = false;
  }
}

async function loadOrders() {
  try {
    const res = await api.get('/admin/orders');
    if (res.success && res.data) {
      const list = res.data.content || res.data || [];
      orders.value = list.map(o => ({ ...o, _prevStatus: o.orderStatus }));
    }
  } catch (err) {
    console.error('Lỗi nạp đơn hàng admin:', err);
  }
}

async function updateStatus(order) {
  const previousStatus = order._prevStatus;
  try {
    const res = await api.patch(`/admin/orders/${order.orderCode}/status`, { status: order.orderStatus });
    if (res.success) {
      order._prevStatus = order.orderStatus;
      if (res.data?.paymentStatus) {
        order.paymentStatus = res.data.paymentStatus;
      }
      toastStore.success(`Đã cập nhật đơn #${order.orderCode} sang trạng thái: ${order.orderStatus}`);
    } else {
      order.orderStatus = previousStatus;
      toastStore.error(res.message || 'Không thể cập nhật trạng thái đơn hàng.');
    }
  } catch (err) {
    order.orderStatus = previousStatus;
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể cập nhật trạng thái đơn hàng. Vui lòng thử lại!');
  }
}

function printLabel(order) {
  selectedPrintOrder.value = order;
}

function triggerPrint() {
  window.print();
}

onMounted(() => {
  loadOrders();
});
</script>
