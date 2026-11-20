import { defineStore } from 'pinia';
import { ref } from 'vue';

function safeJsonParse(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

export const useProductStore = defineStore('product', () => {
  // Trạng thái Quick View Modal
  const isQuickViewOpen = ref(false);
  const quickViewProduct = ref(null);

  function openQuickView(product) {
    quickViewProduct.value = product;
    isQuickViewOpen.value = true;
  }

  function closeQuickView() {
    isQuickViewOpen.value = false;
    quickViewProduct.value = null;
  }

  // Danh sách sản phẩm vừa xem (tối đa 8 sản phẩm)
  const recentlyViewed = ref(safeJsonParse('fashion_recently_viewed', []));

  function addRecentlyViewed(product) {
    if (!product || !product.id) return;

    // Lọc bỏ nếu sản phẩm đã có trong danh sách
    const filtered = recentlyViewed.value.filter(p => p.id !== product.id);

    // Đưa sản phẩm mới nhất lên đầu
    filtered.unshift({
      id: product.id,
      name: product.name,
      slug: product.slug,
      thumbnailUrl: product.thumbnailUrl,
      basePrice: product.basePrice,
      salePrice: product.salePrice,
      categoryName: product.categoryName,
      isFlashSale: product.isFlashSale
    });

    // Giới hạn tối đa 8 sản phẩm
    recentlyViewed.value = filtered.slice(0, 8);
    localStorage.setItem('fashion_recently_viewed', JSON.stringify(recentlyViewed.value));
  }

  function clearRecentlyViewed() {
    recentlyViewed.value = [];
    localStorage.removeItem('fashion_recently_viewed');
  }

  return {
    isQuickViewOpen,
    quickViewProduct,
    openQuickView,
    closeQuickView,
    recentlyViewed,
    addRecentlyViewed,
    clearRecentlyViewed
  };
});
