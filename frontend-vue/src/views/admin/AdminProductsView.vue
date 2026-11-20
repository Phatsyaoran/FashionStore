<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Quản Lý Sản Phẩm & Biến Thể
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Bật/tắt nhanh Flash Sale, nhập kho nhanh các biến thể và xuất báo cáo tồn kho (.CSV)
        </p>
      </div>

      <div class="flex items-center gap-3">
        <!-- Nút Xuất Tồn Kho (.CSV) (Phase 11) -->
        <button
          type="button"
          @click="exportInventoryCsv"
          :disabled="isExporting"
          class="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-xl font-bold text-xs shadow-md shadow-emerald-600/20 transition-all flex items-center gap-2"
        >
          <span v-if="isExporting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>📥</span>
          <span>Xuất Tồn Kho (.CSV)</span>
        </button>

        <router-link
          to="/admin"
          class="text-xs font-bold text-gray-600 hover:text-gray-900 px-3 py-2 rounded-xl hover:bg-gray-100 transition-colors"
        >
          ← Về Dashboard
        </router-link>
      </div>
    </div>

    <!-- Products Table -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[700px] text-left text-xs">
          <thead class="bg-gray-50 border-b border-gray-100 text-gray-400 font-bold uppercase tracking-wider">
            <tr>
              <th class="p-4">Hình ảnh</th>
              <th class="p-4">Tên sản phẩm</th>
              <th class="p-4">Danh mục</th>
              <th class="p-4">Giá bán</th>
              <th class="p-4 text-center">Bật/Tắt Flash Sale</th>
              <th class="p-4">Tổng tồn kho</th>
              <th class="p-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="item in products" :key="item.id" class="hover:bg-gray-50 transition-colors">
              <td class="p-4">
                <img
                  :src="item.thumbnailUrl"
                  :alt="item.name"
                  @error="onImageError"
                  class="w-12 h-14 object-cover rounded-xl bg-gray-100 border border-gray-100"
                />
              </td>
              <td class="p-4 font-bold text-gray-900 max-w-xs">
                <span class="line-clamp-2">{{ item.name }}</span>
              </td>
              <td class="p-4 text-gray-500 font-medium">
                {{ item.categoryName }}
              </td>
              <td class="p-4">
                <div class="font-bold text-rose-600">
                  {{ formatVND(item.salePrice || item.basePrice) }}
                </div>
                <div v-if="item.salePrice && item.basePrice > item.salePrice" class="text-[10px] text-gray-400 line-through">
                  {{ formatVND(item.basePrice) }}
                </div>
              </td>

              <!-- BẬT/TẮT FLASH SALE NHANH (Yêu cầu Phase 11) -->
              <td class="p-4 text-center">
                <div class="flex flex-col items-center gap-1">
                  <button
                    type="button"
                    @click="toggleFlashSale(item)"
                    :disabled="togglingId === item.id"
                    class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-rose-500"
                    :class="item.isFlashSale ? 'bg-rose-600' : 'bg-gray-200'"
                    :title="item.isFlashSale ? 'Tắt Flash Sale' : 'Bật Flash Sale'"
                  >
                    <span
                      class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out flex items-center justify-center text-[10px]"
                      :class="item.isFlashSale ? 'translate-x-5' : 'translate-x-0'"
                    >
                      {{ item.isFlashSale ? '⚡' : '' }}
                    </span>
                  </button>
                  <span class="text-[10px] font-bold" :class="item.isFlashSale ? 'text-rose-600' : 'text-gray-400'">
                    {{ item.isFlashSale ? 'Đang bật' : 'Đang tắt' }}
                  </span>
                </div>
              </td>

              <!-- TỒN KHO & NHẬP KHO NHANH (Yêu cầu Phase 11) -->
              <td class="p-4">
                <div class="flex items-center gap-2">
                  <span
                    class="font-bold px-2.5 py-1 rounded-lg text-xs"
                    :class="item.totalStock > 5 ? 'bg-emerald-50 text-emerald-700' : (item.totalStock > 0 ? 'bg-amber-50 text-amber-700' : 'bg-rose-50 text-rose-700')"
                  >
                    {{ item.totalStock }} chiếc
                  </span>
                  <button
                    type="button"
                    @click="openStockModal(item)"
                    class="px-2 py-1 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-[11px] font-bold transition-colors"
                    title="Chỉnh sửa tồn kho từng biến thể"
                  >
                    ✏️ Sửa kho
                  </button>
                </div>
              </td>

              <td class="p-4 text-right">
                <div class="inline-flex items-center gap-2">
                  <router-link
                    :to="`/product/${item.slug}`"
                    target="_blank"
                    class="px-3 py-1.5 bg-gray-100 hover:bg-rose-50 hover:text-rose-600 rounded-xl font-bold transition-colors inline-block"
                  >
                    Xem web ↗
                  </router-link>
                  <button
                    type="button"
                    @click="deleteProduct(item)"
                    :disabled="deletingId === item.id"
                    class="px-2.5 py-1.5 bg-rose-50 hover:bg-rose-600 hover:text-white text-rose-600 rounded-xl font-bold transition-colors inline-flex items-center gap-1 disabled:opacity-50"
                    title="Xóa sản phẩm an toàn"
                  >
                    <span v-if="deletingId === item.id" class="w-3.5 h-3.5 border-2 border-rose-600 border-t-transparent rounded-full animate-spin"></span>
                    <span v-else>🗑️</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL NHẬP KHO NHANH BIẾN THỂ (Quick Stock Edit Modal) (Phase 11) -->
    <div
      v-if="selectedProductForStock"
      class="fixed inset-0 z-50 overflow-y-auto bg-black/70 backdrop-blur-sm flex items-center justify-center p-4"
    >
      <div class="bg-white rounded-3xl p-6 sm:p-8 max-w-xl max-h-[90vh] overflow-y-auto w-full space-y-6 shadow-2xl">
        <!-- Modal Header -->
        <div class="flex justify-between items-start pb-4 border-b border-gray-100">
          <div class="flex items-center gap-3">
            <img
              :src="selectedProductForStock.thumbnailUrl"
              :alt="selectedProductForStock.name"
              class="w-12 h-14 object-cover rounded-xl bg-gray-100"
            />
            <div>
              <h3 class="font-extrabold text-sm text-gray-900 line-clamp-1">
                {{ selectedProductForStock.name }}
              </h3>
              <p class="text-xs text-gray-500 mt-0.5">
                Nhập kho nhanh từng phân loại Size & Màu sắc
              </p>
            </div>
          </div>
          <button
            @click="closeStockModal"
            class="text-gray-400 hover:text-gray-700 font-bold text-lg p-1"
          >
            ✕
          </button>
        </div>

        <!-- Variants List -->
        <div v-if="loadingVariants" class="py-12 text-center text-xs text-gray-400">
          <span class="inline-block w-5 h-5 border-2 border-rose-500 border-t-transparent rounded-full animate-spin mb-2"></span>
          <div>Đang nạp danh sách biến thể...</div>
        </div>

        <div v-else class="space-y-3 max-h-96 overflow-y-auto pr-1">
          <div
            v-for="variant in productVariants"
            :key="variant.id"
            class="p-3.5 bg-gray-50 rounded-2xl border border-gray-100 flex items-center justify-between gap-3"
          >
            <!-- Variant Info -->
            <div class="flex items-center gap-2.5">
              <span
                class="w-3.5 h-3.5 rounded-full border border-gray-300 shadow-inner shrink-0"
                :style="{ backgroundColor: variant.colorHex }"
              ></span>
              <div>
                <div class="font-bold text-xs text-gray-900">
                  Size {{ variant.size }} - {{ variant.colorName }}
                </div>
                <div class="font-mono text-[10px] text-gray-400">
                  SKU: {{ variant.sku }}
                </div>
              </div>
            </div>

            <!-- Quick Adjustments -->
            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <button
                  type="button"
                  @click="adjustStock(variant, -5)"
                  class="w-6 h-6 rounded-lg bg-white hover:bg-gray-200 border border-gray-200 font-bold text-[11px] text-gray-700"
                >
                  -5
                </button>
                <input
                  type="number"
                  min="0"
                  v-model.number="variant.stockQuantity"
                  class="w-16 px-2 py-1 bg-white border border-gray-200 rounded-lg text-center text-xs font-black text-gray-900 focus:outline-none focus:ring-1 focus:ring-rose-500"
                />
                <button
                  type="button"
                  @click="adjustStock(variant, 5)"
                  class="w-6 h-6 rounded-lg bg-white hover:bg-gray-200 border border-gray-200 font-bold text-[11px] text-gray-700"
                >
                  +5
                </button>
                <button
                  type="button"
                  @click="adjustStock(variant, 10)"
                  class="w-7 h-6 rounded-lg bg-white hover:bg-gray-200 border border-gray-200 font-bold text-[10px] text-gray-700"
                >
                  +10
                </button>
              </div>

              <!-- Save button -->
              <button
                type="button"
                @click="saveVariantStock(variant)"
                :disabled="savingVariantId === variant.id"
                class="px-3 py-1 bg-rose-600 hover:bg-rose-700 text-white rounded-lg text-xs font-bold transition-all shadow-sm"
              >
                {{ savingVariantId === variant.id ? '...' : 'Lưu' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="pt-3 border-t border-gray-100 flex justify-end">
          <button
            type="button"
            @click="closeStockModal"
            class="px-5 py-2.5 bg-gray-900 text-white font-bold text-xs rounded-xl hover:bg-gray-800 transition-colors"
          >
            Hoàn tất đóng
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/services/api';
import { formatVND, onImageError } from '@/services/formatters';

const products = ref([]);
const isExporting = ref(false);
const togglingId = ref(null);
const deletingId = ref(null);

// Modal Stock State
const selectedProductForStock = ref(null);
const productVariants = ref([]);
const loadingVariants = ref(false);
const savingVariantId = ref(null);

async function loadProducts() {
  try {
    const res = await api.get('/products?pageSize=50');
    if (res.success && res.data) {
      products.value = res.data.content || [];
    }
  } catch (err) {
    console.error('Lỗi nạp sản phẩm admin:', err);
  }
}

// XÓA SẢN PHẨM AN TOÀN (Kiểm tra ràng buộc đơn hàng)
async function deleteProduct(item) {
  if (!confirm(`Bạn có chắc chắn muốn xóa sản phẩm "${item.name}"?`)) return;
  deletingId.value = item.id;
  try {
    const res = await api.delete(`/admin/products/${item.id}`);
    if (res.success) {
      alert(res.message || `Đã xóa sản phẩm "${item.name}" thành công!`);
      products.value = products.value.filter(p => p.id !== item.id);
    } else {
      alert(res.message || 'Không thể xóa sản phẩm.');
    }
  } catch (err) {
    const msg = err.response?.data?.message || 'Lỗi khi xóa sản phẩm. Sản phẩm có thể đã phát sinh đơn hàng.';
    alert(msg);
  } finally {
    deletingId.value = null;
  }
}

// BẬT/TẮT FLASH SALE NHANH
async function toggleFlashSale(item) {
  togglingId.value = item.id;
  try {
    const res = await api.patch(`/admin/products/${item.id}/quick-toggle-flashsale`);
    if (res.success && res.data) {
      item.isFlashSale = res.data.isFlashSale;
      item.flashSaleEndTime = res.data.flashSaleEndTime;
      const stateStr = item.isFlashSale ? 'BẬT' : 'TẮT';
      alert(`Đã ${stateStr} Flash Sale cho sản phẩm "${item.name}"`);
    }
  } catch (err) {
    console.error('Lỗi bật/tắt flash sale:', err);
    alert('Không thể cập nhật Flash Sale. Vui lòng thử lại!');
  } finally {
    togglingId.value = null;
  }
}

// XUẤT CSV TỒN KHO (Phase 11)
async function exportInventoryCsv() {
  try {
    isExporting.value = true;
    const data = await api.get('/admin/export/inventory-csv', {
      responseType: 'blob'
    });

    const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '');
    link.setAttribute('download', `ton-kho-${dateStr}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.error('Lỗi xuất file CSV tồn kho:', err);
    alert('Không thể xuất file CSV tồn kho. Vui lòng thử lại!');
  } finally {
    isExporting.value = false;
  }
}

// MỞ MODAL NHẬP KHO NHANH
async function openStockModal(product) {
  selectedProductForStock.value = product;
  loadingVariants.value = true;
  productVariants.value = [];

  try {
    const res = await api.get(`/products/id/${product.id}`);
    if (res.success && res.data) {
      productVariants.value = res.data.variants || [];
    }
  } catch (err) {
    console.error('Lỗi nạp biến thể sản phẩm:', err);
    alert('Không thể nạp biến thể sản phẩm.');
  } finally {
    loadingVariants.value = false;
  }
}

function adjustStock(variant, delta) {
  const current = variant.stockQuantity || 0;
  variant.stockQuantity = Math.max(0, current + delta);
}

async function saveVariantStock(variant) {
  if (savingVariantId.value) return;
  savingVariantId.value = variant.id;
  try {
    const res = await api.patch(`/admin/variants/${variant.id}/stock`, {
      stockQuantity: variant.stockQuantity
    });
    if (res.success) {
      alert(`Đã cập nhật tồn kho SKU: ${variant.sku} thành ${variant.stockQuantity} chiếc`);
      // Cập nhật lại tổng tồn kho của sản phẩm đang xem
      const total = productVariants.value.reduce((sum, v) => sum + (v.stockQuantity || 0), 0);
      if (selectedProductForStock.value) {
        selectedProductForStock.value.totalStock = total;
      }
      const prodInList = products.value.find(p => p.id === selectedProductForStock.value?.id);
      if (prodInList) {
        prodInList.totalStock = total;
      }
    }
  } catch (err) {
    console.error('Lỗi lưu tồn kho:', err);
    alert('Không thể lưu số lượng tồn kho.');
  } finally {
    savingVariantId.value = null;
  }
}

function closeStockModal() {
  selectedProductForStock.value = null;
  productVariants.value = [];
}

onMounted(() => {
  loadProducts();
});
</script>
