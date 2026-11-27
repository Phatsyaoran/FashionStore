<template>
  <div v-if="loading" class="py-32 text-center">
    <div class="inline-block animate-spin rounded-full h-10 w-10 border-4 border-[#7A1032] border-t-transparent"></div>
    <p class="text-xs text-[#6B7280] mt-3 font-medium">Đang nạp chi tiết sản phẩm...</p>
  </div>

  <div v-else-if="product" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-10 space-y-12 sm:space-y-16">
    
    <!-- Breadcrumb -->
    <div class="text-xs text-[#6B7280] flex items-center flex-nowrap gap-2 min-w-0 overflow-hidden">
      <router-link to="/" class="hover:text-[#7A1032] transition-colors whitespace-nowrap shrink-0">Trang chủ</router-link>
      <span class="text-neutral-300 shrink-0">/</span>
      <router-link to="/catalog" class="hover:text-[#7A1032] transition-colors whitespace-nowrap shrink-0">Cửa hàng</router-link>
      <span class="text-neutral-300 shrink-0">/</span>
      <span class="text-[#18181B] font-medium truncate min-w-0">{{ product.name }}</span>
    </div>

    <!-- Main Detail Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12">
      
      <!-- Left: Gallery Image (Sticky column for optimal browsing) -->
      <div class="lg:col-span-6 space-y-4 lg:sticky lg:top-24 self-start">
        <div
          class="relative bg-neutral-100 rounded-2xl overflow-hidden aspect-[3/4] border border-neutral-200/80 shadow-sm select-none"
          @touchstart="onGalleryTouchStart"
          @touchend="onGalleryTouchEnd"
        >
          <img
            :src="activeImageUrl"
            :alt="product.name"
            @error="onImageError"
            class="w-full h-full object-cover transition-transform duration-700 ease-[cubic-bezier(0.22,1,0.36,1)] hover:scale-[1.03]"
          />
          <span
            v-if="product.isFlashSale"
            class="absolute top-4 left-4 bg-[#7A1032] text-white text-[10px] font-bold uppercase tracking-wider px-3 py-1 rounded-md shadow-sm flex items-center gap-1"
          >
            <span>⚡</span> Flash Sale
          </span>

          <!-- Image counter pill when multiple images exist -->
          <span
            v-if="allGalleryImages.length > 1"
            class="absolute bottom-4 right-4 bg-white/90 backdrop-blur-sm text-[#18181B] border border-neutral-200 text-[11px] font-semibold px-2.5 py-1 rounded-md shadow-sm"
          >
            {{ activeImageIndex + 1 }} / {{ allGalleryImages.length }}
          </span>

          <!-- Wishlist Heart Button -->
          <button
            type="button"
            @click="handleToggleWishlist"
            :title="isFavorited ? 'Bỏ khỏi danh sách yêu thích' : 'Thêm vào danh sách yêu thích'"
            class="absolute top-4 right-4 z-10 w-10 h-10 rounded-full bg-white/90 backdrop-blur-sm border border-neutral-200/80 flex items-center justify-center shadow-sm hover:border-[#C9963B] hover:scale-105 active:scale-95 transition-all"
          >
            <svg
              class="w-5 h-5 transition-colors"
              :class="isFavorited ? 'text-[#7A1032] fill-[#7A1032]' : 'text-[#18181B] hover:text-[#7A1032] fill-none'"
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
            class="w-16 h-20 sm:w-20 sm:h-24 rounded-xl overflow-hidden border-2 shrink-0 transition-all bg-neutral-100"
            :class="activeImageIndex === idx ? 'border-[#7A1032] opacity-100 shadow-sm' : 'border-neutral-200 opacity-70 hover:opacity-100'"
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
        <div class="grid grid-cols-3 gap-2.5 pt-1 text-center text-[11px] text-[#6B7280]">
          <div class="p-3 rounded-xl bg-white border border-neutral-200/80 flex flex-col items-center gap-1 shadow-sm">
            <span class="text-sm">📸</span>
            <span class="font-medium text-[#18181B]">Ảnh thật 100%</span>
          </div>
          <div class="p-3 rounded-xl bg-white border border-neutral-200/80 flex flex-col items-center gap-1 shadow-sm">
            <span class="text-sm">🔄</span>
            <span class="font-medium text-[#18181B]">Đổi trả 60 ngày</span>
          </div>
          <div class="p-3 rounded-xl bg-white border border-neutral-200/80 flex flex-col items-center gap-1 shadow-sm">
            <span class="text-sm">📦</span>
            <span class="font-medium text-[#18181B]">Đồng kiểm nhận</span>
          </div>
        </div>
      </div>

      <!-- Right: Product Actions & Selectors -->
      <div class="lg:col-span-6 space-y-6">
        <div class="space-y-2.5">
          <span class="text-[11px] font-bold uppercase tracking-[0.16em] text-[#7A1032]">
            {{ product.categoryName || 'Thời Trang' }}
          </span>
          <h1 class="text-2xl sm:text-3xl font-bold text-[#18181B] tracking-tight leading-snug">
            {{ product.name }}
          </h1>

          <!-- Reviews summary -->
          <div class="flex flex-wrap items-center gap-3 text-xs pt-0.5">
            <template v-if="(reviewStats.totalReviews || product.reviewCount || 0) > 0">
              <div class="flex items-center text-[#C9963B] font-bold">
                <span>★</span>
                <span class="ml-1 text-[#18181B]">{{ reviewStats.ratingAverage || product.ratingAverage }}</span>
              </div>
              <span class="text-neutral-300">|</span>
              <span class="text-[#6B7280]">{{ reviewStats.totalReviews || product.reviewCount }} đánh giá thực tế</span>
            </template>
            <span v-else class="text-[#6B7280]">Chưa có đánh giá nào</span>
            <span class="text-neutral-300">|</span>
            <span class="text-[#6B7280]">SKU: <code class="font-mono text-[#7A1032] font-semibold">{{ currentVariant?.sku || 'N/A' }}</code></span>
          </div>

          <!-- Price Box -->
          <div class="mt-4 p-4 sm:p-5 rounded-xl bg-white border border-neutral-200/80 shadow-sm flex flex-wrap items-baseline gap-3">
            <span class="text-2xl sm:text-3xl font-extrabold tracking-tight text-[#7A1032]">
              {{ formatVND(currentPrice) }}
            </span>
            <span v-if="product.salePrice && product.basePrice > product.salePrice" class="text-sm sm:text-base text-neutral-400 line-through">
              {{ formatVND(product.basePrice) }}
            </span>
            <span
              v-if="product.discountPercent"
              class="text-[11px] font-bold bg-[#7A1032]/10 text-[#7A1032] border border-[#7A1032]/20 px-2.5 py-0.5 rounded-md"
            >
              Tiết kiệm {{ product.discountPercent }}%
            </span>
          </div>
        </div>

        <!-- Color Selector -->
        <div class="space-y-2.5 pt-2 border-t border-neutral-200/80">
          <div class="flex justify-between items-center text-xs">
            <span class="text-[#6B7280] font-medium">Màu sắc: <strong class="text-[#18181B]">{{ selectedColor }}</strong></span>
          </div>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="color in availableColors"
              :key="color.name"
              type="button"
              @click="selectedColor = color.name"
              class="flex items-center gap-2 px-3.5 h-10 rounded-lg border text-xs font-semibold transition-all"
              :class="selectedColor === color.name ? 'border-[#7A1032] bg-[#7A1032] text-white shadow-sm' : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
            >
              <span class="w-3.5 h-3.5 rounded-full border border-neutral-300" :style="{ backgroundColor: color.hex }"></span>
              <span>{{ color.name }}</span>
            </button>
          </div>
        </div>

        <!-- Size Selector & SMART SIZE PREDICTOR BUTTON -->
        <div class="space-y-2.5 pt-1">
          <div class="flex flex-wrap items-center justify-between gap-2 text-xs">
            <span class="text-[#6B7280] font-medium">Kích thước (Size): <strong class="text-[#18181B]">{{ selectedSize }}</strong></span>
            
            <!-- Nút Trợ lý Gợi ý Size Thông Minh (Luxury AI Glow) -->
            <button
              type="button"
              @click="isSizePredictorOpen = true"
              class="ai-glow inline-flex items-center gap-1.5 text-xs font-semibold text-white luxury-gradient px-3.5 py-1.5 rounded-lg active:scale-[0.98] transition-all shadow-sm"
            >
              <span>📏</span>
              <span>Tư vấn chọn Size AI</span>
            </button>
          </div>

          <div class="flex flex-wrap gap-2">
            <button
              v-for="size in availableSizes"
              :key="size"
              type="button"
              @click="selectedSize = size"
              class="min-w-[48px] h-11 px-3 rounded-lg border flex items-center justify-center text-xs font-bold transition-all"
              :class="selectedSize === size ? 'border-[#7A1032] bg-[#7A1032] text-white shadow-sm' : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
            >
              {{ size }}
            </button>
          </div>
        </div>

        <!-- Real-time Stock Indicator -->
        <div class="pt-1 text-xs">
          <div v-if="currentVariant && currentVariant.stockQuantity > 0" class="flex items-center gap-2 text-emerald-700 font-medium">
            <span class="w-2 h-2 rounded-full bg-emerald-500"></span>
            <span>Còn lại <strong class="font-bold text-emerald-800">{{ currentVariant.stockQuantity }}</strong> sản phẩm sẵn sàng giao</span>
          </div>
          <div v-else class="flex items-center gap-2 text-[#7A1032] font-semibold bg-[#7A1032]/5 p-3 rounded-lg border border-[#7A1032]/20">
            <span>✕ Biến thể này tạm thời hết hàng. Vui lòng chọn màu hoặc size khác!</span>
          </div>
        </div>

        <!-- Add to cart & Quantity actions -->
        <div class="pt-2 flex flex-col sm:flex-row gap-3">
          <!-- Counter -->
          <div class="flex items-center border border-neutral-200 rounded-lg overflow-hidden bg-white h-12 w-full sm:w-32 justify-between px-2 shadow-sm">
            <button
              type="button"
              @click="quantity = Math.max(1, quantity - 1)"
              class="w-9 h-9 flex items-center justify-center text-[#18181B] hover:bg-neutral-100 rounded-md text-sm font-bold transition-colors"
            >-</button>
            <span class="font-bold text-sm text-[#18181B]">{{ quantity }}</span>
            <button
              type="button"
              @click="quantity = currentVariant ? Math.min(currentVariant.stockQuantity, quantity + 1) : quantity + 1"
              class="w-9 h-9 flex items-center justify-center text-[#18181B] hover:bg-neutral-100 rounded-md text-sm font-bold transition-colors"
            >+</button>
          </div>

          <!-- Add Button -->
          <button
            type="button"
            @click="handleAddToCart"
            :disabled="!currentVariant || currentVariant.stockQuantity <= 0"
            class="btn-sheen silky-hover flex-1 h-12 rounded-lg font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2.5"
            :class="(!currentVariant || currentVariant.stockQuantity <= 0) ? 'bg-neutral-200 text-neutral-400 cursor-not-allowed' : 'bg-[#7A1032] hover:bg-[#5C0B27] text-white active:scale-[0.98] shadow-sm hover:shadow-md'"
          >
            <svg class="w-4 h-4 text-[#F3D078]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span>Thêm Vào Giỏ Hàng</span>
          </button>
        </div>

        <!-- Guarantee Policies -->
        <div class="pt-5 border-t border-neutral-200/80 grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs text-[#6B7280]">
          <div class="flex items-center gap-2.5">
            <span>🛡️</span> 100% Chính hãng phân phối
          </div>
          <div class="flex items-center gap-2.5">
            <span>📦</span> Miễn phí đổi trả 30 ngày
          </div>
          <div class="flex items-center gap-2.5">
            <span>🚚</span> Freeship toàn quốc đơn từ 500k
          </div>
          <div class="flex items-center gap-2.5">
            <span>💬</span> Tư vấn hỗ trợ 24/7
          </div>
        </div>
      </div>

    </div>

    <!-- GỢI Ý PHỐI ĐỒ THEO SET (MIX & MATCH OUTFIT — Unified Starfield + Inset Sparkling Star Border + Matched Center Divider) -->
    <div
      v-if="mixMatchProducts.length > 0"
      v-reveal
      class="relative rounded-2xl overflow-hidden luxury-gradient shadow-[0_20px_50px_-15px_rgba(74,8,32,0.35)]"
    >
      <!-- Unified Interactive Starfield covering entire frame -->
      <StarfieldOverlay :min-stars="90" />

      <!-- Inset Sparkling Starlight Border (8px inset ~2mm, 12px corner radius) -->
      <SparkleBorderOverlay :inset="8" :radius="12" />

      <!-- Header Band -->
      <div class="relative z-20 px-4 pt-5 pb-3.5 sm:px-8 sm:pt-6 sm:pb-5 text-[#FFFDF9] flex flex-col md:flex-row md:items-center justify-between gap-3 md:gap-4">
        <div>
          <div class="flex flex-wrap sm:flex-nowrap items-center gap-2 sm:gap-2.5">
            <span class="px-2 py-0.5 text-[10px] font-bold bg-white/15 text-[#F3D078] border border-[#F3D078]/40 rounded uppercase tracking-[0.14em] whitespace-nowrap shrink-0">
              AI Outfit Set
            </span>
            <h2 class="text-base sm:text-xl font-bold tracking-tight text-[#FFFDF9]">
              Phối Đồ Chuẩn Gu (Mix & Match)
            </h2>
          </div>
          <p class="text-xs text-white/80 mt-1">Set đồ hoàn hảo được stylist AI gợi ý dành riêng cho món đồ bạn đang xem</p>
        </div>

        <div class="hidden sm:inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white/15 border border-white/20 text-xs text-[#F3D078] font-medium">
          <span>✨</span> Tiết kiệm thời gian phối đồ • Mua trọn bộ 1-click
        </div>
      </div>

      <!-- Single Standalone Sparkling Star Divider (aligned 8px inset with SparkleBorderOverlay) -->
      <SparkleDivider class="relative z-20 mx-[8px] my-2.5 md:my-3" />

      <!-- Outfits Row (Crisp White Cards floating cleanly inside the inset star border) -->
      <div class="relative z-20 p-4 sm:p-5 md:p-6 pt-3 sm:pt-4 md:pt-5">
        <div class="grid grid-cols-1 lg:grid-cols-4 gap-3.5 md:gap-5 items-stretch">
          <!-- 3 Items in Set (Cols 1-3): Compact horizontal cards on mobile, 3-col vertical cards on md+ -->
          <div class="lg:col-span-3 grid grid-cols-1 md:grid-cols-3 gap-2.5 md:gap-4">
            
            <!-- Item 1: Current Product -->
            <div class="bg-white/95 backdrop-blur-sm rounded-xl p-2.5 md:p-3.5 border border-white/60 shadow-lg silky-hover hover:-translate-y-1 hover:shadow-xl flex flex-row md:flex-col items-center md:items-stretch md:justify-between gap-3 md:gap-0 h-full relative group">
              <div class="w-[72px] h-[90px] shrink-0 md:w-full md:h-auto md:aspect-[3/4] rounded-lg overflow-hidden md:mb-3 bg-neutral-100">
                <img
                  :src="product.thumbnailUrl"
                  :alt="product.name"
                  @error="onImageError"
                  class="w-full h-full object-cover group-hover:scale-[1.04] transition-transform duration-700 ease-[cubic-bezier(0.22,1,0.36,1)]"
                />
              </div>
              <div class="flex-1 min-w-0 flex flex-col justify-between gap-1.5 md:gap-0">
                <span class="inline-block w-fit md:absolute md:top-3 md:left-3 bg-[#18181B]/85 text-[#F3D078] text-[9px] font-bold px-2 py-0.5 rounded uppercase z-10 whitespace-nowrap">
                  Món 1 (Đang xem)
                </span>
                <h4 class="text-xs font-semibold text-[#18181B] line-clamp-2 md:line-clamp-1 md:mb-1 leading-snug">{{ product.name }}</h4>
                <div class="flex items-center justify-between gap-2 text-xs">
                  <span class="font-bold text-[#7A1032] whitespace-nowrap">{{ formatVND(currentPrice) }}</span>
                  <span class="text-[10px] text-[#6B7280] bg-neutral-100 px-2 py-0.5 rounded whitespace-nowrap shrink-0">
                    Size: {{ selectedSize }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Item 2: Mix-Match Product 1 -->
            <div
              v-if="mixMatchProducts[0]"
              class="bg-white/95 backdrop-blur-sm rounded-xl p-2.5 md:p-3.5 border border-white/60 shadow-lg silky-hover hover:-translate-y-1 hover:shadow-xl flex flex-row md:flex-col items-center md:items-stretch md:justify-between gap-3 md:gap-0 h-full relative group"
            >
              <router-link :to="`/product/${mixMatchProducts[0].slug}`" class="w-[72px] h-[90px] shrink-0 md:w-full md:h-auto md:aspect-[3/4] rounded-lg overflow-hidden md:mb-3 bg-neutral-100 block">
                <img
                  :src="mixMatchProducts[0].thumbnailUrl"
                  :alt="mixMatchProducts[0].name"
                  @error="onImageError"
                  class="w-full h-full object-cover group-hover:scale-[1.04] transition-transform duration-700 ease-[cubic-bezier(0.22,1,0.36,1)]"
                />
              </router-link>
              <div class="flex-1 min-w-0 space-y-1.5 md:space-y-2">
                <span class="inline-block w-fit md:absolute md:top-3 md:left-3 bg-[#7A1032] text-white text-[9px] font-bold px-2 py-0.5 rounded uppercase z-10 whitespace-nowrap">
                  Món 2 (Gợi ý)
                </span>
                <router-link :to="`/product/${mixMatchProducts[0].slug}`" class="block">
                  <h4 class="text-xs font-semibold text-[#18181B] hover:text-[#7A1032] line-clamp-2 md:line-clamp-1 leading-snug transition-colors">
                    {{ mixMatchProducts[0].name }}
                  </h4>
                </router-link>
                <div class="flex items-center justify-between gap-2 text-xs">
                  <span class="font-bold text-[#7A1032] whitespace-nowrap">
                    {{ formatVND(mixMatchSelectedVariants[mixMatchProducts[0].id]?.price || mixMatchProducts[0].salePrice || mixMatchProducts[0].basePrice) }}
                  </span>
                  <div class="flex items-center gap-1 shrink-0">
                    <span class="text-[10px] text-[#6B7280]">Size:</span>
                    <select
                      v-model="mixMatchSelectedSizes[mixMatchProducts[0].id]"
                      class="bg-white text-[#18181B] text-[11px] font-semibold rounded-md px-2 py-1 border border-neutral-200 focus:outline-none focus:ring-1 focus:ring-[#7A1032]"
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
              class="bg-white/95 backdrop-blur-sm rounded-xl p-2.5 md:p-3.5 border border-white/60 shadow-lg silky-hover hover:-translate-y-1 hover:shadow-xl flex flex-row md:flex-col items-center md:items-stretch md:justify-between gap-3 md:gap-0 h-full relative group"
            >
              <router-link :to="`/product/${mixMatchProducts[1].slug}`" class="w-[72px] h-[90px] shrink-0 md:w-full md:h-auto md:aspect-[3/4] rounded-lg overflow-hidden md:mb-3 bg-neutral-100 block">
                <img
                  :src="mixMatchProducts[1].thumbnailUrl"
                  :alt="mixMatchProducts[1].name"
                  @error="onImageError"
                  class="w-full h-full object-cover group-hover:scale-[1.04] transition-transform duration-700 ease-[cubic-bezier(0.22,1,0.36,1)]"
                />
              </router-link>
              <div class="flex-1 min-w-0 space-y-1.5 md:space-y-2">
                <span class="inline-block w-fit md:absolute md:top-3 md:left-3 bg-[#7A1032] text-white text-[9px] font-bold px-2 py-0.5 rounded uppercase z-10 whitespace-nowrap">
                  Món 3 (Gợi ý)
                </span>
                <router-link :to="`/product/${mixMatchProducts[1].slug}`" class="block">
                  <h4 class="text-xs font-semibold text-[#18181B] hover:text-[#7A1032] line-clamp-2 md:line-clamp-1 leading-snug transition-colors">
                    {{ mixMatchProducts[1].name }}
                  </h4>
                </router-link>
                <div class="flex items-center justify-between gap-2 text-xs">
                  <span class="font-bold text-[#7A1032] whitespace-nowrap">
                    {{ formatVND(mixMatchSelectedVariants[mixMatchProducts[1].id]?.price || mixMatchProducts[1].salePrice || mixMatchProducts[1].basePrice) }}
                  </span>
                  <div class="flex items-center gap-1 shrink-0">
                    <span class="text-[10px] text-[#6B7280]">Size:</span>
                    <select
                      v-model="mixMatchSelectedSizes[mixMatchProducts[1].id]"
                      class="bg-white text-[#18181B] text-[11px] font-semibold rounded-md px-2 py-1 border border-neutral-200 focus:outline-none focus:ring-1 focus:ring-[#7A1032]"
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
          <div class="bg-white/95 backdrop-blur-sm rounded-xl p-4 md:p-5 border border-white/60 shadow-lg flex flex-col justify-between space-y-3.5 md:space-y-5 h-full">
            <div>
              <span class="text-[11px] font-bold text-[#7A1032] uppercase tracking-[0.14em] block">
                Trọn bộ phối chuẩn gu
              </span>
              <div class="text-xs text-[#6B7280] mt-1">
                Gồm {{ 1 + mixMatchProducts.length }} món thời trang kết hợp hoàn hảo
              </div>
              <div class="mt-3 md:mt-5 pt-3 md:pt-4 border-t border-neutral-200/80 flex items-baseline justify-between md:block">
                <span class="text-xs text-[#6B7280] block">Tổng giá trị cả set:</span>
                <div class="text-xl md:text-2xl font-extrabold text-[#7A1032] md:mt-1">
                  {{ formatVND(totalSetPrice) }}
                </div>
              </div>
            </div>

            <button
              type="button"
              @click="handleAddSetToCart"
              class="btn-sheen silky-hover active:scale-[0.98] w-full h-11 px-4 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-sm"
            >
              <span>🛍️</span>
              <span>Thêm Cả Bộ Vào Giỏ</span>
            </button>
          </div>

        </div>
      </div>
    </div>

    <!-- Description & Fit Reviews Tabs -->
    <div v-reveal class="bg-white rounded-2xl border border-neutral-200/80 p-5 sm:p-8 shadow-sm space-y-8">
      
      <!-- Tab Headers -->
      <div class="flex border-b border-neutral-200/80 gap-6 text-xs sm:text-sm font-bold">
        <button
          @click="activeTab = 'desc'"
          class="pb-3.5 border-b-2 transition-colors"
          :class="activeTab === 'desc' ? 'border-[#7A1032] text-[#7A1032]' : 'border-transparent text-[#6B7280] hover:text-[#18181B]'"
        >
          Mô Tả Sản Phẩm
        </button>
        <button
          @click="activeTab = 'reviews'"
          class="pb-3.5 border-b-2 transition-colors flex items-center gap-2"
          :class="activeTab === 'reviews' ? 'border-[#7A1032] text-[#7A1032]' : 'border-transparent text-[#6B7280] hover:text-[#18181B]'"
        >
          <span>Đánh Giá Thực Tế</span>
          <span class="bg-[#7A1032]/10 text-[#7A1032] px-2 py-0.5 rounded-md text-xs font-bold">
            {{ reviewStats.totalReviews || product.reviews?.length || 0 }}
          </span>
        </button>
      </div>

      <!-- Tab Content: Description -->
      <div v-if="activeTab === 'desc'" class="prose max-w-none text-[#18181B]/90 text-sm leading-relaxed space-y-5">
        <p>{{ product.description }}</p>
        <div class="bg-[#FAFAFA] rounded-xl p-5 border border-neutral-200/80 space-y-2 text-xs">
          <h4 class="font-bold text-[#7A1032] uppercase tracking-wider text-[11px]">Hướng dẫn bảo quản trang phục</h4>
          <ul class="list-disc pl-5 space-y-1.5 text-[#6B7280]">
            <li>Giặt ở nhiệt độ thường, lộn trái sản phẩm trước khi cho vào máy giặt.</li>
            <li>Không ngâm chung quần áo màu sáng với quần áo tối màu.</li>
            <li>Ủi ở nhiệt độ trung bình dưới 150°C, tránh ủi trực tiếp lên các chi tiết in ấn.</li>
            <li>Phơi ở nơi thoáng mát râm mát, tránh ánh nắng gắt trực tiếp làm phai màu vải.</li>
          </ul>
        </div>
      </div>

      <!-- Tab Content: Fit Reviews -->
      <div v-else class="space-y-8">
        
        <div
          v-if="(reviewStats.totalReviews || 0) === 0"
          class="bg-[#FAFAFA] border border-dashed border-neutral-300 rounded-xl p-8 text-center space-y-2"
        >
          <span class="text-3xl block">💬</span>
          <h3 class="text-sm font-bold text-[#18181B]">Chưa có đánh giá nào</h3>
          <p class="text-xs text-[#6B7280] max-w-md mx-auto">
            Sản phẩm này hiện chưa có đánh giá từ khách hàng. Hãy là người đầu tiên chia sẻ cảm nhận về chất liệu và độ vừa vặn bên dưới!
          </p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6 bg-[#FAFAFA] p-5 sm:p-6 rounded-xl border border-neutral-200/80">
          
          <!-- Rating Average Box -->
          <div class="flex items-center gap-6 border-b md:border-b-0 md:border-r border-neutral-200/80 pb-5 md:pb-0 md:pr-6">
            <div class="text-center">
              <div class="text-4xl sm:text-5xl font-extrabold text-[#7A1032] leading-none">
                {{ reviewStats.ratingAverage ?? 0 }}
              </div>
              <div class="flex items-center justify-center text-[#C9963B] text-sm mt-1.5">
                ★★★★★
              </div>
              <p class="text-[11px] text-[#6B7280] mt-1">Dựa trên {{ reviewStats.totalReviews }} đánh giá</p>
            </div>

            <!-- Star distribution bars -->
            <div class="flex-1 space-y-1.5 text-xs text-[#6B7280]">
              <div v-for="s in [5, 4, 3, 2, 1]" :key="s" class="flex items-center gap-2">
                <span class="w-9 font-medium text-[11px]">{{ s }} sao</span>
                <div class="flex-1 bg-neutral-200 rounded-full h-1.5 overflow-hidden">
                  <div
                    class="bg-[#C9963B] h-full rounded-full"
                    :style="{ width: `${calcStarPercent(s)}%` }"
                  ></div>
                </div>
                <span class="text-[10px] w-6 text-right text-[#6B7280]">{{ reviewStats.starCounts?.[s] || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- Fit Feedback Stats Breakdown -->
          <div class="space-y-3 flex flex-col justify-center">
            <div class="flex flex-wrap items-center justify-between gap-2 text-xs font-bold text-[#18181B]">
              <span>Cảm nhận độ vừa vặn thực tế</span>
              <span class="text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-md border border-emerald-200 text-[11px]">
                {{ reviewStats.fitPercent ?? 0 }}% chuẩn form
              </span>
            </div>

            <div class="space-y-2.5 text-xs">
              <div>
                <div class="flex justify-between text-[11px] mb-1 font-medium text-[#6B7280]">
                  <span>✓ Vừa vặn chuẩn phom</span>
                  <span class="font-bold text-[#18181B]">{{ reviewStats.fitPercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-neutral-200 rounded-full h-1.5 overflow-hidden">
                  <div class="bg-emerald-500 h-full rounded-full" :style="{ width: `${reviewStats.fitPercent ?? 0}%` }"></div>
                </div>
              </div>

              <div>
                <div class="flex justify-between text-[11px] mb-1 font-medium text-[#6B7280]">
                  <span>Hơi ôm / Hơi chật so với dáng</span>
                  <span class="font-bold text-[#18181B]">{{ reviewStats.tightPercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-neutral-200 rounded-full h-1.5 overflow-hidden">
                  <div class="bg-[#C9963B] h-full rounded-full" :style="{ width: `${reviewStats.tightPercent ?? 0}%` }"></div>
                </div>
              </div>

              <div>
                <div class="flex justify-between text-[11px] mb-1 font-medium text-[#6B7280]">
                  <span>Hơi rộng thoải mái / Oversize</span>
                  <span class="font-bold text-[#18181B]">{{ reviewStats.loosePercent ?? 0 }}%</span>
                </div>
                <div class="w-full bg-neutral-200 rounded-full h-1.5 overflow-hidden">
                  <div class="bg-[#5E1B55] h-full rounded-full" :style="{ width: `${reviewStats.loosePercent ?? 0}%` }"></div>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- FILTERS BY STAR & FIT FEEDBACK -->
        <div class="flex flex-wrap items-center justify-between gap-3 pb-3 border-b border-neutral-200/80">
          <div class="flex flex-wrap gap-2 text-xs">
            <span class="text-[#6B7280] font-medium self-center mr-1">Lọc sao:</span>
            <button
              v-for="s in [0, 5, 4, 3, 2, 1]"
              :key="s"
              type="button"
              @click="filterStar = s"
              class="px-3 py-1.5 rounded-lg border font-semibold transition-all text-xs"
              :class="filterStar === s ? 'border-[#7A1032] bg-[#7A1032] text-white' : 'border-neutral-200 bg-white text-[#6B7280] hover:text-[#18181B] hover:border-[#C9963B]'"
            >
              {{ s === 0 ? 'Tất cả sao' : `${s} ★` }}
            </button>
          </div>

          <div class="flex items-center gap-2 text-xs">
            <span class="text-[#6B7280] font-medium">Độ vừa:</span>
            <select
              v-model="filterFit"
              class="h-9 bg-white border border-neutral-200 rounded-lg px-3 font-medium text-[#18181B] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
            >
              <option value="">Tất cả cảm nhận</option>
              <option value="VUA_VAN">Chỉ xem: Vừa vặn chuẩn</option>
              <option value="CHAT">Chỉ xem: Hơi chật</option>
              <option value="RONG">Chỉ xem: Hơi rộng</option>
            </select>
          </div>
        </div>

        <!-- REVIEWS LIST -->
        <div class="space-y-4">
          <div v-if="filteredReviews.length === 0" class="py-12 text-center text-xs text-[#6B7280] bg-[#FAFAFA] rounded-xl border border-neutral-200/80">
            Không có đánh giá nào phù hợp với bộ lọc đã chọn.
          </div>

          <div
            v-for="rev in filteredReviews"
            :key="rev.id"
            class="p-5 rounded-xl bg-white border border-neutral-200/80 space-y-3"
          >
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div class="flex items-center gap-3">
                <span class="w-9 h-9 rounded-full luxury-gradient text-white font-bold flex items-center justify-center text-xs">
                  {{ (rev.customerName || 'K').charAt(0).toUpperCase() }}
                </span>
                <div>
                  <div class="flex items-center gap-2">
                    <h4 class="font-bold text-xs text-[#18181B]">{{ rev.customerName }}</h4>
                    <span
                      v-if="rev.verifiedPurchase"
                      class="text-[10px] text-emerald-700 font-medium bg-emerald-50 px-1.5 py-0.5 rounded border border-emerald-200"
                    >
                      ✓ Đã mua hàng
                    </span>
                  </div>
                  <div class="flex items-center text-[#C9963B] text-xs mt-0.5">
                    <span v-for="star in 5" :key="star">{{ star <= rev.rating ? '★' : '☆' }}</span>
                  </div>
                </div>
              </div>

              <span class="text-[11px] text-[#6B7280]">
                {{ formatDateTime(rev.createdAt) }}
              </span>
            </div>

            <div class="p-2.5 rounded-lg bg-[#FAFAFA] border border-neutral-200/80 text-xs text-[#6B7280] flex flex-wrap items-center gap-2">
              <span class="font-medium text-[#18181B] flex items-center gap-1">
                <span>👤</span>
                Khách cao {{ rev.customerHeightCm ? `${rev.customerHeightCm}cm` : '172cm' }} - {{ rev.customerWeightKg ? `${rev.customerWeightKg}kg` : '65kg' }}
              </span>
              <span class="text-neutral-300">|</span>
              <span>
                Phân loại: <strong class="text-[#18181B]">{{ rev.purchasedColor || 'Đen Basic' }}</strong> - <strong class="text-[#18181B]">Size {{ rev.purchasedSize || 'L' }}</strong>
              </span>
              <span class="text-neutral-300">|</span>
              <span
                class="px-2 py-0.5 rounded font-semibold text-[11px]"
                :class="rev.fitFeedback === 'CHAT' ? 'bg-amber-50 text-amber-800 border border-amber-200' : rev.fitFeedback === 'RONG' ? 'bg-purple-50 text-purple-800 border border-purple-200' : 'bg-emerald-50 text-emerald-700 border border-emerald-200'"
              >
                Độ vừa: {{ rev.fitFeedback === 'CHAT' ? 'Hơi chật so với dáng' : rev.fitFeedback === 'RONG' ? 'Hơi rộng thoải mái' : 'Vừa vặn chuẩn form' }}
              </span>
            </div>

            <p class="text-xs text-[#18181B]/90 leading-relaxed pt-1">
              {{ rev.comment }}
            </p>
          </div>
        </div>

        <!-- FORM GỬI ĐÁNH GIÁ MỚI -->
        <div class="pt-6 border-t border-neutral-200/80">
          <div class="bg-[#FAFAFA] rounded-xl p-5 sm:p-7 border border-neutral-200/80 space-y-5">
            <div>
              <h3 class="text-base font-bold text-[#18181B]">
                Gửi Đánh Giá & Chia Sẻ Vóc Dáng
              </h3>
              <p class="text-xs text-[#6B7280] mt-1">
                Thông số chiều cao, cân nặng và cảm nhận của bạn sẽ giúp cộng đồng chọn size chính xác nhất.
              </p>
            </div>

            <form @submit.prevent="submitReview" class="space-y-4">
              <div>
                <label class="block text-xs font-semibold text-[#6B7280] mb-1">Mức độ hài lòng *</label>
                <div class="flex items-center gap-1.5 text-2xl text-[#C9963B] cursor-pointer">
                  <span
                    v-for="s in 5"
                    :key="s"
                    @click="newReview.rating = s"
                    class="hover:scale-110 transition-transform"
                  >
                    {{ s <= newReview.rating ? '★' : '☆' }}
                  </span>
                  <span class="ml-2 text-xs font-bold text-[#18181B]">
                    {{ newReview.rating }}/5 sao
                  </span>
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3.5">
                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Họ và tên</label>
                  <input
                    type="text"
                    v-model="newReview.customerName"
                    placeholder="VD: Nguyễn Văn A (hoặc ẩn danh)"
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
                  />
                </div>

                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Chiều cao (cm) *</label>
                  <input
                    type="number"
                    min="140"
                    max="210"
                    v-model.number="newReview.customerHeightCm"
                    required
                    placeholder="172"
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
                  />
                </div>

                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Cân nặng (kg) *</label>
                  <input
                    type="number"
                    min="35"
                    max="150"
                    v-model.number="newReview.customerWeightKg"
                    required
                    placeholder="65"
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
                  />
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3.5">
                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Size bạn đã mua *</label>
                  <select
                    v-model="newReview.purchasedSize"
                    required
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] font-medium"
                  >
                    <option v-for="s in availableSizes" :key="s" :value="s">Size {{ s }}</option>
                  </select>
                </div>

                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Màu sắc đã mua *</label>
                  <select
                    v-model="newReview.purchasedColor"
                    required
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] font-medium"
                  >
                    <option v-for="c in availableColors" :key="c.name" :value="c.name">{{ c.name }}</option>
                  </select>
                </div>

                <div class="space-y-1.5">
                  <label class="block text-xs font-medium text-[#6B7280]">Cảm nhận độ vừa vặn *</label>
                  <select
                    v-model="newReview.fitFeedback"
                    required
                    class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] font-semibold"
                  >
                    <option value="VUA_VAN">✓ Vừa vặn chuẩn form</option>
                    <option value="CHAT">Hơi chật so với form</option>
                    <option value="RONG">Hơi rộng thoải mái</option>
                  </select>
                </div>
              </div>

              <div class="space-y-1.5">
                <label class="block text-xs font-medium text-[#6B7280]">Nhận xét chi tiết *</label>
                <textarea
                  v-model="newReview.comment"
                  required
                  rows="3"
                  placeholder="Chia sẻ về chất vải, đường may, khả năng thấm hút mồ hôi và lời khuyên chọn size cho người mua sau..."
                  class="w-full px-3.5 py-2.5 rounded-lg border border-neutral-200 text-xs bg-white text-[#18181B] placeholder:text-neutral-400 focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032]"
                ></textarea>
              </div>

              <button
                type="submit"
                :disabled="submittingReview"
                class="btn-sheen silky-hover active:scale-[0.98] h-11 px-6 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider inline-flex items-center gap-2 disabled:opacity-50"
              >
                <span v-if="submittingReview">Đang gửi đánh giá...</span>
                <span v-else>Gửi Đánh Giá Ngay</span>
              </button>
            </form>
          </div>
        </div>

      </div>

    </div>

    <!-- SẢN PHẨM BẠN VỪA XEM -->
    <RecentlyViewed />

    <!-- MODAL TRỢ LÝ GỢI Ý SIZE THÔNG MINH -->
    <SmartSizePredictorModal
      :is-open="isSizePredictorOpen"
      :available-sizes="availableSizes"
      @close="isSizePredictorOpen = false"
      @select-size="handleSizeSelected"
    />

  </div>

  <!-- 404 / NOT FOUND STATE -->
  <div v-else class="max-w-xl mx-auto py-24 px-4 text-center space-y-4">
    <div class="w-20 h-20 rounded-2xl bg-white border border-neutral-200/80 shadow-sm text-[#7A1032] flex items-center justify-center text-3xl mx-auto">
      🛍️
    </div>
    <h2 class="text-2xl font-bold text-[#18181B] tracking-tight">Không Tìm Thấy Sản Phẩm</h2>
    <p class="text-xs text-[#6B7280] max-w-md mx-auto">
      Sản phẩm bạn đang tìm kiếm có thể đã ngừng kinh doanh, hết hàng hoặc đường dẫn không còn chính xác.
    </p>
    <div class="pt-4 flex justify-center gap-3">
      <router-link
        to="/catalog"
        class="btn-sheen h-11 px-6 bg-[#7A1032] hover:bg-[#5C0B27] text-white rounded-lg font-bold text-xs transition-all inline-flex items-center gap-2"
      >
        <span>← Khám Phá Cửa Hàng</span>
      </router-link>
      <router-link
        to="/"
        class="h-11 px-6 bg-white border border-neutral-200 hover:border-[#C9963B] text-[#18181B] rounded-lg font-bold text-xs transition-all inline-flex items-center gap-2"
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
import StarfieldOverlay from '@/components/StarfieldOverlay.vue';
import SparkleBorderOverlay from '@/components/SparkleBorderOverlay.vue';
import SparkleDivider from '@/components/SparkleDivider.vue';

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
