<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
    
    <!-- Breadcrumb & Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-gray-200">
      <div>
        <div class="text-xs text-gray-500 flex items-center gap-2 mb-1">
          <router-link to="/" class="hover:text-rose-600">Trang chủ</router-link>
          <span>/</span>
          <span class="text-gray-900 font-semibold">Cửa hàng</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
          Bộ Sưu Tập Thời Trang
        </h1>
        <p class="text-xs text-gray-500 mt-1">Tìm thấy {{ totalProducts }} sản phẩm phù hợp</p>
      </div>

      <!-- Sort dropdown & mobile filter trigger -->
      <div class="flex items-center gap-3">
        <button
          @click="showMobileFilter = !showMobileFilter"
          class="lg:hidden px-4 py-2 bg-white border border-gray-200 rounded-xl text-xs font-bold text-gray-700 flex items-center gap-2 shadow-sm"
        >
          <span>⚡ Bộ Lọc</span>
        </button>

        <div class="flex items-center gap-2 text-xs">
          <span class="text-gray-500 font-medium">Sắp xếp:</span>
          <select
            v-model="filters.sort"
            @change="fetchProducts(0)"
            class="bg-white border border-gray-200 rounded-xl px-3 py-2 font-semibold text-gray-800 focus:outline-none focus:ring-2 focus:ring-rose-500"
          >
            <option value="latest">Mới nhất</option>
            <option value="price_asc">Giá tăng dần</option>
            <option value="price_desc">Giá giảm dần</option>
            <option value="sale">Ưu đãi nhiều nhất</option>
          </select>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
      
      <!-- FILTER SIDEBAR (Desktop & Mobile Drawer) -->
      <aside
        :class="showMobileFilter ? 'fixed inset-0 z-50 bg-white p-6 overflow-y-auto block' : 'hidden lg:block'"
        class="space-y-6"
      >
        <div class="flex lg:hidden justify-between items-center pb-4 border-b">
          <h3 class="font-bold text-base text-gray-900">Bộ Lọc Đa Tiêu Chí</h3>
          <button @click="showMobileFilter = false" class="text-gray-500 font-bold text-lg">✕</button>
        </div>

        <!-- Filter 1: Categories -->
        <div class="space-y-3">
          <h4 class="text-xs font-bold uppercase tracking-wider text-gray-900">Danh Mục</h4>
          <div class="space-y-1.5 text-xs">
            <button
              type="button"
              @click="selectCategory('')"
              class="w-full text-left py-1.5 px-3 rounded-xl transition-colors font-medium flex justify-between"
              :class="!filters.categorySlug ? 'bg-rose-50 text-rose-600 font-bold' : 'text-gray-600 hover:bg-gray-50'"
            >
              <span>Tất cả danh mục</span>
            </button>
            <button
              v-for="cat in categories"
              :key="cat.id"
              type="button"
              @click="selectCategory(cat.slug)"
              class="w-full text-left py-1.5 px-3 rounded-xl transition-colors font-medium flex justify-between items-center"
              :class="filters.categorySlug === cat.slug ? 'bg-rose-50 text-rose-600 font-bold' : 'text-gray-600 hover:bg-gray-50'"
            >
              <span>{{ cat.name }}</span>
              <span class="text-[10px] text-gray-400">({{ cat.productCount || 0 }})</span>
            </button>
          </div>
        </div>

        <!-- Filter 2: Price Range -->
        <div class="space-y-3 pt-4 border-t border-gray-100">
          <h4 class="text-xs font-bold uppercase tracking-wider text-gray-900">Khoảng Giá (VNĐ)</h4>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <input
                type="number"
                v-model.number="filters.minPrice"
                placeholder="Từ"
                step="50000"
                class="w-full px-3 py-1.5 border border-gray-200 rounded-xl text-xs bg-gray-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-rose-500"
              />
              <span class="text-gray-400 text-xs">-</span>
              <input
                type="number"
                v-model.number="filters.maxPrice"
                placeholder="Đến"
                step="50000"
                class="w-full px-3 py-1.5 border border-gray-200 rounded-xl text-xs bg-gray-50 focus:bg-white focus:outline-none focus:ring-1 focus:ring-rose-500"
              />
            </div>
            <button
              @click="fetchProducts(0)"
              class="w-full py-1.5 bg-gray-900 hover:bg-rose-600 text-white font-bold text-xs rounded-xl transition-colors"
            >
              Áp dụng giá
            </button>
          </div>
        </div>

        <!-- Filter 3: Sizes -->
        <div class="space-y-3 pt-4 border-t border-gray-100">
          <h4 class="text-xs font-bold uppercase tracking-wider text-gray-900">Kích Cỡ (Size)</h4>
          <div class="grid grid-cols-4 gap-2">
            <button
              v-for="s in ['S', 'M', 'L', 'XL']"
              :key="s"
              type="button"
              @click="toggleSize(s)"
              class="py-2 rounded-xl text-xs font-bold border transition-all text-center"
              :class="filters.size === s ? 'border-rose-600 bg-rose-600 text-white shadow-sm' : 'border-gray-200 text-gray-700 hover:bg-gray-50'"
            >
              {{ s }}
            </button>
          </div>
        </div>

        <!-- Filter 4: Colors -->
        <div class="space-y-3 pt-4 border-t border-gray-100">
          <h4 class="text-xs font-bold uppercase tracking-wider text-gray-900">Màu Sắc</h4>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="col in popularColors"
              :key="col.name"
              type="button"
              @click="toggleColor(col.name)"
              class="flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-medium transition-all"
              :class="filters.color === col.name ? 'border-rose-600 bg-rose-50 text-rose-700 font-bold' : 'border-gray-200 text-gray-600 hover:bg-gray-50'"
            >
              <span class="w-3 h-3 rounded-full border border-gray-300" :style="{ backgroundColor: col.hex }"></span>
              <span>{{ col.name }}</span>
            </button>
          </div>
        </div>

        <!-- Filter 5: Flash Sale -->
        <div class="pt-4 border-t border-gray-100">
          <label class="flex items-center gap-2 cursor-pointer text-xs font-bold text-gray-800">
            <input
              type="checkbox"
              v-model="filters.isFlashSale"
              @change="fetchProducts(0)"
              class="w-4 h-4 rounded text-rose-600 focus:ring-rose-500 accent-rose-600 cursor-pointer"
            />
            <span class="text-rose-600">⚡ Chỉ xem Flash Sale</span>
          </label>
        </div>

        <!-- Reset Button -->
        <div class="pt-4 border-t border-gray-100">
          <button
            type="button"
            @click="resetFilters"
            class="w-full py-2 border border-gray-200 text-gray-600 hover:text-rose-600 hover:bg-rose-50 font-semibold text-xs rounded-xl transition-all"
          >
            Đặt lại bộ lọc
          </button>
        </div>
      </aside>

      <!-- PRODUCTS CONTENT AREA -->
      <main class="lg:col-span-3 space-y-6">
        
        <!-- Loading Spinner -->
        <div v-if="loading" class="py-24 text-center">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-rose-500 border-t-transparent"></div>
          <p class="text-xs text-gray-500 mt-2 font-medium">Đang tải danh sách sản phẩm...</p>
        </div>

        <!-- Empty state -->
        <div v-else-if="products.length === 0" class="py-24 text-center bg-white rounded-3xl border border-gray-100 p-8">
          <span class="text-5xl block mb-3">🔍</span>
          <h3 class="text-base font-bold text-gray-800">Không tìm thấy sản phẩm phù hợp</h3>
          <p class="text-xs text-gray-500 mt-1">Hãy thử nới lỏng các tiêu chí lọc giá, kích cỡ hoặc đổi từ khóa tìm kiếm.</p>
          <button
            @click="resetFilters"
            class="mt-4 px-5 py-2.5 bg-rose-600 text-white font-bold text-xs rounded-xl hover:bg-rose-700 shadow-md"
          >
            Xóa tất cả bộ lọc
          </button>
        </div>

        <!-- Product Grid with Quick View Button on Cards -->
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 gap-4 sm:gap-6">
          <ProductCard
            v-for="product in products"
            :key="product.id"
            :product="product"
          />
        </div>

        <!-- Pagination Controls -->
        <div v-if="totalPages > 1" class="pt-8 border-t border-gray-100 flex items-center justify-center gap-2">
          <button
            :disabled="currentPage === 0"
            @click="fetchProducts(currentPage - 1)"
            class="px-4 py-2 rounded-xl border border-gray-200 text-xs font-bold transition-colors disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            ← Trước
          </button>
          
          <button
            v-for="page in totalPages"
            :key="page"
            @click="fetchProducts(page - 1)"
            class="w-9 h-9 rounded-xl text-xs font-bold transition-all"
            :class="currentPage === (page - 1) ? 'bg-rose-600 text-white shadow-md shadow-rose-200' : 'border border-gray-200 text-gray-700 hover:bg-gray-50'"
          >
            {{ page }}
          </button>

          <button
            :disabled="currentPage >= totalPages - 1"
            @click="fetchProducts(currentPage + 1)"
            class="px-4 py-2 rounded-xl border border-gray-200 text-xs font-bold transition-colors disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50"
          >
            Sau →
          </button>
        </div>
      </main>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/services/api';
