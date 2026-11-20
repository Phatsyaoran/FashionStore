import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import { useAuthStore } from '@/stores/authStore';

function safeJsonParse(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

function normalizeProduct(p) {
  if (!p || p.id == null) return null;
  return {
    id: Number(p.id),
    name: p.name || 'Sản phẩm thời trang',
    slug: p.slug || '',
    thumbnailUrl: p.thumbnailUrl || '',
    basePrice: Number(p.basePrice || 0),
    salePrice: p.salePrice != null ? Number(p.salePrice) : null,
    categoryName: p.categoryName || 'Thời Trang',
    isFlashSale: !!p.isFlashSale,
    availableSizes: Array.isArray(p.availableSizes) ? p.availableSizes : ['M', 'L'],
    availableColors: Array.isArray(p.availableColors) ? p.availableColors : ['Tiêu chuẩn']
  };
}

export const useWishlistStore = defineStore('wishlist', () => {
  const authStore = useAuthStore();
  const items = ref([]);

  function getStorageKey(userId) {
    return userId ? `fashion_wishlist_user_${userId}` : null;
  }

  function loadForCurrentUser() {
    const userId = authStore.user?.id;
    if (!authStore.isAuthenticated || !userId) {
      items.value = [];
      return;
    }
    const key = getStorageKey(userId);
    const rawList = safeJsonParse(key, []);
    if (!Array.isArray(rawList)) {
      items.value = [];
      return;
    }
    const seen = new Set();
    const cleaned = [];
    for (const entry of rawList) {
      const normalized = normalizeProduct(entry);
      if (normalized && normalized.slug && !seen.has(normalized.id)) {
        seen.add(normalized.id);
        cleaned.push(normalized);
      }
    }
    items.value = cleaned;
  }

  function saveToLocalStorage() {
    const key = getStorageKey(authStore.user?.id);
    if (!key) return;
    localStorage.setItem(key, JSON.stringify(items.value));
  }

  // Tự động nạp lại hoặc dọn sạch khi đăng nhập / đổi tài khoản / đăng xuất
  watch(
    () => [authStore.isAuthenticated, authStore.user?.id],
    () => {
      loadForCurrentUser();
    },
    { immediate: true }
  );

  const totalWishlist = computed(() => items.value.length);

  const isInWishlist = computed(() => {
    return (productId) => {
      if (!authStore.isAuthenticated || productId == null) return false;
      const targetId = Number(productId);
      return items.value.some((item) => Number(item.id) === targetId);
    };
  });

  function toggleWishlist(product) {
    if (!authStore.isAuthenticated) {
      return { requiresAuth: true, added: false };
    }
    const normalized = normalizeProduct(product);
    if (!normalized) return { requiresAuth: false, added: false };

    const index = items.value.findIndex((item) => Number(item.id) === normalized.id);
    let added = false;
    if (index > -1) {
      items.value.splice(index, 1);
      added = false;
    } else {
      items.value.unshift(normalized);
      added = true;
    }
    saveToLocalStorage();
    return { requiresAuth: false, added };
  }

  function removeFromWishlist(productId) {
    const targetId = Number(productId);
    items.value = items.value.filter((item) => Number(item.id) !== targetId);
    saveToLocalStorage();
  }

  function clearWishlist() {
    items.value = [];
    saveToLocalStorage();
  }

  return {
    items,
    totalWishlist,
    isInWishlist,
    toggleWishlist,
    removeFromWishlist,
    clearWishlist,
    loadForCurrentUser
  };
});
