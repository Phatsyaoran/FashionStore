<template>
  <header class="sticky top-0 z-40 luxury-gradient text-[#FFFDF9] border-b border-white/15 shadow-md transition-all no-print">
    <!-- Interactive Starfield Overlay (clipped inside header without clipping dropdowns) -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <StarfieldOverlay :min-stars="65" />
    </div>

    <!-- Top Announcement Bar -->
    <div class="relative z-10 bg-black/25 text-[#FFFDF9]/90 text-[10.5px] sm:text-[11px] py-2 px-3 sm:px-4 font-medium tracking-wide border-b border-white/10">
      <div class="max-w-7xl mx-auto flex items-center justify-center md:justify-between gap-2 md:gap-4">
        <div class="flex items-center gap-1.5 sm:gap-2 min-w-0 md:truncate">
          <span class="w-1.5 h-1.5 rounded-full bg-[#F3D078] shrink-0"></span>
          <span class="whitespace-nowrap md:truncate"><span class="hidden sm:inline">Ưu đãi thành viên: </span>Nhập mã <strong class="text-[#F3D078] font-bold px-1.5 py-0.5 bg-white/15 rounded border border-[#F3D078]/35">WELCOME10</strong> giảm 10%<span class="hidden min-[380px]:inline"> đơn từ 200k</span></span>
        </div>
        <div class="hidden md:flex items-center gap-4 text-[#FFFDF9]/75 shrink-0">
          <span>Freeship đơn từ 500k</span>
          <span class="text-white/30">•</span>
          <span>Đổi trả 60 ngày tận nơi</span>
        </div>
      </div>
    </div>

    <!-- Main Navigation Bar (64px - 72px) -->
    <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-4">
      <div class="flex items-center justify-between h-16 lg:h-[72px] gap-3 lg:gap-6">
        
        <!-- Left: Hamburger (Mobile/Tablet) + Editorial Logo -->
        <div class="flex items-center gap-2 flex-shrink-0">
          <button
            type="button"
            @click="isMobileMenuOpen = true"
            class="lg:hidden min-w-[44px] min-h-[44px] -ml-2 rounded-lg text-[#FFFDF9] hover:bg-white/15 transition-colors flex items-center justify-center"
            aria-label="Mở menu điều hướng"
          >
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <!-- Logo -->
          <router-link to="/" class="flex items-center gap-2.5 flex-shrink-0 group">
            <div class="w-9 h-9 rounded-lg bg-white/15 backdrop-blur-md border border-[#F3D078]/50 group-hover:border-[#F3D078] flex items-center justify-center text-[#F3D078] transition-all shadow-sm">
              <span class="text-lg font-extrabold tracking-tight">F</span>
            </div>
            <div>
              <span class="text-lg font-extrabold tracking-tight text-[#FFFDF9] block leading-none">FASHION<span class="text-[#F3D078]">.VN</span></span>
              <span class="text-[9px] text-[#FFFDF9]/75 uppercase tracking-[0.18em] block font-semibold mt-0.5">Modern Luxury</span>
            </div>
          </router-link>
        </div>

        <!-- Center: Navigation Links (Desktop) -->
        <nav class="hidden lg:flex items-center gap-7 text-[13px] font-semibold text-[#FFFDF9]/85">
          <router-link to="/" class="hover:text-[#F3D078] transition-colors py-1" active-class="text-[#F3D078] font-bold">Trang Chủ</router-link>
          <router-link to="/catalog" class="hover:text-[#F3D078] transition-colors py-1" active-class="text-[#F3D078] font-bold">Cửa Hàng</router-link>
          <router-link to="/catalog?isFlashSale=true" class="text-[#F3D078] hover:text-white flex items-center gap-1 font-bold py-1">
            <span>⚡</span> Flash Sale
          </router-link>
          <router-link
            v-if="authStore.isAuthenticated"
            to="/order-tracking"
            class="hover:text-[#F3D078] transition-colors py-1"
            active-class="text-[#F3D078] font-bold"
          >
            Tra Cứu Đơn
          </router-link>
        </nav>

        <!-- Right: Compact Search + Auth + Wishlist + Cart -->
        <div class="flex items-center gap-1.5 sm:gap-2 flex-1 lg:flex-initial justify-end">

          <!-- Search Bar (Frosted glass inside luxury header) -->
          <div class="hidden md:flex flex-1 max-w-[240px] xl:max-w-[280px] relative mr-1" ref="searchContainerRef">
            <form @submit.prevent="handleSearch" class="relative w-full">
              <input
                type="text"
                v-model="searchKeyword"
                @focus="onSearchFocus"
                @input="onSearchInput"
                @keydown.esc="isSearchOpen = false"
                placeholder="Tìm kiếm sản phẩm..."
                class="w-full pl-9 pr-8 py-2 rounded-lg border border-white/25 bg-white/15 focus:bg-white/20 focus:outline-none focus:ring-1 focus:ring-[#F3D078] focus:border-[#F3D078] text-xs text-white placeholder:text-white/75 transition-all"
              />
              <button type="submit" class="absolute left-2.5 top-1/2 -translate-y-1/2 text-white/80 hover:text-[#F3D078]" aria-label="Tìm kiếm">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </button>
              <button
                v-if="searchKeyword"
                type="button"
                @click="clearSearch"
                class="absolute right-2.5 top-1/2 -translate-y-1/2 text-white/80 hover:text-white text-xs font-bold"
              >
                ✕
              </button>
            </form>

            <!-- Instant Search Dropdown (Clean White) -->
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
                class="absolute top-full right-0 w-80 sm:w-96 mt-2 bg-white text-[#18181B] rounded-xl shadow-xl border border-neutral-200/80 overflow-hidden z-50 divide-y divide-neutral-100"
              >
                <div v-if="isSearching" class="p-4 text-center text-xs text-[#6B7280] flex items-center justify-center gap-2">
                  <span class="w-3.5 h-3.5 border-2 border-[#7A1032] border-t-transparent rounded-full animate-spin"></span>
                  <span>Đang tìm kiếm sản phẩm...</span>
                </div>

                <div v-else-if="searchResults.length === 0" class="p-5 text-center text-xs text-[#6B7280]">
                  Không tìm thấy sản phẩm nào khớp với "<strong class="text-[#18181B]">{{ searchKeyword }}</strong>"
                </div>

                <div v-else class="max-h-80 overflow-y-auto divide-y divide-neutral-100">
                  <div
                    v-for="product in searchResults"
                    :key="product.id"
                    @click="selectSearchResult(product)"
                    class="p-2.5 hover:bg-neutral-50 cursor-pointer flex items-center gap-3 transition-colors group"
                  >
                    <img
                      :src="product.thumbnailUrl"
                      :alt="product.name"
                      @error="onImageError"
                      class="w-11 aspect-[3/4] object-cover rounded-lg bg-neutral-100 flex-shrink-0 border border-neutral-200/60"
                      loading="lazy"
                    />
                    <div class="flex-1 min-w-0">
                      <span class="text-[10px] font-semibold text-[#6B7280] uppercase tracking-wider block">
                        {{ product.categoryName || 'Thời Trang' }}
                      </span>
                      <h4 class="text-xs font-bold text-[#18181B] truncate group-hover:text-[#7A1032]">
                        {{ product.name }}
                      </h4>
                      <div class="flex items-center gap-2 mt-0.5">
                        <span class="text-xs font-extrabold text-[#7A1032]">
                          {{ formatVND(product.salePrice || product.basePrice) }}
                        </span>
                        <span
                          v-if="product.salePrice && product.basePrice > product.salePrice"
                          class="text-[10px] text-[#6B7280]/70 line-through"
                        >
                          {{ formatVND(product.basePrice) }}
                        </span>
                        <span
                          v-if="product.isFlashSale"
                          class="text-[9px] font-bold bg-[#7A1032] text-white px-1.5 py-0.5 rounded uppercase"
                        >
                          SALE
                        </span>
                      </div>
                    </div>
                    <span class="text-neutral-300 group-hover:text-[#7A1032] text-sm pr-1">→</span>
                  </div>
                </div>

                <div v-if="searchResults.length > 0" class="p-2.5 bg-neutral-50 text-center">
                  <button
                    type="button"
                    @click="handleSearch"
                    class="text-xs font-bold text-[#7A1032] hover:underline"
                  >
                    Xem tất cả kết quả cho "{{ searchKeyword }}" →
                  </button>
                </div>
              </div>
            </transition>
          </div>
          
          <!-- Auth Menu -->
          <div v-if="authStore.isAuthenticated" class="relative" ref="userMenuRef">
            <button
              type="button"
              @click.stop="isUserMenuOpen = !isUserMenuOpen"
              class="min-h-[44px] flex items-center gap-2 px-2.5 rounded-lg hover:bg-white/15 transition-colors text-xs font-semibold text-[#FFFDF9]"
              :aria-expanded="isUserMenuOpen"
            >
              <span class="w-7 h-7 rounded-full bg-white/15 border border-[#F3D078]/50 text-[#F3D078] flex items-center justify-center font-bold text-[11px]">
                {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
              </span>
              <span class="hidden sm:inline max-w-[92px] truncate">{{ authStore.user?.fullName }}</span>
              <svg class="w-3.5 h-3.5 text-white/75 hidden sm:block transition-transform" :class="{ 'rotate-180': isUserMenuOpen }" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- Dropdown (Clean White) -->
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
                class="absolute right-0 mt-2 w-56 bg-white text-[#18181B] rounded-xl shadow-xl border border-neutral-200/80 py-1.5 z-50"
              >
                <div class="px-4 py-2.5 border-b border-neutral-100 text-xs">
                  <p class="font-bold text-[#18181B] truncate">{{ authStore.user?.fullName }}</p>
                  <p class="text-[#6B7280] truncate text-[11px]">{{ authStore.user?.email }}</p>
                </div>
                <router-link
                  v-if="authStore.isAdmin"
                  to="/admin"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs font-bold text-[#7A1032] hover:bg-neutral-50"
                >
                  Quản trị hệ thống (Admin)
                </router-link>
                <router-link
                  to="/profile"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs text-[#18181B] hover:bg-neutral-50 font-medium"
                >
                  Đơn hàng & Tài khoản
                </router-link>
                <router-link
                  to="/order-tracking"
                  @click="isUserMenuOpen = false"
                  class="block px-4 py-2.5 text-xs text-[#6B7280] hover:text-[#18181B] hover:bg-neutral-50"
                >
                  Tra cứu vận đơn
                </router-link>
                <button
                  type="button"
                  @click="handleLogout"
                  class="w-full text-left px-4 py-2.5 text-xs text-[#7A1032] hover:bg-rose-50 border-t border-neutral-100 font-semibold"
                >
                  Đăng xuất
                </button>
              </div>
            </transition>
          </div>

          <div v-else class="flex items-center shrink-0">
            <router-link
              to="/login"
              class="min-w-[44px] min-h-[44px] inline-flex items-center justify-center text-xs font-bold text-[#FFFDF9] hover:text-[#F3D078] px-2 min-[420px]:px-3 rounded-lg hover:bg-white/15 transition-colors whitespace-nowrap shrink-0"
              aria-label="Đăng nhập"
              title="Đăng nhập"
            >
              <svg class="w-5 h-5 min-[420px]:hidden" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span class="hidden min-[420px]:inline">Đăng nhập</span>
            </router-link>
          </div>

          <!-- Wishlist Popover Button -->
          <button
            v-if="route.path !== '/wishlist'"
            ref="wishlistTriggerRef"
            type="button"
            @click="handleOpenWishlist"
            class="relative min-w-[44px] min-h-[44px] rounded-lg hover:bg-white/15 text-[#FFFDF9] hover:text-[#F3D078] transition-colors flex items-center justify-center"
            aria-label="Yêu thích"
            title="Danh sách yêu thích"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span
              v-if="authStore.isAuthenticated && wishlistStore.totalWishlist > 0"
              class="absolute top-1 right-1 bg-[#F3D078] text-[#1E0930] text-[10px] font-extrabold min-w-[18px] h-[18px] px-1 rounded-full flex items-center justify-center shadow-sm"
            >
              {{ wishlistStore.totalWishlist }}
            </span>
          </button>

          <!-- Cart Popover Button -->
          <button
            v-if="route.path !== '/cart'"
            ref="cartTriggerRef"
            type="button"
            @click="handleToggleCart"
            class="relative min-w-[44px] min-h-[44px] rounded-lg hover:bg-white/15 text-[#FFFDF9] hover:text-[#F3D078] transition-colors flex items-center justify-center"
            aria-label="Giỏ hàng"
            title="Giỏ hàng"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span
              v-if="cartStore.totalItems > 0"
              class="absolute top-1 right-1 bg-white text-[#5C0B27] text-[10px] font-extrabold min-w-[18px] h-[18px] px-1 rounded-full flex items-center justify-center shadow-sm"
            >
              {{ cartStore.totalItems }}
            </span>
          </button>

        </div>

      </div>
    </div>

    <!-- MOBILE NAVIGATION DRAWER (< lg) — Clean White -->
    <Teleport to="body">
      <Transition name="drawer-left" :duration="380">
        <div v-if="isMobileMenuOpen" class="fixed inset-0 z-[9990] lg:hidden flex no-print" role="dialog" aria-modal="true">
          <div class="drawer-backdrop fixed inset-0 z-[9990] bg-black/45 backdrop-blur-[2px]" @click="isMobileMenuOpen = false"></div>

          <div class="drawer-panel relative z-[9995] w-[86vw] max-w-sm bg-white h-screen shadow-2xl flex flex-col overflow-hidden text-[#18181B]">
            <!-- Drawer Top -->
            <div class="px-5 py-4 luxury-gradient text-[#FFFDF9] flex items-center justify-between">
              <router-link to="/" @click="isMobileMenuOpen = false" class="flex items-center gap-2.5">
                <div class="w-8 h-8 rounded-lg bg-white/15 border border-[#F3D078]/50 flex items-center justify-center text-[#F3D078] font-extrabold text-sm">
                  F
                </div>
                <span class="text-base font-extrabold text-[#FFFDF9] tracking-tight">FASHION<span class="text-[#F3D078]">.VN</span></span>
              </router-link>
              <button
                type="button"
                @click="isMobileMenuOpen = false"
                class="min-w-[44px] min-h-[44px] -mr-2 rounded-lg text-white/80 hover:text-white hover:bg-white/15 transition-colors flex items-center justify-center"
                aria-label="Đóng menu"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- Mobile Search Bar -->
            <div class="p-4 border-b border-neutral-200/80 bg-neutral-50/70">
              <form @submit.prevent="handleMobileSearch" class="relative">
                <input
                  type="text"
                  v-model="searchKeyword"
                  @input="onSearchInput"
                  placeholder="Tìm áo thun, sơ mi, quần jean..."
                  class="w-full min-h-[44px] pl-10 pr-9 py-2.5 rounded-lg border border-neutral-200 bg-white focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] text-xs text-[#18181B]"
                />
                <button type="submit" class="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </button>
                <button
                  v-if="searchKeyword"
                  type="button"
                  @click="clearSearch"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400 text-xs font-bold"
                >
                  ✕
                </button>
              </form>

              <div v-if="searchKeyword.trim().length > 0" class="mt-2 bg-white rounded-xl border border-neutral-200/80 max-h-56 overflow-y-auto divide-y divide-neutral-100">
                <div v-if="isSearching" class="p-3 text-center text-[11px] text-[#6B7280]">
                  Đang tìm kiếm...
                </div>
                <div v-else-if="searchResults.length === 0" class="p-3 text-center text-[11px] text-[#6B7280]">
                  Không tìm thấy sản phẩm khớp "{{ searchKeyword }}"
                </div>
                <div
                  v-else
                  v-for="product in searchResults"
                  :key="product.id"
                  @click="selectMobileSearchResult(product)"
                  class="p-2.5 flex items-center gap-3 hover:bg-neutral-50 cursor-pointer"
                >
                  <img
                    :src="product.thumbnailUrl"
                    :alt="product.name"
                    @error="onImageError"
                    class="w-10 aspect-[3/4] object-cover rounded-lg bg-neutral-100 shrink-0"
                  />
                  <div class="min-w-0 flex-1">
                    <div class="text-xs font-bold text-[#18181B] truncate">{{ product.name }}</div>
                    <div class="text-[11px] font-bold text-[#7A1032]">{{ formatVND(product.salePrice || product.basePrice) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Scrollable Menu Body -->
            <div class="flex-1 overflow-y-auto p-4 space-y-5">
              <nav class="space-y-1 text-sm font-semibold text-[#18181B]">
                <router-link to="/" @click="isMobileMenuOpen = false" class="min-h-[44px] flex items-center gap-3 px-3 rounded-lg hover:bg-neutral-100">
                  Trang Chủ
                </router-link>
                <router-link to="/catalog" @click="isMobileMenuOpen = false" class="min-h-[44px] flex items-center gap-3 px-3 rounded-lg hover:bg-neutral-100">
                  Tất Cả Sản Phẩm
                </router-link>
                <router-link to="/catalog?isFlashSale=true" @click="isMobileMenuOpen = false" class="min-h-[44px] flex items-center gap-2 px-3 rounded-lg bg-rose-50 text-[#7A1032] font-bold">
                  <span>⚡</span> Flash Sale Ưu Đãi
                </router-link>
                <router-link
                  v-if="authStore.isAuthenticated"
                  to="/order-tracking"
                  @click="isMobileMenuOpen = false"
                  class="min-h-[44px] flex items-center gap-3 px-3 rounded-lg hover:bg-neutral-100"
                >
                  Tra Cứu Đơn Hàng
                </router-link>
                <router-link
                  v-if="authStore.isAuthenticated && route.path !== '/wishlist'"
                  to="/wishlist"
                  @click="isMobileMenuOpen = false"
                  class="min-h-[44px] flex items-center justify-between px-3 rounded-lg hover:bg-neutral-100"
                >
                  <span>Danh Sách Yêu Thích</span>
                  <span class="text-xs font-bold text-[#7A1032]">{{ wishlistStore.totalWishlist }}</span>
                </router-link>
                <router-link
                  v-if="route.path !== '/cart'"
                  to="/cart"
                  @click="isMobileMenuOpen = false"
                  class="min-h-[44px] flex items-center justify-between px-3 rounded-lg hover:bg-neutral-100"
                >
                  <span>Giỏ Hàng</span>
                  <span class="text-xs font-bold text-[#18181B]">{{ cartStore.totalItems }}</span>
                </router-link>
              </nav>

              <div v-if="mobileCategories.length > 0" class="pt-4 border-t border-neutral-200/80">
                <p class="px-3 text-[10px] font-bold uppercase tracking-[0.14em] text-[#6B7280] mb-2">Danh Mục Thời Trang</p>
                <div class="space-y-0.5">
                  <router-link
                    v-for="cat in mobileCategories"
                    :key="cat.id"
                    :to="`/catalog?categorySlug=${cat.slug}`"
                    @click="isMobileMenuOpen = false"
                    class="min-h-[44px] flex items-center justify-between px-3 rounded-lg text-xs font-medium text-[#6B7280] hover:bg-neutral-100 hover:text-[#18181B]"
                  >
                    <span>{{ cat.name }}</span>
                    <span class="text-[11px] text-neutral-400">{{ cat.productCount || 0 }}</span>
                  </router-link>
                </div>
              </div>
            </div>

            <!-- Drawer Footer: Account -->
            <div class="p-4 border-t border-neutral-200/80 bg-neutral-50">
              <div v-if="authStore.isAuthenticated" class="space-y-3">
                <div class="flex items-center gap-2.5">
                  <span class="w-9 h-9 rounded-full bg-[#7A1032] text-white flex items-center justify-center font-bold text-xs shrink-0">
                    {{ (authStore.user?.fullName || 'U').charAt(0).toUpperCase() }}
                  </span>
                  <div class="min-w-0">
                    <p class="text-xs font-bold text-[#18181B] truncate">{{ authStore.user?.fullName }}</p>
                    <p class="text-[11px] text-[#6B7280] truncate">{{ authStore.user?.email }}</p>
                  </div>
                </div>
                <div class="grid grid-cols-2 gap-2">
                  <router-link
                    to="/profile"
                    @click="isMobileMenuOpen = false"
                    class="min-h-[44px] flex items-center justify-center px-3 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold transition-colors"
                  >
                    Tài khoản
                  </router-link>
                  <router-link
                    v-if="authStore.isAdmin"
                    to="/admin"
                    @click="isMobileMenuOpen = false"
                    class="min-h-[44px] flex items-center justify-center px-3 rounded-lg border border-neutral-300 bg-white text-[#18181B] text-xs font-bold"
                  >
                    Quản trị
                  </router-link>
                  <button
                    type="button"
                    @click="handleLogout"
                    :class="authStore.isAdmin ? 'col-span-2' : ''"
                    class="min-h-[44px] flex items-center justify-center px-3 rounded-lg border border-neutral-200 bg-white hover:bg-rose-50 text-[#7A1032] text-xs font-bold transition-colors"
                  >
                    Đăng xuất
                  </button>
                </div>
              </div>
              <div v-else class="grid grid-cols-2 gap-3">
                <router-link
                  to="/login"
                  @click="isMobileMenuOpen = false"
                  class="min-h-[44px] flex items-center justify-center rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold transition-colors"
                >
                  Đăng nhập
                </router-link>
                <router-link
                  to="/register"
                  @click="isMobileMenuOpen = false"
                  class="min-h-[44px] flex items-center justify-center rounded-lg border border-neutral-300 bg-white text-[#18181B] text-xs font-bold"
                >
                  Đăng ký
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- CART POPOVER — Clean White Luxury (No backdrop, free page scroll) -->
    <Teleport to="body">
      <Transition name="fs-popover">
        <div
          v-if="cartStore.isDrawerOpen"
          ref="cartPopoverRef"
          role="dialog"
          aria-modal="false"
          class="fixed top-[74px] right-4 sm:right-6 z-[9995] w-[calc(100vw-2rem)] sm:w-[400px] max-h-[calc(100vh-100px)] flex flex-col rounded-2xl bg-white border border-stone-200/90 shadow-[0_20px_50px_-12px_rgba(24,24,27,0.22),0_0_0_1px_rgba(24,24,27,0.04)] overflow-hidden text-[#18181B] no-print"
        >
          <!-- Popover Header (Clean White Minimal) -->
          <div class="bg-white border-b border-stone-100 px-5 py-4 flex items-center justify-between gap-3 shrink-0">
            <div>
              <h2 class="text-sm font-extrabold uppercase tracking-wider text-stone-900">
                Giỏ Hàng ({{ cartStore.totalItems }})
              </h2>
              <p class="text-xs text-stone-500 mt-0.5">Kiểm tra sản phẩm trước khi thanh toán</p>
            </div>
            <button
              type="button"
              @click="cartStore.toggleDrawer(false)"
              class="w-8 h-8 rounded-full text-stone-400 hover:text-stone-800 hover:bg-stone-100 flex items-center justify-center transition-colors shrink-0"
              aria-label="Đóng giỏ hàng"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Free Shipping Bar -->
          <div class="px-5 py-3 bg-[#FAF9F8] border-b border-stone-100 shrink-0">
            <FreeShippingBar />
          </div>

          <!-- Popover Items List -->
          <div class="flex-1 min-h-0 overflow-y-auto p-4 space-y-3 bg-[#FAF9F8]">
            <div v-if="cartStore.items.length === 0" class="py-14 text-center space-y-3">
              <p class="text-stone-900 font-semibold text-sm">Giỏ hàng của bạn đang trống</p>
              <p class="text-xs text-stone-500">Khám phá các thiết kế mới nhất của FashionStore</p>
              <router-link
                to="/catalog"
                @click="cartStore.toggleDrawer(false)"
                class="btn-sheen inline-flex items-center justify-center min-h-[42px] mt-2 px-6 bg-[#5C0B27] hover:bg-[#4A0820] text-white font-bold text-xs rounded-xl shadow-md transition-colors"
              >
                Khám phá cửa hàng
              </router-link>
            </div>

            <div
              v-for="item in cartStore.items"
              :key="item.variantId"
              class="flex gap-3 p-3 bg-white rounded-xl border border-stone-200/80 shadow-sm hover:border-[#8F5A32]/40 hover:shadow-md transition-all duration-300"
            >
              <img
                :src="item.thumbnailUrl"
                :alt="item.productName"
                @error="onImageError"
                class="w-18 h-22 sm:w-20 sm:h-24 object-cover rounded-xl bg-neutral-100 flex-shrink-0 border border-stone-100"
              />
              <div class="flex-1 min-w-0 flex flex-col justify-between">
                <div>
                  <div class="flex justify-between items-start gap-2">
                    <router-link
                      :to="`/product/${item.productSlug}`"
                      @click="cartStore.toggleDrawer(false)"
                      class="text-xs sm:text-[13px] font-semibold text-stone-900 hover:text-[#5C0B27] line-clamp-2 leading-snug transition-colors"
                    >
                      {{ item.productName }}
                    </router-link>
                    <button
                      type="button"
                      @click="cartStore.removeFromCart(item.variantId)"
                      class="p-1.5 -mt-1 -mr-1 rounded-full text-stone-400 hover:text-[#5C0B27] hover:bg-stone-100 transition-colors shrink-0"
                      title="Xóa sản phẩm"
                      aria-label="Xóa sản phẩm"
                    >
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                  <div class="flex items-center gap-2 mt-1.5 text-[11px] text-stone-500">
                    <span class="bg-stone-100 text-stone-800 px-2 py-0.5 rounded font-semibold">Size {{ item.size }}</span>
                    <span class="flex items-center gap-1 truncate">
                      <span class="w-2.5 h-2.5 rounded-full border border-stone-300 shrink-0" :style="{ backgroundColor: item.colorHex }"></span>
                      <span class="truncate">{{ item.colorName }}</span>
                    </span>
                  </div>
                </div>

                <div class="flex justify-between items-center mt-2.5 gap-2">
                  <div class="text-sm font-bold text-[#7A1032]">
                    {{ formatVND(item.price) }}
                  </div>
                  <!-- Quantity Stepper -->
                  <div class="flex items-center h-8 border border-stone-200 rounded-lg overflow-hidden bg-white">
                    <button
                      type="button"
                      @click="cartStore.updateQuantity(item.variantId, item.quantity - 1)"
                      class="w-8 h-8 flex items-center justify-center text-stone-800 hover:bg-stone-100 text-sm font-bold transition-colors"
                    >-</button>
                    <span class="w-8 text-center text-xs font-bold text-stone-900">{{ item.quantity }}</span>
                    <button
                      type="button"
                      @click="cartStore.updateQuantity(item.variantId, item.quantity + 1)"
                      :disabled="item.quantity >= item.stockQuantity"
                      class="w-8 h-8 flex items-center justify-center text-stone-800 hover:bg-stone-100 text-sm font-bold disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                    >+</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Popover Footer -->
          <div v-if="cartStore.items.length > 0" class="p-4 sm:p-5 bg-white border-t border-stone-100 shadow-[0_-8px_24px_-12px_rgba(24,24,27,0.08)] space-y-2 shrink-0">
            <div class="flex justify-between text-xs text-stone-500">
              <span>Tạm tính:</span>
              <span class="font-semibold text-stone-900">{{ formatVND(cartStore.subTotal) }}</span>
            </div>
            <div class="flex justify-between text-xs text-stone-500">
              <span>Phí vận chuyển:</span>
              <span v-if="cartStore.isFreeShip" class="font-bold text-emerald-700">Miễn phí</span>
              <span v-else class="font-semibold text-stone-900">{{ formatVND(cartStore.shippingFee) }}</span>
            </div>

            <div class="pt-2 border-t border-stone-100 flex justify-between items-baseline">
              <span class="text-xs font-bold uppercase tracking-wider text-stone-900">Tổng cộng</span>
              <span class="text-lg font-extrabold text-[#7A1032]">{{ formatVND(cartStore.totalAmount) }}</span>
            </div>

            <div class="grid grid-cols-2 gap-2.5 pt-1.5">
              <router-link
                to="/cart"
                @click="cartStore.toggleDrawer(false)"
                class="py-2.5 px-3.5 flex items-center justify-center rounded-xl border border-stone-200 bg-white hover:bg-stone-50 text-stone-800 text-xs font-bold transition-colors"
              >
                Xem Giỏ Hàng
              </router-link>
              <button
                type="button"
                @click="handleProceedToCheckout"
                class="btn-sheen active:scale-[0.98] py-2.5 px-3.5 flex items-center justify-center rounded-xl bg-[#5C0B27] hover:bg-[#4A0820] text-white text-xs font-bold shadow-md transition-all"
              >
                Thanh Toán →
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- WISHLIST POPOVER — Clean White Luxury (No backdrop, free page scroll) -->
    <Teleport to="body">
      <Transition name="fs-popover">
        <div
          v-if="isWishlistOpen"
          ref="wishlistPopoverRef"
          role="dialog"
          aria-modal="false"
          class="fixed top-[74px] right-4 sm:right-6 z-[9995] w-[calc(100vw-2rem)] sm:w-[400px] max-h-[calc(100vh-100px)] flex flex-col rounded-2xl bg-white border border-stone-200/90 shadow-[0_20px_50px_-12px_rgba(24,24,27,0.22),0_0_0_1px_rgba(24,24,27,0.04)] overflow-hidden text-[#18181B] no-print"
        >
          <!-- Popover Header (Clean White Minimal) -->
          <div class="bg-white border-b border-stone-100 px-5 py-4 flex items-center justify-between gap-3 shrink-0">
            <div>
              <h2 class="text-sm font-extrabold uppercase tracking-wider text-stone-900">
                Yêu Thích ({{ wishlistStore.totalWishlist }})
              </h2>
              <p class="text-xs text-stone-500 mt-0.5">Các sản phẩm bạn đã lưu lại</p>
            </div>
            <button
              type="button"
              @click="isWishlistOpen = false"
              class="w-8 h-8 rounded-full text-stone-400 hover:text-stone-800 hover:bg-stone-100 flex items-center justify-center transition-colors shrink-0"
              aria-label="Đóng yêu thích"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Popover Body -->
          <div class="flex-1 min-h-0 overflow-y-auto p-4 space-y-3 bg-[#FAF9F8]">
            <div v-if="wishlistStore.items.length === 0" class="py-14 text-center space-y-3">
              <p class="text-stone-900 font-semibold text-sm">Chưa có sản phẩm yêu thích nào</p>
              <p class="text-xs text-stone-500">Nhấn biểu tượng trái tim để lưu mẫu bạn ưng ý</p>
              <router-link
                to="/catalog"
                @click="isWishlistOpen = false"
                class="btn-sheen inline-flex items-center justify-center min-h-[42px] mt-2 px-6 bg-[#5C0B27] hover:bg-[#4A0820] text-white font-bold text-xs rounded-xl shadow-md transition-colors"
              >
                Khám phá ngay
              </router-link>
            </div>

            <div
              v-for="item in wishlistStore.items"
              :key="item.id"
              class="flex gap-3 p-3 bg-white rounded-xl border border-stone-200/80 shadow-sm hover:border-[#8F5A32]/40 hover:shadow-md transition-all duration-300 group"
            >
              <router-link :to="`/product/${item.slug}`" @click="isWishlistOpen = false" class="flex-shrink-0">
                <img
                  :src="item.thumbnailUrl"
                  :alt="item.name"
                  @error="onImageError"
                  class="w-18 h-22 sm:w-20 sm:h-24 object-cover rounded-xl bg-neutral-100 border border-stone-100 group-hover:scale-[1.03] transition-transform duration-500"
                />
              </router-link>

              <div class="flex-1 min-w-0 flex flex-col justify-between">
                <div>
                  <div class="flex justify-between items-start gap-2">
                    <router-link
                      :to="`/product/${item.slug}`"
                      @click="isWishlistOpen = false"
                      class="text-xs sm:text-[13px] font-semibold text-stone-900 hover:text-[#5C0B27] line-clamp-2 leading-snug transition-colors"
                    >
                      {{ item.name }}
                    </router-link>
                    <button
                      type="button"
                      @click="wishlistStore.removeFromWishlist(item.id)"
                      class="p-1.5 -mt-1 -mr-1 rounded-full text-stone-400 hover:text-[#5C0B27] hover:bg-stone-100 transition-colors shrink-0"
                      title="Xóa khỏi yêu thích"
                      aria-label="Xóa khỏi yêu thích"
                    >
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                  <span class="text-[11px] text-stone-500 uppercase tracking-wider font-medium block mt-1">
                    {{ item.categoryName || 'Thời Trang' }}
                  </span>
                </div>

                <div class="flex justify-between items-center mt-2.5 gap-2">
                  <div class="text-sm font-bold text-[#7A1032]">
                    {{ formatVND(item.salePrice || item.basePrice) }}
                  </div>
                  <button
                    type="button"
                    @click="openQuickViewFromWishlist(item)"
                    class="h-8 px-3 rounded-lg bg-[#5C0B27] hover:bg-[#4A0820] text-white text-xs font-semibold transition-colors shrink-0"
                  >
                    Thêm vào giỏ
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Popover Footer -->
          <div v-if="wishlistStore.items.length > 0" class="p-4 sm:p-5 bg-white border-t border-stone-100 shadow-[0_-8px_24px_-12px_rgba(24,24,27,0.08)] grid grid-cols-2 gap-2.5 shrink-0">
            <button
              type="button"
              @click="wishlistStore.clearWishlist"
              class="py-2.5 px-3.5 flex items-center justify-center rounded-xl border border-stone-200 bg-white text-stone-800 hover:bg-stone-50 text-xs font-bold transition-colors"
            >
              Xóa tất cả
            </button>
            <router-link
              to="/wishlist"
              @click="isWishlistOpen = false"
              class="btn-sheen active:scale-[0.98] py-2.5 px-3.5 flex items-center justify-center rounded-xl bg-[#5C0B27] hover:bg-[#4A0820] text-white text-xs font-bold shadow-md transition-all text-center"
            >
              Xem trang Yêu thích →
            </router-link>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Inset Sparkling Star Line at Bottom Edge of Header (8px inset above bottom edge) -->
    <div class="absolute left-0 right-0 bottom-[8px] z-20 pointer-events-none">
      <SparkleDivider />
    </div>
  </header>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import { useCartStore } from '@/stores/cartStore';
import { useWishlistStore } from '@/stores/wishlistStore';
import { useProductStore } from '@/stores/productStore';
import { useToastStore } from '@/stores/toastStore';
import { formatVND, onImageError } from '@/services/formatters';
import FreeShippingBar from '@/components/FreeShippingBar.vue';
import StarfieldOverlay from '@/components/StarfieldOverlay.vue';
import SparkleDivider from '@/components/SparkleDivider.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const cartStore = useCartStore();
const wishlistStore = useWishlistStore();
const productStore = useProductStore();
const toastStore = useToastStore();

const searchKeyword = ref('');
const isSearchOpen = ref(false);
const isSearching = ref(false);
const searchResults = ref([]);
const searchContainerRef = ref(null);
const userMenuRef = ref(null);
const cartPopoverRef = ref(null);
const cartTriggerRef = ref(null);
const wishlistPopoverRef = ref(null);
const wishlistTriggerRef = ref(null);
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

function handleOpenWishlist() {
  if (!authStore.isAuthenticated) {
    toastStore.info('Vui lòng đăng nhập để sử dụng danh sách yêu thích');
    router.push({ name: 'login', query: { redirect: '/wishlist' } });
    return;
  }
  const next = !isWishlistOpen.value;
  if (next) cartStore.toggleDrawer(false);
  isWishlistOpen.value = next;
}

function handleToggleCart() {
  const next = !cartStore.isDrawerOpen;
  if (next) isWishlistOpen.value = false;
  cartStore.toggleDrawer(next);
}

function handleProceedToCheckout() {
  cartStore.toggleDrawer(false);
  if (!authStore.isAuthenticated) {
    toastStore.info('Vui lòng đăng nhập để tiến hành đặt hàng');
    router.push({ name: 'login', query: { redirect: '/checkout' } });
    return;
  }
  router.push('/checkout');
}

function handleLogout() {
  isUserMenuOpen.value = false;
  isMobileMenuOpen.value = false;
  isWishlistOpen.value = false;
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

function handlePopoverPointerDown(event) {
  const target = event.target;
  if (
    cartStore.isDrawerOpen &&
    cartPopoverRef.value &&
    !cartPopoverRef.value.contains(target) &&
    !(cartTriggerRef.value && cartTriggerRef.value.contains(target))
  ) {
    cartStore.toggleDrawer(false);
  }
  if (
    isWishlistOpen.value &&
    wishlistPopoverRef.value &&
    !wishlistPopoverRef.value.contains(target) &&
    !(wishlistTriggerRef.value && wishlistTriggerRef.value.contains(target))
  ) {
    isWishlistOpen.value = false;
  }
}

function handleKeydown(event) {
  if (event.key === 'Escape') {
    if (cartStore.isDrawerOpen) cartStore.toggleDrawer(false);
    if (isWishlistOpen.value) isWishlistOpen.value = false;
    if (isSearchOpen.value) isSearchOpen.value = false;
    if (isUserMenuOpen.value) isUserMenuOpen.value = false;
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

watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/cart') {
      cartStore.toggleDrawer(false);
    }
    if (newPath === '/wishlist') {
      isWishlistOpen.value = false;
    }
  },
  { immediate: true }
);

watch(
  () => isMobileMenuOpen.value,
  (isMobileOpen) => {
    document.body.style.overflow = isMobileOpen ? 'hidden' : '';
    document.documentElement.style.overflow = isMobileOpen ? 'hidden' : '';
  },
  { immediate: true }
);

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  document.addEventListener('pointerdown', handlePopoverPointerDown);
  document.addEventListener('keydown', handleKeydown);
  loadMobileCategories();
});

onUnmounted(() => {
  document.body.style.overflow = '';
  document.documentElement.style.overflow = '';
  document.removeEventListener('click', handleClickOutside);
  document.removeEventListener('pointerdown', handlePopoverPointerDown);
  document.removeEventListener('keydown', handleKeydown);
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
});
</script>
