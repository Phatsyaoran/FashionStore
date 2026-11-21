<template>
  <div class="min-h-screen bg-[#F8FAFC] text-slate-900 flex">
    <!-- Desktop Sidebar (>= 1024px) -->
    <aside class="hidden lg:flex lg:flex-col lg:w-64 lg:fixed lg:inset-y-0 bg-white border-r border-slate-200 z-30 no-print">
      <!-- Brand -->
      <div class="h-16 px-5 flex items-center justify-between border-b border-slate-200 shrink-0">
        <router-link to="/admin" class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-lg bg-slate-900 text-white flex items-center justify-center font-extrabold text-sm">
            F
          </div>
          <div class="leading-tight">
            <div class="text-sm font-bold tracking-tight text-slate-900">FashionStore</div>
            <div class="text-[10px] font-semibold uppercase tracking-widest text-slate-400">Admin Console</div>
          </div>
        </router-link>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 overflow-y-auto p-3 space-y-1">
        <div class="px-3 pt-2 pb-1.5 text-[10px] font-bold uppercase tracking-widest text-slate-400">
          Quản trị vận hành
        </div>
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-xs font-semibold transition-all"
          :class="isActive(item.to)
            ? 'bg-slate-900 text-white shadow-sm'
            : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'"
        >
          <span class="w-4 h-4 flex items-center justify-center shrink-0" v-html="item.icon"></span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- Bottom Actions -->
      <div class="p-3 border-t border-slate-200 space-y-1 shrink-0">
        <router-link
          to="/"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-xs font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
        >
          <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
          <span>Xem cửa hàng</span>
        </router-link>
        <button
          type="button"
          @click="handleLogout"
          class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-xs font-medium text-red-600 hover:bg-red-50 transition-colors"
        >
          <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          <span>Đăng xuất</span>
        </button>
      </div>
    </aside>

    <!-- Main Column -->
    <div class="flex-1 flex flex-col min-w-0 lg:pl-64">
      <!-- Topbar -->
      <header class="h-16 bg-white/95 backdrop-blur-md border-b border-slate-200 sticky top-0 z-30 px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-4 no-print">
        <div class="flex items-center gap-3 min-w-0">
          <!-- Mobile/Tablet Hamburger (< 1024px) -->
          <button
            type="button"
            @click="isMobileSidebarOpen = true"
            class="lg:hidden inline-flex items-center justify-center w-9 h-9 rounded-lg border border-slate-200 bg-white text-slate-700 hover:bg-slate-50 transition-colors shrink-0"
            aria-label="Mở menu quản trị"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <div class="min-w-0">
            <div class="text-sm font-bold text-slate-900 truncate">{{ currentPageTitle }}</div>
            <div class="hidden sm:block text-[11px] text-slate-400">FashionStore Management System</div>
          </div>
        </div>

        <div class="flex items-center gap-3 shrink-0">
          <router-link
            to="/"
            class="hidden sm:inline-flex items-center gap-1.5 h-8 px-3 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 text-xs font-medium text-slate-700 transition-colors"
          >
            <span>Cửa hàng</span>
            <span class="text-slate-400">↗</span>
          </router-link>

          <div class="flex items-center gap-2.5 pl-3 border-l border-slate-200">
            <div class="w-8 h-8 rounded-full bg-slate-900 text-white text-xs font-bold flex items-center justify-center shrink-0">
              {{ (authStore.user?.fullName || 'A').charAt(0).toUpperCase() }}
            </div>
            <div class="hidden md:block leading-tight">
              <div class="text-xs font-semibold text-slate-900 max-w-[140px] truncate">
                {{ authStore.user?.fullName || 'Administrator' }}
              </div>
              <div class="text-[10px] text-slate-400">Quản trị viên</div>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 p-4 sm:p-6 lg:p-8 max-w-[1440px] w-full mx-auto">
        <slot />
      </main>
    </div>

    <!-- Mobile & Tablet Slide-over Sidebar Drawer (< 1024px) -->
    <Teleport to="body">
      <div
        v-if="isMobileSidebarOpen"
        class="fixed inset-0 z-[9990] lg:hidden no-print"
        role="dialog"
        aria-modal="true"
      >
        <!-- Backdrop -->
        <div
          class="fixed inset-0 z-[9990] bg-slate-900/45 backdrop-blur-[2px] transition-opacity"
          @click="isMobileSidebarOpen = false"
        ></div>

        <!-- Drawer Panel -->
        <div class="fixed inset-y-0 left-0 z-[9995] w-72 max-w-[85vw] h-screen bg-white border-r border-slate-200 shadow-2xl flex flex-col">
          <div class="h-16 px-5 flex items-center justify-between border-b border-slate-200 shrink-0">
            <div class="flex items-center gap-2.5">
              <div class="w-8 h-8 rounded-lg bg-slate-900 text-white flex items-center justify-center font-extrabold text-sm">
                F
              </div>
              <div class="leading-tight">
                <div class="text-sm font-bold text-slate-900">FashionStore</div>
                <div class="text-[10px] font-semibold uppercase tracking-widest text-slate-400">Admin Console</div>
              </div>
            </div>
            <button
              type="button"
              @click="isMobileSidebarOpen = false"
              class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:bg-slate-100 hover:text-slate-900"
              aria-label="Đóng menu"
            >
              ✕
            </button>
          </div>

          <nav class="flex-1 overflow-y-auto p-3 space-y-1">
            <div class="px-3 pt-2 pb-1.5 text-[10px] font-bold uppercase tracking-widest text-slate-400">
              Quản trị vận hành
            </div>
            <router-link
              v-for="item in navItems"
              :key="item.to"
              :to="item.to"
              @click="isMobileSidebarOpen = false"
              class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-xs font-semibold transition-all"
              :class="isActive(item.to)
                ? 'bg-slate-900 text-white shadow-sm'
                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'"
            >
              <span class="w-4 h-4 flex items-center justify-center shrink-0" v-html="item.icon"></span>
              <span>{{ item.label }}</span>
            </router-link>
          </nav>

          <div class="p-3 border-t border-slate-200 space-y-1 shrink-0">
            <router-link
              to="/"
              @click="isMobileSidebarOpen = false"
              class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-xs font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-900"
            >
              <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              <span>Xem cửa hàng</span>
            </router-link>
            <button
              type="button"
              @click="handleLogout"
              class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-xs font-medium text-red-600 hover:bg-red-50"
            >
              <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              <span>Đăng xuất</span>
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isMobileSidebarOpen = ref(false);

const navItems = [
  {
    to: '/admin',
    label: 'Tổng quan',
    icon: '<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"/></svg>'
  },
  {
    to: '/admin/orders',
    label: 'Đơn hàng & Vận đơn',
    icon: '<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>'
  },
  {
    to: '/admin/products',
    label: 'Sản phẩm & Kho',
    icon: '<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>'
  },
  {
    to: '/admin/coupons',
    label: 'Mã giảm giá',
    icon: '<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"/></svg>'
  },
  {
    to: '/admin/categories',
    label: 'Danh mục',
    icon: '<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M3 7a2 2 0 012-2h4l2 2h8a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"/></svg>'
  }
];

function isActive(path) {
  if (path === '/admin') return route.path === '/admin';
  return route.path.startsWith(path);
}

const currentPageTitle = computed(() => {
  return route.meta?.title || 'Bảng điều khiển Quản trị';
});

function handleLogout() {
  isMobileSidebarOpen.value = false;
  authStore.logout();
  router.push('/login');
}

watch(isMobileSidebarOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : '';
});

watch(() => route.fullPath, () => {
  isMobileSidebarOpen.value = false;
});

onUnmounted(() => {
  document.body.style.overflow = '';
});
</script>
