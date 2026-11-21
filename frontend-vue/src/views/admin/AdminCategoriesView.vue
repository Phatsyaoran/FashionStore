<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
          Quản Lý Danh Mục Sản Phẩm
        </h1>
        <p class="text-xs text-slate-500 mt-0.5">
          Thêm mới, cập nhật mô tả/slug và xóa an toàn các danh mục chưa có sản phẩm
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <button
          type="button"
          @click="openCreateModal"
          class="h-9 px-3.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg font-semibold text-xs transition-colors inline-flex items-center gap-1.5"
        >
          <span>＋</span>
          <span>Thêm Danh Mục</span>
        </button>

        <router-link
          to="/admin"
          class="h-9 px-3 inline-flex items-center text-xs font-semibold text-slate-600 hover:text-slate-900 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 transition-colors"
        >
          ← Tổng quan
        </router-link>
      </div>
    </div>

    <!-- Categories Table -->
    <div class="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
      <table class="w-full min-w-[650px] text-left text-xs">
        <thead class="bg-slate-50 border-b border-slate-200 text-xs uppercase tracking-wider text-slate-500">
          <tr>
            <th class="px-4 py-3 font-semibold">ID</th>
            <th class="px-4 py-3 font-semibold">Tên danh mục</th>
            <th class="px-4 py-3 font-semibold">Slug</th>
            <th class="px-4 py-3 font-semibold">Mô tả</th>
            <th class="px-4 py-3 font-semibold text-center">Số sản phẩm</th>
            <th class="px-4 py-3 font-semibold text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200/80">
          <tr v-if="loading">
            <td colspan="6" class="p-8 text-center text-slate-400">Đang tải danh sách danh mục...</td>
          </tr>
          <tr v-else-if="categories.length === 0">
            <td colspan="6" class="p-8 text-center text-slate-400">Chưa có danh mục nào.</td>
          </tr>
          <tr v-else v-for="cat in categories" :key="cat.id" class="hover:bg-slate-50/80 transition-colors">
            <td class="px-4 py-3.5 font-mono text-slate-400">#{{ cat.id }}</td>
            <td class="px-4 py-3.5 font-semibold text-slate-900">{{ cat.name }}</td>
            <td class="px-4 py-3.5 font-mono text-slate-600 text-[11px]">{{ cat.slug }}</td>
            <td class="px-4 py-3.5 text-slate-500 max-w-xs truncate">{{ cat.description || '—' }}</td>
            <td class="px-4 py-3.5 text-center">
              <span class="px-2.5 py-0.5 rounded-full bg-slate-100 text-slate-700 font-semibold text-[11px]">
                {{ cat.productCount || 0 }} SP
              </span>
            </td>
            <td class="px-4 py-3.5 text-right">
              <div class="inline-flex items-center gap-1.5">
                <button
                  type="button"
                  @click="openEditModal(cat)"
                  class="h-8 px-3 bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-lg font-semibold text-[11px] transition-colors"
                >
                  Sửa
                </button>
                <button
                  type="button"
                  @click="deleteCategory(cat)"
                  :disabled="deletingId === cat.id"
                  class="h-8 px-2.5 bg-red-50 hover:bg-red-600 text-red-600 hover:text-white border border-red-200 rounded-lg font-semibold text-[11px] transition-colors disabled:opacity-50"
                  title="Xóa danh mục"
                >
                  {{ deletingId === cat.id ? '...' : 'Xóa' }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Create / Edit Category -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-[9990] bg-slate-900/50 backdrop-blur-[2px] flex items-center justify-center p-4 no-print"
      >
        <form
          @submit.prevent="submitForm"
          class="relative z-[9995] bg-white rounded-2xl border border-slate-200 max-w-md max-h-[90vh] flex flex-col overflow-hidden w-full shadow-2xl"
        >
          <!-- Sticky Header -->
          <div class="px-6 py-4 flex items-center justify-between border-b border-slate-200 shrink-0">
            <h3 class="text-sm sm:text-base font-bold text-slate-900">
              {{ editingId ? 'Chỉnh Sửa Danh Mục' : 'Thêm Danh Mục Mới' }}
            </h3>
            <button type="button" @click="closeModal" class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-700 font-bold">✕</button>
          </div>

          <!-- Scrollable Body -->
          <div class="flex-1 overflow-y-auto p-6 space-y-4">
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Tên danh mục *</label>
              <input
                v-model="form.name"
                type="text"
                required
                placeholder="VD: Phụ Kiện Thời Trang"
                class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              />
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Slug URL (tùy chọn - tự sinh nếu để trống)</label>
              <input
                v-model="form.slug"
                type="text"
                placeholder="phu-kien-thoi-trang"
                class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs font-mono focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              />
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Mô tả</label>
              <textarea
                v-model="form.description"
                rows="3"
                placeholder="Mô tả ngắn gọn về danh mục..."
                class="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              ></textarea>
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
              {{ saving ? 'Đang lưu...' : (editingId ? 'Lưu Thay Đổi' : 'Tạo Danh Mục') }}
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

const toastStore = useToastStore();
const categories = ref([]);
const loading = ref(false);
const deletingId = ref(null);
const showModal = ref(false);
const editingId = ref(null);
const saving = ref(false);

const form = reactive({
  name: '',
  slug: '',
  description: ''
});

async function loadCategories() {
  loading.value = true;
  try {
    const res = await api.get('/admin/categories');
    if (res.success && Array.isArray(res.data)) {
      categories.value = res.data;
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể tải danh sách danh mục.');
  } finally {
    loading.value = false;
  }
}

function openCreateModal() {
  editingId.value = null;
  form.name = '';
  form.slug = '';
  form.description = '';
  showModal.value = true;
}

function openEditModal(cat) {
  editingId.value = cat.id;
  form.name = cat.name || '';
  form.slug = cat.slug || '';
  form.description = cat.description || '';
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
      name: form.name.trim(),
      slug: form.slug.trim() || null,
      description: form.description.trim()
    };

    const res = editingId.value
      ? await api.put(`/admin/categories/${editingId.value}`, payload)
      : await api.post('/admin/categories', payload);

    if (res.success) {
      toastStore.success(res.message || 'Lưu danh mục thành công!');
      closeModal();
      await loadCategories();
    } else {
      toastStore.error(res.message || 'Không thể lưu danh mục.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi khi lưu danh mục.');
  } finally {
    saving.value = false;
  }
}

async function deleteCategory(cat) {
  if (deletingId.value) return;
  if (!confirm(`Bạn có chắc chắn muốn xóa danh mục "${cat.name}"?`)) return;

  deletingId.value = cat.id;
  try {
    const res = await api.delete(`/admin/categories/${cat.id}`);
    if (res.success) {
      toastStore.success(res.message || `Đã xóa danh mục "${cat.name}"`);
      categories.value = categories.value.filter(c => c.id !== cat.id);
    } else {
      toastStore.error(res.message || 'Không thể xóa danh mục.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể xóa danh mục đang chứa sản phẩm.');
  } finally {
    deletingId.value = null;
  }
}

onMounted(() => {
  loadCategories();
});
</script>
