<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Quản Lý Mã Giảm Giá (Coupons)
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Tạo mã khuyến mãi mới, điều chỉnh hạn mức và bật/tắt kích hoạt nhanh
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2.5">
        <button
          type="button"
          @click="openCreateModal"
          class="px-4 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold text-xs shadow-md shadow-rose-600/20 transition-all flex items-center gap-1.5"
        >
          <span>＋</span>
          <span>Tạo Mã Giảm Giá</span>
        </button>

        <router-link
          to="/admin"
          class="text-xs font-bold text-gray-600 hover:text-gray-900 px-3 py-2 rounded-xl hover:bg-gray-100 transition-colors"
        >
          ← Về Dashboard
        </router-link>
      </div>
    </div>

    <!-- Coupons Table -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[750px] text-left text-xs">
          <thead class="bg-gray-50 border-b border-gray-100 text-gray-400 font-bold uppercase tracking-wider">
            <tr>
              <th class="p-4">Mã Coupon</th>
              <th class="p-4">Mức giảm</th>
              <th class="p-4">Đơn tối thiểu</th>
              <th class="p-4">Lượt sử dụng</th>
              <th class="p-4">Ngày hết hạn</th>
              <th class="p-4 text-center">Trạng thái</th>
              <th class="p-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="loading">
              <td colspan="7" class="p-8 text-center text-gray-400">Đang tải danh sách mã giảm giá...</td>
            </tr>
            <tr v-else-if="coupons.length === 0">
              <td colspan="7" class="p-8 text-center text-gray-400">Chưa có mã giảm giá nào.</td>
            </tr>
            <tr v-else v-for="c in coupons" :key="c.id" class="hover:bg-gray-50 transition-colors">
              <td class="p-4">
                <span class="font-mono font-black text-rose-700 bg-rose-50 border border-rose-200 px-2.5 py-1 rounded-lg">
                  {{ c.code }}
                </span>
              </td>
              <td class="p-4">
                <div class="font-extrabold text-gray-900">Giảm {{ c.discountPercent }}%</div>
                <div class="text-[11px] text-gray-500">Tối đa {{ formatVND(c.maxDiscountAmount) }}</div>
              </td>
              <td class="p-4 font-bold text-gray-700">
                {{ formatVND(c.minOrderValue) }}
              </td>
              <td class="p-4">
                <span class="font-bold text-gray-900">{{ c.usedCount || 0 }}</span>
                <span class="text-gray-400"> / {{ c.usageLimit }}</span>
              </td>
              <td class="p-4 text-gray-600">
                <div>{{ formatDateTime(c.expiryDate) }}</div>
                <span v-if="isExpired(c.expiryDate)" class="text-[10px] font-bold text-rose-600">Đã hết hạn</span>
              </td>
              <td class="p-4 text-center">
                <button
                  type="button"
                  @click="toggleCoupon(c)"
                  :disabled="togglingId === c.id"
                  class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none"
                  :class="c.isActive ? 'bg-emerald-500' : 'bg-gray-200'"
                >
                  <span
                    class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow transition duration-200"
                    :class="c.isActive ? 'translate-x-5' : 'translate-x-0'"
                  ></span>
                </button>
                <div class="text-[10px] font-bold mt-0.5" :class="c.isActive ? 'text-emerald-600' : 'text-gray-400'">
                  {{ c.isActive ? 'Đang bật' : 'Đã tắt' }}
                </div>
              </td>
              <td class="p-4 text-right">
                <button
                  type="button"
                  @click="openEditModal(c)"
                  class="px-3 py-1.5 bg-amber-50 hover:bg-amber-500 hover:text-white text-amber-700 rounded-xl font-bold transition-colors"
                >
                  ✏️ Sửa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Create / Edit Coupon -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-50 overflow-y-auto bg-black/70 backdrop-blur-sm flex items-center justify-center p-4"
    >
      <div class="bg-white rounded-3xl p-6 sm:p-8 max-w-lg max-h-[90vh] overflow-y-auto w-full space-y-5 shadow-2xl">
        <div class="flex items-center justify-between pb-3 border-b border-gray-100">
          <h3 class="text-base font-black text-gray-900">
            {{ editingId ? '✏️ Chỉnh Sửa Mã Giảm Giá' : '➕ Tạo Mã Giảm Giá Mới' }}
          </h3>
          <button type="button" @click="closeModal" class="text-gray-400 hover:text-gray-700 font-bold text-lg p-1">✕</button>
        </div>

        <form @submit.prevent="submitForm" class="space-y-4">
          <div>
            <label class="block text-xs font-bold text-gray-700 mb-1">Mã Coupon (tự viết hoa) *</label>
            <input
              v-model="form.code"
              type="text"
              required
              placeholder="VD: SUMMER20"
              class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs font-mono uppercase font-bold focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-bold text-gray-700 mb-1">Phần trăm giảm (%) *</label>
              <input
                v-model.number="form.discountPercent"
                type="number"
                min="1"
                max="100"
                required
                class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-700 mb-1">Giảm tối đa (VNĐ) *</label>
              <input
                v-model.number="form.maxDiscountAmount"
                type="number"
                min="1000"
                required
                class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-700 mb-1">Đơn tối thiểu (VNĐ) *</label>
              <input
                v-model.number="form.minOrderValue"
                type="number"
                min="0"
                required
                class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>

            <div>
              <label class="block text-xs font-bold text-gray-700 mb-1">Giới hạn lượt dùng *</label>
              <input
                v-model.number="form.usageLimit"
                type="number"
                min="1"
                required
                class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label class="block text-xs font-bold text-gray-700 mb-1">Ngày giờ hết hạn *</label>
            <input
              v-model="form.expiryDate"
              type="datetime-local"
              required
              class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div class="flex items-center gap-2">
            <input id="couponActive" v-model="form.isActive" type="checkbox" class="w-4 h-4 rounded text-rose-600" />
            <label for="couponActive" class="text-xs font-bold text-gray-800 cursor-pointer">Kích hoạt ngay</label>
          </div>

          <div class="pt-3 border-t border-gray-100 flex justify-end gap-2">
            <button type="button" @click="closeModal" class="px-4 py-2 rounded-xl border border-gray-200 text-xs font-bold text-gray-700">
              Hủy
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="px-5 py-2 rounded-xl bg-rose-600 hover:bg-rose-700 disabled:opacity-50 text-white text-xs font-bold shadow-sm"
            >
              {{ saving ? 'Đang lưu...' : (editingId ? 'Lưu Thay Đổi' : 'Tạo Mã') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '@/services/api';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, formatDateTime } from '@/services/formatters';

const toastStore = useToastStore();
const coupons = ref([]);
const loading = ref(false);
const togglingId = ref(null);
const showModal = ref(false);
const editingId = ref(null);
const saving = ref(false);

const form = reactive({
  code: '',
  discountPercent: 10,
  maxDiscountAmount: 50000,
  minOrderValue: 200000,
  expiryDate: '',
  usageLimit: 100,
  isActive: true
});

function isExpired(dateStr) {
  if (!dateStr) return false;
  return new Date(dateStr) < new Date();
}

function toDatetimeLocal(isoStr) {
  if (!isoStr) return '';
  return String(isoStr).slice(0, 16);
}

function defaultExpiryLocal() {
  const d = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000);
  const pad = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T23:59`;
}

async function loadCoupons() {
  loading.value = true;
  try {
    const res = await api.get('/admin/coupons');
    if (res.success && Array.isArray(res.data)) {
      coupons.value = res.data;
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể tải danh sách mã giảm giá.');
  } finally {
    loading.value = false;
  }
}

function openCreateModal() {
  editingId.value = null;
  form.code = '';
  form.discountPercent = 10;
  form.maxDiscountAmount = 50000;
  form.minOrderValue = 200000;
  form.expiryDate = defaultExpiryLocal();
  form.usageLimit = 100;
  form.isActive = true;
  showModal.value = true;
}

function openEditModal(c) {
  editingId.value = c.id;
  form.code = c.code || '';
  form.discountPercent = c.discountPercent || 10;
  form.maxDiscountAmount = c.maxDiscountAmount || 50000;
  form.minOrderValue = c.minOrderValue || 0;
  form.expiryDate = toDatetimeLocal(c.expiryDate);
  form.usageLimit = c.usageLimit || 100;
  form.isActive = !!c.isActive;
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingId.value = null;
}

async function submitForm() {
  if (saving.value) return;
  saving.value = true;
  try {
    const payload = {
      code: form.code.trim().toUpperCase(),
      discountPercent: Number(form.discountPercent),
      maxDiscountAmount: Number(form.maxDiscountAmount),
      minOrderValue: Number(form.minOrderValue),
      expiryDate: form.expiryDate.length === 16 ? `${form.expiryDate}:00` : form.expiryDate,
      usageLimit: Number(form.usageLimit),
      isActive: !!form.isActive
    };

    const res = editingId.value
      ? await api.put(`/admin/coupons/${editingId.value}`, payload)
      : await api.post('/admin/coupons', payload);

    if (res.success) {
      toastStore.success(res.message || 'Lưu mã giảm giá thành công!');
      closeModal();
      await loadCoupons();
    } else {
      toastStore.error(res.message || 'Không thể lưu mã giảm giá.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi khi lưu mã giảm giá.');
  } finally {
    saving.value = false;
  }
}

async function toggleCoupon(c) {
  if (togglingId.value) return;
  togglingId.value = c.id;
  try {
    const res = await api.patch(`/admin/coupons/${c.id}/toggle`);
    if (res.success && res.data) {
      c.isActive = res.data.isActive;
      toastStore.success(`Đã ${c.isActive ? 'BẬT' : 'TẮT'} mã giảm giá ${c.code}`);
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể đổi trạng thái mã giảm giá.');
  } finally {
    togglingId.value = null;
  }
}

onMounted(() => {
  loadCoupons();
});
</script>
