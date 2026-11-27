import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request Interceptor: Tự động đính kèm JWT Bearer Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Chuẩn hóa dữ liệu trả về và xử lý lỗi thân thiện bằng tiếng Việt
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    let errorMsg = 'Lỗi kết nối máy chủ. Vui lòng kiểm tra lại mạng.';
    if (error.response) {
      if (error.response.data && error.response.data.message) {
        errorMsg = error.response.data.message;
      } else if (error.response.status === 400) {
        errorMsg = 'Dữ liệu gửi lên không hợp lệ. Vui lòng kiểm tra lại thông tin.';
      } else if (error.response.status === 401) {
        errorMsg = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
        const requestUrl = error.config?.url || '';
        if (!requestUrl.includes('/auth/login') && !requestUrl.includes('/auth/register')) {
          import('@/stores/authStore').then(({ useAuthStore }) => {
            try {
              useAuthStore().logout();
            } catch {
              localStorage.removeItem('token');
              localStorage.removeItem('user');
            }
          });
          import('@/router').then(({ default: router }) => {
            if (router.currentRoute.value.path !== '/login') {
              router.push('/login');
            }
          });
        }
      } else if (error.response.status === 403) {
        errorMsg = 'Bạn không có quyền thực hiện thao tác này.';
      } else if (error.response.status === 404) {
        errorMsg = 'Không tìm thấy dữ liệu yêu cầu trên hệ thống.';
      } else if (error.response.status >= 500) {
        errorMsg = 'Máy chủ đang bận hoặc gặp sự cố xử lý. Vui lòng thử lại sau.';
      }
    } else if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      errorMsg = 'Kết nối quá thời gian chờ (timeout). Vui lòng thử lại.';
    }
    error.friendlyMessage = errorMsg;
    console.error('API Error:', errorMsg);
    return Promise.reject(error);
  }
);

export default api;
