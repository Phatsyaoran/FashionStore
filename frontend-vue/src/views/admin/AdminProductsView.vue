<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
          Quản Lý Sản Phẩm & Biến Thể
        </h1>
        <p class="text-xs text-slate-500 mt-0.5">
          Bật/tắt Flash Sale, nhập kho nhanh biến thể và xuất báo cáo tồn kho (.CSV)
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <button
          type="button"
          @click="openCreateModal"
          class="h-9 px-3.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg font-semibold text-xs transition-colors inline-flex items-center gap-1.5"
        >
          <span>＋</span>
          <span>Thêm sản phẩm</span>
        </button>

        <button
          type="button"
          @click="exportInventoryCsv"
          :disabled="isExporting"
          class="h-9 px-3.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-lg font-semibold text-xs transition-colors inline-flex items-center gap-1.5"
        >
          <span v-if="isExporting" class="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          <span>📥</span>
          <span>Xuất CSV</span>
        </button>

        <router-link
          to="/admin"
          class="h-9 px-3 inline-flex items-center text-xs font-semibold text-slate-600 hover:text-slate-900 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 transition-colors"
        >
          ← Tổng quan
        </router-link>
      </div>
    </div>

    <!-- Search Toolbar -->
    <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-3.5 flex flex-wrap items-center justify-between gap-3">
      <div class="relative flex-1 min-w-[240px] max-w-md">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Tìm nhanh sản phẩm theo tên, danh mục hoặc slug..."
          class="w-full h-9 pl-9 pr-8 rounded-lg border border-slate-200 bg-slate-50 focus:bg-white text-xs focus:outline-none focus:ring-1 focus:ring-slate-900 focus:border-slate-900"
        />
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-xs">🔍</span>
        <button
          v-if="searchQuery"
          type="button"
          @click="searchQuery = ''"
          class="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 text-xs font-bold"
        >
          ✕
        </button>
      </div>
      <div class="text-xs text-slate-500 font-medium">
        Hiển thị <span class="font-bold text-slate-900">{{ filteredProducts.length }}</span> / {{ products.length }} sản phẩm
      </div>
    </div>

    <!-- Products Table -->
    <div class="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
      <table class="w-full min-w-[760px] text-left text-xs">
        <thead class="bg-slate-50 border-b border-slate-200 text-xs uppercase tracking-wider text-slate-500">
          <tr>
            <th class="px-4 py-3 font-semibold">Ảnh</th>
            <th class="px-4 py-3 font-semibold">Tên sản phẩm</th>
            <th class="px-4 py-3 font-semibold">Danh mục</th>
            <th class="px-4 py-3 font-semibold">Giá bán</th>
            <th class="px-4 py-3 font-semibold text-center">Flash Sale</th>
            <th class="px-4 py-3 font-semibold">Tồn kho</th>
            <th class="px-4 py-3 font-semibold text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200/80">
          <tr v-for="item in filteredProducts" :key="item.id" class="hover:bg-slate-50/80 transition-colors">
            <td class="px-4 py-3">
              <img
                :src="item.thumbnailUrl"
                :alt="item.name"
                @error="onImageError"
                class="w-11 h-14 object-cover rounded-lg bg-slate-100 border border-slate-200"
              />
            </td>
            <td class="px-4 py-3 font-semibold text-slate-900 max-w-xs">
              <span class="line-clamp-2">{{ item.name }}</span>
            </td>
            <td class="px-4 py-3 text-slate-500">
              <span class="px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 font-medium text-[11px]">
                {{ item.categoryName }}
              </span>
            </td>
            <td class="px-4 py-3">
              <div class="font-bold text-slate-900">
                {{ formatVND(item.salePrice || item.basePrice) }}
              </div>
              <div v-if="item.salePrice && item.basePrice > item.salePrice" class="text-[10px] text-slate-400 line-through">
                {{ formatVND(item.basePrice) }}
              </div>
            </td>

            <!-- BẬT/TẮT FLASH SALE NHANH -->
            <td class="px-4 py-3 text-center">
              <div class="flex flex-col items-center gap-1">
                <button
                  type="button"
                  @click="toggleFlashSale(item)"
                  :disabled="togglingId === item.id"
                  class="relative inline-flex h-5 w-10 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none"
                  :class="item.isFlashSale ? 'bg-red-600' : 'bg-slate-200'"
                  :title="item.isFlashSale ? 'Tắt Flash Sale' : 'Bật Flash Sale'"
                >
                  <span
                    class="pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out flex items-center justify-center text-[9px]"
                    :class="item.isFlashSale ? 'translate-x-5' : 'translate-x-0'"
                  >
                    {{ item.isFlashSale ? '⚡' : '' }}
                  </span>
                </button>
                <span class="text-[10px] font-semibold" :class="item.isFlashSale ? 'text-red-600' : 'text-slate-400'">
                  {{ item.isFlashSale ? 'Đang bật' : 'Tắt' }}
                </span>
              </div>
            </td>

            <!-- TỒN KHO & NHẬP KHO NHANH -->
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <span
                  class="font-semibold px-2.5 py-0.5 rounded-full text-[11px] border"
                  :class="item.totalStock > 5
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                    : (item.totalStock > 0
                      ? 'bg-amber-50 text-amber-700 border-amber-200'
                      : 'bg-red-50 text-red-700 border-red-200')"
                >
                  {{ item.totalStock }} chiếc
                </span>
                <button
                  type="button"
                  @click="openStockModal(item)"
                  class="h-7 px-2.5 bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-md text-[11px] font-semibold transition-colors"
                  title="Chỉnh sửa tồn kho từng biến thể"
                >
                  Sửa kho
                </button>
              </div>
            </td>

            <td class="px-4 py-3 text-right">
              <div class="inline-flex items-center gap-1.5">
                <button
                  type="button"
                  @click="openEditModal(item)"
                  class="h-8 px-2.5 bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-lg font-semibold text-[11px] transition-colors"
                  title="Chỉnh sửa sản phẩm & biến thể"
                >
                  Sửa
                </button>
                <router-link
                  :to="`/product/${item.slug}`"
                  target="_blank"
                  class="h-8 px-2.5 inline-flex items-center bg-white hover:bg-slate-100 border border-slate-200 text-slate-700 rounded-lg font-semibold text-[11px] transition-colors"
                >
                  Xem ↗
                </router-link>
                <button
                  type="button"
                  @click="deleteProduct(item)"
                  :disabled="deletingId === item.id"
                  class="h-8 px-2.5 bg-red-50 hover:bg-red-600 text-red-600 hover:text-white border border-red-200 rounded-lg font-semibold text-[11px] transition-colors inline-flex items-center gap-1 disabled:opacity-50"
                  title="Xóa sản phẩm an toàn"
                >
                  <span v-if="deletingId === item.id" class="w-3 h-3 border-2 border-current border-t-transparent rounded-full animate-spin"></span>
                  <span v-else>Xóa</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- MODAL NHẬP KHO NHANH BIẾN THỂ (Quick Stock Edit Modal) -->
    <Teleport to="body">
      <div
        v-if="selectedProductForStock"
        class="fixed inset-0 z-[9990] bg-slate-900/50 backdrop-blur-[2px] flex items-center justify-center p-4 no-print"
      >
        <div class="relative z-[9995] bg-white rounded-2xl border border-slate-200 max-w-xl max-h-[90vh] flex flex-col overflow-hidden w-full shadow-2xl">
          <!-- Modal Header -->
          <div class="px-6 py-4 flex justify-between items-start border-b border-slate-200 shrink-0">
            <div class="flex items-center gap-3 min-w-0">
              <img
                :src="selectedProductForStock.thumbnailUrl"
                :alt="selectedProductForStock.name"
                class="w-11 h-14 object-cover rounded-lg bg-slate-100 border border-slate-200 shrink-0"
              />
              <div class="min-w-0">
                <h3 class="font-bold text-sm text-slate-900 line-clamp-1">
                  {{ selectedProductForStock.name }}
                </h3>
                <p class="text-[11px] text-slate-500 mt-0.5">
                  Nhập kho nhanh từng phân loại Size & Màu sắc
                </p>
              </div>
            </div>
            <button
              @click="closeStockModal"
              class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-700 font-bold shrink-0"
            >
              ✕
            </button>
          </div>

          <!-- Scrollable Variants List -->
          <div class="flex-1 overflow-y-auto p-6">
            <div v-if="loadingVariants" class="py-12 text-center text-xs text-slate-400">
              <span class="inline-block w-5 h-5 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mb-2"></span>
              <div>Đang nạp danh sách biến thể...</div>
            </div>

            <div v-else class="space-y-2.5">
              <div
                v-for="variant in productVariants"
                :key="variant.id"
                class="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 flex flex-wrap items-center justify-between gap-3"
              >
                <div class="flex items-center gap-2.5">
                  <span
                    class="w-3.5 h-3.5 rounded-full border border-slate-300 shrink-0"
                    :style="{ backgroundColor: variant.colorHex }"
                  ></span>
                  <div>
                    <div class="font-semibold text-xs text-slate-900">
                      Size {{ variant.size }} - {{ variant.colorName }}
                    </div>
                    <div class="font-mono text-[10px] text-slate-400">
                      SKU: {{ variant.sku }}
                    </div>
                  </div>
                </div>

                <div class="flex items-center gap-2">
                  <div class="flex items-center gap-1">
                    <button
                      type="button"
                      @click="adjustStock(variant, -5)"
                      class="w-7 h-7 rounded-md bg-white hover:bg-slate-100 border border-slate-200 font-semibold text-[11px] text-slate-700"
                    >
                      -5
                    </button>
                    <input
                      type="number"
                      min="0"
                      v-model.number="variant.stockQuantity"
                      class="w-16 h-7 px-2 bg-white border border-slate-200 rounded-md text-center text-xs font-bold text-slate-900 focus:outline-none focus:ring-1 focus:ring-slate-900"
                    />
                    <button
                      type="button"
                      @click="adjustStock(variant, 5)"
                      class="w-7 h-7 rounded-md bg-white hover:bg-slate-100 border border-slate-200 font-semibold text-[11px] text-slate-700"
                    >
                      +5
                    </button>
                    <button
                      type="button"
                      @click="adjustStock(variant, 10)"
                      class="w-8 h-7 rounded-md bg-white hover:bg-slate-100 border border-slate-200 font-semibold text-[10px] text-slate-700"
                    >
                      +10
                    </button>
                  </div>

                  <button
                    type="button"
                    @click="saveVariantStock(variant)"
                    :disabled="savingVariantId === variant.id"
                    class="h-7 px-3 bg-slate-900 hover:bg-slate-800 text-white rounded-md text-xs font-semibold transition-colors"
                  >
                    {{ savingVariantId === variant.id ? '...' : 'Lưu' }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Sticky Footer -->
          <div class="px-6 py-4 bg-slate-50 border-t border-slate-200 flex justify-end shrink-0">
            <button
              type="button"
              @click="closeStockModal"
              class="h-9 px-5 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-xs rounded-lg transition-colors"
            >
              Hoàn tất đóng
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- MODAL THÊM / CHỈNH SỬA SẢN PHẨM & BIẾN THỂ -->
    <Teleport to="body">
      <div
        v-if="showProductModal"
        class="fixed inset-0 z-[9990] bg-slate-900/50 backdrop-blur-[2px] flex items-center justify-center p-4 no-print"
      >
        <form
          @submit.prevent="submitProductForm"
          class="relative z-[9995] bg-white rounded-2xl border border-slate-200 max-w-3xl max-h-[90vh] flex flex-col overflow-hidden w-full shadow-2xl"
        >
          <!-- Sticky Header -->
          <div class="px-6 py-4 flex items-center justify-between border-b border-slate-200 shrink-0">
            <div>
              <h3 class="text-base font-bold text-slate-900">
                {{ editingProductId ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}
              </h3>
              <p class="text-[11px] text-slate-500 mt-0.5">
                Upload ảnh trực tiếp hoặc nhập URL, và cấu hình đầy đủ danh sách biến thể Size/Màu
              </p>
            </div>
            <button
              type="button"
              @click="closeProductModal"
              class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-700 font-bold"
            >
              ✕
            </button>
          </div>

          <!-- Scrollable Form Body -->
          <div class="flex-1 overflow-y-auto p-6 space-y-5">
            <!-- Basic Info -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="sm:col-span-2">
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Tên sản phẩm *</label>
                <input
                  v-model="productForm.name"
                  type="text"
                  required
                  placeholder="VD: Áo Thun Cotton Compact Cổ Tròn"
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Danh mục *</label>
                <select
                  v-model="productForm.categoryId"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs bg-white focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                >
                  <option disabled value="">-- Chọn danh mục --</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Slug URL (tùy chọn - tự sinh nếu để trống)</label>
                <input
                  v-model="productForm.slug"
                  type="text"
                  placeholder="ao-thun-cotton-compact"
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs font-mono focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Giá gốc (VNĐ) *</label>
                <input
                  v-model.number="productForm.basePrice"
                  type="number"
                  min="0"
                  required
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-700 mb-1.5">Giá khuyến mãi (VNĐ, tùy chọn)</label>
                <input
                  v-model.number="productForm.salePrice"
                  type="number"
                  min="0"
                  class="w-full h-10 px-3.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                />
              </div>

              <div class="sm:col-span-2 flex items-center gap-2 pt-1">
                <input
                  id="isFlashSaleCheckbox"
                  v-model="productForm.isFlashSale"
                  type="checkbox"
                  class="w-4 h-4 rounded accent-slate-900"
                />
                <label for="isFlashSaleCheckbox" class="text-xs font-semibold text-slate-800 cursor-pointer">
                  ⚡ Kích hoạt Flash Sale cho sản phẩm này
                </label>
              </div>
            </div>

            <!-- Image Upload + URL -->
            <div class="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-3">
              <label class="block text-xs font-semibold text-slate-800">Ảnh đại diện sản phẩm</label>
              <div class="flex flex-col sm:flex-row gap-4 items-start">
                <div class="w-24 h-28 rounded-xl overflow-hidden bg-white border border-slate-200 shrink-0 flex items-center justify-center">
                  <img
                    v-if="productForm.thumbnailUrl"
                    :src="productForm.thumbnailUrl"
                    alt="Preview"
                    @error="onImageError"
                    class="w-full h-full object-cover"
                  />
                  <span v-else class="text-[11px] text-slate-400 text-center px-2">Chưa có ảnh</span>
                </div>

                <div class="flex-1 space-y-2.5 w-full">
                  <div class="flex flex-wrap items-center gap-2">
                    <label class="h-9 px-3.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold cursor-pointer transition-colors inline-flex items-center gap-1.5">
                      <span>{{ uploadingImage ? 'Đang tải lên...' : '📤 Upload ảnh từ máy' }}</span>
                      <input
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        class="hidden"
                        :disabled="uploadingImage"
                        @change="handleImageUpload"
                      />
                    </label>
                    <span class="text-[11px] text-slate-400">Hỗ trợ JPG, PNG, WEBP</span>
                  </div>

                  <input
                    v-model="productForm.thumbnailUrl"
                    type="text"
                    placeholder="Hoặc dán đường dẫn ảnh (/uploads/... hoặc https://...)"
                    class="w-full h-9 px-3.5 rounded-lg border border-slate-200 bg-white text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
                  />
                </div>
              </div>

              <!-- Album ảnh phụ (Product Gallery) -->
              <div class="pt-3 border-t border-slate-200 space-y-2.5">
                <div class="flex flex-wrap items-center justify-between gap-2">
                  <label class="text-xs font-semibold text-slate-800">
                    Album ảnh phụ ({{ productForm.galleryImages.length }} ảnh)
                  </label>
                  <label class="h-8 px-3 rounded-lg bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 text-[11px] font-semibold cursor-pointer transition-colors inline-flex items-center gap-1">
                    <span>{{ uploadingGallery ? 'Đang tải...' : '🖼️ + Thêm nhiều ảnh phụ' }}</span>
                    <input
                      type="file"
                      multiple
                      accept="image/jpeg,image/png,image/webp"
                      class="hidden"
                      :disabled="uploadingGallery"
                      @change="handleGalleryUpload"
                    />
                  </label>
                </div>

                <div v-if="productForm.galleryImages.length > 0" class="grid grid-cols-4 sm:grid-cols-6 gap-2">
                  <div
                    v-for="(imgUrl, idx) in productForm.galleryImages"
                    :key="`${imgUrl}-${idx}`"
                    class="relative group aspect-[3/4] rounded-lg overflow-hidden bg-white border border-slate-200"
                  >
                    <img :src="imgUrl" alt="Gallery" @error="onImageError" class="w-full h-full object-cover" />
                    <button
                      type="button"
                      @click="removeGalleryImage(idx)"
                      class="absolute top-1 right-1 w-5 h-5 rounded-full bg-slate-900/75 hover:bg-red-600 text-white text-[10px] font-bold flex items-center justify-center"
                      title="Xóa ảnh phụ"
                    >
                      ✕
                    </button>
                  </div>
                </div>
                <p v-else class="text-[11px] text-slate-400">
                  Chưa có ảnh phụ. Bạn có thể chọn nhiều ảnh cùng lúc để hiển thị album ở trang chi tiết sản phẩm.
                </p>
              </div>
            </div>

            <!-- Description -->
            <div>
              <label class="block text-xs font-semibold text-slate-700 mb-1.5">Mô tả sản phẩm</label>
              <textarea
                v-model="productForm.description"
                rows="3"
                placeholder="Chất liệu, phom dáng, hướng dẫn bảo quản..."
                class="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 text-xs focus:ring-1 focus:ring-slate-900 focus:border-slate-900 focus:outline-none"
              ></textarea>
            </div>

            <!-- Variants Editor -->
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <label class="text-xs font-bold text-slate-900 uppercase tracking-wider">
                  Danh sách Biến thể (Size / Màu / Giá / Tồn kho) *
                </label>
                <button
                  type="button"
                  @click="addVariantRow"
                  class="h-8 px-3 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-semibold transition-colors"
                >
                  + Thêm dòng biến thể
                </button>
              </div>

              <div class="space-y-2.5">
                <div
                  v-for="(v, idx) in productForm.variants"
                  :key="v.id || `new-${idx}`"
                  class="p-3 rounded-xl bg-slate-50 border border-slate-200/80 grid grid-cols-2 sm:grid-cols-12 gap-2 items-center"
                >
                  <div class="sm:col-span-2">
                    <label class="block text-[10px] font-semibold text-slate-500 mb-0.5">Size *</label>
                    <input
                      v-model="v.size"
                      type="text"
                      required
                      placeholder="M / L / 30"
                      class="w-full h-8 px-2.5 rounded-md border border-slate-200 bg-white text-xs font-semibold"
                    />
                  </div>
                  <div class="sm:col-span-3">
                    <label class="block text-[10px] font-semibold text-slate-500 mb-0.5">Tên màu *</label>
                    <div class="flex items-center gap-1.5">
                      <input
                        v-model="v.colorHex"
                        type="color"
                        class="w-7 h-7 rounded cursor-pointer border border-slate-200 shrink-0"
                      />
                      <input
                        v-model="v.colorName"
                        type="text"
                        required
                        placeholder="Đen / Trắng"
                        class="w-full h-8 px-2 rounded-md border border-slate-200 bg-white text-xs"
                      />
                    </div>
                  </div>
                  <div class="sm:col-span-2">
                    <label class="block text-[10px] font-semibold text-slate-500 mb-0.5">SKU</label>
                    <input
                      v-model="v.sku"
                      type="text"
                      placeholder="Tự sinh"
                      class="w-full h-8 px-2 rounded-md border border-slate-200 bg-white text-[11px] font-mono"
                    />
                  </div>
                  <div class="sm:col-span-2">
                    <label class="block text-[10px] font-semibold text-slate-500 mb-0.5">Giá (VNĐ) *</label>
                    <input
                      v-model.number="v.price"
                      type="number"
                      min="0"
                      required
                      class="w-full h-8 px-2 rounded-md border border-slate-200 bg-white text-xs font-semibold"
                    />
                  </div>
                  <div class="sm:col-span-2">
                    <label class="block text-[10px] font-semibold text-slate-500 mb-0.5">Tồn kho *</label>
                    <input
                      v-model.number="v.stockQuantity"
                      type="number"
                      min="0"
                      required
                      class="w-full h-8 px-2 rounded-md border border-slate-200 bg-white text-xs font-semibold text-center"
                    />
                  </div>
                  <div class="sm:col-span-1 flex justify-end pt-3 sm:pt-4">
                    <button
                      type="button"
                      @click="removeVariantRow(idx)"
                      :disabled="productForm.variants.length <= 1"
                      class="p-1.5 rounded-md text-red-600 hover:bg-red-50 disabled:opacity-30 transition-colors"
                      title="Xóa dòng biến thể"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Sticky Footer Actions -->
          <div class="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-end gap-2 shrink-0">
            <button
              type="button"
              @click="closeProductModal"
              class="h-9 px-4 rounded-lg border border-slate-200 bg-white text-slate-700 font-semibold text-xs hover:bg-slate-50"
            >
              Hủy bỏ
            </button>
            <button
              type="submit"
              :disabled="savingProduct || uploadingImage"
              class="h-9 px-5 rounded-lg bg-slate-900 hover:bg-slate-800 disabled:opacity-50 text-white font-semibold text-xs transition-colors"
            >
              {{ savingProduct ? 'Đang lưu...' : (editingProductId ? 'Lưu Cập Nhật' : 'Tạo Sản Phẩm') }}
            </button>
          </div>
        </form>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import api from '@/services/api';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';

const toastStore = useToastStore();
const products = ref([]);
const categories = ref([]);
const searchQuery = ref('');
const isExporting = ref(false);
const togglingId = ref(null);
const deletingId = ref(null);

// Modal Create / Edit Product State
const showProductModal = ref(false);
const editingProductId = ref(null);
const savingProduct = ref(false);
const uploadingImage = ref(false);
const uploadingGallery = ref(false);

const productForm = reactive({
  name: '',
  slug: '',
  categoryId: '',
  description: '',
  basePrice: 250000,
  salePrice: null,
  isFlashSale: false,
  thumbnailUrl: '',
  galleryImages: [],
  variants: []
});

const filteredProducts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return products.value;
  return products.value.filter(p =>
    (p.name || '').toLowerCase().includes(q) ||
    (p.categoryName || '').toLowerCase().includes(q) ||
    (p.slug || '').toLowerCase().includes(q)
  );
});