import ProductCard from '@/components/ProductCard.vue';

const route = useRoute();
const router = useRouter();

const categories = ref([]);
const products = ref([]);
const loading = ref(false);
const showMobileFilter = ref(false);

const totalProducts = ref(0);
const totalPages = ref(1);
const currentPage = ref(0);

const popularColors = [
  { name: 'Đen', hex: '#000000' },
  { name: 'Trắng', hex: '#FFFFFF' },
  { name: 'Xanh Navy', hex: '#1B2A4A' },
  { name: 'Be', hex: '#F5F5DC' },
  { name: 'Xám', hex: '#808080' },
  { name: 'Đỏ', hex: '#800020' }
];

const filters = reactive({
  categorySlug: route.query.categorySlug || '',
  minPrice: null,
  maxPrice: null,
  size: '',
  color: '',
  isFlashSale: route.query.isFlashSale === 'true',
  keyword: route.query.keyword || '',
  sort: 'latest'
});

async function fetchCategories() {
  try {
    const res = await api.get('/categories');
    if (res.success) categories.value = res.data || [];
  } catch (err) {
    console.error('Lỗi nạp danh mục:', err);
  }
}

async function fetchProducts(page = 0) {
  loading.value = true;
  currentPage.value = page;

  try {
    const params = {
      page: page,
      pageSize: 12,
      sort: filters.sort
    };
    if (filters.categorySlug) params.categorySlug = filters.categorySlug;
    if (filters.minPrice) params.minPrice = filters.minPrice;
    if (filters.maxPrice) params.maxPrice = filters.maxPrice;
    if (filters.size) params.size = filters.size;
    if (filters.color) params.color = filters.color;
    if (filters.isFlashSale) params.isFlashSale = true;
    if (filters.keyword) params.keyword = filters.keyword;

    const res = await api.get('/products', { params });
    if (res.success && res.data) {
      products.value = res.data.content || [];
      totalProducts.value = res.data.totalElements || 0;
      totalPages.value = res.data.totalPages || 1;
    }
  } catch (err) {
    console.error('Lỗi nạp sản phẩm:', err);
    products.value = [];
  } finally {
    loading.value = false;
    showMobileFilter.value = false;
  }
}

function selectCategory(slug) {
  filters.categorySlug = slug;
  fetchProducts(0);
}

function toggleSize(s) {
  filters.size = filters.size === s ? '' : s;
  fetchProducts(0);
}

function toggleColor(colName) {
  filters.color = filters.color === colName ? '' : colName;
  fetchProducts(0);
}

function resetFilters() {
  filters.categorySlug = '';
  filters.minPrice = null;
  filters.maxPrice = null;
  filters.size = '';
  filters.color = '';
  filters.isFlashSale = false;
  filters.keyword = '';
  filters.sort = 'latest';
  router.push('/catalog');
  fetchProducts(0);
}

// Theo dõi khi route query thay đổi (từ Search bar hoặc Flash Sale menu)
watch(() => route.query, (newQ) => {
  if (newQ.keyword !== undefined) filters.keyword = newQ.keyword;
  if (newQ.categorySlug !== undefined) filters.categorySlug = newQ.categorySlug;
  if (newQ.isFlashSale !== undefined) filters.isFlashSale = newQ.isFlashSale === 'true';
  fetchProducts(0);
});

onMounted(() => {
  fetchCategories();
  fetchProducts(0);
});
</script>
