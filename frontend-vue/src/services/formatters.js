/**
 * Ảnh mẫu thời trang mặc định khi link ảnh sản phẩm bị lỗi hoặc thiếu
 */
export const DEFAULT_PRODUCT_IMAGE = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80';

/**
 * Xử lý lỗi load ảnh tự động chuyển sang ảnh mẫu chất lượng cao
 * @param {Event} e
 */
export function onImageError(e) {
  if (e && e.target && e.target.src !== DEFAULT_PRODUCT_IMAGE) {
    e.target.src = DEFAULT_PRODUCT_IMAGE;
  }
}

/**
 * Định dạng tiền tệ Việt Nam Đồng (VNĐ) chuẩn Quốc tế
 * @param {number|string} amount
 * @returns {string} Ví dụ: 150.000 ₫
 */
export function formatVND(amount) {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return '0 ₫';
  }
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0
  }).format(Number(amount));
}

/**
 * Định dạng ngày giờ chuẩn Việt Nam
 * @param {string|Date} dateVal
 * @returns {string} Ví dụ: 18/11/2026 14:30
 */
export function formatDateTime(dateVal) {
  if (!dateVal) return '';
  const d = new Date(dateVal);
  if (isNaN(d.getTime())) return '';
  return d.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

/**
 * Định dạng ngày tháng
 * @param {string|Date} dateVal
 * @returns {string} Ví dụ: 18/11/2026
 */
export function formatDate(dateVal) {
  if (!dateVal) return '';
  const d = new Date(dateVal);
  if (isNaN(d.getTime())) return '';
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}
