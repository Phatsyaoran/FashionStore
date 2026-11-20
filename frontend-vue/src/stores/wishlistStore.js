import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

function safeJsonParse(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

export const useWishlistStore = defineStore('wishlist', () => {
  const items = ref(safeJsonParse('fashion_wishlist', []));

  const totalWishlist = computed(() => items.value.length);

  const isInWishlist = computed(() => {
    return (productId) => items.value.some((item) => item.id === productId);
  });

  function saveToLocalStorage() {
    localStorage.setItem('fashion_wishlist', JSON.stringify(items.value));
  }

  function toggleWishlist(product) {
    if (!product || !product.id) return;
    const index = items.value.findIndex((item) => item.id === product.id);
    if (index > -1) {
      items.value.splice(index, 1);
    } else {
      items.value.unshift({
        id: product.id,
        name: product.name,
        slug: product.slug,
        thumbnailUrl: product.thumbnailUrl,
        basePrice: product.basePrice,
        salePrice: product.salePrice,
        categoryName: product.categoryName,
        isFlashSale: product.isFlashSale,
        availableSizes: product.availableSizes || ['M', 'L'],
        availableColors: product.availableColors || ['Tiêu chuẩn']
      });
    }
    saveToLocalStorage();
  }

  function removeFromWishlist(productId) {
    items.value = items.value.filter((item) => item.id !== productId);
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
    clearWishlist
  };
});
