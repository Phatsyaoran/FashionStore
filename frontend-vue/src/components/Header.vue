<template>
  <header class="sticky top-0 z-40 backdrop-blur-md bg-white/90 border-b border-gray-100 shadow-soft transition-all no-print">
    <!-- Top Announcement Bar (Coolmate / Uniqlo Style) -->
    <div class="bg-gradient-to-r from-gray-950 via-gray-900 to-rose-950 text-white text-[11px] py-2 px-4 font-semibold tracking-wide border-b border-white/5">
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <div class="flex items-center gap-2 truncate">
          <span class="inline-block animate-pulse">🔥</span>
          <span>Ưu đãi thành viên: Nhập mã <strong class="text-amber-300 font-black px-1.5 py-0.5 bg-white/10 rounded-md border border-amber-300/30">WELCOME10</strong> giảm 10% đơn từ 200k!</span>
        </div>
        <div class="hidden md:flex items-center gap-4 text-gray-300 font-medium">
          <span class="flex items-center gap-1"><span>🚚</span> Freeship đơn từ 500k</span>
          <span>•</span>
          <span class="flex items-center gap-1"><span>🔄</span> Đổi trả 60 ngày tận nơi</span>
        </div>
      </div>
    </div>

    <!-- Main Navigation Bar -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16 sm:h-20 gap-3 sm:gap-4">
        
        <!-- Left: Hamburger (Mobile/Tablet) + Logo -->
        <div class="flex items-center gap-2.5 flex-shrink-0">
          <button
            type="button"
            @click="isMobileMenuOpen = true"
            class="lg:hidden p-2 -ml-1 rounded-xl text-gray-700 hover:bg-gray-100 hover:text-rose-600 transition-colors"
            aria-label="Mở menu điều hướng"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <!-- Logo -->
          <router-link to="/" class="flex items-center gap-2.5 flex-shrink-0 group">
            <div class="w-9 h-9 sm:w-10 sm:h-10 rounded-2xl bg-gradient-to-tr from-rose-600 to-amber-500 flex items-center justify-center text-white shadow-md shadow-rose-200 group-hover:scale-105 transition-transform">
              <span class="text-lg sm:text-xl font-black">F</span>
            </div>
            <div>
              <span class="text-lg sm:text-xl font-extrabold tracking-tight text-gray-900 block leading-tight">FASHION<span class="text-rose-600">.VN</span></span>
              <span class="text-[9px] sm:text-[10px] text-gray-400 uppercase tracking-widest block font-semibold">Phong cách thời thượng</span>
            </div>
          </router-link>
        </div>

        <!-- Search Bar (Desktop) with Instant Search Dropdown (Phase 10) -->
        <div class="hidden md:flex flex-1 max-w-md mx-4 relative" ref="searchContainerRef">
          <form @submit.prevent="handleSearch" class="relative w-full">
            <input
              type="text"
              v-model="searchKeyword"
              @focus="onSearchFocus"
              @input="onSearchInput"
              @keydown.esc="isSearchOpen = false"
              placeholder="Tìm áo thun, sơ mi, quần jean, đầm váy..."
              class="w-full pl-10 pr-8 py-2.5 rounded-full border border-gray-200 bg-gray-50/80 focus:bg-white focus:outline-none focus:ring-2 focus:ring-rose-500 focus:border-transparent text-xs text-gray-800 placeholder-gray-400 transition-all shadow-inner"
            />
            <button type="submit" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-rose-600">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </button>
            <button
              v-if="searchKeyword"
              type="button"
              @click="clearSearch"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 text-xs font-bold"
            >
              ✕
            </button>
          </form>

          <!-- Instant Search Dropdown -->
          <transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="opacity-0 translate-y-1 scale-95"
            enter-to-class="opacity-100 translate-y-0 scale-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="opacity-100 translate-y-0 scale-100"
            leave-to-class="opacity-0 translate-y-1 scale-95"
          >
            <div
              v-if="isSearchOpen && searchKeyword.trim().length > 0"
              class="absolute top-full left-0 right-0 mt-2 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50 divide-y divide-gray-50"
            >
              <div v-if="isSearching" class="p-4 text-center text-xs text-gray-400 flex items-center justify-center gap-2">
                <span class="w-3.5 h-3.5 border-2 border-rose-500 border-t-transparent rounded-full animate-spin"></span>
                <span>Đang tìm kiếm sản phẩm...</span>
              </div>

              <div v-else-if="searchResults.length === 0" class="p-5 text-center text-xs text-gray-500">
                <span class="text-xl block mb-1">🔍</span>
                Không tìm thấy sản phẩm nào khớp với "<strong>{{ searchKeyword }}</strong>"
              </div>

              <div v-else class="max-h-80 overflow-y-auto divide-y divide-gray-50">
                <div
                  v-for="product in searchResults"
                  :key="product.id"
                  @click="selectSearchResult(product)"
                  class="p-2.5 hover:bg-rose-50/50 cursor-pointer flex items-center gap-3 transition-colors group"
                >
                  <img
                    :src="product.thumbnailUrl"
                    :alt="product.name"
                    @error="onImageError"
                    class="w-11 h-14 object-cover rounded-xl bg-gray-100 flex-shrink-0 border border-gray-100"
                    loading="lazy"
                  />
                  <div class="flex-1 min-w-0">
                    <span class="text-[10px] font-semibold text-rose-600 uppercase tracking-wider block">
                      {{ product.categoryName || 'Thời Trang' }}
                    </span>
                    <h4 class="text-xs font-bold text-gray-800 truncate group-hover:text-rose-600 transition-colors">
                      {{ product.name }}
                    </h4>
                    <div class="flex items-center gap-2 mt-0.5">
                      <span class="text-xs font-extrabold text-rose-600">
                        {{ formatVND(product.salePrice || product.basePrice) }}
                      </span>
                      <span
                        v-if="product.salePrice && product.basePrice > product.salePrice"
                        class="text-[10px] text-gray-400 line-through"
                      >
                        {{ formatVND(product.basePrice) }}
                      </span>
                      <span
                        v-if="product.isFlashSale"
                        class="text-[9px] font-black bg-rose-600 text-white px-1.5 py-0.2 rounded uppercase"
                      >
                        ⚡ SALE
                      </span>
                    </div>
                  </div>
                  <span class="text-gray-300 group-hover:text-rose-600 text-sm font-bold pr-1">→</span>
                </div>
              </div>

              <div v-if="searchResults.length > 0" class="p-2.5 bg-gray-50 text-center">
                <button
                  type="button"
                  @click="handleSearch"
                  class="text-xs font-bold text-rose-600 hover:text-rose-700 hover:underline"
                >
                  Xem tất cả kết quả cho "{{ searchKeyword }}" →
                </button>
              </div>
            </div>
          </transition>
        </div>

        <!-- Navigation Links -->
        <nav class="hidden lg:flex items-center gap-6 text-sm font-semibold text-gray-700">
          <router-link to="/" class="hover:text-rose-600 transition-colors" active-class="text-rose-600">Trang Chủ</router-link>
          <router-link to="/catalog" class="hover:text-rose-600 transition-colors" active-class="text-rose-600">Cửa Hàng</router-link>
          <router-link to="/catalog?isFlashSale=true" class="hover:text-rose-600 text-rose-600 flex items-center gap-1 font-bold">
            <span>⚡</span> Flash Sale
          </router-link>
          <router-link to="/order-tracking" class="hover:text-rose-600 transition-colors" active-class="text-rose-600">Tra Cứu Đơn</router-link>
        </nav>

        <!-- Right User Actions -->
        <div class="flex items-center gap-3">
          
          <!-- Auth Menu (Click toggle for touch & desktop) -->
          <div v-if="authStore.isAuthenticated" class="relative" ref="userMenuRef">
            <button
              type="button"
              @click.stop="isUserMenuOpen = !isUserMenuOpen"
              class="flex items-center gap-2 py-1.5 px-2.5 sm:px-3 rounded-full hover:bg-gray-100 transition-colors text-xs font-semibold text-gray-700"
              :aria-expanded="isUserMenuOpen"
            >
              <span class="w-7 h-7 rounded-full bg-rose-100 text-rose-700 flex items-center justify-center font-bold">
                {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
              </span>
              <span class="hidden sm:inline max-w-[100px] truncate">{{ authStore.user?.fullName }}</span>
              <svg class="w-3.5 h-3.5 text-gray-400 hidden sm:block transition-transform" :class="{ 'rotate-180': isUserMenuOpen }" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- Dropdown -->
            <transition
              enter-active-class="transition duration-150 ease-out"
              enter-from-class="opacity-0 scale-95 -translate-y-1"
              enter-to-class="opacity-100 scale-100 translate-y-0"
              leave-active-class="transition duration-100 ease-in"
              leave-from-class="opacity-100 scale-100 translate-y-0"
              leave-to-class="opacity-0 scale-95 -translate-y-1"
            >
              <div
                v-if="isUserMenuOpen"
                class="absolute right-0 mt-2 w-52 bg-white rounded-2xl shadow-xl border border-gray-100 py-2 z-50"
              >
                <div class="px-4 py-2 border-b border-gray-100 text-xs">
                  <p class="font-bold text-gray-900 truncate">{{ authStore.user?.fullName }}</p>
                  <p class="text-gray-400 truncate">{{ authStore.user?.email }}</p>
                </div>
                <router-link
                  v-if="authStore.isAdmin"
                  to="/admin"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs font-bold text-rose-600 hover:bg-rose-50"
                >
                  🛠️ Quản trị Admin
                </router-link>
                <router-link
                  to="/profile"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs text-gray-700 hover:bg-gray-50 font-semibold"
                >
                  📦 Đơn hàng của tôi
                </router-link>
                <router-link
                  to="/order-tracking"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs text-gray-500 hover:bg-gray-50"
                >
                  🔍 Tra cứu vận đơn
                </router-link>
                <button
                  type="button"
                  @click="handleLogout"
                  class="w-full text-left px-4 py-2.5 text-xs text-rose-600 hover:bg-rose-50 border-t border-gray-50 font-semibold"
                >
                  Đăng xuất
                </button>
              </div>
            </transition>
          </div>

          <div v-else class="flex items-center gap-1.5">
            <router-link
              to="/login"
              class="text-xs font-bold text-gray-700 hover:text-rose-600 px-3 py-2 rounded-xl hover:bg-gray-50 transition-colors"
            >
              Đăng nhập
            </router-link>
          </div>

          <!-- Wishlist Drawer Button (Phase 10) -->
          <button
            type="button"
            @click="isWishlistOpen = true"
            class="relative p-2.5 rounded-full hover:bg-rose-50 text-gray-800 hover:text-rose-600 transition-colors flex items-center justify-center"
            aria-label="Yêu thích"
            title="Danh sách yêu thích"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span
              v-if="wishlistStore.totalWishlist > 0"
              class="absolute -top-1 -right-1 bg-rose-600 text-white text-[10px] font-black w-5 h-5 rounded-full flex items-center justify-center border-2 border-white shadow-sm"
            >
              {{ wishlistStore.totalWishlist }}
            </span>
          </button>

          <!-- Cart Drawer Button -->
          <button
            type="button"
            @click="cartStore.toggleDrawer(true)"
            class="relative p-2.5 rounded-full hover:bg-rose-50 text-gray-800 hover:text-rose-600 transition-colors flex items-center justify-center"
            aria-label="Giỏ hàng"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span
              v-if="cartStore.totalItems > 0"
              class="absolute -top-1 -right-1 bg-rose-600 text-white text-[10px] font-black w-5 h-5 rounded-full flex items-center justify-center border-2 border-white shadow-sm animate-bounce"
            >
              {{ cartStore.totalItems }}
            </span>
          </button>

        </div>

      </div>
    </div>

    <!-- MOBILE NAVIGATION DRAWER (< lg) -->
    <transition
      enter-active-class="transition-opacity duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="isMobileMenuOpen" class="fixed inset-0 z-50 lg:hidden flex" role="dialog" aria-modal="true">
        <div class="fixed inset-0 bg-black/60 backdrop-blur-sm" @click="isMobileMenuOpen = false"></div>

        <div class="relative w-[85vw] max-w-xs bg-white h-full shadow-2xl flex flex-col z-10 overflow-hidden">
          <!-- Drawer Top -->
          <div class="p-4 border-b border-gray-100 flex items-center justify-between bg-gray-50/80">
            <router-link to="/" @click="isMobileMenuOpen = false" class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-xl bg-gradient-to-tr from-rose-600 to-amber-500 flex items-center justify-center text-white font-black text-sm">
                F
              </div>
              <span class="text-base font-extrabold text-gray-900">FASHION<span class="text-rose-600">.VN</span></span>
            </router-link>
            <button
              type="button"
              @click="isMobileMenuOpen = false"
              class="p-2 rounded-xl text-gray-500 hover:text-gray-900 hover:bg-gray-200/70 transition-colors"
              aria-label="Đóng menu"
            >
              ✕
            </button>
          </div>

          <!-- Mobile Search Bar with Instant Results -->
          <div class="p-4 border-b border-gray-100">
            <form @submit.prevent="handleMobileSearch" class="relative">
              <input
                type="text"
                v-model="searchKeyword"
                @input="onSearchInput"
                placeholder="Tìm áo thun, sơ mi, quần jean..."
                class="w-full pl-9 pr-8 py-2.5 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-rose-500 text-xs text-gray-800"
              />
              <button type="submit" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </button>
              <button
                v-if="searchKeyword"
                type="button"
                @click="clearSearch"
                class="absolute right-2.5 top-1/2 -translate-y-1/2 text-gray-400 text-xs font-bold"
              >
                ✕
              </button>
            </form>

            <!-- Instant Search Results inside Drawer -->
            <div v-if="searchKeyword.trim().length > 0" class="mt-2 bg-gray-50 rounded-xl border border-gray-100 max-h-56 overflow-y-auto divide-y divide-gray-100">
              <div v-if="isSearching" class="p-3 text-center text-[11px] text-gray-400">
                Đang tìm kiếm...
              </div>
              <div v-else-if="searchResults.length === 0" class="p-3 text-center text-[11px] text-gray-500">
                Không tìm thấy sản phẩm khớp "{{ searchKeyword }}"
              </div>
              <div
                v-else
                v-for="product in searchResults"
                :key="product.id"
                @click="selectMobileSearchResult(product)"
                class="p-2 flex items-center gap-2.5 hover:bg-rose-50 cursor-pointer"
              >
                <img
                  :src="product.thumbnailUrl"
                  :alt="product.name"
                  @error="onImageError"
                  class="w-10 h-12 object-cover rounded-lg bg-gray-100 shrink-0"
                />
                <div class="min-w-0 flex-1">
                  <div class="text-xs font-bold text-gray-800 truncate">{{ product.name }}</div>
                  <div class="text-[11px] font-extrabold text-rose-600">{{ formatVND(product.salePrice || product.basePrice) }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Scrollable Menu Body -->
          <div class="flex-1 overflow-y-auto p-4 space-y-5">
            <!-- Main Links -->
            <nav class="space-y-1 text-sm font-bold text-gray-800">
              <router-link to="/" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl hover:bg-rose-50 hover:text-rose-600">
                <span>🏠</span> Trang Chủ
              </router-link>
              <router-link to="/catalog" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl hover:bg-rose-50 hover:text-rose-600">
                <span>🛍️</span> Tất Cả Sản Phẩm
              </router-link>
              <router-link to="/catalog?isFlashSale=true" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl bg-rose-50/70 text-rose-600">
                <span>⚡</span> Săn Flash Sale
              </router-link>
              <router-link to="/order-tracking" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl hover:bg-rose-50 hover:text-rose-600">
                <span>📦</span> Tra Cứu Đơn Hàng
              </router-link>
              <router-link to="/cart" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl hover:bg-rose-50 hover:text-rose-600">
                <span>🛒</span> Giỏ Hàng ({{ cartStore.totalItems }})
              </router-link>
            </nav>

            <!-- Categories -->
            <div v-if="mobileCategories.length > 0" class="pt-3 border-t border-gray-100">
              <p class="px-3 text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-2">Danh Mục Thời Trang</p>
              <div class="space-y-1">
                <router-link
                  v-for="cat in mobileCategories"
                  :key="cat.id"
                  :to="`/catalog?categorySlug=${cat.slug}`"
                  @click="isMobileMenuOpen = false"
                  class="flex items-center justify-between px-3 py-2 rounded-xl text-xs font-semibold text-gray-700 hover:bg-gray-50 hover:text-rose-600"
                >
                  <span>{{ cat.name }}</span>
                  <span class="text-[10px] text-gray-400">{{ cat.productCount || 0 }}</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Drawer Footer: Account -->
          <div class="p-4 border-t border-gray-100 bg-gray-50/70">
            <div v-if="authStore.isAuthenticated" class="space-y-2">
              <div class="flex items-center gap-2.5">
                <span class="w-8 h-8 rounded-full bg-rose-100 text-rose-700 flex items-center justify-center font-bold text-xs">
                  {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
                </span>
                <div class="min-w-0">
                  <p class="text-xs font-bold text-gray-900 truncate">{{ authStore.user?.fullName }}</p>
                  <p class="text-[11px] text-gray-400 truncate">{{ authStore.user?.email }}</p>
                </div>
              </div>
              <div class="flex gap-2 pt-1">
                <router-link
                  to="/profile"
                  @click="isMobileMenuOpen = false"
                  class="flex-1 text-center py-2 px-3 rounded-xl bg-rose-600 text-white text-xs font-bold"
                >
                  📦 Đơn của tôi
                </router-link>
                <router-link
                  v-if="authStore.isAdmin"
                  to="/admin"
                  @click="isMobileMenuOpen = false"
                  class="flex-1 text-center py-2 px-3 rounded-xl bg-gray-900 text-white text-xs font-bold"
                >
                  🛠️ Admin
                </router-link>
                <button
                  type="button"
                  @click="handleLogout"
                  class="flex-1 py-2 px-3 rounded-xl border border-rose-200 bg-rose-50 text-rose-600 text-xs font-bold"
                >
                  Thoát
                </button>
              </div>
            </div>
            <div v-else class="flex gap-2">
              <router-link
                to="/login"
                @click="isMobileMenuOpen = false"
                class="flex-1 text-center py-2.5 rounded-xl bg-rose-600 text-white text-xs font-bold shadow-sm"
              >
                Đăng nhập
              </router-link>
              <router-link
                to="/register"
                @click="isMobileMenuOpen = false"
                class="flex-1 text-center py-2.5 rounded-xl border border-gray-200 bg-white text-gray-800 text-xs font-bold"
              >
                Đăng ký
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- CART DRAWER (SLIDE-OVER) WITH FREE SHIPPING PROGRESS BAR -->
    <div v-if="cartStore.isDrawerOpen" class="fixed inset-0 z-50 overflow-hidden" role="dialog" aria-modal="true">
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" @click="cartStore.toggleDrawer(false)"></div>

      <div class="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div class="w-screen max-w-md bg-white shadow-2xl flex flex-col">
          
          <!-- Drawer Header -->
          <div class="p-5 border-b border-gray-100 flex items-center justify-between bg-gray-50/50">
            <div class="flex items-center gap-2">
              <span class="text-xl">🛍️</span>
              <h2 class="text-base font-bold text-gray-900">Giỏ Hàng ({{ cartStore.totalItems }} sản phẩm)</h2>
            </div>
            <button
              @click="cartStore.toggleDrawer(false)"
              class="text-gray-400 hover:text-gray-700 p-1.5 rounded-lg hover:bg-gray-100"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- THANH TIẾN TRÌNH FREESHIP ĐỘNG TRONG CART DRAWER (Yêu cầu Phase 6) -->
          <div class="p-4 bg-white border-b border-gray-100">
            <FreeShippingBar />
          </div>

          <!-- Drawer Items List -->
          <div class="flex-1 overflow-y-auto p-4 space-y-4">
            <div v-if="cartStore.items.length === 0" class="py-16 text-center">
              <span class="text-5xl block mb-3">🛒</span>
              <p class="text-gray-500 font-medium text-sm">Giỏ hàng của bạn đang trống</p>
              <router-link
                to="/catalog"
                @click="cartStore.toggleDrawer(false)"
                class="inline-block mt-4 px-5 py-2.5 bg-rose-600 text-white font-bold text-xs rounded-xl hover:bg-rose-700 shadow-md shadow-rose-200"
              >
                Khám phá sản phẩm ngay
              </router-link>
            </div>

            <div
              v-for="item in cartStore.items"
              :key="item.variantId"
              class="flex gap-3 p-3 rounded-2xl border border-gray-100 hover:border-gray-200 bg-white transition-all shadow-sm"
            >
              <img
                :src="item.thumbnailUrl"
                :alt="item.productName"
                @error="onImageError"
                class="w-16 h-20 object-cover rounded-xl bg-gray-50 flex-shrink-0"
              />
              <div class="flex-1 flex flex-col justify-between">
                <div>
                  <div class="flex justify-between items-start">
                    <h4 class="text-xs font-bold text-gray-900 line-clamp-1">{{ item.productName }}</h4>
                    <button
                      @click="cartStore.removeFromCart(item.variantId)"
                      class="text-gray-400 hover:text-rose-600 text-xs ml-2"
                    >
                      ✕
                    </button>
                  </div>
                  <div class="flex items-center gap-2 mt-1 text-[11px] text-gray-500">
                    <span class="bg-gray-100 px-1.5 py-0.5 rounded font-medium">Size: {{ item.size }}</span>
                    <span class="flex items-center gap-1">
                      <span class="w-2.5 h-2.5 rounded-full border border-gray-300" :style="{ backgroundColor: item.colorHex }"></span>
                      <span>{{ item.colorName }}</span>
                    </span>
                  </div>
                </div>

                <div class="flex justify-between items-center mt-2">
                  <div class="text-xs font-bold text-rose-600">
                    {{ formatVND(item.price) }}
                  </div>
                  <!-- Quantity -->
                  <div class="flex items-center border border-gray-200 rounded-lg overflow-hidden bg-gray-50">
                    <button
                      @click="cartStore.updateQuantity(item.variantId, item.quantity - 1)"
                      class="w-6 h-6 flex items-center justify-center text-gray-600 hover:bg-gray-200 text-xs font-bold"
                    >-</button>
                    <span class="w-7 text-center text-xs font-bold text-gray-900">{{ item.quantity }}</span>
                    <button
                      @click="cartStore.updateQuantity(item.variantId, item.quantity + 1)"
                      :disabled="item.quantity >= item.stockQuantity"
                      class="w-6 h-6 flex items-center justify-center text-gray-600 hover:bg-gray-200 text-xs font-bold disabled:opacity-40 disabled:cursor-not-allowed"
                    >+</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Drawer Footer -->
          <div v-if="cartStore.items.length > 0" class="p-5 border-t border-gray-100 bg-gray-50/50 space-y-3">
            <div class="flex justify-between text-xs text-gray-600">
              <span>Tạm tính tiền hàng:</span>
              <span class="font-bold text-gray-900 text-sm">{{ formatVND(cartStore.subTotal) }}</span>
            </div>
            <div class="flex justify-between text-xs text-gray-600">
              <span>Phí vận chuyển:</span>
              <span v-if="cartStore.isFreeShip" class="font-bold text-emerald-600 uppercase">Miễn phí</span>
              <span v-else class="font-medium text-gray-900">{{ formatVND(cartStore.shippingFee) }}</span>
            </div>

            <div class="pt-2 border-t border-gray-200 flex justify-between items-baseline">
              <span class="text-sm font-bold text-gray-900">Tổng cộng:</span>
              <span class="text-lg font-black text-rose-600">{{ formatVND(cartStore.totalAmount) }}</span>
            </div>

            <div class="grid grid-cols-2 gap-2 pt-2">
              <router-link
                to="/cart"
                @click="cartStore.toggleDrawer(false)"
                class="py-3 px-4 rounded-xl border border-gray-300 hover:bg-gray-100 text-gray-700 text-xs font-bold text-center transition-colors"
              >
                Xem Giỏ Hàng
              </router-link>
              <router-link
                to="/checkout"
                @click="cartStore.toggleDrawer(false)"
                class="py-3 px-4 rounded-xl bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold text-center shadow-md hover:shadow-lg transition-all"
              >
                Thanh Toán →
              </router-link>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- WISHLIST DRAWER (SLIDE-OVER) (Phase 10) -->
    <div v-if="isWishlistOpen" class="fixed inset-0 z-50 overflow-hidden" role="dialog" aria-modal="true">
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" @click="isWishlistOpen = false"></div>

      <div class="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div class="w-screen max-w-md bg-white shadow-2xl flex flex-col">
          
          <!-- Drawer Header -->
          <div class="p-5 border-b border-gray-100 flex items-center justify-between bg-gray-50/50">
            <div class="flex items-center gap-2">
              <span class="text-xl">❤️</span>
              <h2 class="text-base font-bold text-gray-900">Danh Sách Yêu Thích ({{ wishlistStore.totalWishlist }} món)</h2>
            </div>
            <button
              @click="isWishlistOpen = false"
              class="text-gray-400 hover:text-gray-700 p-1.5 rounded-lg hover:bg-gray-100"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Drawer Body -->
          <div class="flex-1 overflow-y-auto p-4 space-y-3">
            <div v-if="wishlistStore.items.length === 0" class="py-16 text-center">
              <span class="text-5xl block mb-3">🤍</span>
              <p class="text-gray-500 font-medium text-sm">Chưa có sản phẩm nào được lưu vào danh sách yêu thích</p>
              <router-link
                to="/catalog"
                @click="isWishlistOpen = false"
                class="inline-block mt-4 px-5 py-2.5 bg-rose-600 text-white font-bold text-xs rounded-xl hover:bg-rose-700 shadow-md shadow-rose-200"
              >
                Khám phá và thả tim ngay
              </router-link>
            </div>

            <div
              v-for="item in wishlistStore.items"
              :key="item.id"
              class="flex gap-3 p-3 rounded-2xl border border-gray-100 hover:border-rose-200 bg-white transition-all shadow-sm group"
            >
              <router-link :to="`/product/${item.slug}`" @click="isWishlistOpen = false">
                <img
                  :src="item.thumbnailUrl"
                  :alt="item.name"
                  @error="onImageError"
                  class="w-16 h-20 object-cover rounded-xl bg-gray-50 flex-shrink-0 group-hover:scale-105 transition-transform"
                />
              </router-link>

              <div class="flex-1 flex flex-col justify-between">
                <div>
                  <div class="flex justify-between items-start">
                    <router-link
                      :to="`/product/${item.slug}`"
                      @click="isWishlistOpen = false"
                      class="text-xs font-bold text-gray-900 hover:text-rose-600 line-clamp-1 transition-colors"
                    >
                      {{ item.name }}
                    </router-link>
                    <button
                      @click="wishlistStore.removeFromWishlist(item.id)"
                      class="text-gray-400 hover:text-rose-600 text-xs ml-2 p-1"
                      title="Xóa khỏi yêu thích"
                    >
                      ✕
                    </button>
                  </div>
                  <span class="text-[10px] text-gray-400 font-medium block mt-0.5">
                    {{ item.categoryName || 'Thời Trang' }}
                  </span>
                </div>

                <div class="flex justify-between items-center mt-2">
                  <div class="text-xs font-bold text-rose-600">
                    {{ formatVND(item.salePrice || item.basePrice) }}
                  </div>
                  <button
                    type="button"
                    @click="openQuickViewFromWishlist(item)"
                    class="px-3 py-1.5 rounded-xl bg-rose-50 hover:bg-rose-600 text-rose-600 hover:text-white text-xs font-bold transition-all shadow-sm"
                  >
                    Thêm vào giỏ
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Drawer Footer -->
          <div v-if="wishlistStore.items.length > 0" class="p-4 border-t border-gray-100 bg-gray-50/50 flex justify-between items-center">
            <button
              @click="wishlistStore.clearWishlist"
              class="text-xs text-gray-500 hover:text-rose-600 font-semibold"
            >
              Xóa tất cả
            </button>
            <router-link
              to="/catalog"
              @click="isWishlistOpen = false"
              class="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-xl shadow-sm transition-colors"
            >
              Xem thêm sản phẩm →
            </router-link>
          </div>

        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { useWishlistStore } from '@/stores/wishlistStore';
import { useProductStore } from '@/stores/productStore';
import { formatVND, onImageError } from '@/services/formatters';
import FreeShippingBar from '@/components/FreeShippingBar.vue';

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const wishlistStore = useWishlistStore();
const productStore = useProductStore();

const searchKeyword = ref('');
const isSearchOpen = ref(false);
const isSearching = ref(false);
const searchResults = ref([]);
const searchContainerRef = ref(null);
const userMenuRef = ref(null);
const isUserMenuOpen = ref(false);
const isMobileMenuOpen = ref(false);
const mobileCategories = ref([]);
const isWishlistOpen = ref(false);
let searchDebounceTimer = null;

function onSearchFocus() {
  if (searchKeyword.value.trim().length > 0) {
    isSearchOpen.value = true;
    if (searchResults.value.length === 0) {
      fetchSearchResults(searchKeyword.value.trim());
    }
  }
}

function onSearchInput() {
  const query = searchKeyword.value.trim();
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);

  if (!query) {
    searchResults.value = [];
    isSearchOpen.value = false;
    return;
  }

  isSearchOpen.value = true;
  isSearching.value = true;

  searchDebounceTimer = setTimeout(() => {
    fetchSearchResults(query);
  }, 300);
}

async function fetchSearchResults(keyword) {
  try {
    const res = await api.get('/products', {
      params: {
        keyword: keyword,
        pageSize: 5
      }
    });
    if (res.success && res.data) {
      searchResults.value = res.data.content || [];
    } else {
      searchResults.value = [];
    }
  } catch (err) {
    console.error('Lỗi tìm kiếm nhanh:', err);
    searchResults.value = [];
  } finally {
    isSearching.value = false;
  }
}

function clearSearch() {
  searchKeyword.value = '';
  searchResults.value = [];
  isSearchOpen.value = false;
}

function selectSearchResult(product) {
  isSearchOpen.value = false;
  searchKeyword.value = '';
  router.push(`/product/${product.slug}`);
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    isSearchOpen.value = false;
    router.push({
      path: '/catalog',
      query: { keyword: searchKeyword.value.trim() }
    });
  }
}

function selectMobileSearchResult(product) {
  isMobileMenuOpen.value = false;
  selectSearchResult(product);
}

function handleMobileSearch() {
  if (searchKeyword.value.trim()) {
    isMobileMenuOpen.value = false;
    handleSearch();
  }
}

function handleLogout() {
  isUserMenuOpen.value = false;
  isMobileMenuOpen.value = false;
  authStore.logout();
}

function openQuickViewFromWishlist(item) {
  isWishlistOpen.value = false;
  productStore.openQuickView(item);
}

function handleClickOutside(event) {
  if (searchContainerRef.value && !searchContainerRef.value.contains(event.target)) {
    isSearchOpen.value = false;
  }
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    isUserMenuOpen.value = false;
  }
}

async function loadMobileCategories() {
  try {
    const res = await api.get('/categories');
    if (res.success && Array.isArray(res.data)) {
      mobileCategories.value = res.data;
    }
  } catch {
    // Bỏ qua nếu chưa tải được danh mục phụ
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  loadMobileCategories();
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
});
</script>
