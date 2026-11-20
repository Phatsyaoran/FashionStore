import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import api from '@/services/api';

function safeJsonParse(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

export const useCartStore = defineStore('cart', () => {
  const FREE_SHIP_THRESHOLD = 500000;
  const DEFAULT_SHIPPING_FEE = 30000;

  // Lưu giỏ hàng trong localStorage
  const items = ref(safeJsonParse('fashion_cart', []));
  const isDrawerOpen = ref(false);
  const coupon = ref(safeJsonParse('fashion_coupon', null));

  function saveCart() {
    localStorage.setItem('fashion_cart', JSON.stringify(items.value));
  }

  function saveCoupon() {
    if (coupon.value) {
      localStorage.setItem('fashion_coupon', JSON.stringify(coupon.value));
    } else {
      localStorage.removeItem('fashion_coupon');
    }
  }

  // Tổng số lượng sản phẩm
  const totalItems = computed(() => {
    return items.value.reduce((acc, item) => acc + item.quantity, 0);
  });

  // Tạm tính tiền hàng (SubTotal)
  const subTotal = computed(() => {
    return items.value.reduce((acc, item) => acc + (item.price * item.quantity), 0);
  });

  // Số tiền còn thiếu để đạt Freeship
  const missingForFreeShip = computed(() => {
    return Math.max(0, FREE_SHIP_THRESHOLD - subTotal.value);
  });

  // % tiến trình Freeship (0 - 100)
  const freeShipPercent = computed(() => {
    if (subTotal.value <= 0) return 0;
    return Math.min(100, Math.round((subTotal.value / FREE_SHIP_THRESHOLD) * 100));
  });

  // Trạng thái đã đạt Freeship hay chưa
  const isFreeShip = computed(() => {
    return subTotal.value >= FREE_SHIP_THRESHOLD;
  });

  // Phí ship thực tế
  const shippingFee = computed(() => {
    if (items.value.length === 0) return 0;
    return isFreeShip.value ? 0 : DEFAULT_SHIPPING_FEE;
  });

  // Số tiền giảm giá từ Coupon
  const discountAmount = computed(() => {
    if (!coupon.value || subTotal.value < (coupon.value.minOrderValue || 0)) {
      return 0;
    }
    const percent = coupon.value.discountPercent || 0;
    const maxDiscount = coupon.value.maxDiscountAmount || 0;
    const calc = (subTotal.value * percent) / 100;
    return Math.min(calc, maxDiscount);
  });

  // Tổng thanh toán cuối cùng
  const totalAmount = computed(() => {
    return Math.max(0, subTotal.value + shippingFee.value - discountAmount.value);
  });

  // Thêm vào giỏ hàng
  function addToCart(product, variant, quantity = 1) {
    if (!variant || variant.stockQuantity <= 0) {
      return { success: false, message: 'Biến thể này hiện đã hết hàng' };
    }

    const existingIndex = items.value.findIndex(item => item.variantId === variant.id);

    if (existingIndex > -1) {
      const newQty = items.value[existingIndex].quantity + quantity;
      if (newQty > variant.stockQuantity) {
        items.value[existingIndex].quantity = variant.stockQuantity;
        saveCart();
        return {
          success: false,
          message: `Số lượng đã đạt tối đa tồn kho (${variant.stockQuantity} chiếc)`
        };
      }
      items.value[existingIndex].quantity = newQty;
    } else {
      items.value.push({
        productId: product.id,
        productName: product.name,
        productSlug: product.slug,
        thumbnailUrl: product.thumbnailUrl,
        variantId: variant.id,
        size: variant.size,
        colorName: variant.colorName,
        colorHex: variant.colorHex,
        sku: variant.sku,
        price: variant.price,
        stockQuantity: variant.stockQuantity,
        quantity: Math.min(quantity, variant.stockQuantity)
      });
    }

    saveCart();
    isDrawerOpen.value = true;
    return { success: true, message: 'Đã thêm sản phẩm vào giỏ hàng' };
  }

  function updateQuantity(variantId, quantity) {
    const item = items.value.find(i => i.variantId === variantId);
    if (!item) return;

    if (quantity <= 0) {
      removeFromCart(variantId);
      return;
    }

    item.quantity = Math.min(quantity, item.stockQuantity);
    saveCart();
  }

  function removeFromCart(variantId) {
    items.value = items.value.filter(i => i.variantId !== variantId);
    saveCart();
  }

  function clearCart() {
    items.value = [];
    coupon.value = null;
    saveCart();
    saveCoupon();
  }

  function toggleDrawer(open) {
    if (open !== undefined) {
      isDrawerOpen.value = open;
    } else {
      isDrawerOpen.value = !isDrawerOpen.value;
    }
  }

  async function applyCouponCode(code) {
    if (!code || !code.trim()) {
      return { success: false, message: 'Vui lòng nhập mã giảm giá' };
    }
    try {
      const res = await api.post('/coupons/apply', {
        code: code.trim(),
        orderValue: subTotal.value
      });

      if (res.success && res.data) {
        coupon.value = res.data;
        saveCoupon();
        return { success: true, message: res.message || 'Áp dụng mã giảm giá thành công!' };
      }
      return { success: false, message: res.message || 'Mã giảm giá không hợp lệ' };
    } catch (err) {
      const msg = err.response?.data?.message || 'Không thể áp dụng mã giảm giá này';
      return { success: false, message: msg };
    }
  }

  function removeCoupon() {
    coupon.value = null;
    saveCoupon();
  }

  return {
    items,
    isDrawerOpen,
    coupon,
    totalItems,
    subTotal,
    missingForFreeShip,
    freeShipPercent,
    isFreeShip,
    shippingFee,
    discountAmount,
    totalAmount,
    FREE_SHIP_THRESHOLD,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
    toggleDrawer,
    applyCouponCode,
    removeCoupon
  };
});
