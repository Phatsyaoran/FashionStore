<template>
  <footer class="bg-gray-900 text-gray-400 text-xs border-t border-gray-800 mt-20 no-print">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- Col 1 -->
        <div class="space-y-3">
          <div class="flex items-center gap-2 text-white">
            <span class="w-8 h-8 rounded-xl bg-rose-600 flex items-center justify-center font-black text-sm">F</span>
            <span class="text-base font-black tracking-tight">FASHION<span class="text-rose-500">.VN</span></span>
          </div>
          <p class="text-xs text-gray-400 leading-relaxed">
            Hệ thống thương mại điện tử thời trang nam nữ cao cấp, cung cấp sản phẩm may mặc chuẩn phom dáng người Việt, chất lượng bền bỉ và dịch vụ tận tâm.
          </p>
          <div class="pt-2 text-[11px] text-gray-500 space-y-1">
            <p>📍 Trụ sở: 88 Phố Huế, Quận Hai Bà Trưng, Hà Nội</p>
            <p>📞 Hotline CSKH: 1900 8899 (8:00 - 22:00)</p>
            <p>✉️ Email: support@fashion.vn</p>
          </div>
        </div>

        <!-- Col 2 -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-white mb-4">Khám Phá Cửa Hàng</h4>
          <ul class="space-y-2">
            <li><router-link to="/" class="hover:text-rose-400 transition-colors">Trang chủ FashionStore</router-link></li>
            <li><router-link to="/catalog" class="hover:text-rose-400 transition-colors">Bộ sưu tập mới nhất</router-link></li>
            <li><router-link to="/catalog?isFlashSale=true" class="hover:text-rose-400 transition-colors">Săn Flash Sale giá sốc</router-link></li>
            <li><router-link to="/cart" class="hover:text-rose-400 transition-colors">Giỏ hàng của bạn</router-link></li>
          </ul>
        </div>

        <!-- Col 3 -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-white mb-4">Hỗ Trợ Khách Hàng</h4>
          <ul class="space-y-2">
            <li><router-link to="/order-tracking" class="hover:text-rose-400 transition-colors">Tra cứu tiến độ đơn hàng</router-link></li>
            <li><router-link to="/login" class="hover:text-rose-400 transition-colors">Đăng nhập tài khoản</router-link></li>
            <li><router-link to="/register" class="hover:text-rose-400 transition-colors">Đăng ký thành viên mới</router-link></li>
            <li><router-link to="/checkout" class="hover:text-rose-400 transition-colors">Thanh toán & Freeship từ 500k</router-link></li>
          </ul>
        </div>

        <!-- Col 4 -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-white mb-4">Phương Thức Thanh Toán</h4>
          <p class="text-xs text-gray-400 mb-3">Thanh toán an toàn, bảo mật tuyệt đối với công nghệ chuẩn VietQR:</p>
          <div class="flex flex-wrap gap-2 items-center">
            <span class="bg-gray-800 text-gray-300 px-3 py-1.5 rounded-lg border border-gray-700 font-bold text-[11px] flex items-center gap-1.5">
              <span>⚡</span> VietQR QuickLink (MB Bank)
            </span>
            <span class="bg-gray-800 text-gray-300 px-3 py-1.5 rounded-lg border border-gray-700 font-bold text-[11px] flex items-center gap-1.5">
              <span>📦</span> COD (Thanh toán khi nhận)
            </span>
          </div>
          <div class="mt-4 p-3 bg-gray-800/60 rounded-xl border border-gray-800">
            <span class="text-[11px] text-emerald-400 font-medium">✓ Đã xác thực bảo mật SSL 256-bit</span>
          </div>
        </div>
      </div>

      <div class="mt-12 pt-6 border-t border-gray-800/80 flex flex-col sm:flex-row items-center justify-between text-[11px] text-gray-500 gap-2">
        <p>© 2026 FashionStore.vn. Thiết kế và vận hành bởi Principal Software Architect.</p>
        <p>Bảo lưu mọi quyền. Giá đã bao gồm thuế VAT (VNĐ).</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>
