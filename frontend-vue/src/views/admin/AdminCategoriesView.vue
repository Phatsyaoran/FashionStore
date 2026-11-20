<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Quản Lý Danh Mục Sản Phẩm
        </h1>
        <p class="text-xs text-gray-500 mt-1">
          Thêm mới, cập nhật mô tả/slug và xóa an toàn các danh mục chưa có sản phẩm
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2.5">
        <button
          type="button"
          @click="openCreateModal"
          class="px-4 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold text-xs shadow-md shadow-rose-600/20 transition-all flex items-center gap-1.5"
        >
          <span>＋</span>
          <span>Thêm Danh Mục</span>
        </button>

        <router-link
          to="/admin"
          class="text-xs font-bold text-gray-600 hover:text-gray-900 px-3 py-2 rounded-xl hover:bg-gray-100 transition-colors"
        >
          ← Về Dashboard
        </router-link>
      </div>
    </div>

    <!-- Categories Table -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[650px] text-left text-xs">
          <thead class="bg-gray-50 border-b border-gray-100 text-gray-400 font-bold uppercase tracking-wider">
            <tr>
              <th class="p-4">ID</th>
              <th class="p-4">Tên danh mục</th>
              <th class="p-4">Slug</th>
              <th class="p-4">Mô tả</th>
              <th class="p-4 text-center">Số sản phẩm</th>
              <th class="p-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-if="loading">
              <td colspan="6" class="p-8 text-center text-gray-400">Đang tải danh sách danh mục...</td>
            </tr>
            <tr v-else-if="categories.length === 0">
              <td colspan="6" class="p-8 text-center text-gray-400">Chưa có danh mục nào.</td>
            </tr>
            <tr v-else v-for="cat in categories" :key="cat.id" class="hover:bg-gray-50 transition-colors">
              <td class="p-4 font-mono text-gray-400">#{{ cat.id }}</td>
              <td class="p-4 font-bold text-gray-900">{{ cat.name }}</td>
              <td class="p-4 font-mono text-rose-600">{{ cat.slug }}</td>
              <td class="p-4 text-gray-500 max-w-xs truncate">{{ cat.description || '—' }}</td>
              <td class="p-4 text-center">
                <span class="px-2.5 py-1 rounded-full bg-gray-100 text-gray-800 font-bold">
                  {{ cat.productCount || 0 }} SP
                </span>
              </td>
              <td class="p-4 text-right">
                <div class="inline-flex items-center gap-1.5">
                  <button
                    type="button"
                    @click="openEditModal(cat)"
                    class="px-3 py-1.5 bg-amber-50 hover:bg-amber-500 hover:text-white text-amber-700 rounded-xl font-bold transition-colors"
                  >
                    ✏️ Sửa
                  </button>
                  <button
                    type="button"
                    @click="deleteCategory(cat)"
                    :disabled="deletingId === cat.id"
                    class="px-2.5 py-1.5 bg-rose-50 hover:bg-rose-600 hover:text-white text-rose-600 rounded-xl font-bold transition-colors disabled:opacity-50"
                    title="Xóa danh mục"
                  >
                    {{ deletingId === cat.id ? '...' : '🗑️' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Create / Edit Category -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-[9990] overflow-y-auto bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 no-print"
      >
        <div class="relative z-[9995] bg-white rounded-3xl p-6 sm:p-8 max-w-md max-h-[90vh] overflow-y-auto w-full space-y-5 shadow-2xl">
        <div class="flex items-center justify-between pb-3 border-b border-gray-100">
          <h3 class="text-base font-black text-gray-900">
            {{ editingId ? '✏️ Chỉnh Sửa Danh Mục' : '➕ Thêm Danh Mục Mới' }}
          </h3>
          <button type="button" @click="closeModal" class="text-gray-400 hover:text-gray-700 font-bold text-lg p-1">✕</button>
        </div>

        <form @submit.prevent="submitForm" class="space-y-4">
          <div>
            <label class="block text-xs font-bold text-gray-700 mb-1">Tên danh mục *</label>
            <input
              v-model="form.name"
              type="text"
              required
              placeholder="VD: Phụ Kiện Thời Trang"
              class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-xs font-bold text-gray-700 mb-1">Slug URL (tùy chọn - tự sinh nếu để trống)</label>
            <input
              v-model="form.slug"
              type="text"
              placeholder="phu-kien-thoi-trang"
              class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs font-mono focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-xs font-bold text-gray-700 mb-1">Mô tả</label>
            <textarea
              v-model="form.description"
              rows="3"
              placeholder="Mô tả ngắn gọn về danh mục..."
              class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            ></textarea>
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
              {{ saving ? 'Đang lưu...' : (editingId ? 'Lưu Thay Đổi' : 'Tạo Danh Mục') }}
            </button>
          </div>
        </form>
        </div>
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
