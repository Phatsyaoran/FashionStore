<template>
  <footer class="relative overflow-hidden luxury-gradient text-[#FFFDF9]/90 text-xs border-t border-white/15 mt-20 no-print">
    <StarfieldOverlay :min-stars="140" />
    <!-- Inset Sparkling Star Line (8px below top edge, mirroring Header) -->
    <SparkleDivider class="absolute left-0 right-0 top-[8px] z-20 pointer-events-none" />
    <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-16 pb-14 lg:pt-20 lg:pb-16">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-10">
        <!-- Col 1: Brand -->
        <div class="space-y-4">
          <div class="flex items-center gap-2.5 text-[#FFFDF9]">
            <span class="w-8 h-8 rounded-lg bg-white/15 backdrop-blur-md border border-[#F3D078]/50 text-[#F3D078] flex items-center justify-center font-black text-sm tracking-tight shadow-sm">F</span>
            <span class="text-base font-extrabold tracking-tight">FASHION<span class="text-[#F3D078]">.VN</span></span>
          </div>
          <p class="text-xs text-[#FFFDF9]/80 leading-relaxed">
            Thương hiệu thời trang đương đại tối giản, chú trọng chất liệu tự nhiên bền bỉ và phom dáng chuẩn người Việt.
          </p>
          <div class="pt-1 text-[11px] text-[#FFFDF9]/75 space-y-1.5">
            <p>88 Phố Huế, Quận Hai Bà Trưng, Hà Nội</p>
            <p>Hotline: <span class="text-[#F3D078] font-semibold">1900 8899</span> (08:00 – 22:00)</p>
            <p>Email: <span class="text-[#FFFDF9] font-semibold">support@fashion.vn</span></p>
          </div>
        </div>

        <!-- Col 2: Storefront -->
        <div>
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#F3D078] mb-4">Khám Phá Cửa Hàng</h4>
          <ul class="space-y-2.5">
            <li><router-link to="/" class="hover:text-[#F3D078] transition-colors">Trang chủ FashionStore</router-link></li>
            <li><router-link to="/catalog" class="hover:text-[#F3D078] transition-colors">Bộ sưu tập mới nhất</router-link></li>
            <li><router-link to="/catalog?isFlashSale=true" class="text-[#F3D078] hover:text-white font-semibold transition-colors">⚡ Flash Sale ưu đãi</router-link></li>
            <li><router-link to="/cart" class="hover:text-[#F3D078] transition-colors">Giỏ hàng của bạn</router-link></li>
          </ul>
        </div>

        <!-- Col 3: Customer Support -->
        <div>
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#F3D078] mb-4">Hỗ Trợ Khách Hàng</h4>
          <ul class="space-y-2.5">
            <li v-if="authStore.isAuthenticated">
              <router-link to="/order-tracking" class="hover:text-[#F3D078] transition-colors">Tra cứu tiến độ đơn hàng</router-link>
            </li>
            <li v-if="authStore.isAuthenticated">
              <router-link to="/wishlist" class="hover:text-[#F3D078] transition-colors">Danh sách yêu thích</router-link>
            </li>
            <li v-if="!authStore.isAuthenticated">
              <router-link to="/login" class="hover:text-[#F3D078] transition-colors">Đăng nhập tài khoản</router-link>
            </li>
            <li v-if="!authStore.isAuthenticated">
              <router-link to="/register" class="hover:text-[#F3D078] transition-colors">Đăng ký thành viên mới</router-link>
            </li>
            <li><router-link to="/cart" class="hover:text-[#F3D078] transition-colors">Chính sách Freeship từ 500k</router-link></li>
          </ul>
        </div>

        <!-- Col 4: Payments -->
        <div>
          <h4 class="text-[11px] font-bold uppercase tracking-[0.14em] text-[#F3D078] mb-4">Thanh Toán An Toàn</h4>
          <p class="text-xs text-[#FFFDF9]/80 leading-relaxed mb-4">
            Hỗ trợ chuyển khoản nhanh chuẩn VietQR 24/7 và thanh toán khi nhận hàng (COD).
          </p>
          <div class="flex flex-wrap gap-2">
            <span class="bg-white/15 backdrop-blur-md text-[#FFFDF9] px-3 py-1.5 rounded-lg border border-white/20 font-semibold text-[11px]">
              VietQR QuickLink
            </span>
            <span class="bg-white/15 backdrop-blur-md text-[#FFFDF9] px-3 py-1.5 rounded-lg border border-white/20 font-semibold text-[11px]">
              COD Toàn Quốc
            </span>
          </div>
          <div class="mt-4 pt-3 border-t border-white/15 text-[11px] text-[#FFFDF9]/75">
            ✓ Đổi trả 60 ngày • Đồng kiểm khi nhận
          </div>
        </div>
      </div>

      <div class="mt-12 pt-6 border-t border-white/15 flex flex-col sm:flex-row items-center justify-between text-[11px] text-[#FFFDF9]/70 gap-2">
        <p>© 2026 FashionStore.vn — Clean White Luxury Fashion.</p>
        <p>Giá đã bao gồm thuế VAT (VNĐ).</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import StarfieldOverlay from '@/components/StarfieldOverlay.vue';
import SparkleDivider from '@/components/SparkleDivider.vue';

const authStore = useAuthStore();
</script>
