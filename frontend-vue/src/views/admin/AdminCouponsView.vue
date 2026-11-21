<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
          Quản Lý Mã Giảm Giá (Coupons)
        </h1>
        <p class="text-xs text-slate-500 mt-0.5">
          Tạo mã khuyến mãi mới, điều chỉnh hạn mức và bật/tắt kích hoạt nhanh
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <button
          type="button"
          @click="openCreateModal"
          class="h-9 px-3.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg font-semibold text-xs transition-colors inline-flex items-center gap-1.5"
        >
          <span>＋</span>
          <span>Tạo Mã Giảm Giá</span>
        </button>

        <router-link
          to="/admin"
          class="h-9 px-3 inline-flex items-center text-xs font-semibold text-slate-600 hover:text-slate-900 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 transition-colors"
        >
          ← Tổng quan
        </router-link>
      </div>
    </div>

    <!-- Coupons Table -->
    <div class="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
      <table class="w-full min-w-[760px] text-left text-xs">
        <thead class="bg-slate-50 border-b border-slate-200 text-xs uppercase tracking-wider text-slate-500">
          <tr>
            <th class="px-4 py-3 font-semibold">Mã Coupon</th>
            <th class="px-4 py-3 font-semibold">Mức giảm</th>
            <th class="px-4 py-3 font-semibold">Đơn tối thiểu</th>
            <th class="px-4 py-3 font-semibold">Lượt sử dụng</th>
            <th class="px-4 py-3 font-semibold">Ngày hết hạn</th>
            <th class="px-4 py-3 font-semibold text-center">Trạng thái</th>
            <th class="px-4 py-3 font-semibold text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200/80">
          <tr v-if="loading">
            <td colspan="7" class="p-8 text-center text-slate-400">Đang tải danh sách mã giảm giá...</td>
          </tr>
          <tr v-else-if="coupons.length === 0">
            <td colspan="7" class="p-8 text-center text-slate-400">Chưa có mã giảm giá nào.</td>
          </tr>
          <tr v-else v-for="c in coupons" :key="c.id" class="hover:bg-slate-50/80 transition-colors">
            <td class="px-4 py-3.5">
              <span class="font-mono font-bold text-slate-900 bg-slate-100 border border-slate-200 px-2.5 py-1 rounded-md">
                {{ c.code }}
              </span>
            </td>
            <td class="px-4 py-3.5">
              <div class="font-bold text-slate-900">Giảm {{ c.discountPercent }}%</div>
              <div class="text-[11px] text-slate-500">Tối đa {{ formatVND(c.maxDiscountAmount) }}</div>
            </td>
            <td class="px-4 py-3.5 font-semibold text-slate-700">
              {{ formatVND(c.minOrderValue) }}
            </td>
            <td class="px-4 py-3.5">
              <span class="font-bold text-slate-900">{{ c.usedCount || 0 }}</span>
              <span class="text-slate-400"> / {{ c.usageLimit }}</span>
            </td>
            <td class="px-4 py-3.5 text-slate-600">
              <div>{{ formatDateTime(c.expiryDate) }}</div>
              <span v-if="isExpired(c.expiryDate)" class="inline-block mt-0.5 px-2 py-0.5 rounded-full text-[10px] font-semibold bg-red-50 text-red-700 border border-red-200">
                Đã hết hạn
              </span>
            </td>
            <td class="px-4 py-3.5 text-center">
              <button
                type="button"
                @click="toggleCoupon(c)"
                :disabled="togglingId === c.id"
                class="relative inline-flex h-5 w-10 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none"
                :class="c.isActive ? 'bg-emerald-500' : 'bg-slate-200'"
              >
                <span
                  class="pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow transition duration-200"
                  :class="c.isActive ? 'translate-x-5' : 'translate-x-0'"
                ></span>
              </button>
              <div class="text-[10px] font-semibold mt-0.5" :class="c.isActive ? 'text-emerald-600' : 'text-slate-400'">
                {{ c.isActive ? 'Đang bật' : 'Đã tắt' }}
              </div>
            </td>
            <td class="px-4 py-3.5 text-right">
              <button
                type="button"
                @click="openEditModal(c)"
                class="h-8 px-3 bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-lg font-semibold text-[11px] transition-colors"
              >
                Sửa
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Create / Edit Coupon -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-[9990] bg-slate-900/50 backdrop-blur-[2px] flex items-center justify-center p-4 no-print"
      >
        <form
          @submit.prevent="submitForm"
          class="relative z-[9995] bg-white rounded-2xl border border-slate-200 max-w-lg max-h-[90vh] flex flex-col overflow-hidden w-full shadow-2xl"
        >
          <!-- Sticky Header -->
          <div class="px-6 py-4 flex items-center justify-between border-b border-slate-200 shrink-0">
            <h3 class="text-sm sm:text-base font-bold text-slate-900">
              {{ editingId ? 'Chỉnh Sửa Mã Giảm Giá' : 'Tạo Mã Giảm Giá Mới' }}
            </h3>
            <button type="button" @click="closeModal" class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-700 font-bold">✕</button>
          </div>

          <!-- Scrollable Body -->
          <div class="flex-1 overflow-y-auto p-6 space-y-4">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Mã Coupon (tự viết hoa) *</label>
              <input
                v-model="form.code"
                type="text"
                required
                placeholder="VD: SUMMER20"
                class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs font-mono uppercase font-bold focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              />
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Phần trăm giảm (%) *</label>
                <input
                  v-model.number="form.discountPercent"
                  type="number"
                  min="1"
                  max="100"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Giảm tối đa (VNĐ) *</label>
                <input
                  v-model.number="form.maxDiscountAmount"
                  type="number"
                  min="1000"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Đơn tối thiểu (VNĐ) *</label>
                <input
                  v-model.number="form.minOrderValue"
                  type="number"
                  min="0"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Giới hạn lượt dùng *</label>
                <input
                  v-model.number="form.usageLimit"
                  type="number"
                  min="1"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Ngày giờ hết hạn *</label>
              <input
                v-model="form.expiryDate"
                type="datetime-local"
                required
                class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              />
            </div>

            <div class="flex items-center gap-2 pt-1">
              <input id="couponActive" v-model="form.isActive" type="checkbox" class="w-4 h-4 rounded accent-slate-900" />
              <label for="couponActive" class="text-xs font-semibold text-slate-800 cursor-pointer">Kích hoạt ngay</label>
            </div>
          </div>

          <!-- Sticky Footer -->
          <div class="px-6 py-4 bg-slate-50 border-t border-slate-200 flex justify-end gap-2 shrink-0">
            <button type="button" @click="closeModal" class="h-9 px-4 rounded-lg border border-slate-200 bg-white text-xs font-semibold text-slate-700 hover:bg-slate-50">
              Hủy
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="h-9 px-5 rounded-lg bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white text-xs font-semibold transition-colors"
            >
              {{ saving ? 'Đang lưu...' : (editingId ? 'Lưu Thay Đổi' : 'Tạo Mã') }}
            </button>
          </div>
        </form>
      </div>
    </Teleport>
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
