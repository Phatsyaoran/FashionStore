<template>
  <div class="max-w-md mx-auto px-4 py-12 sm:py-16 space-y-6">
    <div class="text-center space-y-2">
      <div class="inline-flex w-11 h-11 rounded-xl luxury-gradient text-[#FFFDF9] items-center justify-center font-extrabold text-lg shadow-sm">
        F
      </div>
      <h1 class="text-2xl font-bold text-[#18181B] tracking-tight">Đăng Nhập Tài Khoản</h1>
      <p class="text-xs text-[#6B7280]">Truy cập để quản lý đơn hàng và tích lũy ưu đãi</p>
    </div>

    <form @submit.prevent="handleLogin" class="bg-white rounded-2xl border border-neutral-200/80 p-6 sm:p-8 shadow-sm space-y-4">
      <div v-if="errorMsg" class="p-3 bg-rose-50 border border-rose-200 rounded-lg text-rose-700 text-xs font-medium">
        {{ errorMsg }}
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Email đăng nhập</label>
        <input
          type="email"
          v-model="email"
          required
          placeholder="Nhập email của bạn..."
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Mật khẩu</label>
        <input
          type="password"
          v-model="password"
          required
          placeholder="Nhập mật khẩu..."
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <button
        type="submit"
        :disabled="loading"
        class="btn-sheen silky-hover active:scale-[0.98] w-full h-11 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
      >
        <span v-if="loading">Đang đăng nhập...</span>
        <span v-else>Đăng Nhập</span>
      </button>

      <div class="pt-4 border-t border-neutral-200/80 text-center text-xs text-[#6B7280]">
        Chưa có tài khoản?
        <router-link to="/register" class="text-[#7A1032] font-bold hover:underline">
          Đăng ký ngay
        </router-link>
      </div>

      <!-- Quick Fill Demo Customer Account -->
      <div class="pt-1 flex justify-center gap-2 text-[11px]">
        <button
          type="button"
          @click="fillCustomer"
          class="text-[#6B7280] hover:text-[#7A1032] bg-[#FAFAFA] hover:bg-neutral-100 px-3 py-1.5 rounded-lg border border-neutral-200 transition-colors"
        >
          👤 Dùng thử tài khoản Khách hàng
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const loading = ref(false);
const errorMsg = ref('');

function fillCustomer() {
  email.value = 'khachhang@fashion.vn';
  password.value = '123456';
}

async function handleLogin() {
  if (loading.value) return;
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await authStore.login(email.value, password.value);
    if (res.success) {
      const redirect = typeof route.query.redirect === 'string' && route.query.redirect.startsWith('/')
        ? route.query.redirect
        : null;
      if (redirect) {
        router.push(redirect);
      } else if (authStore.isAdmin) {
        router.push('/admin');
      } else {
        router.push('/');
      }
    } else {
      errorMsg.value = res.message;
    }
  } catch (err) {
    errorMsg.value = 'Lỗi hệ thống khi đăng nhập';
  } finally {
    loading.value = false;
  }
}
</script>
