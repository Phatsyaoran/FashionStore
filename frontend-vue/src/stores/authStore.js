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

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '');
  const user = ref(safeJsonParse('user', null));

  const isAuthenticated = computed(() => !!token.value);
  const isAdmin = computed(() => {
    const role = user.value?.role;
    return role === 'ADMIN' || role === 'ROLE_ADMIN';
  });

  function setAuth(data) {
    token.value = data.token;
    user.value = {
      id: data.id,
      fullName: data.fullName,
      email: data.email,
      phone: data.phone,
      address: data.address,
      role: data.role
    };
    localStorage.setItem('token', token.value);
    localStorage.setItem('user', JSON.stringify(user.value));
  }

  function logout() {
    token.value = '';
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  async function login(email, password) {
    try {
      const res = await api.post('/auth/login', { email, password });
      if (res.success && res.data) {
        setAuth(res.data);
        return { success: true, message: res.message };
      }
      return { success: false, message: res.message || 'Đăng nhập không thành công' };
    } catch (err) {
      const msg = err.response?.data?.message || 'Email hoặc mật khẩu không chính xác';
      return { success: false, message: msg };
    }
  }

  async function register(registerData) {
    try {
      const res = await api.post('/auth/register', registerData);
      if (res.success && res.data) {
        setAuth(res.data);
        return { success: true, message: res.message };
      }
      return { success: false, message: res.message || 'Đăng ký không thành công' };
    } catch (err) {
      const msg = err.response?.data?.message || 'Đăng ký thất bại, email có thể đã tồn tại';
      return { success: false, message: msg };
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    setAuth
  };
});
