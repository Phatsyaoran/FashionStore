<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
    
    <!-- Breadcrumb & Header -->
    <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 pb-6 border-b border-neutral-200/80">
      <div>
        <div class="text-xs text-[#6B7280] flex items-center gap-2 mb-1.5">
          <router-link to="/" class="hover:text-[#7A1032] transition-colors">Trang chủ</router-link>
          <span class="text-neutral-300">/</span>
          <span class="text-[#18181B] font-semibold">Cửa hàng</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold text-[#18181B] tracking-tight">
          Bộ Sưu Tập Thời Trang
        </h1>
        <p class="text-xs text-[#6B7280] mt-1">Tìm thấy <strong class="text-[#7A1032]">{{ totalProducts }}</strong> sản phẩm phù hợp</p>
      </div>

      <!-- Sort dropdown & mobile filter trigger -->
      <div class="flex items-center gap-2.5">
        <button
          type="button"
          @click="showMobileFilter = true"
          class="lg:hidden min-h-[44px] px-4 bg-white border border-neutral-200 hover:border-[#C9963B]/70 rounded-lg text-xs font-bold text-[#18181B] flex items-center gap-2 transition-colors shadow-sm"
        >
          <span>☰ Bộ lọc</span>
        </button>

        <div class="flex items-center gap-2 text-xs">
          <span class="text-[#6B7280] font-medium hidden sm:inline">Sắp xếp:</span>
          <select
            v-model="filters.sort"
            @change="fetchProducts(0)"
            class="h-11 bg-white border border-neutral-200 rounded-lg px-3.5 font-semibold text-[#18181B] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] shadow-sm"
          >
            <option value="latest">Mới nhất</option>
            <option value="price_asc">Giá tăng dần</option>
            <option value="price_desc">Giá giảm dần</option>
            <option value="sale">Ưu đãi nhiều nhất</option>
          </select>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
      
      <!-- DESKTOP FILTER SIDEBAR (Clean White Luxury) -->
      <aside class="hidden lg:block lg:sticky lg:top-24 bg-white rounded-xl border border-neutral-200/80 shadow-sm p-5 space-y-6">
        <!-- Filter 1: Categories -->
        <div class="space-y-2.5">
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Danh Mục</h4>
          <div class="space-y-1 text-xs">
            <button
              type="button"
              @click="selectCategory('')"
              class="w-full text-left py-2 px-3 rounded-lg transition-colors font-medium flex justify-between"
              :class="!filters.categorySlug ? 'bg-[#7A1032] text-white font-bold shadow-sm' : 'text-[#6B7280] hover:bg-neutral-100 hover:text-[#18181B]'"
            >
              <span>Tất cả danh mục</span>
            </button>
            <button
              v-for="cat in categories"
              :key="cat.id"
              type="button"
              @click="selectCategory(cat.slug)"
              class="w-full text-left py-2 px-3 rounded-lg transition-colors font-medium flex justify-between items-center"
              :class="filters.categorySlug === cat.slug ? 'bg-[#7A1032] text-white font-bold shadow-sm' : 'text-[#6B7280] hover:bg-neutral-100 hover:text-[#18181B]'"
            >
              <span>{{ cat.name }}</span>
              <span class="text-[10px]" :class="filters.categorySlug === cat.slug ? 'text-white/80' : 'text-neutral-400'">{{ cat.productCount || 0 }}</span>
            </button>
          </div>
        </div>

        <!-- Filter 2: Price Range -->
        <div class="space-y-2.5 pt-5 border-t border-neutral-200/80">
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Khoảng Giá (VNĐ)</h4>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <input
                type="number"
                v-model.number="filters.minPrice"
                placeholder="Từ"
                step="50000"
                class="w-full h-10 px-3 border border-neutral-200 rounded-lg text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
              />
              <span class="text-neutral-400 text-xs">-</span>
              <input
                type="number"
                v-model.number="filters.maxPrice"
                placeholder="Đến"
                step="50000"
                class="w-full h-10 px-3 border border-neutral-200 rounded-lg text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
              />
            </div>
            <button
              type="button"
              @click="fetchProducts(0)"
              class="btn-sheen w-full h-10 bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs rounded-lg transition-colors"
            >
              Áp dụng giá
            </button>
          </div>
        </div>

        <!-- Filter 3: Sizes -->
        <div class="space-y-3 pt-5 border-t border-neutral-200/80">
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Kích Cỡ (Size)</h4>
          <div class="space-y-2.5">
            <p class="text-[10px] font-semibold text-[#6B7280] uppercase tracking-wider">Size Áo / Váy</p>
            <div class="grid grid-cols-5 gap-1.5">
              <button
                v-for="s in ['S', 'M', 'L', 'XL', 'XXL']"
                :key="s"
                type="button"
                @click="toggleSize(s)"
                class="py-1.5 rounded-lg text-xs font-bold border transition-all text-center"
                :class="filters.size === s ? 'border-[#7A1032] bg-[#7A1032] text-white' : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
              >
                {{ s }}
              </button>
            </div>
            <p class="text-[10px] font-semibold text-[#6B7280] uppercase tracking-wider pt-1">Size Quần</p>
            <div class="grid grid-cols-5 gap-1.5">
              <button
                v-for="s in ['28', '29', '30', '31', '32']"
                :key="s"
                type="button"
                @click="toggleSize(s)"
                class="py-1.5 rounded-lg text-xs font-bold border transition-all text-center"
                :class="filters.size === s ? 'border-[#7A1032] bg-[#7A1032] text-white' : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
              >
                {{ s }}
              </button>
            </div>
          </div>
        </div>

        <!-- Filter 4: Colors -->
        <div class="space-y-2.5 pt-5 border-t border-neutral-200/80">
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Màu Sắc</h4>
          <div class="flex flex-wrap gap-1.5">
            <button
              v-for="col in popularColors"
              :key="col.name"
              type="button"
              @click="toggleColor(col.name)"
              class="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg border text-xs font-medium transition-all"
              :class="filters.color === col.name ? 'border-[#7A1032] bg-[#7A1032] text-white font-bold' : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
            >
              <span class="w-3 h-3 rounded-full border border-neutral-300" :style="{ backgroundColor: col.hex }"></span>
              <span>{{ col.name }}</span>
            </button>
          </div>
        </div>

        <!-- Filter 5: Flash Sale -->
        <div class="pt-5 border-t border-neutral-200/80">
          <label class="flex items-center gap-2.5 cursor-pointer text-xs font-bold text-[#7A1032]">
            <input
              type="checkbox"
              v-model="filters.isFlashSale"
              @change="fetchProducts(0)"
              class="w-4 h-4 rounded accent-[#7A1032] cursor-pointer"
            />
            <span>⚡ Chỉ xem Flash Sale</span>
          </label>
        </div>

        <!-- Reset Button -->
        <div class="pt-4 border-t border-neutral-200/80">
          <button
            type="button"
            @click="resetFilters"
            class="w-full h-10 border border-neutral-200 bg-white text-[#18181B] hover:bg-neutral-50 hover:border-neutral-300 font-semibold text-xs rounded-lg transition-colors"
          >
            Đặt lại bộ lọc
          </button>
        </div>
      </aside>

      <!-- MOBILE FILTER DRAWER (< lg) — Teleported to body -->
      <Teleport to="body">
        <div v-if="showMobileFilter" class="fixed inset-0 z-[9990] lg:hidden flex no-print" role="dialog" aria-modal="true">
          <div class="fixed inset-0 z-[9990] bg-black/45 backdrop-blur-[2px]" @click="showMobileFilter = false"></div>
          <div class="relative z-[9995] w-[86vw] max-w-sm bg-white border-r border-neutral-200 h-screen shadow-2xl flex flex-col text-[#18181B]">
            <div class="px-5 py-4 border-b border-neutral-200 flex items-center justify-between">
              <h3 class="font-extrabold text-sm uppercase tracking-wider text-[#18181B]">Bộ Lọc Sản Phẩm</h3>
              <button
                type="button"
                @click="showMobileFilter = false"
                class="min-w-[44px] min-h-[44px] -mr-2 rounded-lg text-[#6B7280] hover:bg-neutral-100 hover:text-[#18181B] flex items-center justify-center"
              >
                ✕
              </button>
            </div>

            <div class="flex-1 overflow-y-auto p-5 space-y-6">
              <div class="space-y-2">
                <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Danh Mục</h4>
                <div class="space-y-1 text-xs">
                  <button
                    type="button"
                    @click="selectCategory('')"
                    class="w-full min-h-[40px] text-left px-3 rounded-lg font-medium flex justify-between items-center"
                    :class="!filters.categorySlug ? 'bg-[#7A1032] text-white font-bold' : 'text-[#6B7280] hover:bg-neutral-100'"
                  >
                    <span>Tất cả danh mục</span>
                  </button>
                  <button
                    v-for="cat in categories"
                    :key="cat.id"
                    type="button"
                    @click="selectCategory(cat.slug)"
                    class="w-full min-h-[40px] text-left px-3 rounded-lg font-medium flex justify-between items-center"
                    :class="filters.categorySlug === cat.slug ? 'bg-[#7A1032] text-white font-bold' : 'text-[#6B7280] hover:bg-neutral-100'"
                  >
                    <span>{{ cat.name }}</span>
                    <span class="text-[10px] opacity-70">{{ cat.productCount || 0 }}</span>
                  </button>
                </div>
              </div>

              <div class="space-y-2 pt-4 border-t border-neutral-200/80">
                <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Khoảng Giá (VNĐ)</h4>
                <div class="flex items-center gap-2">
                  <input type="number" v-model.number="filters.minPrice" placeholder="Từ" step="50000" class="w-full h-11 px-3 border border-neutral-200 bg-white text-[#18181B] rounded-lg text-xs" />
                  <span class="text-neutral-400">-</span>
                  <input type="number" v-model.number="filters.maxPrice" placeholder="Đến" step="50000" class="w-full h-11 px-3 border border-neutral-200 bg-white text-[#18181B] rounded-lg text-xs" />
                </div>
              </div>

              <div class="space-y-2 pt-4 border-t border-neutral-200/80">
                <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Kích Cỡ</h4>
                <div class="grid grid-cols-5 gap-1.5">
                  <button
                    v-for="s in ['S', 'M', 'L', 'XL', 'XXL', '28', '29', '30', '31', '32']"
                    :key="s"
                    type="button"
                    @click="toggleSize(s)"
                    class="py-2 rounded-lg text-xs font-bold border"
                    :class="filters.size === s ? 'border-[#7A1032] bg-[#7A1032] text-white' : 'border-neutral-200 bg-white text-[#18181B]'"
                  >
                    {{ s }}
                  </button>
                </div>
              </div>

              <div class="space-y-2 pt-4 border-t border-neutral-200/80">
                <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#7A1032]">Màu Sắc</h4>
                <div class="flex flex-wrap gap-2">
                  <button
                    v-for="col in popularColors"
                    :key="col.name"
                    type="button"
                    @click="toggleColor(col.name)"
                    class="flex items-center gap-1.5 px-3 py-2 rounded-lg border text-xs font-medium"
                    :class="filters.color === col.name ? 'border-[#7A1032] bg-[#7A1032] text-white font-bold' : 'border-neutral-200 bg-white text-[#18181B]'"
                  >
                    <span class="w-3 h-3 rounded-full border border-neutral-300" :style="{ backgroundColor: col.hex }"></span>
                    <span>{{ col.name }}</span>
                  </button>
                </div>
              </div>

              <div class="pt-4 border-t border-neutral-200/80">
                <label class="flex items-center gap-2.5 cursor-pointer text-xs font-bold text-[#7A1032]">
                  <input type="checkbox" v-model="filters.isFlashSale" @change="fetchProducts(0)" class="w-4 h-4 rounded accent-[#7A1032]" />
                  <span>⚡ Chỉ xem Flash Sale</span>
                </label>
              </div>
            </div>

            <div class="p-4 border-t border-neutral-200 bg-gray-50/80 grid grid-cols-2 gap-3">
              <button type="button" @click="resetFilters" class="min-h-[44px] rounded-lg border border-neutral-200 bg-white text-[#18181B] text-xs font-bold">
                Đặt lại
              </button>
              <button type="button" @click="fetchProducts(0)" class="min-h-[44px] rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold">
                Áp dụng
              </button>
            </div>
          </div>
        </div>
      </Teleport>

      <!-- PRODUCTS CONTENT AREA -->
      <main class="lg:col-span-3 space-y-6">
        
        <!-- Skeleton Loading Shimmer -->
        <div v-if="loading" class="grid grid-cols-2 md:grid-cols-3 gap-3 sm:gap-6">
          <div
            v-for="n in 6"
            :key="n"
            class="bg-white rounded-xl border border-neutral-200/80 overflow-hidden"
          >
            <div class="aspect-[3/4] skeleton-shimmer"></div>
            <div class="p-4 space-y-2.5">
              <div class="h-3 w-1/3 rounded skeleton-shimmer"></div>
              <div class="h-4 w-5/6 rounded skeleton-shimmer"></div>
              <div class="h-4 w-1/2 rounded skeleton-shimmer"></div>
            </div>
          </div>
        </div>

        <!-- Empty state -->
        <div v-else-if="products.length === 0" class="py-20 text-center bg-white rounded-xl border border-neutral-200/80 shadow-sm p-8 space-y-3">
          <h3 class="text-base font-bold text-[#18181B]">Không tìm thấy sản phẩm phù hợp</h3>
          <p class="text-xs text-[#6B7280] max-w-sm mx-auto">Hãy thử nới lỏng các tiêu chí lọc giá, kích cỡ hoặc đổi từ khóa tìm kiếm.</p>
          <button
            type="button"
            @click="resetFilters"
            class="btn-sheen mt-2 px-6 h-11 bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs rounded-lg transition-colors"
          >
            Xóa tất cả bộ lọc
          </button>
        </div>

        <!-- Product Grid -->
        <div v-else class="grid grid-cols-2 md:grid-cols-3 gap-3 sm:gap-6">
          <ProductCard
            v-for="(product, idx) in products"
            :key="product.id"
            v-reveal="(idx % 3) * 60"
            :product="product"
          />
        </div>

        <!-- Pagination Controls -->
        <div v-if="totalPages > 1" class="pt-8 border-t border-neutral-200/80 flex items-center justify-center gap-2">
          <button
            type="button"
            :disabled="currentPage === 0"
            @click="fetchProducts(currentPage - 1)"
            class="h-10 px-4 rounded-lg border border-neutral-200 bg-white text-xs font-bold text-[#18181B] transition-colors disabled:opacity-40 disabled:cursor-not-allowed hover:border-[#C9963B]"
          >
            ← Trước
          </button>
          
          <button
            v-for="page in totalPages"
            :key="page"
            type="button"
            @click="fetchProducts(page - 1)"
            class="w-10 h-10 rounded-lg text-xs font-bold transition-all"
            :class="currentPage === (page - 1) ? 'bg-[#7A1032] text-white shadow-sm' : 'border border-neutral-200 bg-white text-[#6B7280] hover:text-[#18181B] hover:border-[#C9963B]'"
          >
            {{ page }}
          </button>

          <button
            type="button"
            :disabled="currentPage >= totalPages - 1"
            @click="fetchProducts(currentPage + 1)"
            class="h-10 px-4 rounded-lg border border-neutral-200 bg-white text-xs font-bold text-[#18181B] transition-colors disabled:opacity-40 disabled:cursor-not-allowed hover:border-[#C9963B]"
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

// Theo dõi khi route query thay đổi (từ Search bar, Danh mục, Flash Sale hoặc quay về /catalog)
watch(() => route.query, (newQ) => {
  filters.keyword = newQ.keyword ? String(newQ.keyword) : '';
  filters.categorySlug = newQ.categorySlug ? String(newQ.categorySlug) : '';
  filters.isFlashSale = newQ.isFlashSale === 'true';

  if (Object.keys(newQ).length === 0) {
    filters.minPrice = null;
    filters.maxPrice = null;
    filters.size = '';
    filters.color = '';
    filters.sort = 'latest';
  }

  fetchProducts(0);
});

onMounted(() => {
  fetchCategories();
  fetchProducts(0);
});
</script>