// Modal Stock State
const selectedProductForStock = ref(null);
const productVariants = ref([]);
const loadingVariants = ref(false);
const savingVariantId = ref(null);

async function loadCategories() {
  try {
    const res = await api.get('/categories');
    if (res.success && Array.isArray(res.data)) {
      categories.value = res.data;
    }
  } catch (err) {
    console.error('Lỗi nạp danh mục:', err);
  }
}

async function loadProducts() {
  try {
    const res = await api.get('/products?pageSize=100');
    if (res.success && res.data) {
      products.value = res.data.content || [];
    }
  } catch (err) {
    console.error('Lỗi nạp sản phẩm admin:', err);
  }
}

function openCreateModal() {
  editingProductId.value = null;
  productForm.name = '';
  productForm.slug = '';
  productForm.categoryId = categories.value[0]?.id || '';
  productForm.description = '';
  productForm.basePrice = 250000;
  productForm.salePrice = null;
  productForm.isFlashSale = false;
  productForm.thumbnailUrl = '';
  productForm.galleryImages = [];
  productForm.variants = [
    { id: null, size: 'M', colorName: 'Đen', colorHex: '#111827', sku: '', price: 250000, stockQuantity: 20 }
  ];
  showProductModal.value = true;
}

async function openEditModal(item) {
  editingProductId.value = item.id;
  showProductModal.value = true;
  try {
    const res = await api.get(`/products/id/${item.id}`);
    if (res.success && res.data) {
      const d = res.data;
      productForm.name = d.name || '';
      productForm.slug = d.slug || '';
      productForm.categoryId = d.categoryId || '';
      productForm.description = d.description || '';
      productForm.basePrice = d.basePrice || 0;
      productForm.salePrice = d.salePrice ?? null;
      productForm.isFlashSale = !!d.isFlashSale;
      productForm.thumbnailUrl = d.thumbnailUrl || '';
      productForm.galleryImages = Array.isArray(d.galleryImages) ? [...d.galleryImages] : [];
      productForm.variants = (d.variants && d.variants.length > 0)
        ? d.variants.map(v => ({
            id: v.id,
            size: v.size || 'M',
            colorName: v.colorName || 'Đen',
            colorHex: v.colorHex || '#111827',
            sku: v.sku || '',
            price: v.price || d.salePrice || d.basePrice || 0,
            stockQuantity: v.stockQuantity ?? 0
          }))
        : [{ id: null, size: 'M', colorName: 'Đen', colorHex: '#111827', sku: '', price: d.basePrice || 0, stockQuantity: 10 }];
    }
  } catch (err) {
    toastStore.error('Không thể tải chi tiết sản phẩm để chỉnh sửa.');
    showProductModal.value = false;
  }
}

