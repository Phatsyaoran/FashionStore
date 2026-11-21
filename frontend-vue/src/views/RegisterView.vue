<template>
  <div class="max-w-md mx-auto px-4 py-12 sm:py-16 space-y-6">
    <div class="text-center space-y-2">
      <div class="inline-flex w-11 h-11 rounded-xl luxury-gradient text-[#FFFDF9] items-center justify-center font-extrabold text-lg shadow-sm">
        F
      </div>
      <h1 class="text-2xl font-bold text-[#18181B] tracking-tight">Tạo Tài Khoản Mới</h1>
      <p class="text-xs text-[#6B7280]">Đăng ký để nhận voucher giảm 10% ngay hôm nay</p>
    </div>

    <form @submit.prevent="handleRegister" class="bg-white rounded-2xl border border-neutral-200/80 p-6 sm:p-8 shadow-sm space-y-4">
      <div v-if="errorMsg" class="p-3 bg-rose-50 border border-rose-200 rounded-lg text-rose-700 text-xs font-medium">
        {{ errorMsg }}
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Họ và tên *</label>
        <input
          type="text"
          v-model="form.fullName"
          required
          placeholder="Nguyễn Văn A"
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Email *</label>
        <input
          type="email"
          v-model="form.email"
          required
          placeholder="email@example.com"
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Mật khẩu *</label>
        <input
          type="password"
          v-model="form.password"
          required
          placeholder="Tối thiểu 6 ký tự"
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Số điện thoại</label>
        <input
          type="tel"
          v-model="form.phone"
          placeholder="0912345678"
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <div class="space-y-1.5">
        <label class="block text-xs font-medium text-[#6B7280]">Địa chỉ</label>
        <input
          type="text"
          v-model="form.address"
          placeholder="Quận/Huyện, Tỉnh/TP"
          class="w-full h-11 px-3.5 rounded-lg border border-neutral-200 bg-white text-[#18181B] placeholder:text-neutral-400 text-xs focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] focus:outline-none"
        />
      </div>

      <button
        type="submit"
        :disabled="loading"
        class="btn-sheen silky-hover active:scale-[0.98] w-full h-11 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white font-bold text-xs uppercase tracking-wider disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
      >
        <span v-if="loading">Đang đăng ký...</span>
        <span v-else>Đăng Ký Tài Khoản</span>
      </button>

      <div class="pt-4 border-t border-neutral-200/80 text-center text-xs text-[#6B7280]">
        Đã có tài khoản?
        <router-link to="/login" class="text-[#7A1032] font-bold hover:underline">
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
import { useToastStore } from '@/stores/toastStore';

const router = useRouter();
const authStore = useAuthStore();
const toastStore = useToastStore();

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
      toastStore.success('Đăng ký tài khoản thành công!');
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
