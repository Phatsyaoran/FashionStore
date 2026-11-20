import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import CatalogView from '@/views/CatalogView.vue';
import ProductDetailView from '@/views/ProductDetailView.vue';
import CartView from '@/views/CartView.vue';
import CheckoutView from '@/views/CheckoutView.vue';
import OrderTrackingView from '@/views/OrderTrackingView.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import ProfileView from '@/views/ProfileView.vue';
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue';
import AdminProductsView from '@/views/admin/AdminProductsView.vue';
import AdminOrdersView from '@/views/admin/AdminOrdersView.vue';
import AdminCouponsView from '@/views/admin/AdminCouponsView.vue';
import AdminCategoriesView from '@/views/admin/AdminCategoriesView.vue';
import { useAuthStore } from '@/stores/authStore';
import { useToastStore } from '@/stores/toastStore';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { title: 'Trang chủ' }
  },
  {
    path: '/catalog',
    name: 'catalog',
    component: CatalogView,
    meta: { title: 'Cửa hàng' }
  },
  {
    path: '/product/:slug',
    name: 'product-detail',
    component: ProductDetailView,
    meta: { title: 'Chi tiết sản phẩm' }
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartView,
    meta: { title: 'Giỏ hàng' }
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: CheckoutView,
    meta: { title: 'Thanh toán' }
  },
  {
    path: '/order-tracking',
    name: 'order-tracking',
    component: OrderTrackingView,
    meta: { title: 'Tra cứu đơn hàng' }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { title: 'Đăng nhập' }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { title: 'Đăng ký' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true, title: 'Tài khoản của tôi' }
  },
  {
    path: '/admin',
    name: 'admin-dashboard',
    component: AdminDashboardView,
    meta: { requiresAdmin: true, title: 'Quản trị' }
  },
  {
    path: '/admin/products',
    name: 'admin-products',
    component: AdminProductsView,
    meta: { requiresAdmin: true, title: 'Quản lý sản phẩm' }
  },
  {
    path: '/admin/orders',
    name: 'admin-orders',
    component: AdminOrdersView,
    meta: { requiresAdmin: true, title: 'Quản lý đơn hàng' }
  },
  {
    path: '/admin/coupons',
    name: 'admin-coupons',
    component: AdminCouponsView,
    meta: { requiresAdmin: true, title: 'Quản lý mã giảm giá' }
  },
  {
    path: '/admin/categories',
    name: 'admin-categories',
    component: AdminCategoriesView,
    meta: { requiresAdmin: true, title: 'Quản lý danh mục' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, behavior: 'smooth' };
    }
  }
});

// Route Guard (Auth & Admin)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAdmin) {
    if (!authStore.isAuthenticated || !authStore.isAdmin) {
      useToastStore().error('Vui lòng đăng nhập bằng tài khoản Quản Trị Viên (Admin) để truy cập khu vực này.');
      next({ name: 'login' });
      return;
    }
  } else if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      useToastStore().info('Vui lòng đăng nhập để xem thông tin tài khoản và đơn hàng của bạn.');
      next({ name: 'login' });
      return;
    }
  }
  next();
});

router.afterEach((to) => {
  const pageTitle = to.meta?.title;
  document.title = pageTitle ? `${pageTitle} | FashionStore` : 'FashionStore';
});

export default router;