function closeProductModal() {
  showProductModal.value = false;
  editingProductId.value = null;
}

function addVariantRow() {
  productForm.variants.push({
    id: null,
    size: 'L',
    colorName: 'Trắng',
    colorHex: '#FFFFFF',
    sku: '',
    price: productForm.salePrice || productForm.basePrice || 200000,
    stockQuantity: 10
  });
}

function removeVariantRow(idx) {
  if (productForm.variants.length <= 1) return;
  productForm.variants.splice(idx, 1);
}

async function handleImageUpload(event) {
  const file = event.target?.files?.[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  uploadingImage.value = true;
  try {
    const res = await api.post('/admin/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    if (res.success && res.data?.url) {
      productForm.thumbnailUrl = res.data.url;
      toastStore.success('Upload ảnh sản phẩm thành công!');
    } else {
      toastStore.error(res.message || 'Không thể tải ảnh lên.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi upload ảnh. Chỉ hỗ trợ JPG/PNG/WEBP.');
  } finally {
    uploadingImage.value = false;
    if (event.target) event.target.value = '';
  }
}

async function handleGalleryUpload(event) {
  const files = Array.from(event.target?.files || []);
  if (files.length === 0) return;

  uploadingGallery.value = true;
  let uploadedCount = 0;
  try {
    for (const file of files) {
      const formData = new FormData();
      formData.append('file', file);
      const res = await api.post('/admin/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      if (res.success && res.data?.url) {
        productForm.galleryImages.push(res.data.url);
        uploadedCount++;
      }
    }
    if (uploadedCount > 0) {
      toastStore.success(`Đã tải lên ${uploadedCount} ảnh phụ thành công!`);
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Có lỗi khi tải ảnh phụ.');
  } finally {
    uploadingGallery.value = false;
    if (event.target) event.target.value = '';
  }
}

function removeGalleryImage(idx) {
  productForm.galleryImages.splice(idx, 1);
}

async function submitProductForm() {
  if (savingProduct.value) return;

  if (!productForm.name.trim() || !productForm.categoryId) {
    toastStore.error('Vui lòng nhập tên sản phẩm và chọn danh mục.');
    return;
  }
  if (!productForm.variants || productForm.variants.length === 0) {
    toastStore.error('Sản phẩm cần ít nhất 1 biến thể Size/Màu.');
    return;
  }

  savingProduct.value = true;
  try {
    const payload = {
      name: productForm.name.trim(),
      slug: productForm.slug?.trim() || null,
      categoryId: Number(productForm.categoryId),
      description: productForm.description?.trim() || '',
      basePrice: Number(productForm.basePrice),
      salePrice: productForm.salePrice !== null && productForm.salePrice !== '' ? Number(productForm.salePrice) : null,
      isFlashSale: !!productForm.isFlashSale,
      thumbnailUrl: productForm.thumbnailUrl?.trim() || null,
      galleryImages: productForm.galleryImages.filter(u => u && u.trim()),
      variants: productForm.variants.map(v => ({
        id: v.id || null,
        size: String(v.size || '').trim(),
        colorName: String(v.colorName || '').trim(),
        colorHex: v.colorHex || '#111827',
        sku: v.sku?.trim() || null,
        price: Number(v.price),
        stockQuantity: Math.max(0, Number(v.stockQuantity || 0))
      }))
    };

    const res = editingProductId.value
      ? await api.put(`/admin/products/${editingProductId.value}`, payload)
      : await api.post('/admin/products', payload);

    if (res.success) {
      toastStore.success(res.message || (editingProductId.value ? 'Cập nhật sản phẩm thành công!' : 'Tạo sản phẩm mới thành công!'));
      closeProductModal();
      await loadProducts();
    } else {
      toastStore.error(res.message || 'Không thể lưu sản phẩm.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi khi lưu sản phẩm.');
  } finally {
    savingProduct.value = false;
  }
}

// XÓA SẢN PHẨM AN TOÀN (Kiểm tra ràng buộc đơn hàng)
async function deleteProduct(item) {
  if (!confirm(`Bạn có chắc chắn muốn xóa sản phẩm "${item.name}"?`)) return;
  deletingId.value = item.id;
  try {
    const res = await api.delete(`/admin/products/${item.id}`);
    if (res.success) {
      toastStore.success(res.message || `Đã xóa sản phẩm "${item.name}" thành công!`);
      products.value = products.value.filter(p => p.id !== item.id);
    } else {
      toastStore.error(res.message || 'Không thể xóa sản phẩm.');
    }
  } catch (err) {
    const msg = err.response?.data?.message || err.friendlyMessage || 'Lỗi khi xóa sản phẩm. Sản phẩm có thể đã phát sinh đơn hàng.';
    toastStore.error(msg);
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
      toastStore.success(`Đã ${stateStr} Flash Sale cho sản phẩm "${item.name}"`);
    }
  } catch (err) {
    console.error('Lỗi bật/tắt flash sale:', err);
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể cập nhật Flash Sale. Vui lòng thử lại!');
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
    toastStore.error('Không thể xuất file CSV tồn kho. Vui lòng thử lại!');
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
    toastStore.error('Không thể nạp biến thể sản phẩm.');
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
      toastStore.success(`Đã cập nhật tồn kho SKU: ${variant.sku} thành ${variant.stockQuantity} chiếc`);
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
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Không thể lưu số lượng tồn kho.');
  } finally {
    savingVariantId.value = null;
  }
}

function closeStockModal() {
  selectedProductForStock.value = null;
  productVariants.value = [];
}

onMounted(() => {
  loadCategories();
  loadProducts();
});
</script>
