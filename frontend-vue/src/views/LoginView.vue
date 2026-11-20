<template>
  <div class="max-w-md mx-auto px-4 py-16 space-y-6">
    <div class="text-center space-y-2">
      <div class="inline-flex w-12 h-12 rounded-2xl bg-rose-600 text-white items-center justify-center font-black text-xl shadow-md shadow-rose-200">
        F
      </div>
      <h1 class="text-2xl font-black text-gray-900 tracking-tight">Đăng Nhập Tài Khoản</h1>
      <p class="text-xs text-gray-500">Truy cập để quản lý đơn hàng và tích lũy ưu đãi</p>
    </div>

    <form @submit.prevent="handleLogin" class="bg-white rounded-3xl border border-gray-100 p-8 shadow-xl space-y-4">
      <div v-if="errorMsg" class="p-3 bg-rose-50 border border-rose-200 rounded-xl text-rose-700 text-xs font-semibold">
        {{ errorMsg }}
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Email đăng nhập</label>
        <input
          type="email"
          v-model="email"
          required
          placeholder="admin@fashion.vn hoặc khachhang@fashion.vn"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Mật khẩu</label>
        <input
          type="password"
          v-model="password"
          required
          placeholder="Mật khẩu mẫu: 123456"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <button
        type="submit"
        :disabled="loading"
        class="w-full py-3.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs uppercase tracking-wider shadow-md hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <span v-if="loading">Đang đăng nhập...</span>
        <span v-else>Đăng Nhập</span>
      </button>

      <div class="pt-4 border-t border-gray-100 text-center text-xs text-gray-500">
        Chưa có tài khoản?
        <router-link to="/register" class="text-rose-600 font-bold hover:underline">
          Đăng ký ngay
        </router-link>
      </div>

      <!-- Quick Fill Sample Accounts -->
      <div class="pt-2 flex justify-center gap-2 text-[11px]">
        <button
          type="button"
          @click="fillAdmin"
          class="text-gray-500 hover:text-rose-600 bg-gray-50 px-2.5 py-1 rounded-lg border border-gray-200"
        >
          🔑 Tài khoản Admin
        </button>
        <button
          type="button"
          @click="fillCustomer"
          class="text-gray-500 hover:text-rose-600 bg-gray-50 px-2.5 py-1 rounded-lg border border-gray-200"
        >
          👤 Tài khoản Khách
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const loading = ref(false);
const errorMsg = ref('');

function fillAdmin() {
  email.value = 'admin@fashion.vn';
  password.value = '123456';
}

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
      if (authStore.isAdmin) {
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
