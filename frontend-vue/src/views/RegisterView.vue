<template>
  <div class="max-w-md mx-auto px-4 py-16 space-y-6">
    <div class="text-center space-y-2">
      <div class="inline-flex w-12 h-12 rounded-2xl bg-rose-600 text-white items-center justify-center font-black text-xl shadow-md shadow-rose-200">
        F
      </div>
      <h1 class="text-2xl font-black text-gray-900 tracking-tight">Tạo Tài Khoản Mới</h1>
      <p class="text-xs text-gray-500">Đăng ký để nhận voucher giảm 10% ngay hôm nay</p>
    </div>

    <form @submit.prevent="handleRegister" class="bg-white rounded-3xl border border-gray-100 p-8 shadow-xl space-y-4">
      <div v-if="errorMsg" class="p-3 bg-rose-50 border border-rose-200 rounded-xl text-rose-700 text-xs font-semibold">
        {{ errorMsg }}
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Họ và tên *</label>
        <input
          type="text"
          v-model="form.fullName"
          required
          placeholder="Nguyễn Văn A"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Email *</label>
        <input
          type="email"
          v-model="form.email"
          required
          placeholder="email@example.com"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Mật khẩu *</label>
        <input
          type="password"
          v-model="form.password"
          required
          placeholder="Tối thiểu 6 ký tự"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Số điện thoại</label>
        <input
          type="tel"
          v-model="form.phone"
          placeholder="0912345678"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <div class="space-y-1">
        <label class="block text-xs font-semibold text-gray-700">Địa chỉ</label>
        <input
          type="text"
          v-model="form.address"
          placeholder="Quận/Huyện, Tỉnh/TP"
          class="w-full px-4 py-2.5 rounded-xl border border-gray-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
        />
      </div>

      <button
        type="submit"
        :disabled="loading"
        class="w-full py-3.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs uppercase tracking-wider shadow-md hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <span v-if="loading">Đang đăng ký...</span>
        <span v-else>Đăng Ký Tài Khoản</span>
      </button>

      <div class="pt-4 border-t border-gray-100 text-center text-xs text-gray-500">
        Đã có tài khoản?
        <router-link to="/login" class="text-rose-600 font-bold hover:underline">
          Đăng nhập
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  fullName: '',
  email: '',
  password: '',
  phone: '',
  address: ''
});

const loading = ref(false);
const errorMsg = ref('');

async function handleRegister() {
  if (loading.value) return;

  // Validation cơ bản
  if (!form.fullName.trim()) {
    errorMsg.value = 'Vui lòng nhập họ và tên';
    return;
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
    errorMsg.value = 'Địa chỉ email không đúng định dạng';
    return;
  }
  if (form.password.length < 6) {
    errorMsg.value = 'Mật khẩu phải có độ dài từ 6 ký tự trở lên';
    return;
  }
  if (form.phone && !/^0\d{9,10}$/.test(form.phone.trim())) {
    errorMsg.value = 'Số điện thoại không đúng định dạng (bắt đầu bằng 0, 10 chữ số)';
    return;
  }

  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await authStore.register(form);
    if (res.success) {
      alert('Đăng ký tài khoản thành công!');
      router.push('/');
    } else {
      errorMsg.value = res.message;
    }
  } catch (err) {
    errorMsg.value = 'Lỗi hệ thống khi đăng ký';
  } finally {
    loading.value = false;
  }
}
</script>
