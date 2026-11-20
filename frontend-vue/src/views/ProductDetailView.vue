<template>
  <div v-if="loading" class="py-32 text-center">
    <div class="inline-block animate-spin rounded-full h-10 w-10 border-4 border-rose-500 border-t-transparent"></div>
    <p class="text-xs text-gray-500 mt-3 font-semibold">Đang nạp chi tiết sản phẩm...</p>
  </div>

  <div v-else-if="product" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-12">
    
    <!-- Breadcrumb -->
    <div class="text-xs text-gray-500 flex items-center gap-2">
      <router-link to="/" class="hover:text-rose-600">Trang chủ</router-link>
      <span>/</span>
      <router-link to="/catalog" class="hover:text-rose-600">Cửa hàng</router-link>
      <span>/</span>
      <span class="text-gray-900 font-semibold truncate">{{ product.name }}</span>
    </div>

    <!-- Main Detail Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-10 lg:gap-14">
      
      <!-- Left: Gallery Image (Sticky column for optimal browsing) -->
      <div class="space-y-4 lg:sticky lg:top-28 self-start">
        <div
          class="relative bg-gray-100 rounded-3xl overflow-hidden aspect-[3/4] shadow-card border border-gray-100 select-none"
          @touchstart="onGalleryTouchStart"
          @touchend="onGalleryTouchEnd"
        >
          <img
            :src="activeImageUrl"
            :alt="product.name"
            @error="onImageError"
            class="w-full h-full object-cover transition-all duration-500 hover:scale-105"
          />
          <span
            v-if="product.isFlashSale"
            class="absolute top-5 left-5 bg-gradient-to-r from-rose-600 to-orange-500 text-white text-xs font-black uppercase px-3.5 py-1.5 rounded-full shadow-lg animate-pulse flex items-center gap-1.5"
          >
            <span>⚡</span> Flash Sale
          </span>

          <!-- Image counter pill when multiple images exist -->
          <span
            v-if="allGalleryImages.length > 1"
            class="absolute bottom-4 right-4 bg-black/60 backdrop-blur-sm text-white text-[11px] font-bold px-2.5 py-1 rounded-full"
          >
            {{ activeImageIndex + 1 }} / {{ allGalleryImages.length }}
          </span>

          <!-- Wishlist Heart Button -->
          <button
            type="button"
            @click="handleToggleWishlist"
            :title="isFavorited ? 'Bỏ khỏi danh sách yêu thích' : 'Thêm vào danh sách yêu thích'"
            class="absolute top-5 right-5 z-10 w-10 h-10 rounded-full bg-white/95 backdrop-blur-md flex items-center justify-center shadow-md hover:scale-110 active:scale-95 transition-all"
          >
            <svg
              class="w-5 h-5 transition-colors"
              :class="isFavorited ? 'text-rose-600 fill-rose-600' : 'text-gray-400 hover:text-rose-500 fill-none'"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
          </button>
        </div>

        <!-- Thumbnail Strip (Scrollable on Mobile) -->
        <div
          v-if="allGalleryImages.length > 1"
          class="flex items-center gap-2.5 overflow-x-auto pb-1"
        >
          <button
            v-for="(imgUrl, idx) in allGalleryImages"
            :key="`${imgUrl}-${idx}`"
            type="button"
            @click="activeImageIndex = idx"
            class="w-16 h-20 sm:w-20 sm:h-24 rounded-2xl overflow-hidden border-2 shrink-0 transition-all bg-gray-100"
            :class="activeImageIndex === idx ? 'border-rose-600 ring-2 ring-rose-500/25 scale-[1.02]' : 'border-transparent opacity-70 hover:opacity-100'"
          >
            <img
              :src="imgUrl"
              :alt="`${product.name} - ảnh ${idx + 1}`"
              @error="onImageError"
              loading="lazy"
              class="w-full h-full object-cover"
            />
          </button>
        </div>

        <!-- 3 Quick Guarantee Badges under image -->
        <div class="grid grid-cols-3 gap-2 pt-2 text-center text-[11px] text-gray-500">
          <div class="p-2.5 rounded-xl bg-white border border-gray-100 shadow-sm flex flex-col items-center gap-1">
            <span class="text-base">📸</span>
            <span class="font-semibold text-gray-800">Ảnh thật 100%</span>
          </div>
          <div class="p-2.5 rounded-xl bg-white border border-gray-100 shadow-sm flex flex-col items-center gap-1">
            <span class="text-base">🔄</span>
            <span class="font-semibold text-gray-800">Đổi trả 60 ngày</span>
          </div>
          <div class="p-2.5 rounded-xl bg-white border border-gray-100 shadow-sm flex flex-col items-center gap-1">
            <span class="text-base">📦</span>
            <span class="font-semibold text-gray-800">Đồng kiểm khi nhận</span>
          </div>
        </div>
      </div>

      <!-- Right: Product Actions & Selectors -->
      <div class="space-y-6">
        <div>
          <span class="text-xs font-bold uppercase tracking-wider text-rose-600">
            {{ product.categoryName || 'Thời Trang' }}
          </span>
          <h1 class="text-2xl sm:text-3xl font-extrabold text-gray-900 mt-1 leading-snug">
            {{ product.name }}
          </h1>

          <!-- Reviews summary -->
          <div class="flex items-center gap-3 mt-3 text-xs">
            <template v-if="(reviewStats.totalReviews || product.reviewCount || 0) > 0">
              <div class="flex items-center text-amber-500 font-bold">
                <span>★</span>
                <span class="ml-1 text-gray-900">{{ reviewStats.ratingAverage || product.ratingAverage }}</span>
              </div>
              <span class="text-gray-300">|</span>
              <span class="text-gray-500 font-medium">{{ reviewStats.totalReviews || product.reviewCount }} đánh giá thực tế</span>
            </template>
            <span v-else class="text-gray-400 font-medium">Chưa có đánh giá nào</span>
            <span class="text-gray-300">|</span>
            <span class="text-emerald-700 font-medium">SKU: <code class="font-mono">{{ currentVariant?.sku || 'N/A' }}</code></span>
          </div>

          <!-- Price Box -->
          <div class="mt-4 p-4 rounded-2xl bg-gray-50 border border-gray-100 flex items-baseline gap-3">
            <span class="text-3xl font-black text-rose-600">
              {{ formatVND(currentPrice) }}
            </span>
            <span v-if="product.salePrice && product.basePrice > product.salePrice" class="text-base text-gray-400 line-through">
              {{ formatVND(product.basePrice) }}
            </span>
            <span
              v-if="product.discountPercent"
              class="text-xs font-extrabold bg-rose-600 text-white px-2.5 py-0.5 rounded-full"
            >
              Tiết kiệm {{ product.discountPercent }}%
            </span>
          </div>
        </div>

        <!-- Color Selector -->
        <div class="space-y-2 pt-2">
          <div class="flex justify-between items-center text-xs font-medium">
            <span class="text-gray-700">Màu sắc đang chọn: <strong class="text-gray-900">{{ selectedColor }}</strong></span>
          </div>
          <div class="flex flex-wrap gap-2.5">
            <button
              v-for="color in availableColors"
              :key="color.name"
              type="button"
              @click="selectedColor = color.name"
              class="flex items-center gap-2 px-3.5 py-2 rounded-xl border text-xs font-semibold transition-all"
              :class="selectedColor === color.name ? 'border-rose-600 bg-rose-50 text-rose-700 ring-2 ring-rose-600/20' : 'border-gray-200 text-gray-700 hover:bg-gray-50'"
            >
              <span class="w-4 h-4 rounded-full border border-gray-300 shadow-inner" :style="{ backgroundColor: color.hex }"></span>
              <span>{{ color.name }}</span>
            </button>
          </div>
        </div>

        <!-- Size Selector & SMART SIZE PREDICTOR BUTTON (Phase 6) -->
        <div class="space-y-2 pt-2">
          <div class="flex items-center justify-between text-xs font-medium">
            <span class="text-gray-700">Kích thước (Size): <strong class="text-gray-900">{{ selectedSize }}</strong></span>
            
            <!-- Nút Trợ lý Gợi ý Size Thông Minh -->
            <button
              type="button"
              @click="isSizePredictorOpen = true"
              class="inline-flex items-center gap-1.5 text-xs font-bold text-rose-600 hover:text-rose-800 bg-rose-50 hover:bg-rose-100 px-3 py-1 rounded-full border border-rose-200 transition-all shadow-sm"
            >
              <span>📏</span>
              <span>Tư vấn chọn Size chuẩn theo chiều cao & cân nặng</span>
            </button>
          </div>

          <div class="flex flex-wrap gap-2.5">
            <button
              v-for="size in availableSizes"
              :key="size"
              type="button"
              @click="selectedSize = size"
              class="w-12 h-11 rounded-xl border flex items-center justify-center text-xs font-bold transition-all"
              :class="selectedSize === size ? 'border-rose-600 bg-rose-600 text-white shadow-md shadow-rose-200' : 'border-gray-200 text-gray-700 hover:bg-gray-50'"
            >
              {{ size }}
            </button>
          </div>
        </div>

        <!-- Real-time Stock Indicator -->
        <div class="pt-2 text-xs">
          <div v-if="currentVariant && currentVariant.stockQuantity > 0" class="flex items-center gap-2 text-emerald-700 font-semibold">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-ping"></span>
            <span>Còn lại <strong>{{ currentVariant.stockQuantity }}</strong> sản phẩm sẵn sàng giao</span>
          </div>
          <div v-else class="flex items-center gap-2 text-rose-600 font-bold bg-rose-50 p-2.5 rounded-xl border border-rose-200">
            <span>✕ Biến thể này tạm thời hết hàng. Vui lòng chọn màu hoặc size khác!</span>
          </div>
        </div>

        <!-- Add to cart & Quantity actions -->
        <div class="pt-4 flex flex-col sm:flex-row gap-3">
          <!-- Counter -->
          <div class="flex items-center border border-gray-200 rounded-2xl overflow-hidden bg-gray-50 h-12 w-32 justify-between px-2">
            <button
              type="button"
              @click="quantity = Math.max(1, quantity - 1)"
              class="w-8 h-8 flex items-center justify-center text-gray-700 hover:bg-gray-200 rounded-lg text-sm font-bold"
            >-</button>
            <span class="font-bold text-sm text-gray-900">{{ quantity }}</span>
            <button
              type="button"
              @click="quantity = currentVariant ? Math.min(currentVariant.stockQuantity, quantity + 1) : quantity + 1"
              class="w-8 h-8 flex items-center justify-center text-gray-700 hover:bg-gray-200 rounded-lg text-sm font-bold"
            >+</button>
          </div>

          <!-- Add Button -->
          <button
            type="button"
            @click="handleAddToCart"
            :disabled="!currentVariant || currentVariant.stockQuantity <= 0"
            class="flex-1 h-12 rounded-2xl text-white font-bold text-xs uppercase tracking-wider transition-all shadow-lg flex items-center justify-center gap-2.5"
            :class="(!currentVariant || currentVariant.stockQuantity <= 0) ? 'bg-gray-300 cursor-not-allowed' : 'bg-rose-600 hover:bg-rose-700 active:scale-95 shadow-rose-200'"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span>Thêm Vào Giỏ Hàng</span>
          </button>
        </div>

        <!-- Guarantee Policies -->
        <div class="pt-6 border-t border-gray-100 grid grid-cols-2 gap-3 text-[11px] text-gray-600">
          <div class="flex items-center gap-2">
            <span>🛡️</span> 100% Chính hãng phân phối
          </div>
          <div class="flex items-center gap-2">
            <span>📦</span> Miễn phí đổi trả 30 ngày
          </div>
          <div class="flex items-center gap-2">
            <span>🚚</span> Freeship toàn quốc đơn từ 500k
          </div>
          <div class="flex items-center gap-2">
            <span>💬</span> Tư vấn hỗ trợ 24/7
          </div>
        </div>
      </div>

    </div>

    <!-- GỢI Ý PHỐI ĐỒ THEO SET (MIX & MATCH OUTFIT) (Yêu cầu Phase 10) -->
    <div v-if="mixMatchProducts.length > 0" class="bg-gradient-to-br from-rose-950 via-gray-900 to-black text-white rounded-3xl p-6 sm:p-8 shadow-2xl border border-rose-800/40 relative overflow-hidden">
      <!-- Ambient Glow -->
      <div class="absolute -top-24 -right-24 w-80 h-80 bg-rose-500/20 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6 pb-5 border-b border-white/10">
        <div class="flex items-center gap-3">
          <div class="w-11 h-11 rounded-2xl bg-gradient-to-tr from-amber-400 to-rose-600 flex items-center justify-center text-xl shadow-lg shadow-rose-600/40">
            ✨
          </div>
          <div>
            <div class="flex items-center gap-2">
              <h2 class="text-xl sm:text-2xl font-black tracking-tight text-white uppercase">
                Phối Đồ Chuẩn Gu (Mix & Match)
              </h2>
              <span class="px-2 py-0.5 text-[10px] font-black bg-rose-600 text-white rounded uppercase tracking-wider">
                OUTFIT SET
              </span>
            </div>
            <p class="text-xs text-rose-200 mt-0.5">Set đồ hoàn hảo được stylist gợi ý dành riêng cho món đồ bạn đang xem</p>
          </div>
        </div>

        <!-- Combo Savings Badge -->
        <div class="hidden sm:inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs text-amber-300 font-bold">
          <span>🎁</span> Tiết kiệm thời gian phối đồ • Mua trọn bộ 1-click
        </div>
      </div>

      <!-- Outfits Row -->
      <div class="relative z-10 grid grid-cols-1 lg:grid-cols-4 gap-6 items-center">
        <!-- 3 Items in Set (Cols 1-3) -->
        <div class="lg:col-span-3 grid grid-cols-1 sm:grid-cols-3 gap-4 items-center">
          
          <!-- Item 1: Current Product -->
          <div class="bg-white/10 backdrop-blur-md rounded-2xl p-3.5 border border-white/15 flex flex-col justify-between h-full relative group">
            <span class="absolute top-2 left-2 bg-rose-600 text-white text-[9px] font-black px-2 py-0.5 rounded-md uppercase z-10 shadow-sm">
              Món 1 (Đang xem)
            </span>
            <div class="aspect-[3/4] rounded-xl overflow-hidden mb-2.5 bg-gray-800">
              <img
                :src="product.thumbnailUrl"
                :alt="product.name"
                @error="onImageError"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </div>
            <div>
              <h4 class="text-xs font-bold text-white line-clamp-1 mb-1">{{ product.name }}</h4>
              <div class="flex items-center justify-between text-xs">
                <span class="font-extrabold text-amber-300">{{ formatVND(currentPrice) }}</span>
                <span class="text-[10px] text-gray-300 bg-white/15 px-2 py-0.5 rounded">
                  Size: {{ selectedSize }}
                </span>
              </div>
            </div>
          </div>

          <!-- Item 2: Mix-Match Product 1 -->
          <div
            v-if="mixMatchProducts[0]"
            class="bg-white/10 backdrop-blur-md rounded-2xl p-3.5 border border-white/15 flex flex-col justify-between h-full relative group"
          >
            <span class="absolute top-2 left-2 bg-amber-500 text-gray-950 text-[9px] font-black px-2 py-0.5 rounded-md uppercase z-10 shadow-sm">
              Món 2 (Gợi ý)
            </span>
            <router-link :to="`/product/${mixMatchProducts[0].slug}`" class="aspect-[3/4] rounded-xl overflow-hidden mb-2.5 bg-gray-800 block">
              <img
                :src="mixMatchProducts[0].thumbnailUrl"
                :alt="mixMatchProducts[0].name"
                @error="onImageError"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </router-link>
            <div class="space-y-2">
              <router-link :to="`/product/${mixMatchProducts[0].slug}`">
                <h4 class="text-xs font-bold text-white hover:text-amber-300 line-clamp-1 transition-colors">
                  {{ mixMatchProducts[0].name }}
                </h4>
              </router-link>
              <div class="flex items-center justify-between text-xs">
                <span class="font-extrabold text-amber-300">
                  {{ formatVND(mixMatchSelectedVariants[mixMatchProducts[0].id]?.price || mixMatchProducts[0].salePrice || mixMatchProducts[0].basePrice) }}
                </span>
                <!-- Size Selector for Item 2 -->
                <div class="flex items-center gap-1">
                  <span class="text-[10px] text-gray-300">Size:</span>
                  <select
                    v-model="mixMatchSelectedSizes[mixMatchProducts[0].id]"
                    class="bg-gray-800 text-white text-[11px] font-bold rounded-lg px-2 py-1 border border-white/20 focus:outline-none focus:ring-1 focus:ring-rose-500"
                  >
                    <option
                      v-for="sz in getUniqueSizes(mixMatchProducts[0])"
                      :key="sz"
                      :value="sz"
                    >
                      {{ sz }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <!-- Item 3: Mix-Match Product 2 -->
          <div
            v-if="mixMatchProducts[1]"
            class="bg-white/10 backdrop-blur-md rounded-2xl p-3.5 border border-white/15 flex flex-col justify-between h-full relative group"
          >
            <span class="absolute top-2 left-2 bg-amber-500 text-gray-950 text-[9px] font-black px-2 py-0.5 rounded-md uppercase z-10 shadow-sm">
              Món 3 (Gợi ý)
            </span>
            <router-link :to="`/product/${mixMatchProducts[1].slug}`" class="aspect-[3/4] rounded-xl overflow-hidden mb-2.5 bg-gray-800 block">
              <img
                :src="mixMatchProducts[1].thumbnailUrl"
                :alt="mixMatchProducts[1].name"
                @error="onImageError"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
            </router-link>
            <div class="space-y-2">
              <router-link :to="`/product/${mixMatchProducts[1].slug}`">
                <h4 class="text-xs font-bold text-white hover:text-amber-300 line-clamp-1 transition-colors">
                  {{ mixMatchProducts[1].name }}
                </h4>
              </router-link>
              <div class="flex items-center justify-between text-xs">
                <span class="font-extrabold text-amber-300">
                  {{ formatVND(mixMatchSelectedVariants[mixMatchProducts[1].id]?.price || mixMatchProducts[1].salePrice || mixMatchProducts[1].basePrice) }}
                </span>
                <!-- Size Selector for Item 3 -->
                <div class="flex items-center gap-1">
                  <span class="text-[10px] text-gray-300">Size:</span>
                  <select
                    v-model="mixMatchSelectedSizes[mixMatchProducts[1].id]"
                    class="bg-gray-800 text-white text-[11px] font-bold rounded-lg px-2 py-1 border border-white/20 focus:outline-none focus:ring-1 focus:ring-rose-500"
                  >
                    <option
                      v-for="sz in getUniqueSizes(mixMatchProducts[1])"
                      :key="sz"
                      :value="sz"
                    >
                      {{ sz }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- Right Summary Card (Col 4) -->
        <div class="bg-white/10 backdrop-blur-md rounded-2xl p-5 border border-white/15 flex flex-col justify-between space-y-4 h-full">
          <div>
            <span class="text-[11px] font-bold text-gray-300 uppercase tracking-wider block">
              Trọn bộ phối chuẩn gu
            </span>
            <div class="text-xs text-rose-200 mt-1">
              Gồm {{ 1 + mixMatchProducts.length }} món thời trang kết hợp hoàn hảo
            </div>
            <div class="mt-4 pt-3 border-t border-white/10">
              <span class="text-xs text-gray-300 block">Tổng giá trị cả set:</span>
              <div class="text-2xl font-black text-amber-400 mt-0.5">
                {{ formatVND(totalSetPrice) }}
              </div>
            </div>
          </div>

          <button
            type="button"
            @click="handleAddSetToCart"
            class="w-full py-3.5 px-4 rounded-xl bg-gradient-to-r from-rose-600 via-rose-500 to-amber-500 hover:from-rose-500 hover:to-amber-400 text-white font-extrabold text-xs uppercase tracking-wider shadow-lg shadow-rose-600/40 hover:shadow-rose-600/60 active:scale-95 transition-all flex items-center justify-center gap-2"
          >
            <span>🛍️</span>
            <span>Thêm Cả Bộ Vào Giỏ</span>
          </button>
        </div>

      </div>
    </div>

    <!-- Description & Fit Reviews Tabs -->
    <div class="mt-16 bg-white rounded-3xl border border-gray-100 p-6 sm:p-10 shadow-sm space-y-8">
      
      <!-- Tab Headers -->
      <div class="flex border-b border-gray-200 gap-6 text-sm font-bold">
        <button
          @click="activeTab = 'desc'"
          class="pb-3 border-b-2 transition-colors"
          :class="activeTab === 'desc' ? 'border-rose-600 text-rose-600' : 'border-transparent text-gray-500 hover:text-gray-800'"
        >
          Mô Tả Sản Phẩm
        </button>
        <button
          @click="activeTab = 'reviews'"
          class="pb-3 border-b-2 transition-colors flex items-center gap-1.5"
          :class="activeTab === 'reviews' ? 'border-rose-600 text-rose-600' : 'border-transparent text-gray-500 hover:text-gray-800'"
        >
          <span>Đánh Giá Thực Tế Từ Khách Hàng</span>
          <span class="bg-rose-100 text-rose-700 px-2.5 py-0.5 rounded-full text-xs font-bold">
            {{ reviewStats.totalReviews || product.reviews?.length || 0 }}
          </span>
        </button>
      </div>

      <!-- Tab Content: Description -->
      <div v-if="activeTab === 'desc'" class="prose max-w-none text-gray-700 text-sm leading-relaxed space-y-4">
        <p>{{ product.description }}</p>
        <div class="bg-gray-50 rounded-2xl p-5 border border-gray-100 space-y-2 text-xs">
          <h4 class="font-bold text-gray-900">Hướng dẫn bảo quản trang phục:</h4>
          <ul class="list-disc pl-5 space-y-1 text-gray-600">
            <li>Giặt ở nhiệt độ thường, lộn trái sản phẩm trước khi cho vào máy giặt.</li>
            <li>Không ngâm chung quần áo màu sáng với quần áo tối màu.</li>
            <li>Ủi ở nhiệt độ trung bình dưới 150°C, tránh ủi trực tiếp lên các chi tiết in ấn.</li>
            <li>Phơi ở nơi thoáng mát râm mát, tránh ánh nắng gắt trực tiếp làm phai màu vải.</li>
          </ul>
        </div>
      </div>

      <!-- Tab Content: Fit Reviews (Phase 8) -->
      <div v-else class="space-y-10">
        
        <!-- FIT OVERVIEW & RATING BREAKDOWN (Yêu cầu Phase 8) -->
        <div
          v-if="(reviewStats.totalReviews || 0) === 0"
          class="bg-gray-50 border border-dashed border-gray-200 rounded-3xl p-8 text-center space-y-2"
        >
          <span class="text-3xl block">💬</span>
          <h3 class="text-sm font-extrabold text-gray-800">Chưa có đánh giá nào</h3>
          <p class="text-xs text-gray-500 max-w-md mx-auto">
            Sản phẩm này hiện chưa có đánh giá từ khách hàng. Hãy là người đầu tiên chia sẻ cảm nhận về chất liệu và độ vừa vặn bên dưới!
          </p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6 bg-gradient-to-br from-rose-50/50 via-white to-amber-50/40 p-6 rounded-3xl border border-rose-100">
          
          <!-- Rating Average Box -->
          <div class="flex items-center gap-6 border-b md:border-b-0 md:border-r border-gray-200 pb-4 md:pb-0 md:pr-6">
            <div class="text-center">
              <div class="text-5xl font-black text-rose-600 leading-none">
                {{ reviewStats.ratingAverage ?? 0 }}
              </div>
              <div class="flex items-center justify-center text-amber-500 text-sm mt-1.5">
                ★★★★★
              </div>
              <p class="text-[11px] text-gray-500 mt-1">Dựa trên {{ reviewStats.totalReviews }} đánh giá</p>
            </div>

            <!-- Star distribution bars -->
            <div class="flex-1 space-y-1 text-xs text-gray-500">
              <div v-for="s in [5, 4, 3, 2, 1]" :key="s" class="flex items-center gap-2">
                <span class="w-8 font-semibold text-[11px]">{{ s }} sao</span>
                <div class="flex-1 bg-gray-200 rounded-full h-1.5 overflow-hidden">
                  <div
                    class="bg-amber-400 h-full rounded-full"
                    :style="{ width: `${calcStarPercent(s)}%` }"
                  ></div>
                </div>
                <span class="text-[10px] w-6 text-right text-gray-400">{{ reviewStats.starCounts?.[s] || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- Fit Feedback Stats Breakdown (Yêu cầu Phase 8) -->
          <div class="space-y-3 flex flex-col justify-center">
            <div class="flex items-center justify-between text-xs font-bold text-gray-800">
              <span class="flex items-center gap-1.5">
                <span>🧍‍♂️</span> Tỷ lệ cảm nhận độ vừa vặn thực tế:
              </span>
              <span class="text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200 text-[11px]">
                {{ reviewStats.fitPercent ?? 0 }}% đánh giá chuẩn form
              </span>
            </div>

            <div class="space-y-2 text-xs">
              <!-- Fit (Vua van) -->
              <div>
                <div class="flex justify-between text-[11px] mb-1 font-semibold text-emerald-800">
                  <span>✓ Vừa vặn chuẩn phom</span>
                  <span>{{ reviewStats.fitPercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
                  <div class="bg-emerald-500 h-full rounded-full" :style="{ width: `${reviewStats.fitPercent ?? 0}%` }"></div>
                </div>
              </div>

              <!-- Tight (Chat) -->
              <div>
                <div class="flex justify-between text-[11px] mb-1 font-semibold text-amber-800">
                  <span>Hơi ôm / Hơi chật so với dáng</span>
                  <span>{{ reviewStats.tightPercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
                  <div class="bg-amber-500 h-full rounded-full" :style="{ width: `${reviewStats.tightPercent ?? 0}%` }"></div>
                </div>
              </div>

              <!-- Loose (Rong) -->
              <div>
                <div class="flex justify-between text-[11px] mb-1 font-semibold text-indigo-800">
                  <span>Hơi rộng thoải mái / Oversize</span>
                  <span>{{ reviewStats.loosePercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
                  <div class="bg-indigo-500 h-full rounded-full" :style="{ width: `${reviewStats.loosePercent ?? 0}%` }"></div>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- FILTERS BY STAR & FIT FEEDBACK (Yêu cầu Phase 8) -->
        <div class="flex flex-wrap items-center justify-between gap-3 pb-2 border-b border-gray-100">
          <div class="flex flex-wrap gap-2 text-xs">
            <span class="text-gray-400 font-semibold self-center mr-1">Lọc sao:</span>
            <button
              v-for="s in [0, 5, 4, 3, 2, 1]"
              :key="s"
              type="button"
              @click="filterStar = s"
              class="px-3 py-1.5 rounded-xl border font-bold transition-all text-xs"
              :class="filterStar === s ? 'border-rose-600 bg-rose-600 text-white shadow-sm' : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'"
            >
              {{ s === 0 ? 'Tất cả sao' : `${s} ★` }}
            </button>
          </div>

          <div class="flex items-center gap-2 text-xs">
            <span class="text-gray-400 font-semibold">Độ vừa:</span>
            <select
              v-model="filterFit"
              class="bg-gray-50 border border-gray-200 rounded-xl px-3 py-1.5 font-bold text-gray-800 focus:outline-none focus:ring-1 focus:ring-rose-500"
            >
              <option value="">Tất cả cảm nhận</option>
              <option value="VUA_VAN">Chỉ xem: Vừa vặn chuẩn</option>
              <option value="CHAT">Chỉ xem: Hơi chật</option>
              <option value="RONG">Chỉ xem: Hơi rộng</option>
            </select>
          </div>
        </div>

        <!-- REVIEWS LIST WITH DETAILED FIT TAGS (Yêu cầu Phase 8) -->
        <div class="space-y-4">
          <div v-if="filteredReviews.length === 0" class="py-12 text-center text-xs text-gray-500 bg-gray-50 rounded-2xl">
            Không có đánh giá nào phù hợp với bộ lọc đã chọn.
          </div>

          <div
            v-for="rev in filteredReviews"
            :key="rev.id"
            class="p-5 rounded-2xl bg-white border border-gray-100 hover:border-gray-200 shadow-sm transition-all space-y-3"
          >
            <!-- Review Header -->
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div class="flex items-center gap-3">
                <span class="w-9 h-9 rounded-full bg-gradient-to-tr from-rose-500 to-amber-500 text-white font-bold flex items-center justify-center text-xs shadow-sm">
                  {{ (rev.customerName || 'K').charAt(0).toUpperCase() }}
                </span>
                <div>
                  <div class="flex items-center gap-2">
                    <h4 class="font-bold text-xs text-gray-900">{{ rev.customerName }}</h4>
                    <span
                      v-if="rev.verifiedPurchase"
                      class="text-[10px] text-emerald-600 font-medium bg-emerald-50 px-1.5 py-0.2 rounded border border-emerald-100"
                    >
                      ✓ Đã mua hàng
                    </span>
                  </div>
                  <div class="flex items-center text-amber-500 text-xs mt-0.5">
                    <span v-for="star in 5" :key="star">{{ star <= rev.rating ? '★' : '☆' }}</span>
                  </div>
                </div>
              </div>

              <span class="text-[11px] text-gray-400">
                {{ formatDateTime(rev.createdAt) }}
              </span>
            </div>

            <!-- CHUẨN TAG THÔNG SỐ CƠ THỂ VÀ ĐỘ VỪA VẶN (Yêu cầu Phase 8) -->
            <div class="p-2.5 rounded-xl bg-gray-50/80 border border-gray-100 text-xs text-gray-700 flex flex-wrap items-center gap-2">
              <span class="font-semibold text-gray-900 flex items-center gap-1">
                <span>👤</span>
                Khách cao {{ rev.customerHeightCm ? `${rev.customerHeightCm}cm` : '172cm' }} - {{ rev.customerWeightKg ? `${rev.customerWeightKg}kg` : '65kg' }}
              </span>
              <span class="text-gray-300">|</span>
              <span class="text-gray-600">
                Phân loại: <strong class="text-gray-900">{{ rev.purchasedColor || 'Đen Basic' }}</strong> - <strong>Size {{ rev.purchasedSize || 'L' }}</strong>
              </span>
              <span class="text-gray-300">|</span>
              <span
                class="px-2 py-0.5 rounded-md font-bold text-[11px]"
                :class="rev.fitFeedback === 'CHAT' ? 'bg-amber-100 text-amber-800' : rev.fitFeedback === 'RONG' ? 'bg-indigo-100 text-indigo-800' : 'bg-emerald-100 text-emerald-800'"
              >
                Độ vừa: {{ rev.fitFeedback === 'CHAT' ? 'Hơi chật so với dáng' : rev.fitFeedback === 'RONG' ? 'Hơi rộng thoải mái' : 'Vừa vặn chuẩn form' }}
              </span>
            </div>

            <!-- Comment text -->
            <p class="text-xs text-gray-800 leading-relaxed pt-1">
              {{ rev.comment }}
            </p>
          </div>
        </div>

        <!-- FORM GỬI ĐÁNH GIÁ MỚI KÈM VÓC DÁNG (Yêu cầu Phase 8) -->
        <div class="pt-8 border-t border-gray-200">
          <div class="bg-gray-50/80 rounded-3xl p-6 sm:p-8 border border-gray-200 space-y-6">
            <div>
              <h3 class="text-base font-bold text-gray-900 flex items-center gap-2">
                <span>✍️</span> Gửi Đánh Giá & Chia Sẻ Vóc Dáng Của Bạn
              </h3>
              <p class="text-xs text-gray-500 mt-1">
                Thông số chiều cao, cân nặng và cảm nhận của bạn sẽ giúp cộng đồng chọn size chính xác nhất!
              </p>
            </div>

            <form @submit.prevent="submitReview" class="space-y-4">
              <!-- Rating Stars Picker -->
              <div>
                <label class="block text-xs font-bold text-gray-700 mb-1">Mức độ hài lòng *</label>
                <div class="flex items-center gap-1.5 text-2xl text-amber-400 cursor-pointer">
                  <span
                    v-for="s in 5"
                    :key="s"
                    @click="newReview.rating = s"
                    class="hover:scale-125 transition-transform"
                  >
                    {{ s <= newReview.rating ? '★' : '☆' }}
                  </span>
                  <span class="ml-2 text-xs font-bold text-gray-700">
                    {{ newReview.rating }}/5 sao
                  </span>
                </div>
              </div>

              <!-- Inputs row 1: Name, Height, Weight -->
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Họ và tên</label>
                  <input
                    type="text"
                    v-model="newReview.customerName"
                    placeholder="VD: Nguyễn Văn A (hoặc ẩn danh)"
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500"
                  />
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Chiều cao (cm) *</label>
                  <input
                    type="number"
                    min="140"
                    max="210"
                    v-model.number="newReview.customerHeightCm"
                    required
                    placeholder="172"
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500"
                  />
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Cân nặng (kg) *</label>
                  <input
                    type="number"
                    min="35"
                    max="150"
                    v-model.number="newReview.customerWeightKg"
                    required
                    placeholder="65"
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500"
                  />
                </div>
              </div>

              <!-- Inputs row 2: Size, Color, Fit Feedback -->
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Size bạn đã mua *</label>
                  <select
                    v-model="newReview.purchasedSize"
                    required
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500 font-semibold"
                  >
                    <option v-for="s in availableSizes" :key="s" :value="s">Size {{ s }}</option>
                  </select>
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Màu sắc đã mua *</label>
                  <select
                    v-model="newReview.purchasedColor"
                    required
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500 font-semibold"
                  >
                    <option v-for="c in availableColors" :key="c.name" :value="c.name">{{ c.name }}</option>
                  </select>
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-semibold text-gray-700">Cảm nhận độ vừa vặn *</label>
                  <select
                    v-model="newReview.fitFeedback"
                    required
                    class="w-full px-3.5 py-2.5 rounded-xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500 font-bold"
                  >
                    <option value="VUA_VAN">✓ Vừa vặn chuẩn form</option>
                    <option value="CHAT">Hơi chật so với form</option>
                    <option value="RONG">Hơi rộng thoải mái</option>
                  </select>
                </div>
              </div>

              <!-- Comment textarea -->
              <div class="space-y-1">
                <label class="block text-xs font-semibold text-gray-700">Nhận xét chi tiết *</label>
                <textarea
                  v-model="newReview.comment"
                  required
                  rows="3"
                  placeholder="Chia sẻ về chất vải, đường may, khả năng thấm hút mồ hôi và lời khuyên chọn size cho người mua sau..."
                  class="w-full px-4 py-2.5 rounded-2xl border border-gray-200 text-xs bg-white focus:outline-none focus:ring-2 focus:ring-rose-500"
                ></textarea>
              </div>

              <!-- Submit button -->
              <button
                type="submit"
                :disabled="submittingReview"
                class="px-6 py-3 rounded-2xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs uppercase tracking-wider shadow-md hover:shadow-lg transition-all flex items-center gap-2"
              >
                <span v-if="submittingReview">Đang gửi đánh giá...</span>
                <span v-else>Gửi Đánh Giá Ngay</span>
              </button>
            </form>
          </div>
        </div>

      </div>

    </div>

    <!-- SẢN PHẨM BẠN VỪA XEM (Phase 6) -->
    <RecentlyViewed />

    <!-- MODAL TRỢ LÝ GỢI Ý SIZE THÔNG MINH (Phase 6) -->
    <SmartSizePredictorModal
      :is-open="isSizePredictorOpen"
      :available-sizes="availableSizes"
      @close="isSizePredictorOpen = false"
      @select-size="handleSizeSelected"
    />

  </div>

  <!-- 404 / NOT FOUND STATE (Phase UX Polish) -->
  <div v-else class="max-w-xl mx-auto py-24 px-4 text-center space-y-4">
    <div class="w-20 h-20 rounded-full bg-rose-50 text-rose-600 flex items-center justify-center text-4xl mx-auto shadow-inner">
      🛍️
    </div>
    <h2 class="text-2xl font-black text-gray-900 tracking-tight">Không Tìm Thấy Sản Phẩm</h2>
    <p class="text-xs text-gray-500 max-w-md mx-auto">
      Sản phẩm bạn đang tìm kiếm có thể đã ngừng kinh doanh, hết hàng hoặc đường dẫn không còn chính xác.
    </p>
    <div class="pt-4 flex justify-center gap-3">
      <router-link
        to="/catalog"
        class="px-6 py-3 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold text-xs shadow-md transition-all inline-flex items-center gap-2"
      >
        <span>← Khám Phá Cửa Hàng</span>
      </router-link>
      <router-link
        to="/"
        class="px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl font-bold text-xs transition-all inline-flex items-center gap-2"
      >
        <span>Về Trang Chủ</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { useProductStore } from '@/stores/productStore';
import { useWishlistStore } from '@/stores/wishlistStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, formatDateTime, onImageError } from '@/services/formatters';
import SmartSizePredictorModal from '@/components/SmartSizePredictorModal.vue';
import RecentlyViewed from '@/components/RecentlyViewed.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const wishlistStore = useWishlistStore();
const toastStore = useToastStore();

const isFavorited = computed(() => product.value ? wishlistStore.isInWishlist(product.value.id) : false);

function handleToggleWishlist() {
  if (!product.value) return;
  if (!authStore.isAuthenticated) {
    toastStore.info('Vui lòng đăng nhập để sử dụng danh sách yêu thích');
    router.push({ name: 'login', query: { redirect: route.fullPath } });
    return;
  }
  const res = wishlistStore.toggleWishlist(product.value);
  if (res?.added) {
    toastStore.success(`Đã thêm "${product.value.name}" vào danh sách yêu thích`);
  } else {
    toastStore.info(`Đã bỏ "${product.value.name}" khỏi danh sách yêu thích`);
  }
}
const cartStore = useCartStore();
const productStore = useProductStore();

const product = ref(null);
const loading = ref(true);
const activeTab = ref('desc');

const selectedSize = ref('');
const selectedColor = ref('');
const quantity = ref(1);
const isSizePredictorOpen = ref(false);

// Fit Reviews State (Phase 8)
const reviewStats = reactive({
  ratingAverage: 5.0,
  totalReviews: 0,
  tightPercent: 0,
  fitPercent: 100,
  loosePercent: 0,
  starCounts: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 },
  reviews: []
});

const filterStar = ref(0); // 0 = tất cả
const filterFit = ref(''); // '' = tất cả, 'VUA_VAN', 'CHAT', 'RONG'

const newReview = reactive({
  rating: 5,
  customerName: '',
  customerHeightCm: 172,
  customerWeightKg: 65,
  purchasedSize: 'L',
  purchasedColor: '',
  fitFeedback: 'VUA_VAN',
  comment: ''
});
const submittingReview = ref(false);

function calcStarPercent(star) {
  if (!reviewStats.totalReviews || !reviewStats.starCounts?.[star]) return 0;
  return Math.round((reviewStats.starCounts[star] / reviewStats.totalReviews) * 100);
}

const filteredReviews = computed(() => {
  let list = reviewStats.reviews || [];
  if (filterStar.value > 0) {
    list = list.filter(r => r.rating === filterStar.value);
  }
  if (filterFit.value) {
    list = list.filter(r => r.fitFeedback === filterFit.value);
  }
  return list;
});

async function loadProductDetail() {
  loading.value = true;
  try {
    const slug = route.params.slug;
    const res = await api.get(`/products/${slug}`);
    if (res.success && res.data) {
      product.value = res.data;
      activeImageIndex.value = 0;
      if (product.value.name) {
        document.title = `${product.value.name} | FashionStore`;
      }
      
      if (product.value.variants && product.value.variants.length > 0) {
        selectedSize.value = product.value.variants[0].size;
        selectedColor.value = product.value.variants[0].colorName;
        newReview.purchasedSize = selectedSize.value;
        newReview.purchasedColor = selectedColor.value;
      }

      productStore.addRecentlyViewed(product.value);

      // Nạp danh sách review chi tiết (Phase 8 API)
      await loadReviews(product.value.id);

      // Nạp danh sách phối đồ chuẩn gu (Phase 10 API)
      await loadMixMatch(product.value.id);
    }
  } catch (err) {
    console.error('Lỗi nạp sản phẩm:', err);
  } finally {
    loading.value = false;
  }
}

async function loadReviews(productId) {
  try {
    const res = await api.get(`/products/${productId}/reviews`);
    if (res.success && res.data) {
      Object.assign(reviewStats, res.data);
    }
  } catch (err) {
    console.error('Lỗi nạp reviews:', err);
  }
}

async function submitReview() {
  if (!product.value) return;
  submittingReview.value = true;
  try {
    const res = await api.post(`/products/${product.value.id}/reviews`, newReview);
    if (res.success) {
      toastStore.success('Cảm ơn bạn! Đánh giá đã được ghi nhận thành công.');
      newReview.comment = '';
      await loadReviews(product.value.id);
    } else {
      toastStore.error(res.message || 'Không thể gửi đánh giá.');
    }
  } catch (err) {
    toastStore.error(err.response?.data?.message || err.friendlyMessage || 'Lỗi khi gửi đánh giá.');
  } finally {
    submittingReview.value = false;
  }
}

const availableSizes = computed(() => {
  if (!product.value || !product.value.variants) return [];
  const set = new Set();
  product.value.variants.forEach(v => {
    if (v.size) set.add(v.size);
  });
  return Array.from(set);
});

const activeImageIndex = ref(0);
let galleryTouchStartX = 0;

const allGalleryImages = computed(() => {
  if (!product.value) return [];
  const imgs = [];
  if (product.value.thumbnailUrl) imgs.push(product.value.thumbnailUrl);
  if (Array.isArray(product.value.galleryImages)) {
    product.value.galleryImages.forEach(url => {
      if (url && !imgs.includes(url)) imgs.push(url);
    });
  }
  return imgs;
});

const activeImageUrl = computed(() => {
  return allGalleryImages.value[activeImageIndex.value] || product.value?.thumbnailUrl || '';
});

function onGalleryTouchStart(e) {
  galleryTouchStartX = e.changedTouches?.[0]?.screenX || 0;
}

function onGalleryTouchEnd(e) {
  if (allGalleryImages.value.length <= 1) return;
  const endX = e.changedTouches?.[0]?.screenX || 0;
  const diff = galleryTouchStartX - endX;
  if (Math.abs(diff) > 40) {
    if (diff > 0) {
      activeImageIndex.value = (activeImageIndex.value + 1) % allGalleryImages.value.length;
    } else {
      activeImageIndex.value = (activeImageIndex.value - 1 + allGalleryImages.value.length) % allGalleryImages.value.length;
    }
  }
}

const availableColors = computed(() => {
  if (!product.value || !product.value.variants) return [];
  const map = new Map();
  product.value.variants.forEach(v => {
    if (v.colorName && !map.has(v.colorName)) {
      map.set(v.colorName, { name: v.colorName, hex: v.colorHex || '#000000' });
    }
  });
  return Array.from(map.values());
});

const currentVariant = computed(() => {
  if (!product.value || !product.value.variants) return null;
  return product.value.variants.find(
    v => v.size === selectedSize.value && v.colorName === selectedColor.value
  ) || product.value.variants[0];
});

const currentPrice = computed(() => {
  if (currentVariant.value) return currentVariant.value.price;
  if (product.value) return product.value.salePrice || product.value.basePrice;
  return 0;
});

function handleSizeSelected(size) {
  selectedSize.value = size;
  newReview.purchasedSize = size;
}

function handleAddToCart() {
  if (!product.value || !currentVariant.value) return;
  const res = cartStore.addToCart(product.value, currentVariant.value, quantity.value);
  if (!res.success) {
    toastStore.error(res.message);
  }
}

function getUniqueSizes(prod) {
  if (!prod || !Array.isArray(prod.variants)) return [];
  return Array.from(new Set(prod.variants.map(v => v.size).filter(Boolean)));
}

// Mix & Match State & Logic (Phase 10)
const mixMatchProducts = ref([]);
const mixMatchSelectedSizes = reactive({});

const mixMatchSelectedVariants = computed(() => {
  const map = {};
  mixMatchProducts.value.forEach(p => {
    const chosenSize = mixMatchSelectedSizes[p.id];
    const v = p.variants?.find(x => x.size === chosenSize) || p.variants?.[0] || null;
    map[p.id] = v;
  });
  return map;
});

const totalSetPrice = computed(() => {
  let total = currentPrice.value;
  mixMatchProducts.value.forEach(p => {
    const v = mixMatchSelectedVariants.value[p.id];
    total += v ? v.price : (p.salePrice || p.basePrice || 0);
  });
  return total;
});

async function loadMixMatch(productId) {
  try {
    const res = await api.get(`/products/${productId}/mix-match?limit=2`);
    if (res.success && res.data) {
      mixMatchProducts.value = res.data;
      res.data.forEach(p => {
        if (p.variants && p.variants.length > 0) {
          mixMatchSelectedSizes[p.id] = p.variants[0].size;
        }
      });
    }
  } catch (err) {
    console.error('Lỗi nạp gợi ý phối đồ:', err);
  }
}

function handleAddSetToCart() {
  if (!product.value || !currentVariant.value) return;

  // 1. Thêm sản phẩm hiện tại
  cartStore.addToCart(product.value, currentVariant.value, quantity.value || 1);

  // 2. Thêm từng sản phẩm trong set phối
  let addedCount = 1;
  mixMatchProducts.value.forEach(p => {
    const v = mixMatchSelectedVariants.value[p.id];
    if (v && v.stockQuantity > 0) {
      cartStore.addToCart(p, v, 1);
      addedCount++;
    }
  });

  cartStore.isDrawerOpen = true;
  toastStore.success(`Đã thêm thành công cả bộ phối đồ (${addedCount} món) vào giỏ hàng!`);
}

watch(() => route.params.slug, () => {
  loadProductDetail();
});

onMounted(() => {
  loadProductDetail();
});
</script>
